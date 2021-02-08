package rip.vapor.hcf.koth;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.impl.KothTeamData;

public class KothListener implements Listener, Controllable<TeamModule> {

    private final TeamModule teamModule = this.getModule();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Team teamTo = teamModule.findTeam(event.getTo());
        final Player player = event.getPlayer();

        if (teamTo != null && teamTo.hasData(KothTeamData.class)) {
            final KothTeamData teamToData = teamTo.findData(KothTeamData.class);

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