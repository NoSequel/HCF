package rip.vapor.hcf.player.timers.impl.global;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import rip.vapor.hcf.player.timers.impl.GlobalTimer;

public class SOTWTimer extends GlobalTimer {

    public SOTWTimer(long time) {
        super("SOTW", ChatColor.GREEN + ChatColor.BOLD.toString() + "SOTW", false, time, true);
        super.getThread().setActive(true);

        Bukkit.broadcastMessage(ChatColor.GREEN + "The SOTW timer has started, damage is now disabled!");
    }

    @Override
    public void handleTick() {

    }

    @Override
    public void handleEnd() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "The SOTW timer has expired, damage is now enabled!");
        this.getThread().setShouldSubtract(false);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.getThread().isShouldSubtract()) {
            event.setCancelled(true);
        }
    }
}
