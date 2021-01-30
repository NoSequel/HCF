package rip.vapor.hcf.player.classes.bard.abilities.effect;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RegenerationAbility extends TickableBardEffectAbility {

    @Override
    public Material getItemType() {
        return Material.GHAST_TEAR;
    }

    @Override
    public long getEnergy() {
        return 60;
    }

    @Override
    public PotionEffect getHoldEffect() {
        return new PotionEffect(PotionEffectType.REGENERATION, 8*20, 1);
    }

    @Override
    public PotionEffect getClickEffect() {
        return new PotionEffect(PotionEffectType.REGENERATION, 8*20, 2);
    }
}
