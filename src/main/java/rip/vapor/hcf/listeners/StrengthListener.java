package rip.vapor.hcf.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class StrengthListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();

            if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                event.setDamage(Math.max(event.getDamage()-1.2, 0));
            }
        }
    }
}