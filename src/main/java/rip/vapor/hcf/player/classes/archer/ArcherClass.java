package rip.vapor.hcf.player.classes.archer;

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

    public ArcherClass() {
        super("Archer", new Material[]{
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        }, new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 9999999, 2)
        }, new Ability[]{
                new ArcherTagAbility(),
                new SpeedItemAbility()
        });
    }

    @Override
    public List<String> getStrings(Player player) {
        return Collections.emptyList();
    }
}