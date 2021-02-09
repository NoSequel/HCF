package rip.vapor.hcf.player.timers.impl;

import lombok.Getter;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.timers.Timer;
import rip.vapor.hcf.player.timers.thread.impl.GlobalTimerThread;

@Getter
public abstract class GlobalTimer implements Timer {

    private final String name;
    private final String scoreboardTag;

    private final long defaultDuration;
    private final boolean trailing;

    private final GlobalTimerThread thread;

    /**
     * Constructor for creating a new timer
     *
     * @param name            the name of the timer
     * @param scoreboardTag   the tag displayed on the scoreboard
     * @param trailing        whether the timer should be displayed as trailing
     * @param defaultDuration the default duration of the timer
     * @param shouldSubtract  whether the timer should automatically start or not
     */
    public GlobalTimer(String name, String scoreboardTag, boolean trailing, long defaultDuration, boolean shouldSubtract) {
        this.name = name;
        this.scoreboardTag = scoreboardTag;
        this.defaultDuration = defaultDuration;
        this.trailing = trailing;

        this.thread = new GlobalTimerThread(this, shouldSubtract);
        this.thread.runTaskTimer(Vapor.getInstance(), 2L, 0L);
    }

    /**
     * Handle the ticking of the {@link GlobalTimerThread}
     */
    public abstract void handleTick();

    /**
     * Handle the expiration of the timer
     */
    public abstract void handleEnd();


}