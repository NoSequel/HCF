package rip.vapor.hcf.team.koth;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.impl.KothTeamData;

import java.util.Optional;

public class KothListener implements Listener {

    private final TeamModule teamModule;

    /**
     * Constructor to make a new koth listener instance
     *
     * @param handler the handler to get the modules from
     */
    public KothListener(ModuleHandler handler) {
        this.teamModule = handler.find(TeamModule.class);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Optional<Team> teamTo = teamModule.findTeam(event.getTo());
        final Player player = event.getPlayer();

        if (teamTo.isPresent() && teamTo.get().hasData(KothTeamData.class)) {
            final KothTeamData teamToData = teamTo.get().findData(KothTeamData.class);

            if (teamToData != null) {
                final Koth koth = teamToData.getKoth();
                final Location location = player.getLocation();
                final Claim capzone = koth.getCapzone();

                if (koth.isRunning() && koth.getCappingUuid() == null && capzone.getCuboid().isLocationInCuboid(location)) {
                    koth.setCappingUuid(player.getUniqueId());
                }
            }
        }
    }
}