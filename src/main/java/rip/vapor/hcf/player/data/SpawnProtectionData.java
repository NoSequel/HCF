package rip.vapor.hcf.player.data;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.impl.SavableData;
import rip.vapor.hcf.util.JsonBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpawnProtectionData implements SavableData {

    private long durationLeft;

    public SpawnProtectionData() {
        this.durationLeft = 0;
    }

    public SpawnProtectionData(long duration) {
        this.durationLeft = duration;
    }

    public SpawnProtectionData(JsonObject object) {
        this.durationLeft = object.get("duration").getAsLong();
    }

    @Override
    public String getSavePath() {
        return "spawn_protection";
    }

    @Override
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("duration", durationLeft)
                .get();
    }
}
