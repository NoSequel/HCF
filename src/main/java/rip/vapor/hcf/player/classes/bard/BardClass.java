package rip.vapor.hcf.player.classes.bard;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
import rip.vapor.hcf.player.classes.Class;
import rip.vapor.hcf.player.classes.ability.Ability;
import rip.vapor.hcf.player.classes.bard.abilities.BowDamageReduceAbility;
import rip.vapor.hcf.player.classes.bard.abilities.effect.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BardClass extends Class<BardClassData> {

    /**
     * Constructor for creating a new BardClass object
     */
    public BardClass() {
        super("Bard", new Material[]{
                        Material.GOLD_HELMET,
                        Material.GOLD_CHESTPLATE,
                        Material.GOLD_LEGGINGS,
                        Material.GOLD_BOOTS
                }, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
                        new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0),
                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)
                },
                new Ability[]{
                        new BowDamageReduceAbility(),
                        new StrengthAbility(),
                        new FireResistanceAbility(),
                        new JumpBoostAbility(),
                        new RegenerationAbility(),
                        new SpeedAbility(),
                        new ResistanceAbility()
                });
    }

    @Override
    public void onEquip(Player player) {
        super.onEquip(player);

        this.getAbilities().forEach(ability -> ability.handleActivate(player));
        this.getClassData().put(player, new BardClassData());
    }

    @Override
    public void onUnequip(Player player) {
        super.onUnequip(player);

        this.getAbilities().forEach(ability -> ability.handleDeactivate(player));
        this.getClassData().remove(player);
    }

    /**
     * Get the strings of the part
     *
     * @param element the element to add the strings to
     * @param player  the player
     */
    @Override
    public void getStrings(ScoreboardElement element, Player player) {
        final long requiredEnergy = this.getAbilities().stream()
                .filter(ability -> ability instanceof TickableBardEffectAbility)
                .map(TickableBardEffectAbility.class::cast)
                .filter(ability -> player.getItemInHand() != null && ability.getItemType().equals(player.getItemInHand().getType()))
                .map(TickableBardEffectAbility::getEnergy).findFirst().orElse(0L);

        element.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "Bard Energy" + ChatColor.GRAY + ": " + ChatColor.RED + this.getClassData().get(player).getEnergy() + requiredEnergy);
    }
}