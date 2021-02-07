package rip.vapor.hcf.team;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.data.Loadable;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.TeamData;
import rip.vapor.hcf.team.data.impl.GeneralData;
import rip.vapor.hcf.team.data.impl.KothTeamData;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.data.impl.player.invites.InviteTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.database.DatabaseModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class Team implements Controllable<TeamModule>, Loadable<TeamData> {

    private final TeamModule teamController = this.getController();
    private final DatabaseModule databaseController = Vapor.getInstance().getHandler().find(DatabaseModule.class);

    private GeneralData generalData;
    private UUID uniqueId;

    private List<TeamData> data = new ArrayList<>();

    /**
     * Constructor to make a new {@link Team} object with a {@link UUID}
     *
     * @param uuid the unique identifier
     */
    public Team(UUID uuid) {
        this.uniqueId = uuid == null ? UUID.randomUUID() : uuid;

        this.teamController.getTeams().add(this);
    }

    /**
     * Constructor for creating a new Team object
     *
     * @param uuid the uuid of the team
     * @param name the name of the team
     * @param type the team type
     */
    public Team(UUID uuid, String name, TeamType type) {
        this(uuid);

        this.generalData = new GeneralData(name, type, ChatColor.WHITE);
        this.setupData();

        Vapor.getInstance().getLogger().log(Level.INFO, "Creating new team" + (this.hasData(GeneralData.class) ? " with name " + this.findData(GeneralData.class).getName() : ""));
    }

    /**
     * Constructor for creating a new Player Team object
     * This defaults the TeamType to PLAYER_TEAM
     *
     * @param uuid       the uuid of the team
     * @param name       the name of the team
     * @param leaderUuid the leader of the team
     */
    public Team(UUID uuid, String name, UUID leaderUuid) {
        this(uuid, name, TeamType.PLAYER_TEAM);

        this.addData(new PlayerTeamData(leaderUuid));
    }

    /**
     * Constructor for creating a new Team object
     * This constructor automatically adds a ClaimTeamData to the data list
     *
     * @param uuid  the uuid of the team
     * @param name  the name of the team
     * @param type  the type of the claim
     * @param claim the allocated region of the team
     */
    public Team(UUID uuid, String name, TeamType type, Claim claim) {
        this(uuid, name, type);

        this.addData(new ClaimTeamData(claim));
    }

    /**
     * Constructor to make a new {@link Team} object with a {@link Koth} object
     * This constructor automatically adds a {@link KothTeamData} object
     * using the provided {@link Koth} object in the constructor.
     *
     * @param uuid the unique identifier of the team
     * @param name the name of the team
     * @param type the type of the team
     * @param koth the provided koth to make the {@link KothTeamData} object with
     */
    public Team(UUID uuid, String name, TeamType type, Koth koth) {
        this(uuid, name, type);

        this.addData(new KothTeamData(koth));
        this.addData(new ClaimTeamData(koth.getClaim()));
    }

    /**
     * Method to setup the {@link TeamData} fields in the team
     */
    private void setupData() {
        this.addData(generalData);

        if (this.generalData.getType().equals(TeamType.PLAYER_TEAM)) {
            this.addData(new InviteTeamData());
            this.addData(new DTRData(1.1D));
        }
    }

    /**
     * Set the team's claim to a new claim
     *
     * @param claim the claim
     */
    public void addClaim(Claim claim) {
        if (this.findData(ClaimTeamData.class) != null) {
            this.getData().remove(this.findData(ClaimTeamData.class));
        }

        this.addData(new ClaimTeamData(claim));
    }

    /**
     * Check whether a player can interact with the team's claim
     *
     * @param player the player
     * @return whether he can interact
     */
    public boolean canInteract(Player player) {
        if (player.hasPermission("hcteams.bypass.interact") && player.getGameMode().equals(GameMode.CREATIVE)) {
            return true;
        }

        if (this.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            return this.findData(PlayerTeamData.class).contains(player) || this.findData(DTRData.class).isRaidable();
        }

        return this.getGeneralData().getType().canInteract;
    }

    /**
     * Disband the current team
     */
    public void disband() {
        if (this.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            final PlayerTeamData playerTeamData = this.findData(PlayerTeamData.class);
            playerTeamData.broadcast(ChatColor.RED + "Your current team has been disbanded.");
        }

        teamController.getTeams().remove(this);
        databaseController.getDataHandler().delete(this, "teams");
    }

    /**
     * Get the display name of the team
     *
     * @return the display name
     */
    public String getDisplayName(Player player) {
        return this.getGeneralData().getType().formatName(this, player);
    }

    /**
     * Get the formatted name of the team
     *
     * @return the formatted name
     */
    public String getFormattedName() {
        return this.getGeneralData().getName().replace("_", " ");
    }
}