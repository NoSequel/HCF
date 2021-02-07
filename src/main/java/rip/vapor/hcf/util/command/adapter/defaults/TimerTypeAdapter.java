package rip.vapor.hcf.util.command.adapter.defaults;

import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.timers.impl.PlayerTimer;
import rip.vapor.hcf.timers.TimerModule;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.command.CommandSender;

public class TimerTypeAdapter implements TypeAdapter<PlayerTimer>, Controllable<TimerModule> {

    @Override
    public PlayerTimer convert(CommandSender sender, String source) {
        return this.getModule().getTimers().stream()
                .filter(timer -> timer.getName().equalsIgnoreCase(source))
                .findFirst().orElse(null);
    }

    @Override
    public Class<PlayerTimer> getType() {
        return PlayerTimer.class;
    }
}
