package rip.vapor.hcf.util.command.adapter.defaults;

import org.bukkit.command.CommandSender;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.KothTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;

public class KothTypeAdapter implements TypeAdapter<Koth>, Controllable<TeamModule> {

    private final TeamModule teamModule = this.getModule();

    @Override
    public Koth convert(CommandSender sender, String source) throws Exception {
        return teamModule.getTeams().stream()
                .filter(team -> team.getGeneralData().getName().equalsIgnoreCase(source) && team.getGeneralData().getType().equals(TeamType.KOTH_TEAM))
                .findFirst().map(team -> team.findData(KothTeamData.class).getKoth()).orElse(null);

    }

    @Override
    public Class<Koth> getType() {
        return Koth.class;
    }
}
