package rip.vapor.hcf.koth;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.timers.impl.global.KothTimer;
import rip.vapor.hcf.util.JsonBuilder;
import rip.vapor.hcf.util.JsonUtils;

import java.util.UUID;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
@Setter
public class Koth {

    private final KothTimer kothTimer = new KothTimer(this.getKothName(), this);
    private final Team kothTeam = new Team(UUID.randomUUID(), this.getKothName(), TeamType.KOTH_TEAM, this);

    private final String kothName;
    private final long defaultDuration;

    private Claim capzone;
    private Claim claim;

    private boolean isRunning;
    private UUID cappingUuid;

    /**
     * Constructor to load a {@link Koth} object from a {@link JsonObject}
     *
     * @param object the object to load it from
     */
    public Koth(JsonObject object) {
        this.kothName = object.get("kothName").getAsString();
        this.defaultDuration = object.get("defaultDuration").getAsLong();
        this.capzone = new Claim(JsonUtils.getParser().parse(object.get("capzone").getAsString()).getAsJsonObject());
        this.claim = new Claim(JsonUtils.getParser().parse(object.get("claim").getAsString()).getAsJsonObject());
    }

    /**
     * Method to handle ticking the cap
     */
    public void tickCap() {
        if (this.isRunning && this.capzone != null && this.claim != null) {
            if (this.cappingUuid == null || Bukkit.getPlayer(this.cappingUuid) == null) {
                this.cappingUuid = null;

                this.kothTimer.getThread().setShouldSubtract(false);
                this.kothTimer.getThread().setCurrentDuration(this.getDefaultDuration());
            } else if (!this.kothTimer.getThread().isShouldSubtract()) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] "
                        + ChatColor.WHITE + Bukkit.getPlayer(this.cappingUuid).getName()
                        + ChatColor.YELLOW + "is now capping the "
                        + ChatColor.YELLOW + this.getKothName() + ChatColor.GOLD + " KoTH");

                this.kothTimer.getThread().setShouldSubtract(true);

                Bukkit.getLogger().log(Level.INFO, this.cappingUuid + " starting capping KoTH " + this.kothName + " (" + this.capzone.getCuboid().toXYZ() + ")");
            }
        } else if (this.kothTimer.getThread().isShouldSubtract()) {
            this.kothTimer.getThread().setShouldSubtract(false);
        }
    }

    /**
     * Method to serialize a {@link Koth} object to a {@link JsonObject}
     * Used to save into the database/data storage
     *
     * @return the json object
     */
    public JsonObject toJson() {
        return new JsonBuilder()
                .addProperty("kothName", this.kothName)
                .addProperty("defaultDuration", this.defaultDuration)
                .addProperty("capzone", this.capzone.toJson().toString())
                .addProperty("claim", this.claim.toJson().toString()).get();
    }
}