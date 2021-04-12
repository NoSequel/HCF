package rip.vapor.hcf.player.classes.rogue.abilities;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.rogue.RogueClass;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.RogueStabTimer;

@RequiredArgsConstructor
public class RogueStabAbility extends Ability {

    private final TimerModule timerController;
    private final RogueStabTimer rogueStabTimer;

    /**
     * Constructor to make a new rogue stab ability instance
     *
     * @param handler the handler to get the modules from
     */
    public RogueStabAbility(ModuleHandler handler) {
        this.timerController = handler.find(TimerModule.class);
        this.rogueStabTimer = timerController.findTimer(RogueStabTimer.class).get();
    }

    @EventHandler
    public void onDamge(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player player = (Player) event.getDamager();
            final ItemStack item = player.getItemInHand();


            if (this.getActivated().contains(player) && item != null && item.getType().equals(Material.GOLD_SWORD)) {
                final double angle = player.getLocation().getDirection().angle(event.getEntity().getLocation().getDirection()) / 180 * Math.PI;

                if (angle <= 0.025D) {
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
    }
}