package rip.vapor.hcf.listeners.combatwall.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CombatWallBlockList {

    private final Player player;
    private final List<CombatWallBlock> wallBlocks = new ArrayList<>();

    /**
     * Add a new {@link CombatWallBlock} to the list of combat wall blocks
     *
     * @param location the location of the block
     */
    public void add(Location location) {
        this.wallBlocks.add(new CombatWallBlock(location).visualize(player));
    }

    /**
     * Attempt to remove a {@link CombatWallBlock} from the list of wall blocks
     */
    public void attemptRemove() {
        this.wallBlocks.removeIf(wallBlock -> wallBlock.attemptRemove(player, location -> player.getLocation().distanceSquared(location) >= 8));
    }
}
