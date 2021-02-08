package rip.vapor.hcf.player.data;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import rip.vapor.hcf.data.Data;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimSelectionData implements Data {

    private final ClaimSelection claimSelection;

    public ClaimSelectionData() { this.claimSelection = null; }

    /**
     * Constructor for creating a new ClaimSelectionData class instance
     *
     * @param claimSelection the claim selection
     */
    public ClaimSelectionData(ClaimSelection claimSelection) {
        this.claimSelection = claimSelection;
    }

    /**
     * Start a claim task for a {@link Player}
     *
     * @param player the player
     * @return the claim task
     */
    public ClaimSelectionData startClaim(Player player) {
        if(this.claimSelection != null && this.claimSelection.getTeam() != null) {
            player.sendMessage(new String[]{
                    "",
                    ChatColor.GREEN + ChatColor.BOLD.toString() + "You are currently claiming for " + this.claimSelection.getTeam().getFormattedName() + ",",
                    ChatColor.GRAY + "Click " + Action.RIGHT_CLICK_BLOCK.name() + " for the first position",
                    ChatColor.GRAY + "Click " + Action.LEFT_CLICK_BLOCK.name() + " for the second position",
                    ChatColor.YELLOW + "To finish your claiming, sneak while you press " + Action.LEFT_CLICK_AIR.name(),
                    ChatColor.YELLOW + "To cancel claiming, you press " + Action.LEFT_CLICK_AIR.name(),
                    ""
            });
        }

        return this;
    }
}
