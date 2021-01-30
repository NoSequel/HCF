package rip.vapor.hcf.util.command.adapter.defaults;

import rip.vapor.hcf.controller.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamTypeAdapter implements TypeAdapter<Team>, Controllable<TeamController> {

    private final TeamController controller = this.getController();

    @Override
    public Team convert(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("@SELF")) {
            final Player player = (Player) sender;

            return controller.findTeam(player);
        }

        final Player player = Bukkit.getPlayer(source);

        if(player != null && controller.findTeam(player) != null) {
            return controller.findTeam(player);
        }

        return controller.findTeam(source);
    }

    @Override
    public Class<Team> getType() {
        return Team.class;
    }
}
