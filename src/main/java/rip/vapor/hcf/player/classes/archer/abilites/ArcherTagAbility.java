package rip.vapor.hcf.player.classes.archer.abilites;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.archer.ArcherClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArcherTagAbility extends Ability {

    private ClassController classController = Vapor.getInstance().getHandler().findController(ClassController.class);

    @EventHandler
    public void onArcher(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            final Player player = (Player) ((Arrow) event.getDamager()).getShooter();
            final Player damaged = (Player) event.getEntity();

            if (this.classController == null) {
                this.classController = Vapor.getInstance().getHandler().findController(ClassController.class);
            }

            final ArcherClass archerClass = classController.findClass(ArcherClass.class);

            if (archerClass.getEquipped().contains(player)) {
                event.setDamage(Math.min(Math.max(player.getLocation().distance(damaged.getLocation()) / 5, 2), 10));

                player.sendMessage(ChatColor.YELLOW + "[" +
                        ChatColor.BLUE + "Arrow Range " +
                        ChatColor.YELLOW + "(" + ChatColor.RED.toString() + Math.abs(player.getLocation().distance(damaged.getLocation())) + ChatColor.YELLOW + ")]" +
                        ChatColor.GOLD + " Damaged a player. " + ChatColor.BLUE + ChatColor.BOLD.toString() + "(" + event.getDamage() + " hearts)"
                );
            }
        }
    }
}