package rip.vapor.hcf.timers.commands;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.timers.PlayerTimer;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Parameter;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TimerCommand {

    private final TimerController controller = Vapor.getInstance().getHandler().find(TimerController.class);

    @Command(label = "timer", permission = "staff")
    @Subcommand(label = "help", parentLabel = "timer", permission = "staff")
    public void help(Player player) {
        player.sendMessage(new String[]{
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.GOLD + "Timer Help",
                "",
                ChatColor.GRAY + "* /timer start <timer> <player> [duration]",
                ChatColor.GRAY + "* /timer stop <timer> <player>",
                ChatColor.GRAY + "* /timer clear <player>",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
        });
    }

    @Subcommand(label = "start", permission = "staff", parentLabel = "timer")
    public void start(Player player, PlayerTimer timer, Player target, @Parameter(name = "duration", value = "0") String $duration) {
        long duration = Long.parseLong($duration) == 0L ? timer.getDefaultDuration() : Long.parseLong($duration);

        timer.start(target, duration);
        player.sendMessage(ChatColor.GOLD + "You have started the " + ChatColor.WHITE + timer.getName() + ChatColor.GOLD + " timer for " + ChatColor.WHITE + target.getName());

        if ($duration.equals("0")) {
            player.sendMessage(ChatColor.YELLOW + "The duration has been defaulted to the timer's default duration.");
        }
    }

    @Subcommand(label = "stop", permission = "staff", parentLabel = "timer")
    public void stop(Player player, PlayerTimer timer, Player target) {
        if (timer.isOnCooldown(target)) {
            timer.cancel(target);

            player.sendMessage(ChatColor.GOLD + "Cancelled timer for " + ChatColor.WHITE + target.getName());
        } else {
            player.sendMessage(ChatColor.RED + "That player is not on that timer,");
        }
    }

    @Subcommand(label = "clear", permission = "staff", parentLabel = "timer")
    public void clear(Player player, Player target) {
        controller.getTimers().stream()
                .filter(timer -> timer.isOnCooldown(target))
                .forEach(timer -> timer.cancel(target));

        player.sendMessage(ChatColor.GOLD + "Cancelled all timers for " + ChatColor.WHITE + target.getName());
    }
}