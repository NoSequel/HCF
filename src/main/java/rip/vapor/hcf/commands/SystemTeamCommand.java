package rip.vapor.hcf.commands;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.ClaimPriority;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SystemTeamCommand implements Controllable<TeamModule> {

    private final TeamModule controller = this.getController();
    private final PlayerDataModule playerDataController = Vapor.getInstance().getHandler().find(PlayerDataModule.class);

    @Command(label = "systemteam", aliases = {"systeam", "steam"})
    @Subcommand(label = "help", parentLabel = "systemteam")
    public void help(Player player) {
        player.sendMessage(new String[]{
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.BLUE + "General Commands:",
                ChatColor.YELLOW + "/systemteam create <name>" + ChatColor.GRAY + " - Create a new system team",
                ChatColor.YELLOW + "/systemteam delete <name>" + ChatColor.GRAY + " - Delete a new system team",
                "",
                ChatColor.BLUE + "Setup Commands:",
                ChatColor.YELLOW + "/systemteam type <name> <type>" + ChatColor.GRAY + " - Set the type of a system team",
                ChatColor.YELLOW + "/systemteam deathban <name>" + ChatColor.GRAY + " - Toggle a team's deathban status",
                ChatColor.YELLOW + "/systemteam color <name> <color>" + ChatColor.GRAY + " - Set the color of a team",
                ChatColor.YELLOW + "/systemteam claimfor <name>" + ChatColor.GRAY + " - Claim for a system team",
                ChatColor.YELLOW + "/systemteam priority <name> <priority>" + ChatColor.GRAY + " - Set the claim priority of a system team",
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
        });
    }

    @Subcommand(label = "create", parentLabel = "systemteam", permission = "admin")
    public void create(Player player, String name) {
        if (controller.findTeam(name) != null) {
            player.sendMessage(ChatColor.RED + "That team already exists!");
            return;
        } else if (name.length() > 16) {
            player.sendMessage(ChatColor.RED + "Maximum team name length is 16 characters!");
            return;
        } else if (name.length() < 3) {
            player.sendMessage(ChatColor.RED + "Minimum team name length is 3 characters!");
            return;
        } else if (!StringUtils.isAlphanumeric(name)) {
            player.sendMessage(ChatColor.RED + "Your team name has to be alphanumeric.");
            return;
        }

        new Team(null, name, TeamType.SYSTEM_TEAM);

        player.sendMessage(ChatColor.GREEN + "Success.");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "System team " + ChatColor.BLUE + name + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created " + ChatColor.YELLOW + "by " + ChatColor.WHITE + player.getName());
    }

    @Subcommand(label = "delete", parentLabel = "systemteam", permission = "admin")
    public void delete(Player player, Team team) {
        team.disband();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "System team " + ChatColor.BLUE + team.getFormattedName() + ChatColor.YELLOW + " has been " + ChatColor.RED + "deleted " + ChatColor.YELLOW + "by " + ChatColor.WHITE + player.getName());
    }

    @Subcommand(label = "type", parentLabel = "systemteam", permission = "admin")
    public void type(Player player, Team team, String type) {
        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            player.sendMessage(ChatColor.RED + "That team is not a system team.");
            return;
        }

        if (Arrays.stream(TeamType.values()).noneMatch($type -> $type.name().equals(type.toUpperCase()))) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Type by '" + type + "' not found.");
            player.sendMessage(ChatColor.RED + "Choose between: " + Arrays.stream(TeamType.values()).map(TeamType::name).collect(Collectors.joining(", ")));
            return;
        }

        team.getGeneralData().setType(TeamType.valueOf(type.toUpperCase()));
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Type of " + ChatColor.BLUE + team.getDisplayName(player) + ChatColor.YELLOW + " has been changed to " + ChatColor.WHITE + type.toUpperCase());
    }

    @Subcommand(label = "color", parentLabel = "systemteam", permission = "admin")
    public void color(Player player, Team team, String color) {
        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            player.sendMessage(ChatColor.RED + "That team is not a system team.");
            return;
        }

        if (Arrays.stream(ChatColor.values()).noneMatch($type -> $type.name().equals(color.toUpperCase()))) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Color by '" + color + "' not found.");
            player.sendMessage(ChatColor.RED + "Choose between: " + Arrays.stream(ChatColor.values()).map(ChatColor::name).collect(Collectors.joining(", ")));
            return;
        }

        team.getGeneralData().setColor(ChatColor.valueOf(color.toUpperCase()));
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Color of " + ChatColor.BLUE + team.getDisplayName(player) + ChatColor.YELLOW + " has been changed to " + ChatColor.WHITE + color.toUpperCase());
    }

    @Subcommand(label = "claimfor", parentLabel = "systemteam", permission = "admin")
    public void claimfor(Player player, Team team) {
        final PlayerData playerData = this.playerDataController.findPlayerData(player.getUniqueId());

        playerData.addData(new ClaimSelectionData(new ClaimSelection(team)));
        player.sendMessage(new String[]{
                "",
                ChatColor.GREEN + ChatColor.BOLD.toString() + "You are currently claiming for " + team.getFormattedName() + ",",
                ChatColor.GRAY + "Click " + Action.RIGHT_CLICK_BLOCK.name() + " for the first position",
                ChatColor.GRAY + "Click " + Action.LEFT_CLICK_BLOCK.name() + " for the second position",
                ChatColor.YELLOW + "To finish your claiming, sneak while you press " + Action.LEFT_CLICK_AIR.name(),
                ChatColor.YELLOW + "To cancel claiming, sneak while you press " + Action.RIGHT_CLICK_AIR.name(),
                ""
        });
    }

    @Subcommand(label = "priority", parentLabel = "systemteam", permission = "admin")
    public void priority(Player player, Team team, String priority) {
        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            player.sendMessage(ChatColor.RED + "That team is not a system team.");
            return;
        }

        if (Arrays.stream(ClaimPriority.values()).noneMatch($type -> $type.name().equals(priority.toUpperCase()))) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Priority by '" + priority + "' not found.");
            player.sendMessage(ChatColor.RED + "Choose between: " + Arrays.stream(ClaimPriority.values()).map(ClaimPriority::name).collect(Collectors.joining(", ")));
            return;
        }

        team.findData(ClaimTeamData.class).getClaim().setPriority(ClaimPriority.valueOf(priority.toUpperCase()));
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Priority of " + ChatColor.BLUE + team.getDisplayName(player) + ChatColor.YELLOW + " has been changed to " + ChatColor.WHITE + priority.toUpperCase());
    }

    @Subcommand(label = "deathban", parentLabel = "systemteam", permission = "admin")
    public void deathban(Player player, Team team) {
        final ClaimTeamData claimTeamData = team.findData(ClaimTeamData.class);
        final Claim claim = claimTeamData.getClaim();

        claim.setDeathban(!claim.isDeathban());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Deathban status of " + ChatColor.BLUE + team.getDisplayName(player) + ChatColor.YELLOW + " has been changed to " + ChatColor.WHITE + (claim.isDeathban() ? "deathban" : "non-deathban"));
    }

    @Subcommand(label="list", parentLabel = "systemteam")
    public void list(Player player) {
        this.controller.getTeams().stream()
                .filter(team -> team.getGeneralData().getType().equals(TeamType.SYSTEM_TEAM))
                .map(team -> team.getDisplayName(player) + ChatColor.GRAY + " - " + (team.hasData(ClaimTeamData.class) ? team.findData(ClaimTeamData.class).getClaim().getPriority().name() : "None"))
                .forEach(player::sendMessage);
    }

}