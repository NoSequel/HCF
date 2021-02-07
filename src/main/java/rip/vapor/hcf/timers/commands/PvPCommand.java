package rip.vapor.hcf.timers.commands;

import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.timers.TimerModule;
import rip.vapor.hcf.timers.impl.player.SpawnProtectionTimer;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPCommand implements Controllable<TimerModule> {

    private final TimerModule controller = this.getController();
    private final SpawnProtectionTimer spawnProtectionTimer = this.controller.findTimer(SpawnProtectionTimer.class);

    @Command(label = "pvp")
    public void main(Player player) {
        player.sendMessage(new String[]{
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.GOLD + "PvP Help",
                "",
                ChatColor.GRAY + "* /pvp enable",
                ChatColor.GRAY + "* /pvp suicide",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52)
        });
    }

    @Subcommand(label = "enable", parentLabel = "pvp")
    public void enable(Player player) {
        if (spawnProtectionTimer.isOnCooldown(player)) {
            spawnProtectionTimer.cancel(player);

            player.sendMessage(ChatColor.GREEN + "You have disabled your spawn protection timer.");
        } else {
            player.sendMessage(ChatColor.RED + "You are not on a spawn protection timer.");
        }
    }

    @Subcommand(label = "suicide", parentLabel = "pvp")
    public void suicide(Player player) {
        player.setHealth(0.0);
    }
}