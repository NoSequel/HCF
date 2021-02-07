package rip.vapor.hcf.koth;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
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

    private final KothTimer kothTimer;
    private final Team kothTeam;

    private final String kothName;
    private final long defaultDuration;

    private Claim capzone;

    private boolean isRunning;
    private UUID cappingUuid;

    /**
     * Constructor to make a new {@link Koth} object
     *
     * @param kothName        the name of hte koth
     * @param defaultDuration the duration of the koth
     */
    public Koth(String kothName, long defaultDuration) {
        this.kothName = kothName;
        this.defaultDuration = defaultDuration;
        this.kothTimer = new KothTimer(this.getKothName(), this);
        this.kothTeam = new Team(UUID.randomUUID(), this.getKothName(), TeamType.KOTH_TEAM, this);
    }

    /**
     * Constructor to load a {@link Koth} object from a {@link JsonObject}
     *
     * @param object the object to load it from
     */
    public Koth(JsonObject object) {
        this.kothName = object.get("kothName").getAsString();
        this.defaultDuration = object.get("defaultDuration").getAsLong();
        this.capzone = new Claim(JsonUtils.getParser().parse(object.get("capzone").getAsString()).getAsJsonObject());

        this.kothTimer = new KothTimer(this.getKothName(), this);
        System.out.println(kothName);
        this.kothTeam = Vapor.getInstance().getHandler().find(TeamModule.class).findTeam(this.getKothName());
    }

    /**
     * Method to handle ticking the cap
     */
    public void tickCap() {
        if (this.isRunning && this.capzone != null) {
            this.getKothTimer().getThread().setActive(true);

            if (this.cappingUuid == null || Bukkit.getPlayer(this.cappingUuid) == null) {
                this.cappingUuid = null;

                this.kothTimer.getThread().setShouldSubtract(false);
                this.kothTimer.getThread().setCurrentDuration(this.getDefaultDuration());
            } else if (!this.kothTimer.getThread().isShouldSubtract()) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] "
                        + ChatColor.WHITE + Bukkit.getPlayer(this.cappingUuid).getName()
                        + ChatColor.YELLOW + " is now capping the "
                        + ChatColor.YELLOW + this.getKothName() + ChatColor.GOLD + " KoTH");

                this.kothTimer.getThread().setShouldSubtract(true);

                Bukkit.getLogger().log(Level.INFO, this.cappingUuid + " starting capping KoTH " + this.kothName + " (" + this.capzone.getCuboid().toXYZ() + ")");
            }
        } else if (this.kothTimer.getThread().isShouldSubtract() || this.kothTimer.getThread().isActive()) {
            this.kothTimer.getThread().setShouldSubtract(false);
            this.kothTimer.getThread().setActive(false);
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
                .addProperty("capzone", this.capzone.toJson().toString()).get();
    }
}