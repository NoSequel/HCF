package rip.vapor.hcf.player.classes.bard.abilities.effect;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireResistanceAbility extends TickableBardEffectAbility {

    @Override
    public Material getItemType() {
        return Material.MAGMA_CREAM;
    }

    @Override
    public long getEnergy() {
        return 0;
    }

    @Override
    public PotionEffect getHoldEffect() {
        return new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 8, 1);
    }

    @Override
    public PotionEffect getClickEffect() {
        return null;
    }
}