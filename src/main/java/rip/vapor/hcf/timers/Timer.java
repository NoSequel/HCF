package rip.vapor.hcf.timers;

import org.bukkit.event.Listener;
import rip.vapor.hcf.timers.thread.TimerThread;
import rip.vapor.hcf.timers.thread.impl.PlayerTimerThread;

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
