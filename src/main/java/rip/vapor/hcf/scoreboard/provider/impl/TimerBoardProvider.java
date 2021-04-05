package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.module.ModuleHandler;
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

public class TimerBoardProvider implements BoardProvider {

    private final TimerModule timerModule;

    /**
     * Constructor to make a new timer board provider
     *
     * @param handler the handler to get the timer module from
     */
    public TimerBoardProvider(ModuleHandler handler) {
        this.timerModule = handler.find(TimerModule.class);
    }

    @Override
    public List<String> getStrings(Player player) {
        final List<String> strings = new ArrayList<>();

        for (Timer timer : timerModule.getTimers()) {
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