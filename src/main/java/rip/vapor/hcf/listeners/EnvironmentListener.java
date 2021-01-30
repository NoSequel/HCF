package rip.vapor.hcf.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EnvironmentListener implements Listener {

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

}
