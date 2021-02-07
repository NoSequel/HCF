package rip.vapor.hcf.listeners.combatwall;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.listeners.combatwall.block.CombatWallBlockList;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.timers.impl.player.CombatTimer;
import rip.vapor.hcf.timers.impl.player.SpawnProtectionTimer;

import java.util.*;

public class CombatWallListener implements Listener {

    private final TeamController teamController = Vapor.getInstance().getHandler().find(TeamController.class);
    private final TimerController timerController = Vapor.getInstance().getHandler().find(TimerController.class);
    private final Set<CombatWallBlockList> visualizedBlocks = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final CombatWallType combatWallType = this.getWallType(player);

        this.findVisualizedBlockList(player).attemptRemove();

        if (!combatWallType.equals(CombatWallType.NONE) && !this.teamController.findTeam(event.getTo()).equals(this.teamController.findTeam(event.getFrom()))) {
            final CombatWallData data = this.getWallLocation(player, combatWallType);

            if (this.isTeamApplicable(combatWallType, this.teamController.findTeam(event.getTo()))) {
                event.setTo(event.getFrom());
            }

            if (data != null) {
                this.visualizeWall(player, data);
            }
        }
    }

    /**
     * Get the {@link CombatWallType} for a certain {@link Player}
     *
     * @param player the player
     * @return the wall type
     */
    private CombatWallType getWallType(Player player) {
        return this.timerController.findTimer(CombatTimer.class).isOnCooldown(player) ? CombatWallType.SPAWN_TAG
                : this.timerController.findTimer(SpawnProtectionTimer.class).isOnCooldown(player) ? CombatWallType.INVINCIBILITY
                : CombatWallType.NONE;
    }

    /**
     * Check if a {@link Team} is applicable for a certain {@link CombatWallType}
     *
     * @param type the combat wall type
     * @param team the team
     * @return whether its applicable or not
     */
    private boolean isTeamApplicable(CombatWallType type, Team team) {
        return !type.equals(CombatWallType.NONE) && (type.equals(CombatWallType.SPAWN_TAG)
                ? !team.findData(ClaimTeamData.class).getClaim().isDeathban()
                : type.equals(CombatWallType.INVINCIBILITY) && team.hasData(PlayerTeamData.class));
    }

    /**
     * Get the {@link Location} for where to build the combat wall
     *
     * @param player the player
     * @param type   the type of the wall
     * @return the location for the wall or null
     */
    private CombatWallData getWallLocation(Player player, CombatWallType type) {
        for (int i = 0; i < 16; i++) {
            final Location originalLocation = player.getLocation();
            Location location;

            if (this.isTeamApplicable(type, teamController.findTeam((location = originalLocation.clone().add(i, 0, 0))))) {
                return new CombatWallData(teamController.findTeam(location), location);
            } else if (this.isTeamApplicable(type, teamController.findTeam((location = originalLocation.clone().add(0, 0, i))))) {
                return new CombatWallData(teamController.findTeam(location), location);
            } else if (this.isTeamApplicable(type, teamController.findTeam((location = originalLocation.clone().subtract(i, 0, 0))))) {
                return new CombatWallData(teamController.findTeam(location), location);
            } else if (this.isTeamApplicable(type, teamController.findTeam((location = originalLocation.clone().subtract(0, 0, i))))) {
                return new CombatWallData(teamController.findTeam(location), location);
            }
        }

        return null;
    }

    /**
     * Method for visualizing the combat wall
     *
     * @param player the player to build it for
     * @param data   the location for the wall to be built
     */
    private void visualizeWall(Player player, CombatWallData data) {
        final Location wallLocation = data.getLocation();
        final Team wallTeam = data.getTeam();

        if (wallLocation != null) {
            final Location[] locations = new Location[]{
                    wallLocation.clone().add(1, 1, 0),
                    wallLocation.clone().add(0, 1, 0),
                    wallLocation.clone().add(1, 2, 0),
                    wallLocation.clone().add(0, 2, 0),

                    wallLocation.clone().add(1, 0, 0),
                    wallLocation.clone(),

                    wallLocation.clone().subtract(1, 0, 0),
                    wallLocation.clone().subtract(1, 0, 0)
            };

            Arrays.stream(locations)
                    .filter(location -> this.teamController.findTeam(location.clone()).equals(wallTeam))
                    .forEach(location -> this.findVisualizedBlockList(player).add(location));
        }
    }

    /**
     * Get a {@link CombatWallBlockList} from a {@link Player}
     *
     * @param player the player to get it for
     * @return the list
     */
    private CombatWallBlockList findVisualizedBlockList(Player player) {
        return this.visualizedBlocks.stream()
                .filter(block -> block.getPlayer().equals(player))
                .findFirst().orElseGet(() -> {
                    final CombatWallBlockList list = new CombatWallBlockList(player);
                    this.visualizedBlocks.add(list);

                    return list;
                });
    }
}