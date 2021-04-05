package rip.vapor.hcf.player.classes.rogue;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.classes.Class;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.rogue.abilities.RogueStabAbility;

import java.util.Collections;
import java.util.List;

public class RogueClass extends Class<RogueClassData> {

    public RogueClass(ModuleHandler handler) {
        super("Rogue", new Material[] {
                Material.CHAINMAIL_HELMET,
                Material.CHAINMAIL_BOOTS,
                Material.CHAINMAIL_LEGGINGS,
                Material.CHAINMAIL_CHESTPLATE
        }, new PotionEffect[] {
                new PotionEffect(PotionEffectType.SPEED, 999999, 2),
                new PotionEffect(PotionEffectType.JUMP, 999999, 2),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 1)
        }, new Ability[] {
            new RogueStabAbility(handler)
        });
    }

    @Override
    public List<String> getStrings(Player player) {
        return Collections.emptyList();
    }
}
