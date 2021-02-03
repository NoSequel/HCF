package rip.vapor.hcf.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.vapor.hcf.VaporConstants;

import java.util.Map;

public class EnchantmentLimiterListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent event) {
        final Map<Enchantment, Integer> enchantments = event.getEnchantsToAdd();

        enchantments.forEach(((enchantment, level) -> {
            if(VaporConstants.ENCHANTMENT_LIMITS.containsKey(enchantment)) {
                final int maxLevel = VaporConstants.ENCHANTMENT_LIMITS.get(enchantment);

                if (level > maxLevel){
                    enchantments.put(enchantment, maxLevel);
                }
            }
        }));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory();

        if (inventory.getType().equals(InventoryType.ANVIL) && event.getRawSlot() == 2) {
            final ItemStack result = inventory.getItem(2);

            if (result != null) {
                event.setCancelled(true);

                result.getEnchantments().forEach(((enchantment, level) -> {
                    if(VaporConstants.ENCHANTMENT_LIMITS.containsKey(enchantment)) {
                        final int maxLevel = VaporConstants.ENCHANTMENT_LIMITS.get(enchantment);

                        if (level > maxLevel) {
                            result.removeEnchantment(enchantment);
                            result.addEnchantment(enchantment, maxLevel);
                        }
                    }
                }));

                player.updateInventory();
                inventory.clear();

                if (event.getClick().isShiftClick()) {
                    player.getInventory().addItem(result);
                } else {
                    event.setCursor(result);
                }
            }
        }
    }
}