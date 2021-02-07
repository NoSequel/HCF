package rip.vapor.hcf.player.data.deathban;

import com.google.gson.JsonObject;
import rip.vapor.hcf.data.impl.SavableData;
import rip.vapor.hcf.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class DeathbanData implements SavableData {

    private final long expiration;

    public DeathbanData() { this.expiration = 0L; }

    /**
     * Constructor for creating a new DeathbanData for a user
     *
     * @param duration the duration
     */
    public DeathbanData(long duration) {
        this.expiration = System.currentTimeMillis()+duration;
    }

    public DeathbanData(JsonObject object) {
        this.expiration = Long.parseLong(object.get("expiration").getAsString());
    }

    /**
     * Kick a player for this DeathBan
     *
     * @param player the player
     */
    public void kickPlayer(Player player) {
        player.kickPlayer(String.join("\n", new String[]{
                ChatColor.RED + "You are currently deathbanned for " + ChatColor.YELLOW + StringUtils.getFormattedTime(expiration-System.currentTimeMillis(), false),
                ChatColor.RED + "You " + getReason()
        }));
    }

    /**
     * Get the deathban reason in a string
     *
     * @return the reason
     */
    public abstract String getReason();

    @Override
    public String getSavePath() {
        return "deathban";
    }

    @Override
    public JsonObject toJson() {
        return null;
    }
}