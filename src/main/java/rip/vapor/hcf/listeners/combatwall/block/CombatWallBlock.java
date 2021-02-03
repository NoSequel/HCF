package rip.vapor.hcf.listeners.combatwall.block;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;

@RequiredArgsConstructor
public class CombatWallBlock {

    private final Location location;

    /**
     * Attempt to remove a {@link CombatWallBlock}
     *
     * @param player           the player to remove it for
     * @param locationConsumer the function to execute
     */
    public boolean attemptRemove(Player player, Function<Location, Boolean> locationConsumer) {
        if (locationConsumer.apply(location)) {
            player.sendBlockChange(location, Material.AIR, (byte) 0);
            return true;
        }

        return false;
    }

    /**
     * Visualize a {@link CombatWallBlock} to a {@link Player}
     *
     * @param player the player
     * @return the current instance of the wall block
     */
    public CombatWallBlock visualize(Player player) {
        if(location.getBlock().getType().equals(Material.AIR)) {
            player.sendBlockChange(location, Material.GLASS, (byte) 0);
        }

        return this;
    }
}
