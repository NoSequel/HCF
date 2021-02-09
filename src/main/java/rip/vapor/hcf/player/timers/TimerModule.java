package rip.vapor.hcf.player.timers;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import rip.vapor.hcf.player.timers.impl.player.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimerModule implements Module {

    private final List<Timer> timers = new ArrayList<>();

    @Override
    public void enable() {
        this.registerTimer(new EnderpearlTimer());
        this.registerTimer(new CombatTimer());
        this.registerTimer(new TeleportTimer());
        this.registerTimer(new SpawnProtectionTimer());
        this.registerTimer(new ArcherSpeedTimer());
        this.registerTimer(new RogueStabTimer());
    }

    /**
     * Find a timer by a class
     *
     * @param clazz the class
     * @param <T> the type of the timer
     * @return the timer | or null
     */
    public <T extends PlayerTimer> T findTimer(Class<T> clazz) {
        return clazz.cast(this.timers.stream()
                .filter(timer -> timer.getClass().equals(clazz))
                .findFirst().orElse(null));
    }

    /**
     * Register a timer
     *
     * @param timer the timer
     */
    public void registerTimer(Timer timer) {
        Bukkit.getPluginManager().registerEvents(timer, Vapor.getInstance());

        this.timers.add(timer);
    }
}