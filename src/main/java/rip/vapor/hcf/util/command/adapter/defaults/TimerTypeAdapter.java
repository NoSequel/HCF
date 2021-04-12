package rip.vapor.hcf.util.command.adapter.defaults;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TimerTypeAdapter implements TypeAdapter<PlayerTimer> {

    private final TimerModule timerModule;

    /**
     * Constructor to make a new timer type adapter instance
     *
     * @param handler the handler to get the modules from
     */
    public TimerTypeAdapter(ModuleHandler handler) {
        this.timerModule = handler.find(TimerModule.class);
    }

    @Override
    public PlayerTimer convert(CommandSender sender, String source) throws Exception {
        return this.timerModule.getTimers().stream()
                .filter(timer -> timer instanceof PlayerTimer)
                .map(timer -> ((PlayerTimer) timer))
                .filter(timer -> timer.getName().equalsIgnoreCase(source))
                .findFirst().orElse(null);
    }

    @Override
    public Class<PlayerTimer> getType() {
        return PlayerTimer.class;
    }
}
