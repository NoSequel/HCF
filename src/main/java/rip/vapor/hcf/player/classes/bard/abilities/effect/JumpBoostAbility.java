package rip.vapor.hcf.player.classes.bard.abilities.effect;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JumpBoostAbility extends TickableBardEffectAbility {

    @Override
    public Material getItemType() {
        return Material.FEATHER;
    }

    @Override
    public long getEnergy() {
        return 65;
    }

    @Override
    public PotionEffect getHoldEffect() {
        return new PotionEffect(PotionEffectType.JUMP, 8 * 20, 1);
    }

    @Override
    public PotionEffect getClickEffect() {
        return new PotionEffect(PotionEffectType.JUMP, 8 * 20, 3);
    }
}
