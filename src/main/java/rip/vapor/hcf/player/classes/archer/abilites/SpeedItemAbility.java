package rip.vapor.hcf.player.classes.archer.abilites;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.archer.ArcherClass;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.ArcherSpeedTimer;

import java.util.Optional;

public class SpeedItemAbility extends Ability {

    private final Optional<ArcherSpeedTimer> timer = Vapor.getInstance().getHandler().find(TimerModule.class).findTimer(ArcherSpeedTimer.class);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final ItemStack itemStack = event.getItem();
        final Player player = event.getPlayer();

        if (timer.isPresent()) {
            if (this.equippedArcher(player)) {
                if (itemStack != null && itemStack.getType().equals(Material.SUGAR) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                    if (timer.get().isOnCooldown(player)) {
                        player.sendMessage(ChatColor.RED + "You are still on a cooldown.");
                        return;
                    }

                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 3));
                    timer.get().start(player);

                    this.runSpeedTask(player);
                }
            }
        }
    }

    /**
     * Run the speed task to re-add the potion effect to the player
     *
     * @param player the player
     */
    private void runSpeedTask(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Vapor.getInstance(), () -> {

            if (equippedArcher(player)) {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
            }
        }, 8 * 20);
    }

    /**
     * Check if a {@link Player} has the {@link ArcherClass} equipped
     *
     * @param player the player
     * @return the state of the archer class
     */
    private boolean equippedArcher(Player player) {
        final ClassModule classController = Vapor.getInstance().getHandler().find(ClassModule.class);
        final ArcherClass archerClass = classController.findClass(ArcherClass.class);

        return archerClass.getEquipped().contains(player);
    }

}