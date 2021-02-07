package rip.vapor.hcf.team;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.data.DataController;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.ClaimPriority;
import rip.vapor.hcf.team.data.TeamData;
import rip.vapor.hcf.team.data.impl.GeneralData;
import rip.vapor.hcf.team.data.impl.KothTeamData;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.data.impl.player.invites.InviteTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.Cuboid;
import rip.vapor.hcf.util.database.DatabaseModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamModule implements Module, DataController<Team, TeamData> {

    private final List<Team> teams = new ArrayList<>();

    private final List<? extends TeamData> registeredData = new ArrayList<>(Arrays.asList(
            new PlayerTeamData(),
            new ClaimTeamData(),
            new DTRData(),
            new InviteTeamData(),
            new GeneralData(),
            new KothTeamData()
    ));

    @Override
    public void enable() {
        this.loadAll();

        if (!this.teams.isEmpty()) {
            this.teams.forEach(team -> team.setGeneralData(team.findData(GeneralData.class)));
        } else {
            Vapor.getInstance().getLogger().log(Level.INFO, "Setting up default teams.");

            final Claim spawnClaim = new Claim(new Cuboid(new Location(Bukkit.getWorlds().get(0), 100, 100, 100), new Location(Bukkit.getWorlds().get(0), -100, -100, -100)), ClaimPriority.NORMAL);
            final Claim warzoneClaim = new Claim(new Cuboid(new Location(Bukkit.getWorlds().get(0), 750, 750, 750), new Location(Bukkit.getWorlds().get(0), -750, -750, -750)), ClaimPriority.NORMAL);
            final Claim wildernessClaim = new Claim(new Cuboid(new Location(Bukkit.getWorlds().get(0), -1, -1, -1), new Location(Bukkit.getWorlds().get(0), -1, -1, -1)), ClaimPriority.LOW);

            spawnClaim.setDeathban(false);

            new Team(null, "Spawn", TeamType.SAFEZONE_TEAM, spawnClaim);
            new Team(null, "Wilderness", TeamType.WILDERNESS_TEAM, wildernessClaim);
            new Team(null, "Warzone", TeamType.SYSTEM_TEAM, warzoneClaim).getGeneralData().setColor(ChatColor.DARK_RED);
        }
    }

    @Override
    public void disable() {
        final DatabaseModule controller = Vapor.getInstance().getHandler().find(DatabaseModule.class);

        teams.forEach(loadable -> controller.getDataHandler().save(loadable, "teams"));
    }

    @Override
    public void loadAll() {
        final DatabaseModule controller = Vapor.getInstance().getHandler().find(DatabaseModule.class);

        controller.getDataHandler().loadAll(this, "teams", Team.class);
    }

    /**
     * Find a team by a UUID
     *
     * @param uuid the uuid
     * @return the found team | or null
     */
    public Team findTeam(UUID uuid) {
        return this.teams.stream()
                .filter(team -> team.getUniqueId().equals(uuid))
                .findFirst().orElse(null);
    }

    /**
     * Find a team by a name {@link String}
     *
     * @param name the name
     * @return the foun team
     */
    public Team findTeam(String name) {
        return this.findTeamByName(name);
    }

    /**
     * Find a {@link Team} by a {@link String}
     *
     * @param name the name of the team
     * @return the team or null
     */
    public Team findTeamByName(String name) {
        return this.teams.stream()
                .filter(team -> team.getGeneralData() != null && team.getGeneralData().getName() != null && team.getGeneralData().getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    /**
     * Find a team by a player
     *
     * @param player the player
     * @return the team
     */
    public Team findTeam(Player player) {
        final Optional<Team> team = this.teams.stream()
                .filter($team -> $team.hasData(PlayerTeamData.class))
                .filter($team -> $team.findData(PlayerTeamData.class).contains(player))
                .findFirst();

        return team.orElse(null);
    }

    /**
     * Find a team by a location of a player
     *
     * @param location the location
     * @return the found team
     */
    public Team findTeam(Location location) {
        final Optional<Claim> claim = this.teams.stream()
                .filter(team -> team.hasData(ClaimTeamData.class) && team.findData(ClaimTeamData.class).getClaim() != null)
                .map(team -> team.findData(ClaimTeamData.class).getClaim())
                .sorted(Comparator.comparing(claim1 -> ((Claim) claim1).getPriority().priority).reversed())
                .filter($claim -> $claim.getCuboid().isLocationInCuboid(location))
                .findFirst();

        if (claim.isPresent() && claim.get().getTeam() != null) {
            return claim.get().getTeam();
        }

        return this.findTeam("Wilderness");
    }

    /**
     * Find all active {@link Koth}s
     *
     * @return the active koths
     */
    public List<Koth> findActiveKoths() {
        return this.teams.stream()
                .filter(team -> team.getGeneralData().getType().equals(TeamType.KOTH_TEAM) && team.hasData(KothTeamData.class))
                .map(team -> team.findData(KothTeamData.class).getKoth())
                .filter(Koth::isRunning)
                .collect(Collectors.toList());
    }

    /**
     * Find a data object by a class
     *
     * @param data the class
     * @param <T>  the type of the data object
     * @return the found data | or null
     */
    public <T extends TeamData> T findData(Class<T> data) {
        return data.cast(this.registeredData.stream()
                .filter($data -> $data.getClass().equals(data))
                .findFirst().orElse(null));
    }
}