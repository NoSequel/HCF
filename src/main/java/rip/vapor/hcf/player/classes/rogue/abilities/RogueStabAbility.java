package rip.vapor.hcf.player.classes.rogue.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.rogue.RogueClass;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.timers.impl.RogueStabTimer;

public class RogueStabAbility extends Ability {

    private final TimerController timerController = Vapor.getInstance().getHandler().find(TimerController.class);
    private final RogueStabTimer rogueStabTimer = timerController.findTimer(RogueStabTimer.class);

    @EventHandler
    public void onDamge(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player player = (Player) event.getDamager();
            final ItemStack item = player.getItemInHand();

            if (this.equippedRouge(player) && item != null && item.getType().equals(Material.GOLD_SWORD)) {
                if (rogueStabTimer.isOnCooldown(player)) {
                    player.sendMessage(ChatColor.RED + "You are still on a cooldown for this.");
                    event.setCancelled(true);
                    return;
                }

                item.setDurability((short) 0);
                event.setDamage(event.getDamage() + 4);

                rogueStabTimer.start(player);
            }
        }
    }

    /**
     * Check if a {@link Player} has the {@link RogueClass} equipped
     *
     * @param player the player
     * @return the state of the archer class
     */
    private boolean equippedRouge(Player player) {
        final ClassController classController = Vapor.getInstance().getHandler().find(ClassController.class);
        final RogueClass rogueClass = classController.findClass(RogueClass.class);

        return rogueClass.getEquipped().contains(player);
    }
}