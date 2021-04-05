package rip.vapor.hcf.player.classes;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
@Setter
public abstract class Class<T extends ClassData> implements Listener, BoardProvider {


    private final String className;
    private final Material[] requiredArmor;
    private final PotionEffect[] potionEffects;

    private final List<Player> equipped = new ArrayList<>();

    private final Map<Player, T> classData = new HashMap<>();
    private List<Ability> abilities = new ArrayList<>();

    /**
     * Constructor for creating a new Class object
     *
     * @param className     the name of the class
     * @param requiredArmor the armor chiwch is required to equip the class
     * @param potionEffects the potion effects which it gives upon equipment
     */
    public Class(String className, Material[] requiredArmor, PotionEffect[] potionEffects) {
        this.className = className;
        this.requiredArmor = requiredArmor;
        this.potionEffects = potionEffects;
    }

    /**
     * Constructor for creating a new Class object
     *
     * @param className     the name of the class
     * @param requiredArmor the armor which is required to equip the class
     * @param potionEffects the potion effects which it gives upon equipment
     * @param abilities     the abilities which you will receive upon equipment
     */
    public Class(String className, Material[] requiredArmor, PotionEffect[] potionEffects, Ability[] abilities) {
        this(className, requiredArmor, potionEffects);

        this.abilities = Arrays.asList(abilities);
        this.getAbilities().forEach(ability -> Bukkit.getPluginManager().registerEvents(ability, Vapor.getPlugin(Vapor.class)));
    }

    /**
     * Handle the event of equipping a class
     *
     * @param player the player
     */
    public void onEquip(Player player) {
        player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.AQUA + ChatColor.BOLD.toString() + this.getClassName() + ChatColor.GRAY + " --> " + ChatColor.GREEN + "Enabled!");

        this.equipped.add(player);

        Arrays.stream(this.potionEffects).forEach(player::addPotionEffect);
    }

    /**
     * Handle the event of unequipping the class
     *
     * @param player the player
     */
    public void onUnequip(Player player) {
        player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.AQUA + ChatColor.BOLD.toString() + this.getClassName() + ChatColor.GRAY + " --> " + ChatColor.RED + "Disabled!");

        this.equipped.remove(player);

        Arrays.stream(this.potionEffects)
                .map(PotionEffect::getType)
                .forEach(player::removePotionEffect);
    }
}