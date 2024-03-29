package rip.vapor.hcf.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.BalanceData;
import rip.vapor.hcf.player.data.SpawnProtectionData;
import rip.vapor.hcf.player.data.deathban.DeathbanData;
import rip.vapor.hcf.player.timers.Timer;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import rip.vapor.hcf.player.timers.impl.player.SpawnProtectionTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerListeners implements Listener {

    private final PlayerDataModule playerDataModule;
    private final TeamModule teamModule;
    private final TimerModule timerModule;

    /**
     * Constructor to make a new player listener object
     *
     * @param handler the handler to get the modules from
     */
    public PlayerListeners(ModuleHandler handler) {
        this.playerDataModule = handler.find(PlayerDataModule.class);
        this.teamModule = handler.find(TeamModule.class);
        this.timerModule = handler.find(TimerModule.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = playerDataModule.findOrElseMake(player.getUniqueId());

        if (player.isDead()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
        }

        if (playerData.hasData(DeathbanData.class)) {
            final DeathbanData deathbanData = playerData.findData(DeathbanData.class);

            if (deathbanData.getExpiration() - System.currentTimeMillis() <= 0) {
                playerData.removeData(deathbanData);
                timerModule.findTimer(SpawnProtectionTimer.class).ifPresent(timer -> timer.start(player));

                return;
            }

            playerData.findData(DeathbanData.class).kickPlayer(player);
        }

        if (player.isOnline()) { // check if the player is online & hasn't been kicked yet.
            final Optional<Team> team = this.teamModule.findTeam(player);

            if (!player.hasPlayedBefore()) {
                timerModule.findTimer(SpawnProtectionTimer.class).ifPresent(timer -> timer.start(player));
            }

            if (!playerData.hasData(BalanceData.class)) {
                playerData.addData(new BalanceData());
            }

            if (playerData.hasData(SpawnProtectionData.class)) {
                final SpawnProtectionData data = playerData.findData(SpawnProtectionData.class);
                timerModule.findTimer(SpawnProtectionTimer.class).ifPresent(timer -> timer.start(player, data.getDurationLeft()));
            }

            if (team.isPresent() && team.get().hasData(PlayerTeamData.class)) {
                team.get().findData(PlayerTeamData.class).broadcast(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + player.getName());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final Player player = event.getPlayer();
        final Optional<Team> team = this.teamModule.findTeam(player);

        team.ifPresent(value -> value.findData(PlayerTeamData.class)
                .broadcast(ChatColor.RED + "Member Offline: " + ChatColor.WHITE + player.getName()));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        for(Timer timer : this.timerModule.getTimers()) {
            if(timer instanceof PlayerTimer && ((PlayerTimer) timer).isOnCooldown(player)) {
                ((PlayerTimer) timer).cancel(player);
            }
        }
    }
}