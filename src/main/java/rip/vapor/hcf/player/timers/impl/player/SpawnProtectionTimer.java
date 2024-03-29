package rip.vapor.hcf.player.timers.impl.player;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.SpawnProtectionData;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class SpawnProtectionTimer extends PlayerTimer {

    private final Map<Player, PlayerData> data = new HashMap<>();
    private final ModuleHandler handler;

    public SpawnProtectionTimer(ModuleHandler handler) {
        super("SpawnProtection", ChatColor.GREEN + ChatColor.BOLD.toString() + "Invincibility", false, 60000*30);
        this.handler = handler;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();

        if ((entity instanceof Player && this.isOnCooldown((Player) entity)) || (damager instanceof Player && this.isOnCooldown((Player) damager)) || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player && this.isOnCooldown((Player) ((Projectile) damager).getShooter()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void handleTick(Player player) {
        final PlayerData playerData = this.findData(player);
        SpawnProtectionData data = playerData.findData(SpawnProtectionData.class);

        if (data == null) {
            playerData.addData((data = new SpawnProtectionData(this.getDefaultDuration())));
        }


        data.setDurationLeft(this.getDuration(player));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        this.data.put(event.getPlayer(), handler.find(PlayerDataModule.class).findPlayerData(event.getPlayer().getUniqueId()));
    }

    @Override
    public void handleEnd(Player player) {
        final PlayerData data = this.findData(player);

        if(data != null && data.hasData(SpawnProtectionData.class)) {
            data.removeData(data.findData(SpawnProtectionData.class));
        }

        this.data.remove(player);
    }

    @Override
    public void handleCancel(Player player) {
        final PlayerData data = this.findData(player);

        if(data != null && data.hasData(SpawnProtectionData.class)) {
            data.removeData(data.findData(SpawnProtectionData.class));
        }

        this.data.remove(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = this.findData(player);
        final SpawnProtectionData protectionData = data.findData(SpawnProtectionData.class);

        if (this.isOnCooldown(player) && protectionData == null) {
            data.addData(new SpawnProtectionData(this.getDuration(player)));
        } else if (!this.isOnCooldown(player) && protectionData != null) {
            protectionData.setDurationLeft(0L);
        }
    }

    /**
     * Find a data object by a player
     *
     * @param player the player
     * @return the object | or null
     */
    private PlayerData findData(Player player) {
        return data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(player))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }
}
