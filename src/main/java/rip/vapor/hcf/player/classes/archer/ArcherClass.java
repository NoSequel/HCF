package rip.vapor.hcf.player.classes.archer;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.Class;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.archer.abilites.ArcherTagAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.vapor.hcf.player.classes.archer.abilites.SpeedItemAbility;

import java.util.Collections;
import java.util.List;

public class ArcherClass extends Class<ArcherClassData> {

    public ArcherClass(Vapor plugin) {
        super("Archer", new Material[]{
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        }, new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 9999999, 2)
        }, new Ability[]{
                new ArcherTagAbility(),
                new SpeedItemAbility(plugin)
        });
    }

    /**
     * Get the strings of the part
     *
     * @param element the element to add the strings to
     * @param player  the player
     */
    @Override
    public void getStrings(ScoreboardElement element, Player player) {

    }
}