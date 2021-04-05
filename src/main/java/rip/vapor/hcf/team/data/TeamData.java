package rip.vapor.hcf.team.data;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.data.Data;
import rip.vapor.hcf.team.TeamModule;

public interface TeamData extends Data {

    /**
     * Get the {@link TeamModule} instance
     *
     * @return the team module
     */
    default TeamModule getModule() {
        return Vapor.getPlugin(Vapor.class).getHandler().find(TeamModule.class);
    }

}
