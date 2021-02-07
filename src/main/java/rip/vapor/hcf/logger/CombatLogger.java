package rip.vapor.hcf.logger;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class CombatLogger implements Controllable<CombatLoggerModule> {

    private final UUID playerUuid;
    private final ItemStack[] drops;
    private final Location location;

    private final Villager villager;

    /**
     * Constructor for making a new {@link CombatLogger} object
     *
     * @param playerUuid the unique identifier of the combat logger
     * @param drops      the items which it will drop when you kill the combat logger
     * @param location   the location the player logged out
     */
    public CombatLogger(UUID playerUuid, String playerName, ItemStack[] drops, Location location) {
        Bukkit.getLogger().log(Level.INFO, "Constructing new CombatLogger object with UUID " + playerUuid.toString());

        this.playerUuid = playerUuid;
        this.drops = drops;
        this.location = location;

        this.villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        this.setupVillager(playerName);

        this.getController().getLoggers().add(this);
    }

    /**
     * Constructor for making a new {@link CombatLogger} object
     * Gets all field data from {@link Player} object and calls <p>this(UUID, ItemStack[], Location);</p>
     *
     * @param player the player to get the data from
     */
    public CombatLogger(Player player) {
        this(player.getUniqueId(), player.getName(), player.getInventory().getContents(), player.getLocation());
    }

    /**
     * Method for setting the data of the villager
     *
     * @param name the name of the villager
     */
    private void setupVillager(String name) {
        this.villager.setMaxHealth(25);
        this.villager.setHealth(25);
        this.villager.setCustomName(ChatColor.YELLOW + name);
        this.villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6000000, 9999));

        Bukkit.getScheduler().runTaskLater(Vapor.getInstance(), this::destruct, 20 * 15);
    }

    /**
     * Method for dropping the items
     */
    public void dropItems() {
        Arrays.stream(this.drops)
                .filter(Objects::nonNull)
                .forEach(item -> this.location.getWorld().dropItem(location, item));
    }

    /**
     * Destruct the {@link CombatLogger} object
     */
    public void destruct() {
        this.villager.remove();
    }
}
