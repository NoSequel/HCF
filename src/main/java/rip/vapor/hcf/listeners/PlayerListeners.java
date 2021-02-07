package rip.vapor.hcf.listeners;

import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.SpawnProtectionData;
import rip.vapor.hcf.player.data.deathban.DeathbanData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.timers.TimerModule;
import rip.vapor.hcf.timers.impl.PlayerTimer;
import rip.vapor.hcf.timers.impl.player.SpawnProtectionTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener, Controllable<PlayerDataModule> {

    private final PlayerDataModule module = this.getModule();
    private final TeamModule teamController = Vapor.getInstance().getHandler().find(TeamModule.class);
    private final TimerModule timerController = Vapor.getInstance().getHandler().find(TimerModule.class);


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = module.findOrElseMake(player.getUniqueId());

        if (player.isDead()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
        }

        if (playerData.hasData(DeathbanData.class)) {
            final DeathbanData deathbanData = playerData.findData(DeathbanData.class);

            if (deathbanData.getExpiration() - System.currentTimeMillis() <= 0) {
                playerData.removeData(deathbanData);
                timerController.findTimer(SpawnProtectionTimer.class).start(player);

                return;
            }

            playerData.findData(DeathbanData.class).kickPlayer(player);
        }

        if (player.isOnline()) { // check if the player is online & hasn't been kicked yet.
            if (!player.hasPlayedBefore()) {
                timerController.findTimer(SpawnProtectionTimer.class).start(player);
            }

            if (playerData.hasData(SpawnProtectionData.class)) {
                final SpawnProtectionData data = playerData.findData(SpawnProtectionData.class);
                timerController.findTimer(SpawnProtectionTimer.class).start(player, data.getDurationLeft());
            }

            if (teamController.findTeam(player) != null) {
                final Team team = teamController.findTeam(player);
                final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);

                playerTeamData.broadcast(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + player.getName());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        event.setQuitMessage(null);

        if (teamController.findTeam(player) != null) {
            final Team team = teamController.findTeam(player);
            final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);

            playerTeamData.broadcast(ChatColor.RED + "Member Offline: " + ChatColor.WHITE + player.getName());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        this.timerController.getTimers().stream()
                .filter(timer -> timer instanceof PlayerTimer)
                .map(timer -> ((PlayerTimer) timer))
                .filter(timer -> timer.isOnCooldown(player))
                .forEach(timer -> timer.cancel(player));
    }
}