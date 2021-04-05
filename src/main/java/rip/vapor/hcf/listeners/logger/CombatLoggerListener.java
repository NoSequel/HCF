package rip.vapor.hcf.listeners.logger;

import lombok.RequiredArgsConstructor;
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
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.CombatLoggerData;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.CombatTimer;

import java.util.Optional;

@RequiredArgsConstructor
public class CombatLoggerListener implements Listener {

    private final CombatLoggerModule combatLoggerModule;
    private final PlayerDataModule playerDataModule;
    private final TimerModule timerModule;

    /**
     * Constructor to make a new combat logger listener instance
     *
     * @param handler the handler to get the modules from
     */
    public CombatLoggerListener(ModuleHandler handler) {
        this.combatLoggerModule = handler.find(CombatLoggerModule.class);
        this.playerDataModule = handler.find(PlayerDataModule.class);
        this.timerModule = handler.find(TimerModule.class);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final Entity entity = event.getEntity();
        final Optional<CombatLogger> logger = this.combatLoggerModule.getLoggers().stream()
                .filter(combatLogger -> combatLogger.getVillager().equals(entity))
                .findFirst();

        if (logger.isPresent() && Bukkit.getPlayer(logger.get().getPlayerUuid()) == null) {
            final PlayerData data = this.playerDataModule.findPlayerData(logger.get().getPlayerUuid());
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
        final Optional<CombatLogger> logger = this.combatLoggerModule.getLoggers().stream()
                .filter(combatLogger -> combatLogger.getVillager().equals(entity))
                .findFirst();

        if (logger.isPresent() && Bukkit.getPlayer(logger.get().getPlayerUuid()) == null) {
            entity.teleport(logger.get().getLocation());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && this.combatLoggerModule.getLoggers().stream()
                .anyMatch(logger -> logger.getVillager().equals(event.getRightClicked()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = this.playerDataModule.findPlayerData(player.getUniqueId());
        final Optional<CombatTimer> combatTimer = this.timerModule.findTimer(CombatTimer.class);

        if (data != null && combatTimer.isPresent() && combatTimer.get().isOnCooldown(player) && !player.isDead()) {
            new CombatLogger(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = this.playerDataModule.findPlayerData(player.getUniqueId());

        if (data != null && data.hasData(CombatLoggerData.class)) {
            final CombatLoggerData combatLoggerData = data.findData(CombatLoggerData.class);

            this.combatLoggerModule.getLoggers().stream()
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
}