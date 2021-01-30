package rip.vapor.hcf.team.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.impl.SaveableData;
import rip.vapor.hcf.team.data.TeamData;

public abstract class SaveableTeamData implements TeamData, SaveableData {

    public SaveableTeamData() {}
    public SaveableTeamData(JsonObject object) {}

}