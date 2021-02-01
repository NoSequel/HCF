package rip.vapor.hcf.commands;

import com.google.common.collect.ImmutableMap;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.controller.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataController;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerRole;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.data.impl.player.invites.InviteTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.timers.impl.CombatTimer;
import rip.vapor.hcf.timers.impl.EnderpearlTimer;
import rip.vapor.hcf.timers.impl.TeleportTimer;
import rip.vapor.hcf.util.NumberUtil;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Parameter;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TeamCommand implements Controllable<TeamController> {

    private final TeamController controller = this.getController();
    private final PlayerDataController playerDataController = Vapor.getInstance().getHandler().find(PlayerDataController.class);
    private final TimerController timerController = Vapor.getInstance().getHandler().find(TimerController.class);

    @Command(label = "faction", aliases = {"f", "team", "t"})
    @Subcommand(label = "help", parentLabel = "faction")
    public void help(Player player) {
        controller.getTeams().stream()
                .map(team -> String.join(", ", new String[]{team.getFormattedName(), team.getData().stream().map(data -> data.getClass().getSimpleName()).collect(Collectors.joining(", "))}))
                .forEach(System.out::println);

        player.sendMessage(new String[]{
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.BLUE + ChatColor.BOLD.toString() + "General Faction Help",
                ChatColor.YELLOW + ChatColor.ITALIC.toString() + "General command for helping with faction commands",
                "",
                ChatColor.BLUE + "General Commands: ",
                ChatColor.YELLOW + "/team help" + ChatColor.GRAY + " - Shows you this page",
                ChatColor.YELLOW + "/team create <name> [acronym]" + ChatColor.GRAY + " - Create a new team",
                ChatColor.YELLOW + "/team disband" + ChatColor.GRAY + " - Disband your current team",
                ChatColor.YELLOW + "/team invite <target>" + ChatColor.GRAY + " - Invite someone to your team",
                ChatColor.YELLOW + "/team accept <team>" + ChatColor.GRAY + " - Accept an invite",
                "",
                ChatColor.BLUE + "Captain Commands: ",
                ChatColor.YELLOW + "/team rename <newName>" + ChatColor.GRAY + " - Rename your team's name",
                ChatColor.YELLOW + "/team sethome" + ChatColor.GRAY + " - Set the team's HQ",
                ChatColor.YELLOW + "/team home" + ChatColor.GRAY + " - Teleport to the team's HQ",
                "",
                ChatColor.BLUE + "Leader Commands: ",
                ChatColor.YELLOW + "/team promote <player>" + ChatColor.GRAY + " - Promote a player to a higher role",
                ChatColor.YELLOW + "/team demote <player>" + ChatColor.GRAY + " - Demote a player to a lower role",
                ChatColor.YELLOW + "/team leader <player>" + ChatColor.GRAY + " - Transfer leadership to someone else",
                ChatColor.YELLOW + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52)
        });
    }

    @Subcommand(label = "create", parentLabel = "faction")
    public void create(Player player, @Parameter(name = "teamName") String teamName) {
        if (controller.findTeam(teamName) != null) {
            player.sendMessage(ChatColor.RED + "That team already exists!");
            return;
        }

        if (teamName.length() > 16) {
            player.sendMessage(ChatColor.RED + "Maximum team name length is 16 characters!");
            return;
        } else if (teamName.length() < 3) {
            player.sendMessage(ChatColor.RED + "Minimum team name length is 3 characters!");
            return;
        } else if (!StringUtils.isAlphanumeric(teamName)) {
            player.sendMessage(ChatColor.RED + "Your team name has to be alphanumeric.");
            return;
        } else if (controller.findTeam(player) != null) {
            player.sendMessage(ChatColor.RED + "You are already in a team!");
            return;
        }


        final Team team = new Team(null, teamName, player.getUniqueId());
        final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);

        player.sendMessage(ChatColor.GREEN + "Success.");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + teamName + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created " + ChatColor.YELLOW + "by " + ChatColor.WHITE + player.getName());
    }

    @Subcommand(label = "disband", parentLabel = "faction")
    public void disband(Player player) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Team team = controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        Bukkit.broadcastMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + team.getGeneralData().getName() + ChatColor.YELLOW + " has been " + ChatColor.RED + "disbanded" + ChatColor.YELLOW + " by " + ChatColor.WHITE + player.getName());

        if (data != null) {
            data.broadcast(ChatColor.GRAY + "The team you were previously in has been disbanded.");
        }

        team.disband();
    }

    @Subcommand(label = "rename", parentLabel = "faction")
    public void rename(Player player, @Parameter(name = "new name") String name) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Team team = controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        if (name.length() > 16) {
            player.sendMessage(ChatColor.RED + "Maximum team name length is 16 characters!");
            return;
        } else if (name.length() < 3) {
            player.sendMessage(ChatColor.RED + "Minimum team name length is 3 characters!");
            return;
        } else if (!StringUtils.isAlphanumeric(name)) {
            player.sendMessage(ChatColor.RED + "Your team name has to be alphanumeric.");
            return;
        } else if (controller.findTeam(name) != null) {
            player.sendMessage(ChatColor.RED + "That name is already taken.");
            return;
        }

        team.getGeneralData().setName(name);
        data.broadcast(ChatColor.GRAY + "Your team's has been renamed to " + ChatColor.WHITE + name);
    }

    @Subcommand(label = "show", aliases = {"info", "who", "i"}, parentLabel = "faction")
    public void show(Player player, @Parameter(name = "team", value = "@SELF") Team team) {
        if (team == null) {
            player.sendMessage(ChatColor.RED + "That team does not exist.");
            return;
        }

        final Date currentDate = new Date(team.getGeneralData().getCreateTime());
        final ClaimTeamData claimTeamData = team.findData(ClaimTeamData.class);

        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            final PlayerTeamData data = team.findData(PlayerTeamData.class);

            final OfflinePlayer leader = Bukkit.getOfflinePlayer(data.getLeader());

            final String captains = data.getCaptains().stream().map(Bukkit::getOfflinePlayer).filter(Objects::nonNull).map(target -> (target.getPlayer() == null ? ChatColor.GRAY.toString() : ChatColor.GREEN.toString()) + target.getName() + (target.getPlayer() == null ? "" : ChatColor.YELLOW + "[" + ChatColor.GREEN + target.getPlayer().getStatistic(Statistic.PLAYER_KILLS) + ChatColor.YELLOW + "]")).collect(Collectors.joining(ChatColor.YELLOW + ", "));
            final String members = data.getMembers().stream().map(Bukkit::getOfflinePlayer).filter(Objects::nonNull).map(target -> (target.getPlayer() == null ? ChatColor.GRAY.toString() : ChatColor.GREEN.toString()) + target.getName() + (target.getPlayer() == null ? "" : ChatColor.YELLOW + "[" + ChatColor.GREEN + target.getPlayer().getStatistic(Statistic.PLAYER_KILLS) + ChatColor.YELLOW + "]")).collect(Collectors.joining(ChatColor.YELLOW + ", "));
            final String coLeaders = data.getCoLeaders().stream().map(Bukkit::getOfflinePlayer).filter(Objects::nonNull).map(target -> (target.getPlayer() == null ? ChatColor.GRAY.toString() : ChatColor.GREEN.toString()) + target.getName() + (target.getPlayer() == null ? "" : ChatColor.YELLOW + "[" + ChatColor.GREEN + target.getPlayer().getStatistic(Statistic.PLAYER_KILLS) + ChatColor.YELLOW + "]")).collect(Collectors.joining(ChatColor.YELLOW + ", "));

            final List<String> messages = new ArrayList<>(Arrays.asList(
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                    ChatColor.BLUE + team.getGeneralData().getName() + ChatColor.GRAY + "[" + data.getOnlineMembers().size() + "/" + data.getAllMembers().size() + "]",
                    "",

                    ChatColor.YELLOW + "Leader: " + (leader.getPlayer() == null ? ChatColor.GRAY : ChatColor.GREEN) + leader.getName() + (leader.getPlayer() == null ? "" : ChatColor.YELLOW + "[" + ChatColor.GREEN + player.getPlayer().getStatistic(Statistic.PLAYER_KILLS) + ChatColor.YELLOW + "]")
            ));

            ImmutableMap.of(
                    ChatColor.YELLOW + "Co Leaders: ", coLeaders,
                    ChatColor.YELLOW + "Captains: ", captains,
                    ChatColor.YELLOW + "Members: ", members
            ).entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> messages.add(entry.getKey() + entry.getValue()));

            final DTRData dtrData = team.findData(DTRData.class);

            messages.addAll(Arrays.asList(
                    ChatColor.YELLOW + "Balance: " + ChatColor.RED + "$" + data.getBalance(),
                    ChatColor.YELLOW + "DTR: " + dtrData.formatDtr() + ChatColor.GRAY + " (" + NumberUtil.round(dtrData.getMaxDtr(), 1) + ")",
                    ChatColor.YELLOW + "Claim: " + ChatColor.RED + (claimTeamData != null ? claimTeamData.getClaim().getCuboid().getChunks() : "0") + " chunks" + ChatColor.YELLOW + ", " + "Home: " + ChatColor.RED + (claimTeamData == null ? "Not Set" : claimTeamData.getHomeAsString()),
                    "",
                    ChatColor.GRAY + ChatColor.ITALIC.toString() + "Founded on " + new SimpleDateFormat("MM/dd/yyyy").format(currentDate) + " at " + new SimpleDateFormat("hh:mm:ss").format(currentDate),
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52)
            ));

            messages.forEach(player::sendMessage);
        } else {
            if (claimTeamData != null) {
                final Claim claim = claimTeamData.getClaim();

                player.sendMessage(new String[]{
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                        ChatColor.BLUE + team.getFormattedName() + ChatColor.YELLOW + "(" + (claim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")",
                        "",
                        ChatColor.YELLOW + "Claim: " + ChatColor.RED + claim.getCuboid().toXYZ(),
                });
            } else {
                player.sendMessage(new String[]{
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                        ChatColor.BLUE + team.getFormattedName() + ChatColor.YELLOW + "(" + ChatColor.RED + "Deathban" + ChatColor.YELLOW + ")",
                        ""
                });
            }

            player.sendMessage(new String[]{
                    ChatColor.YELLOW + "Color: " + team.getGeneralData().getColor() + team.getGeneralData().getColor().name(),
                    ChatColor.YELLOW + "Type: " + ChatColor.WHITE + team.getGeneralData().getType().name(),
                    "",
                    ChatColor.GRAY + ChatColor.ITALIC.toString() + "Founded on " + new SimpleDateFormat("MM/dd/yyyy").format(currentDate) + " at " + new SimpleDateFormat("hh:mm:ss").format(currentDate),
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
            });


        }
    }

    @Subcommand(label = "sethome", parentLabel = "faction")
    public void sethome(Player player) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Team team = controller.findTeam(player);
        final ClaimTeamData data = team.findData(ClaimTeamData.class);

        if (data == null) {
            player.sendMessage(ChatColor.RED + "Your team doesn't have a claim yet.");
            return;
        } else if (!data.getClaim().getCuboid().isLocationInCuboid(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You can only set the team's home in your own claim.");
            return;
        }

        final Location location = player.getLocation();
        final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);

        data.setHome(location);

        if (playerTeamData != null) {
            playerTeamData.broadcast(ChatColor.GRAY + "The team's HQ has been set at (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        }
    }

    @Subcommand(label = "home", parentLabel = "faction")
    public void home(Player player) {
        final Team team = controller.findTeam(player);

        if (team != null && team.findData(ClaimTeamData.class) != null) {
            final ClaimTeamData data = team.findData(ClaimTeamData.class);

            if (data.getHome() == null) {
                player.sendMessage(ChatColor.RED + "Your team doesn't have a home set, set it with /team sethome.");
                return;
            }

            final CombatTimer combatTimer = this.timerController.findTimer(CombatTimer.class);
            final EnderpearlTimer enderpearlTimer = this.timerController.findTimer(EnderpearlTimer.class);

            if (enderpearlTimer.isOnCooldown(player)) {
                player.sendMessage(ChatColor.RED + "You are still under an enderpearl cooldown.");
                return;
            } else if (combatTimer.isOnCooldown(player)) {
                player.sendMessage(ChatColor.RED + "You are currently in combat");
                return;
            }

            this.timerController.findTimer(TeleportTimer.class).start(player);
        }
    }

    @Subcommand(label = "claim", parentLabel = "faction")
    public void claim(Player player) {
        if (!shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final PlayerData playerData = this.playerDataController.findPlayerData(player.getUniqueId());
        final Team team = this.controller.findTeam(player);

        if (playerData.hasData(ClaimSelectionData.class)) {
            player.sendMessage(ChatColor.RED + "You are already claiming.");
            return;
        }

        playerData.addData(new ClaimSelectionData(new ClaimSelection(team)));
        player.sendMessage(new String[]{
                "",
                ChatColor.GREEN + ChatColor.BOLD.toString() + "You are currently claiming for your own faction,",
                ChatColor.GRAY + "* Click " + Action.RIGHT_CLICK_BLOCK.name() + " for the first position",
                ChatColor.GRAY + "* Click " + Action.LEFT_CLICK_BLOCK.name() + " for the second position",
                ChatColor.YELLOW + "To finish your claiming, sneak while you press " + Action.LEFT_CLICK_AIR.name(),
                ChatColor.YELLOW + "To cancel claiming, sneak while you press " + Action.RIGHT_CLICK_AIR.name(),
                ""
        });

        if (team.hasData(ClaimTeamData.class) && team.findData(ClaimTeamData.class).getHome() != null) {
            player.sendMessage(ChatColor.RED + "Note: " + ChatColor.YELLOW + "Your team's HQ will be removed if you make a new claim.");
        }
    }

    @Subcommand(label = "invite", parentLabel = "faction")
    public void invite(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Team team = this.controller.findTeam(player);
        final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);

        if (playerTeamData != null && !playerTeamData.contains(target)) {
            final InviteTeamData inviteTeamData = team.findData(InviteTeamData.class);

            if (inviteTeamData.hasInvite(target)) {
                player.sendMessage(ChatColor.RED + "That player has already been invited to that team.");
                return;
            }

            inviteTeamData.invite(target);
            player.sendMessage(ChatColor.GRAY + "You have invited " + target.getName() + " to your team.");
            target.sendMessage(new String[]{
                    ChatColor.GRAY + "You have been invited to join " + ChatColor.WHITE + team.getGeneralData().getName(),
                    ChatColor.GRAY + "Type /team accept " + team.getGeneralData().getName() + " to accept the invite."
            });
        }
    }

    @Subcommand(label = "accept", aliases = "join", parentLabel = "faction")
    public void accept(Player player, Team team) {
        final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);
        final InviteTeamData inviteTeamData = team.findData(InviteTeamData.class);

        if (playerTeamData == null || inviteTeamData == null) {
            player.sendMessage(ChatColor.RED + "That team is not joinable.");
        } else {
            if (!inviteTeamData.hasInvite(player)) {
                player.sendMessage(ChatColor.RED + "That team has not invited you.");
                return;
            } else if (playerTeamData.contains(player)) {
                player.sendMessage(ChatColor.RED + "You are already in that team.");
                return;
            }

            playerTeamData.join(player);
            playerTeamData.broadcast(ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has joined your team.");
        }
    }

    @Subcommand(label = "promote", parentLabel = "faction")
    public void promote(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Team team = this.controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        if (data == null) {
            player.sendMessage(ChatColor.RED + "That command is not executable in this team.");
            return;
        } else if (!data.contains(target)) {
            player.sendMessage(ChatColor.RED + "That player is not in your team.");
            return;
        } else if (player.equals(target)) {
            player.sendMessage(ChatColor.RED + "You can't promote yourself.");
            return;
        } else if (data.getRole(target.getUniqueId()).priority >= PlayerRole.CO_LEADER.priority) {
            player.sendMessage(ChatColor.GRAY + "To transfer leadership, use /team leader <player>");
            return;
        }

        data.promotePlayer(target.getUniqueId());
        data.broadcast(ChatColor.WHITE + target.getName() + ChatColor.GRAY + " has been promoted to " + ChatColor.WHITE + data.getRole(target.getUniqueId()).name());
    }

    @Subcommand(label = "demote", parentLabel = "faction")
    public void demote(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Team team = this.controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        if (data == null) {
            player.sendMessage(ChatColor.RED + "That command is not executable in this team.");
            return;
        } else if (!data.contains(target)) {
            player.sendMessage(ChatColor.RED + "That player is not in your team.");
            return;
        } else if (player.equals(target)) {
            player.sendMessage(ChatColor.RED + "You can't demote yourself.");
            return;
        } else if (data.getRole(target.getUniqueId()).equals(PlayerRole.MEMBER)) {
            player.sendMessage(ChatColor.GRAY + "To kick a player, use /team kick <player>");
            return;
        }


        data.demotePlayer(target.getUniqueId());
        data.broadcast(ChatColor.WHITE + target.getName() + ChatColor.GRAY + " has been demoted to " + ChatColor.WHITE + data.getRole(target.getUniqueId()).name());
    }

    @Subcommand(label = "leader", parentLabel = "faction")
    public void leader(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Team team = this.controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        if (data == null) {
            player.sendMessage(ChatColor.RED + "That command is not executable in this team.");
            return;
        } else if (!data.contains(target)) {
            player.sendMessage(ChatColor.RED + "That player is not in your team.");
            return;
        } else if (player.equals(target)) {
            player.sendMessage(ChatColor.RED + "You can't transfer ownership to yourself.");
            return;
        }

        data.setLeader(target.getUniqueId());
        data.getCoLeaders().add(player.getUniqueId());
        data.broadcast(ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has transferred the team's ownership to " + ChatColor.WHITE + target.getName());
    }

    @Subcommand(label = "kick", parentLabel = "faction")
    public void kick(Player player, OfflinePlayer target) {
        if (!this.shouldProceed(player, PlayerRole.CO_LEADER)) {
            return;
        }

        final Team team = this.controller.findTeam(player);
        final PlayerTeamData data = team.findData(PlayerTeamData.class);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player does not exist.");
            return;
        }

        if (data != null) {
            if (!data.contains(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "That player is not in your team.");
                return;
            } else if (data.getRole(target.getUniqueId()).priority > data.getRole(player.getUniqueId()).priority) {
                player.sendMessage(ChatColor.RED + "That player has a higher role than you.");
                return;
            }

            data.kick(target.getUniqueId());
            data.broadcast(ChatColor.DARK_GREEN + target.getName() + ChatColor.YELLOW + " has been kicked from the team.");
        }
    }

    @Subcommand(label = "leave", parentLabel = "faction")
    public void leave(Player player) {
        final Team team = this.controller.findTeam(player);

        if (team != null) {
            final PlayerTeamData data = team.findData(PlayerTeamData.class);

            if (data.getRole(player.getUniqueId()).equals(PlayerRole.LEADER)) {
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot leave your own team...");
                player.sendMessage(ChatColor.RED + "Disband your team or transfer your team's leadership.");

                return;
            }

            data.kick(player.getUniqueId());
            data.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " has left the team.");
        }
    }

    /**
     * Check whether the command should proceed the execution
     *
     * @param player       the player
     * @param requiredRole the role which is required to perform the command
     * @return whether it should proceed
     */
    private boolean shouldProceed(Player player, PlayerRole requiredRole) {
        final Team team = controller.findTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }

        if (requiredRole.isHigher(controller.findTeam(player).findData(PlayerTeamData.class).getRole(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return false;
        }

        return true;
    }
}