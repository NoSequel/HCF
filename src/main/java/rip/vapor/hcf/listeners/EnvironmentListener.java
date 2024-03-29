package rip.vapor.hcf.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.GeneralData;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;

import java.util.Optional;

@RequiredArgsConstructor
public class EnvironmentListener implements Listener {

    private final TeamModule teamModule;

    /**
     * Constructor to create a new environment listener instance
     *
     * @param handler the handler to get the modules from
     */
    public EnvironmentListener(ModuleHandler handler) {
        this.teamModule = handler.find(TeamModule.class);
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        final Optional<Team> teamOptional = this.teamModule.findTeam(event.getEntity().getLocation());

        if (teamOptional.isPresent()
                && teamOptional.get().hasData(GeneralData.class)
                && !teamOptional.get().findData(ClaimTeamData.class).getClaim().isDeathban()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        final Location location = event.getLocation();
        final Optional<Team> teamOptional = this.teamModule.findTeam(location);

        if (teamOptional.isPresent()
                && teamOptional.get().hasData(GeneralData.class)
                && !teamOptional.get().findData(ClaimTeamData.class).getClaim().isDeathban()) {
            event.setCancelled(true);
        }
    }
}