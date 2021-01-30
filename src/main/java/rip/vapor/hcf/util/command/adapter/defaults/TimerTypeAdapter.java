package rip.vapor.hcf.util.command.adapter.defaults;

import rip.vapor.hcf.controller.Controllable;
import rip.vapor.hcf.timers.Timer;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.command.CommandSender;

public class TimerTypeAdapter implements TypeAdapter<Timer>, Controllable<TimerController> {

    @Override
    public Timer convert(CommandSender sender, String source) {
        return this.getController().getTimers().stream()
                .filter(timer -> timer.getName().equalsIgnoreCase(source))
                .findFirst().orElse(null);
    }

    @Override
    public Class<Timer> getType() {
        return Timer.class;
    }
}
