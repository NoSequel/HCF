package rip.vapor.hcf.player.timers;

import org.bukkit.event.Listener;
import rip.vapor.hcf.player.timers.thread.TimerThread;

public interface Timer extends Listener {

    /**
     * Get the scoreboard tag of a timer
     *
     * @return the scoreboard tag
     */
    String getScoreboardTag();

    /**
     * Get the default duration of a {@link Timer}
     *
     * @return the default duration
     */
    long getDefaultDuration();

    /**
     * Check if a {@link Timer} should be displayed as a trailing timer
     *
     * @return the state
     */
    boolean isTrailing();

    /**
     * Get the thread for the {@link Timer}
     *
     * @return the thread
     */
    TimerThread<?> getThread();

}
