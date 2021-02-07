package rip.vapor.hcf.player.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import rip.vapor.hcf.data.impl.SavableData;
import rip.vapor.hcf.util.JsonBuilder;

import java.util.UUID;

@Getter
@Setter
public class CombatLoggerData implements SavableData {

    private boolean killed;
    private UUID killerUuid;

    public CombatLoggerData() {}

    /**
     * Constructor for loading a {@link CombatLoggerData} from a {@link JsonObject}
     *
     * @param object the json object to load the data from
     */
    public CombatLoggerData(JsonObject object) {
        this.killed = object.get("killed").getAsBoolean();
        this.killerUuid = UUID.fromString(object.get("killerUuid").getAsString());
    }

    @Override
    public String getSavePath() {
        return "combatlogger";
    }

    @Override
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("killerUuid", killerUuid == null ? UUID.randomUUID() : this.killerUuid)
                .addProperty("killed", this.killed).get();
    }
}
