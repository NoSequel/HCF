package rip.vapor.hcf.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.KothTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import rip.vapor.hcf.util.database.DatabaseModule;

public class KothCommand implements Controllable<TeamModule> {

    private final TeamModule teamModule = this.getModule();
    private final PlayerDataModule playerDataModule = Vapor.getInstance().getHandler().find(PlayerDataModule.class);
    private final DatabaseModule databaseModule = Vapor.getInstance().getHandler().find(DatabaseModule.class);

    @Command(label = "koth", permission = "hcteams.koth")
    public void execute(Player player) {
        player.sendMessage(new String[]{
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.BLUE + "Activity Commands",
                ChatColor.YELLOW + "/koth start <koth>" + ChatColor.GRAY + " - Start a koth",
                ChatColor.YELLOW + "/koth end <koth>" + ChatColor.GRAY + " - End a koth",
                ChatColor.YELLOW + "/koth list" + ChatColor.GRAY + " - List all available koths",
                "",
                ChatColor.BLUE + "Management Commands",
                ChatColor.YELLOW + "/koth create <name>" + ChatColor.GRAY + " - Create a new koth",
                ChatColor.YELLOW + "/koth delete <name>" + ChatColor.GRAY + " - Delete an existing new koth",
                ChatColor.YELLOW + "/koth setcapzone <name>" + ChatColor.GRAY + " - Set the capzone of an existing koth",
                ChatColor.YELLOW + "/koth setclaim <name>" + ChatColor.GRAY + " - Set the claim of an existing koth",
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
        });
    }

    @Subcommand(label = "start", parentLabel = "koth", permission = "hcteams.koth")
    public void start(Player player, Koth koth) {
        if (!koth.isRunning()) {
            koth.setRunning(true);
            koth.tickCap();
            Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] " + ChatColor.DARK_PURPLE + koth.getKothName() + ChatColor.YELLOW + " is now contestable.");
        } else {
            player.sendMessage(ChatColor.RED + "That KoTH is already running.");
        }
    }

    @Subcommand(label = "end", aliases = {"stop"}, parentLabel = "koth", permission = "hcteams.koth")
    public void end(Player player, Koth koth) {
        if (!koth.isRunning()) {
            player.sendMessage(ChatColor.RED + "That KoTH is currently not running.");
        } else {
            koth.setRunning(false);
            koth.setCappingUuid(null);
            koth.tickCap();

            Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] " + ChatColor.DARK_PURPLE + koth.getKothName() + ChatColor.YELLOW + " is no longer contestable.");
        }
    }

    @Subcommand(label = "list", parentLabel = "koth", permission = "hcteams.koth")
    public void list(Player player) {
        this.teamModule.getTeams().stream()
                .filter(team -> team.getGeneralData().getType().equals(TeamType.KOTH_TEAM) && team.hasData(KothTeamData.class))
                .forEach(team -> player.sendMessage(team.getDisplayName(player)));
    }

    @Subcommand(label = "create", parentLabel = "koth", permission = "hcteams.koth.manage")
    public void create(Player player, String name) {
        if (this.getModule().findTeam(name).isPresent()) {
            player.sendMessage(ChatColor.RED + "A team with that name already exists.");
            return;
        }

        new Koth(name, 30 * 1000);
        player.sendMessage(ChatColor.YELLOW + "You have created the " + name + " KoTH.");
    }

    @Subcommand(label = "delete", parentLabel = "koth", permission = "hcteams.koth.manage")
    public void delete(Player player, Koth koth) {
        this.teamModule.getTeams().remove(koth.getKothTeam());
        this.databaseModule.getDataHandler().delete(koth.getKothTeam(), "teams");

        player.sendMessage(ChatColor.YELLOW + "Successfully deleted a koth");
    }

    @Subcommand(label = "setcapzone", parentLabel = "koth", permission = "hcteams.koth.manage")
    public void setCapZone(Player player, Koth koth) {
        final PlayerData playerData = this.playerDataModule.findPlayerData(player.getUniqueId());

        playerData.addData(new ClaimSelectionData(new ClaimSelection(koth.getKothTeam(), true)).startClaim(player));
    }
}