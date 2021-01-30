package rip.vapor.hcf.util.tasks.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.util.tasks.Task;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.data.impl.player.DTRData;

import java.util.ArrayList;
import java.util.List;

public class DTRTask extends Task {

    private final TeamController teamController = Vapor.getInstance().getHandler().findController(TeamController.class);

    public DTRTask() {
        super(0);
    }

    @Override
    public void tick() {
        final List<Team> teams = new ArrayList<>(teamController.getTeams());

        teams.stream()
                .filter(team -> team.hasData(DTRData.class))
                .map(team -> team.findData(DTRData.class))
                .filter(data -> data.getDtr() > data.getMaxDtr())
                .forEach(data -> data.setDtr(data.getMaxDtr()));

        teamController.getTeams().stream()
                .filter(team -> team.hasData(DTRData.class))
                .map(team -> team.findData(DTRData.class))
                .filter(data -> data.getDtr() < data.getMaxDtr() && System.currentTimeMillis() - data.getLastRegen() >= 150000)
                .forEach(data -> {
                    data.setLastRegen(System.currentTimeMillis());
                    data.setDtr(data.getDtr() + 0.1);
                });
    }

    @Override
    public String getName() {
        return "DTR";
    }
}