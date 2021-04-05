package rip.vapor.hcf.util.command.adapter.defaults;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamTypeAdapter implements TypeAdapter<Team> {

    private final TeamModule module;

    /**
     * Constructor to make a new team type adapter
     *
     * @param handler the handler to get the modules from
     */
    public TeamTypeAdapter(ModuleHandler handler) {
        this.module = handler.find(TeamModule.class);
    }

    @Override
    public Team convert(CommandSender sender, String source) throws Exception {
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
