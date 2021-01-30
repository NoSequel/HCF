package rip.vapor.hcf.listeners.combatwall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import rip.vapor.hcf.team.Team;

@RequiredArgsConstructor
@Getter
public class CombatWallData {

    private final Team team;
    private final Location location;

}
