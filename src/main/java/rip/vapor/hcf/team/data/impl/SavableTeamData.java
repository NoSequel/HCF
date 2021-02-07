package rip.vapor.hcf.team.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.impl.SavableData;
import rip.vapor.hcf.team.data.TeamData;

public abstract class SavableTeamData implements TeamData, SavableData {

    /**
     * Constructor to make a new {@link SavableData} object
     */
    public SavableTeamData() {}

    /**
     * Constructor to load a {@link SavableTeamData} object from a {@link JsonObject}
     *
     * @param object the json object to load it from
     */
    public SavableTeamData(JsonObject object) {}

}