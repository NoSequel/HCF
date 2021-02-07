package rip.vapor.hcf.timers.thread;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import rip.vapor.hcf.timers.Timer;

@Getter
public abstract class TimerThread<T extends Timer> extends BukkitRunnable {

    private final T timer;

    /**
     * Constructor to make a new {@link TimerThread} object
     *
     * @param timer the timer
     */
    public TimerThread(T timer) {
        this.timer = timer;
    }
}
