package rip.vapor.hcf.player.timers.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.global.SOTWTimer;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;

@RequiredArgsConstructor
public class SOTWCommand {

    private final ModuleHandler handler;
    private final TimerModule timerModule = this.handler.find(TimerModule.class);

    @Command(label = "sotw")
    public void sotw(CommandSender sender) {
        sender.sendMessage(new String[]{
                ChatColor.RED + "/sotw start <time>",
                ChatColor.RED + "/sotw end",
        });
    }

    @Subcommand(label = "start", parentLabel = "sotw")
    public void start(CommandSender sender, Integer time) {
        this.timerModule.registerTimer(new SOTWTimer(time));
    }

    @Subcommand(label = "end", parentLabel = "sotw")
    public void end(CommandSender sender) {
        this.timerModule.findTimer(SOTWTimer.class).ifPresent(SOTWTimer::handleEnd);
    }
}
