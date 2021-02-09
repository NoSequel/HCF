package rip.vapor.hcf.player.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.data.impl.SavableData;
import rip.vapor.hcf.util.JsonBuilder;

@Getter
@Setter
public class BalanceData implements SavableData {

    private int balance = VaporConstants.BALANCE;

    /**
     * Empty constructor
     */
    public BalanceData() { }

    /**
     * Constructor to make a new {@link BalanceData} object
     *
     * @param object the object to load the data from
     */
    public BalanceData(JsonObject object) {
        this.balance = object.get("balance").getAsInt();
    }

    @Override
    public String getSavePath() {
        return "banace";
    }

    @Override
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("balance", this.balance).get();
    }
}
