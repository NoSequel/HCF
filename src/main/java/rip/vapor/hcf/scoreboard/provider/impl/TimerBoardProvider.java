package rip.vapor.hcf.scoreboard.provider.impl;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
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

    /**
     * Get the strings of the part
     *
     * @param element the element to add the strings to
     * @param player  the player
     */
    @Override
    public void getStrings(ScoreboardElement element, Player player) {
        for (Timer timer : timerModule.getTimers()) {
            if (timer instanceof PlayerTimer && ((PlayerTimer) timer).isOnCooldown(player)) {
                element.add(timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(((PlayerTimer) timer).getDuration(player), timer.isTrailing()));
            } else if (timer instanceof GlobalTimer && timer.getThread().isActive()) {
                if(((GlobalTimer) timer).getThread().getCurrentDuration() > 0) {
                    element.add(timer.getScoreboardTag() + ChatColor.GRAY + ": " + ChatColor.RED + StringUtils.getFormattedTime(((GlobalTimer) timer).getThread().getCurrentDuration(), timer.isTrailing()));
                }
            }
        }
    }
}