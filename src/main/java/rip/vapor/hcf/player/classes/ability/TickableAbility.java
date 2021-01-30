package rip.vapor.hcf.player.classes.ability;

import org.bukkit.entity.Player;

public abstract class TickableAbility extends Ability {

    /**
     * Method called whenever the task ticks
     *
     * @param player the player
     */
    public abstract void tick(Player player);

}
