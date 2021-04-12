package rip.vapor.hcf.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.BalanceData;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Parameter;
import rip.vapor.hcf.util.command.annotation.Subcommand;

public class EcoCommand {

    private final PlayerDataModule playerDataModule;

    /**
     * Constructor to make a new eco command instance
     *
     * @param handler the handler to get the modules from
     */
    public EcoCommand(ModuleHandler handler) {
        this.playerDataModule = handler.find(PlayerDataModule.class);
    }

    @Command(label = "eco", aliases = {"balance", "bal"})
    public void eco(Player player, @Parameter(name = "target", value = "@SELF") Player target) {
        final PlayerData playerData = this.playerDataModule.findPlayerData(target.getUniqueId());
        final BalanceData balanceData = playerData.findData(BalanceData.class);

        if (balanceData != null) {
            player.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.LIGHT_PURPLE + "$" + balanceData.getBalance());
        } else {
            player.sendMessage(ChatColor.RED + "Error!");
        }
    }

    @Subcommand(label = "set", parentLabel = "eco", permission = "hcteams.eco")
    public void setEco(Player player, Player target, Integer amount) {
        final PlayerData playerData = this.playerDataModule.findPlayerData(target.getUniqueId());
        final BalanceData balanceData = playerData.findData(BalanceData.class);

        if (balanceData != null) {
            balanceData.setBalance(amount);
            player.sendMessage(ChatColor.YELLOW + "You have updated " + ChatColor.LIGHT_PURPLE + ChatColor.YELLOW + "'s economy");
        } else {
            player.sendMessage(ChatColor.RED + "Error!");
        }
    }
}