package rip.vapor.hcf.util.command.adapter.defaults;

import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamTypeAdapter implements TypeAdapter<Team>, Controllable<TeamModule> {

    private final TeamModule module = this.getModule();

    @Override
    public Team convert(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("@SELF")) {
            final Player player = (Player) sender;

            return module.findTeam(player).orElse(null);
        }

        final Player player = Bukkit.getPlayer(source);

        return player != null && module.findTeam(player).isPresent()
                ? module.findTeam(player).orElse(null)
                : module.findTeam(source).orElse(null);
    }

    @Override
    public Class<Team> getType() {
        return Team.class;
    }
}
