package rip.vapor.hcf.player.classes.bard.abilities.effect;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.vapor.hcf.Vapor;

public class SpeedAbility extends TickableBardEffectAbility {

    @Override
    public Material getItemType() {
        return Material.SUGAR;
    }

    @Override
    public long getEnergy() {
        return 50;
    }

    @Override
    public PotionEffect getHoldEffect() {
        return new PotionEffect(PotionEffectType.SPEED, 20 * 8, 1);
    }

    @Override
    public PotionEffect getClickEffect() {
        return new PotionEffect(PotionEffectType.SPEED, 20 * 8, 2);
    }

}