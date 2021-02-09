package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.GlobalTimer;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import rip.vapor.hcf.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimerBoardProvider implements BoardProvider {

    private final TimerModule timerController = Vapor.getInstance().getHandler().find(TimerModule.class);

    @Override
    public List<String> getStrings(Player player) {
        final List<String> strings = new ArrayList<>();

        strings.addAll(timerController.getTimers().stream()
                .filter(timer -> timer instanceof PlayerTimer)
                .map(timer -> ((PlayerTimer) timer))
                .filter(timer -> timer.isOnCooldown(player))
                .map(timer -> timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(timer.getDuration(player), timer.isTrailing()))
                .collect(Collectors.toList()));

        strings.addAll(timerController.getTimers().stream()
                .filter(timer -> timer instanceof GlobalTimer)
                .map(timer -> ((GlobalTimer) timer))
                .filter(timer -> timer.getThread().isActive())
                .map(timer -> timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(timer.getThread().getCurrentDuration(), timer.isTrailing()))
                .collect(Collectors.toList()));

        return strings;
    }
}