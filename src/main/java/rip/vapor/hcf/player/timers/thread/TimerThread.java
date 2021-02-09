package rip.vapor.hcf.player.timers.thread;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import rip.vapor.hcf.player.timers.Timer;

@Getter
@Setter
public abstract class TimerThread<T extends Timer> extends BukkitRunnable {

    private final T timer;

    private boolean active;

    /**
     * Constructor to make a new {@link TimerThread} object
     *
     * @param timer the timer
     */
    public TimerThread(T timer) {
        this.timer = timer;
    }

}
