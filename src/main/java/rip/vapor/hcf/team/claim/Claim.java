package rip.vapor.hcf.team.claim;

import com.google.gson.JsonObject;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.util.Cuboid;
import rip.vapor.hcf.util.JsonBuilder;
import rip.vapor.hcf.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Claim {

    private final Cuboid cuboid;

    private ClaimPriority priority;
    private boolean deathban = true;

    /**
     * Constructor for creating a new Claim with a paramaterized claim priority
     *
     * @param cuboid   the cuboid
     * @param priority the priority
     */
    public Claim(Cuboid cuboid, ClaimPriority priority) {
        this.cuboid = cuboid;
        this.priority = priority;
    }

    /**
     * Constructor for creating a new Claim with the default priority
     * The defaulted priority will be NORMAL
     *
     * @param cuboid the cuboid
     */
    public Claim(Cuboid cuboid) {
        this(cuboid, ClaimPriority.NORMAL);
    }

    /**
     * Constructor for loading a {@link Claim} object from a {@link JsonObject}
     *
     * @param object the json object to load it from
     */
    public Claim(JsonObject object) {
        System.out.println(object.toString());

        this.cuboid = new Cuboid(JsonUtils.getParser().parse(object.get("cuboid").getAsString()).getAsJsonObject());
        this.priority = ClaimPriority.valueOf(object.get("priority").getAsString());

        if (object.has("deathban")) {
            this.deathban = object.get("deathban").getAsBoolean();
        }
    }

    /**
     * Method for serializing a {@link Claim} object to a {@link JsonObject}
     *
     * @return the serialized json object
     */
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("cuboid", this.getCuboid().toJson().toString())
                .addProperty("deathban", this.isDeathban())
                .addProperty("priority", this.priority.name()).get();
    }

    /**
     * Get the team which is allocated to the team
     *
     * @return the team
     */
    public Team getTeam() {
        return Vapor.getPlugin(Vapor.class).getHandler().find(TeamModule.class).getTeams().stream()
                .filter(team -> team.hasData(ClaimTeamData.class) && team.findData(ClaimTeamData.class).getClaim().equals(this))
                .findFirst().orElse(null);
    }
}