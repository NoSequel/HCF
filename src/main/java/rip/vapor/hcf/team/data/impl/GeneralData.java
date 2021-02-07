package rip.vapor.hcf.team.data.impl;

import com.google.gson.JsonObject;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.JsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

@Getter
@Setter
public class GeneralData extends SavableTeamData {

    private TeamType type;
    private long createTime;
    private String name;
    private ChatColor color;

    /**
     * Empty constructor
     */
    public GeneralData() {
    }

    /**
     * Constructor for making a new {@link GeneralData} object
     *
     * @param name  the name of the team
     * @param type  the type of the team
     * @param color the color of the team
     */
    public GeneralData(String name, TeamType type, ChatColor color) {
        this.name = name;
        this.type = type;
        this.createTime = System.currentTimeMillis();
        this.color = color;
    }

    /**
     * Constructor for loading a {@link GeneralData} object from a {@link JsonObject}
     *
     * @param object the json object
     */
    public GeneralData(JsonObject object) {
        this(object.get("name").getAsString(), TeamType.valueOf(object.get("type").getAsString()), ChatColor.valueOf(object.get("color").getAsString()));

        this.createTime = object.get("createTime").getAsLong();
        this.color = ChatColor.valueOf(object.get("color").getAsString());
    }

    @Override
    public String getSavePath() {
        return "general";
    }

    @Override
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("createTime", createTime)
                .addProperty("name", name)
                .addProperty("type", type.name())
                .addProperty("color", color.name()).get();
    }
}