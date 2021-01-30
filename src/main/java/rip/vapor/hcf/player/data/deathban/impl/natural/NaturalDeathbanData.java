package rip.vapor.hcf.player.data.deathban.impl.natural;

import com.google.gson.JsonObject;
import rip.vapor.hcf.player.data.deathban.DeathbanData;
import rip.vapor.hcf.util.JsonBuilder;

public class NaturalDeathbanData extends DeathbanData {

    private final NaturalDeathbanType type;

    public NaturalDeathbanData() { this.type = NaturalDeathbanType.UNDEFINED; }

    /**
     * Constructor for creating a DeathbanData object for a player
     *
     * @param type     the killer
     * @param duration the duration
     */
    public NaturalDeathbanData(NaturalDeathbanType type, long duration) {
        super(duration);
        this.type = type;
    }

    public NaturalDeathbanData(JsonObject object) {
        super(object);

        this.type = NaturalDeathbanType.valueOf(object.get("type").getAsString());
    }

    @Override
    public String getSavePath() {
        return "natural_deathban";
    }


    @Override
    public JsonObject toJson() {
        return new JsonBuilder(super.toJson())
                .addProperty("type", type.name()).get();
    }

    @Override
    public String getReason() {
        return type.reason;
    }
}