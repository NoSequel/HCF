package rip.vapor.hcf.team.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.util.JsonBuilder;
import rip.vapor.hcf.util.JsonUtils;

public class KothTeamData extends SavableTeamData {

    private final Koth koth;

    /**
     * Empty constructor
     */
    public KothTeamData() {
        this.koth = null;
    }

    /**
     * Constructor to make a new {@link KothTeamData} object from a {@link Koth}
     *
     * @param koth the koth
     */
    public KothTeamData(Koth koth) {
        this.koth = koth;
    }

    /**
     * Constructor to load a {@link KothTeamData} from a {@link JsonObject}
     *
     * @param object the json object to laod the {@link KothTeamData} from
     */
    public KothTeamData(JsonObject object) {
        super(object);
        this.koth = new Koth(JsonUtils.getParser().parse(object.get("koth").getAsString()).getAsJsonObject());
    }

    @Override
    public String getSavePath() {
        return "koth";
    }

    @Override
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("koth", this.koth.toJson().toString()).get();
    }
}
