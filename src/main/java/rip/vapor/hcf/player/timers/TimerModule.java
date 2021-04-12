package rip.vapor.hcf.player.timers;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.timers.impl.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class TimerModule implements Module {

    private final ModuleHandler handler;
    private final List<Timer> timers = new ArrayList<>();

    @Override
    public void enable() {
        this.registerTimer(new EnderpearlTimer(this.handler));
        this.registerTimer(new CombatTimer(this.handler));
        this.registerTimer(new TeleportTimer(this.handler));
        this.registerTimer(new SpawnProtectionTimer(this.handler));
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
    public <T extends Timer> Optional<T> findTimer(Class<T> clazz) {
        return (Optional<T>) this.timers.stream()
                .filter(timer -> timer.getClass().equals(clazz))
                .findFirst();
    }

    /**
     * Register a timer
     *
     * @param timer the timer
     */
    public void registerTimer(Timer timer) {
        Bukkit.getPluginManager().registerEvents(timer, Vapor.getPlugin(Vapor.class));

        this.timers.add(timer);
    }
}