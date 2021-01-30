package rip.vapor.hcf.player.classes.bard.abilities.effect;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthAbility extends TickableBardEffectAbility {

    @Override
    public Material getItemType() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public long getEnergy() {
        return 80;
    }

    @Override
    public PotionEffect getHoldEffect() {
        return new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 1);
    }

    @Override
    public PotionEffect getClickEffect() {
        return new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 2);
    }
}