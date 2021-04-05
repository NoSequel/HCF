package rip.vapor.hcf.player.timers.commands;

import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.SpawnProtectionTimer;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PvPCommand {

    private final Optional<SpawnProtectionTimer> spawnProtectionTimer;

    /**
     * Constructor to make a new pvp command object
     *
     * @param handler the handler to get the modules from
     */
    public PvPCommand(ModuleHandler handler) {
        this.spawnProtectionTimer = handler.find(TimerModule.class).findTimer(SpawnProtectionTimer.class);
    }

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
        if (spawnProtectionTimer.isPresent() && spawnProtectionTimer.get().isOnCooldown(player)) {
            spawnProtectionTimer.get().cancel(player);

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