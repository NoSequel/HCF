package rip.vapor.hcf.team.data.impl.claim;

import com.google.gson.JsonObject;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.impl.SavableTeamData;
import rip.vapor.hcf.util.JsonBuilder;
import rip.vapor.hcf.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class ClaimTeamData extends SavableTeamData {

    private final Claim claim;
    private Location home;

    /**
     * Empty constructor
     */
    public ClaimTeamData() {
        this.claim = null;
    }

    /**
     * Constructor to make a new {@link ClaimTeamData} object with a provided {@link Claim} object
     *
     * @param claim the provided claim object
     */
    public ClaimTeamData(Claim claim) {
        this.claim = claim;
    }

    /**
     * Constructor to load a {@link ClaimTeamData} from a {@link JsonObject}
     *
     * @param object the json object to load it from
     */
    public ClaimTeamData(JsonObject object) {
        this.claim = new Claim(object);

        if (object.get("home") != null) {
            this.home = StringUtils.locationFromString(object.get("home").getAsString());
        }
    }

    /**
     * Get a home as a string
     *
     * @return the string
     */
    public String getHomeAsString() {
        return home == null ? "Not Set" : home.getBlockX() + ", " + home.getBlockY() + ", " + home.getBlockZ();
    }

    @Override
    public String getSavePath() {
        return "claim_data";
    }

    @Override
    public JsonObject toJson() {
        final JsonObject object = new JsonBuilder(this.claim.toJson())
                .addProperty("deathban", this.getClaim().isDeathban()).get();

        if (this.home != null) {
            object.addProperty("home", StringUtils.toString(home));
        }

        return object;
    }
}
