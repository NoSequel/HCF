package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.timers.Timer;
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

        for (Timer timer : timerController.getTimers()) {
            if (timer instanceof PlayerTimer && ((PlayerTimer) timer).isOnCooldown(player)) {
                strings.add(timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(((PlayerTimer) timer).getDuration(player), timer.isTrailing()));
            } else if (timer instanceof GlobalTimer && timer.getThread().isActive()) {
                if(((GlobalTimer) timer).getThread().getCurrentDuration() > 0) {
                    strings.add(timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(((GlobalTimer) timer).getThread().getCurrentDuration(), timer.isTrailing()));
                }
            }
        }

        return strings;
    }
}