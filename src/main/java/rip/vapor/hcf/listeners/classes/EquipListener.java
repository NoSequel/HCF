package rip.vapor.hcf.listeners.classes;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EquipListener implements Listener {

    private final ClassController classController = Vapor.getInstance().getHandler().findController(ClassController.class);
    private final Material[] armor = new Material[]{
            // BOOTS
            Material.IRON_BOOTS,
            Material.GOLD_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.DIAMOND_BOOTS,
            Material.LEATHER_BOOTS,

            // LEGGINGS
            Material.GOLD_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.DIAMOND_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.LEATHER_LEGGINGS,

            // CHESTPLATES
            Material.GOLD_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.LEATHER_CHESTPLATE,

            // HELMETS
            Material.GOLD_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.LEATHER_HELMET
    };

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getItemInHand() != null && Arrays.stream(armor).anyMatch(type -> type.equals(player.getItemInHand().getType()))) {
            this.attemptEquip(player);
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        if (Arrays.stream(this.armor).anyMatch(type -> type.equals(event.getCurrentItem().getType()))) {
            this.attemptEquip(player);
        }
    }

    /**
     * Attempt to equip classes
     *
     * @param player the player
     */
    private void attemptEquip(Player player) {
        Bukkit.getScheduler().runTaskLater(Vapor.getInstance(), () -> {
            this.classController.getClasses().stream()
                    .filter(clazz -> clazz.getEquipped().contains(player))
                    .filter(clazz -> Arrays.stream(clazz.getRequiredArmor()).anyMatch(type -> !Arrays.stream(player.getInventory().getArmorContents()).map(ItemStack::getType).collect(Collectors.toList()).contains(type)))
                    .forEach(clazz -> clazz.onUnequip(player));

            this.classController.getClasses().stream()
                    .filter(clazz -> !clazz.getEquipped().contains(player))
                    .filter(clazz -> Arrays.stream(clazz.getRequiredArmor()).allMatch(type -> Arrays.stream(player.getInventory().getArmorContents()).map(ItemStack::getType).collect(Collectors.toList()).contains(type)))
                    .forEach(clazz -> clazz.onEquip(player));
        }, 5L);
    }
}