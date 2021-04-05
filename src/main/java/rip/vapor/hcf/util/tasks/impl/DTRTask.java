package rip.vapor.hcf.util.tasks.impl;

import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.util.tasks.Task;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.player.DTRData;

import java.util.ArrayList;
import java.util.List;

public class DTRTask extends Task {

    private final TeamModule teamModule;

    public DTRTask(ModuleHandler handler) {
        super(0);
        this.teamModule = handler.find(TeamModule.class);
    }

    @Override
    public void tick() {
        final List<Team> teams = new ArrayList<>(teamModule.getTeams());

        for(Team team : teams) {
            final DTRData dtrData = team.findData(DTRData.class);

            if(dtrData != null) {
                if(dtrData.getDtr() > dtrData.getMaxDtr()) {
                    dtrData.setDtr(dtrData.getMaxDtr());
                } else if(dtrData.getDtr() < dtrData.getMaxDtr() && System.currentTimeMillis() - dtrData.getLastRegen() >= 150000) {
                    dtrData.setLastRegen(System.currentTimeMillis());
                    dtrData.setDtr(dtrData.getDtr() + 0.1);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "DTR";
    }
}