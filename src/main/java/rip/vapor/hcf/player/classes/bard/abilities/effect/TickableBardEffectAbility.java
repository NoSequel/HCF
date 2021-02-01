package rip.vapor.hcf.player.classes.bard.abilities.effect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.player.classes.ability.TickableAbility;
import rip.vapor.hcf.player.classes.bard.BardClass;
import rip.vapor.hcf.player.classes.bard.BardClassData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;

public abstract class TickableBardEffectAbility extends TickableAbility {

    private final TeamController teamController = Vapor.getInstance().getHandler().find(TeamController.class);
    private ClassController classController;

    /**
     * Get the type of item required to be held
     *
     * @return the item type
     */
    public abstract Material getItemType();

    /**
     * Get the energy required to use the ability
     *
     * @return the energy required
     */
    public abstract long getEnergy();

    /**
     * Get the effect given when the item is held
     *
     * @return the effect
     */
    public abstract PotionEffect getHoldEffect();

    /**
     * Get the effect given upon clicking of the item
     *
     * @return the effect
     */
    public abstract PotionEffect getClickEffect();

    @Override
    public void tick(Player player) {
        if (this.getHoldEffect() != null) {
            if (player.getItemInHand().getType().equals(this.getItemType())) {
                final Team team = teamController.findTeam(player);

                if (team != null) {
                    final PlayerTeamData data = team.findData(PlayerTeamData.class);

                    data.getOnlineMembers().stream()
                            .filter(target -> !target.equals(player) && target.getLocation().distance(player.getLocation()) < 15 && !target.hasPotionEffect(this.getHoldEffect().getType()))
                            .forEach(target -> target.addPotionEffect(this.getHoldEffect()));
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (this.getClickEffect() != null) {
            if (this.classController == null) {
                this.classController = Vapor.getInstance().getHandler().find(ClassController.class);
            }

            final Player player = event.getPlayer();
            final ItemStack itemStack = player.getItemInHand() == null ? event.getItem() : player.getItemInHand();
            final BardClass bard = classController.findClass(BardClass.class);

            if (itemStack != null && itemStack.getType().equals(this.getItemType()) && !event.getAction().equals(Action.PHYSICAL)) {
                if (bard.getClassData().containsKey(player)) {
                    final BardClassData bardData = bard.getClassData().get(player);

                    if (bardData.getEnergy() <= this.getEnergy()) {
                        player.sendMessage(ChatColor.RED + "You need at least " + this.getEnergy() + " energy to use this.");
                        return;
                    }

                    final Team team = teamController.findTeam(player);

                    if (team != null) {
                        final PlayerTeamData data = team.findData(PlayerTeamData.class);

                        data.getOnlineMembers().stream()
                                .filter(target -> target.getLocation().distance(player.getLocation()) < 15)
                                .forEach(target -> target.addPotionEffect(this.getClickEffect()));
                    }

                    bardData.setEnergy(bardData.getEnergy() - this.getEnergy());
                }
            }
        }
    }
}