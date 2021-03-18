package rip.vapor.hcf.player.timers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.global.SOTWTimer;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;

public class SOTWCommand implements Controllable<TimerModule> {

    @Command(label = "sotw")
    public void sotw(CommandSender sender) {
        sender.sendMessage(new String[]{
                ChatColor.RED + "/sotw start <time>",
                ChatColor.RED + "/sotw end",
        });
    }

    @Subcommand(label = "start", parentLabel = "sotw")
    public void start(CommandSender sender, Integer time) {
        this.getModule().registerTimer(new SOTWTimer(time));
    }

    @Subcommand(label = "end", parentLabel = "sotw")
    public void end(CommandSender sender) {
        this.getModule().findTimer(SOTWTimer.class).ifPresent(SOTWTimer::handleEnd);
    }
}
