package rip.vapor.hcf.team.claim.selection;

import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.util.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class ClaimSelection {

    private Location location1, location2;
    private final Team team;

    /**
     * Constructor for creating a new ClaimSelection instance
     * Only used to set the team
     *
     * @param team the team the ClaimSelection is dedicated to
     */
    public ClaimSelection(Team team) {
        this.team = team;
    }

    /**
     * Apply a claim selection to a team
     */
    public void apply() {
        if (!location1.getWorld().equals(location2.getWorld())) {
            throw new IllegalStateException("The second position isn't in the same world as the first one.");
        }

        team.addClaim(new Claim(new Cuboid(location1, location2)));
    }
}