package rip.vapor.hcf.listeners.logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.CombatLoggerData;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.CombatTimer;

import java.util.Optional;

public class CombatLoggerListener implements Listener, Controllable<CombatLoggerModule> {

    private final PlayerDataModule playerController = Vapor.getInstance().getHandler().find(PlayerDataModule.class);
    private final TimerModule timerController = Vapor.getInstance().getHandler().find(TimerModule.class);

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final Entity entity = event.getEntity();
        final Optional<CombatLogger> logger = this.getModule().getLoggers().stream()
                .filter(combatLogger -> combatLogger.getVillager().equals(entity))
                .findFirst();

        if (logger.isPresent() && Bukkit.getPlayer(logger.get().getPlayerUuid()) == null) {
            final PlayerData data = playerController.findPlayerData(logger.get().getPlayerUuid());
            CombatLoggerData combatLoggerData = data.findData(CombatLoggerData.class);

            logger.get().dropItems();

            if (combatLoggerData == null) {
                data.addData((combatLoggerData = new CombatLoggerData()));
            }

            combatLoggerData.setKilled(true);
            combatLoggerData.setKillerUuid(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        final Optional<CombatLogger> logger = this.getModule().getLoggers().stream()
                .filter(combatLogger -> combatLogger.getVillager().equals(entity))
                .findFirst();

        if (logger.isPresent() && Bukkit.getPlayer(logger.get().getPlayerUuid()) == null) {
            entity.teleport(logger.get().getLocation());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && this.getModule().getLoggers().stream()
                .anyMatch(logger -> logger.getVillager().equals(event.getRightClicked()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = playerController.findPlayerData(player.getUniqueId());

        if (data != null && timerController.findTimer(CombatTimer.class).isOnCooldown(player) && !player.isDead()) {
            new CombatLogger(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = playerController.findPlayerData(player.getUniqueId());
        final CombatLoggerData combatLoggerData = data.findData(CombatLoggerData.class);

        this.getModule().getLoggers().stream()
                .filter(logger -> logger.getPlayerUuid() != null && logger.getPlayerUuid().equals(player.getUniqueId()))
                .forEach(CombatLogger::destruct);

        if (combatLoggerData != null && combatLoggerData.isKilled()) {
            combatLoggerData.setKilled(false);

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
            player.setHealth(0);
        }
    }
}