package rip.vapor.hcf.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.Data;

public interface SavableData extends Data {

    /**
     * Get the save path for the {@link SavableData} object
     *
     * @return the save path
     */
    String getSavePath();

    /**
     * Serialize a {@link SavableData} to a {@link JsonObject}
     *
     * @return the json object
     */
    JsonObject toJson();

}