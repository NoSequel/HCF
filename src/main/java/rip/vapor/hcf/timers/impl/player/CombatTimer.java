package rip.vapor.hcf.timers.impl.player;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.timers.impl.PlayerTimer;
import rip.vapor.hcf.timers.TimerModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public class CombatTimer extends PlayerTimer {

    private final TimerModule timerController = Vapor.getInstance().getHandler().find(TimerModule.class);

    public CombatTimer() {
        super("Combat", ChatColor.RED + ChatColor.BOLD.toString() + "Spawn Tag", false, 30000);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Player damager = (Player) event.getDamager();
            final TeleportTimer teleportTimer = timerController.findTimer(TeleportTimer.class);

            Arrays.asList(player, damager).forEach(target -> {
                this.start(target);

                if (teleportTimer.isOnCooldown(target)) {
                    teleportTimer.cancel(target);
                }
            });
        }
    }

    @Override
    public void handleTick(Player player) {
    }

    @Override
    public void handleEnd(Player player) {
    }

    @Override
    public void handleCancel(Player player) {
    }
}