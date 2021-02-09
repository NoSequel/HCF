package rip.vapor.hcf.util.tasks.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.util.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NametagTask extends Task {

    private final TeamModule teamModule = Vapor.getInstance().getHandler().find(TeamModule.class);

    public NametagTask() {
        super(60L);
    }

    @Override
    public void tick() throws Exception {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<Team> teamOptional = this.teamModule.findTeam(player);

            Bukkit.getOnlinePlayers().forEach(target -> {
                final Optional<Team> targetTeamOptional = this.teamModule.findTeam(target);
                final List<String> strings = new ArrayList<>();
                final ChatColor color = !teamOptional.isPresent() || !targetTeamOptional.isPresent() || !targetTeamOptional.get().equals(teamOptional.get())
                        ? ChatColor.RED
                        : ChatColor.GREEN;

                targetTeamOptional
                        .ifPresent(team -> strings.add(ChatColor.GOLD + "[" + team.findData(DTRData.class).formatDtr() + ChatColor.GOLD + "]"));
                strings.add(color + target.getName());

                //new LCPacketNameTag(target, strings).send(player);
            });
        });
    }

    @Override
    public String getName() {
        return "NameTag";
    }
}
