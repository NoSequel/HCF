package rip.vapor.hcf.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.Data;

public interface SaveableData extends Data {

    String getSavePath();
    JsonObject toJson();

}