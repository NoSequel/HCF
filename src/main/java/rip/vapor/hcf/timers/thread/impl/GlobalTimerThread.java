package rip.vapor.hcf.timers.thread.impl;

import lombok.Getter;
import lombok.Setter;
import rip.vapor.hcf.timers.impl.GlobalTimer;
import rip.vapor.hcf.timers.thread.TimerThread;

@Getter
@Setter
public class GlobalTimerThread extends TimerThread<GlobalTimer> {

    private long currentDuration;
    private boolean shouldSubtract;

    /**
     * Constructor to make a new {@link GlobalTimerThread} object for a {@link GlobalTimer}
     *
     * @param timer          the timer
     * @param shouldSubtract whether the timer should actually run or not
     */
    public GlobalTimerThread(GlobalTimer timer, boolean shouldSubtract) {
        super(timer);
        this.shouldSubtract = shouldSubtract;
    }

    @Override
    public void run() {
        if (this.shouldSubtract) {
            if ((this.currentDuration = this.currentDuration - 50) <= 0) {
                this.getTimer().handleEnd();
            }
        }

        this.getTimer().handleTick();
    }
}