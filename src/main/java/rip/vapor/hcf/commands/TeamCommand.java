package rip.vapor.hcf.commands;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.BalanceData;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerRole;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.data.impl.player.invites.InviteTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.player.CombatTimer;
import rip.vapor.hcf.player.timers.impl.player.EnderpearlTimer;
import rip.vapor.hcf.player.timers.impl.player.TeleportTimer;
import rip.vapor.hcf.team.menu.GeneralTeamMenu;
import rip.vapor.hcf.util.NumberUtil;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Parameter;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TeamCommand {
    
    private final TeamModule teamModule;
    private final PlayerDataModule playerDataModule;
    private final TimerModule timerModule;

    /**
     * Constructor to make a new team command instance
     *
     * @param handler the handler to get the modules from
     */
    public TeamCommand(ModuleHandler handler) {
        this.teamModule = handler.find(TeamModule.class);
        this.playerDataModule = handler.find(PlayerDataModule.class);
        this.timerModule = handler.find(TimerModule.class);
    }

    @Command(label = "faction", aliases = {"f", "team", "t"})
    public void execute(Player player) {
        if(teamModule.findTeam(player).isPresent()) {
            new GeneralTeamMenu(player, teamModule.findTeam(player).get()).updateMenu();
        } else {
            this.help(player);
        }
    }

    @Subcommand(label = "help", parentLabel = "faction")
    public void help(Player player) {
        player.sendMessage(new String[]{
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                ChatColor.BLUE + "General Commands: ",
                ChatColor.YELLOW + "/team help" + ChatColor.GRAY + " - Shows you this page",
                ChatColor.YELLOW + "/team create <name>" + ChatColor.GRAY + " - Create a new team",
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
                ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52)
        });
    }


    @Subcommand(label = "create", parentLabel = "faction")
    public void create(Player player, @Parameter(name = "teamName") String teamName) {
        if (teamModule.findTeam(teamName).isPresent()) {
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
        } else if (teamModule.findTeam(player).isPresent()) {
            player.sendMessage(ChatColor.RED + "You are already in a team!");
            return;
        }


        new Team(null, teamName, player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "Success.");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + teamName + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created " + ChatColor.YELLOW + "by " + ChatColor.WHITE + player.getName());
    }

    @Subcommand(label = "disband", parentLabel = "faction")
    public void disband(Player player) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Optional<Team> team = teamModule.findTeam(player);

        if (team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

            Bukkit.broadcastMessage(ChatColor.YELLOW + "Team " +
                    ChatColor.BLUE + team.get().getGeneralData().getName() +
                    ChatColor.YELLOW + " has been " +
                    ChatColor.RED + "disbanded" +
                    ChatColor.YELLOW + " by " + ChatColor.WHITE + player.getName());

            if (data != null) {
                data.broadcast(ChatColor.RED + "The team you were previously in has been disbanded.");
            }

            team.get().disband();
        }
    }

    @Subcommand(label = "rename", parentLabel = "faction")
    public void rename(Player player, @Parameter(name = "new name") String name) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Optional<Team> team = teamModule.findTeam(player);

        if(team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

            if (name.length() > 16) {
                player.sendMessage(ChatColor.RED + "Maximum team name length is 16 characters!");
                return;
            } else if (name.length() < 3) {
                player.sendMessage(ChatColor.RED + "Minimum team name length is 3 characters!");
                return;
            } else if (!StringUtils.isAlphanumeric(name)) {
                player.sendMessage(ChatColor.RED + "Your team name has to be alphanumeric.");
                return;
            } else if (teamModule.findTeam(name).isPresent()) {
                player.sendMessage(ChatColor.RED + "That name is already taken.");
                return;
            }

            team.get().getGeneralData().setName(name);
            data.broadcast(ChatColor.YELLOW + "Your team's has been renamed to " + ChatColor.LIGHT_PURPLE + name);
        }
    }

    @Subcommand(label = "show", aliases = {"info", "who", "i"}, parentLabel = "faction")
    public void show(Player player, @Parameter(name = "team", value = "@SELF") Team team) {
        if (team == null) {
            player.sendMessage(ChatColor.RED + "That team does not exist.");
            return;
        }

        final Date currentDate = new Date(team.getGeneralData().getCreateTime());
        final ClaimTeamData claimTeamData = team.findData(ClaimTeamData.class);

        player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52));

        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
            final PlayerTeamData data = team.findData(PlayerTeamData.class);
            final DTRData dtrData = team.findData(DTRData.class);

            final String members = data.getMembers().stream().map(this::formatPlayer).collect(Collectors.joining(ChatColor.YELLOW + ", "));
            final String captains = data.getCaptains().stream().map(this::formatPlayer).collect(Collectors.joining(ChatColor.YELLOW + ", "));
            final String coLeaders = data.getCoLeaders().stream().map(this::formatPlayer).collect(Collectors.joining(ChatColor.YELLOW + ", "));

            player.sendMessage(new String[]{
                    ChatColor.BLUE + team.getGeneralData().getName() + ChatColor.GRAY + "[" + data.getOnlineMembers().size() + "/" + data.getAllMembers().size() + "]",
                    ChatColor.YELLOW + "Leader: " + this.formatPlayer(data.getLeader())
            });

            if (!coLeaders.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "Co Leaders: " + coLeaders);
            }

            if (!captains.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "Captains: " + captains);
            }

            if (!members.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "Members: " + members);
            }


            player.sendMessage(new String[]{
                    ChatColor.YELLOW + "Balance: " + ChatColor.RED + "$" + data.getBalance(),
                    ChatColor.YELLOW + "DTR: " + dtrData.formatDtr() + ChatColor.GRAY + " (" + NumberUtil.round(dtrData.getMaxDtr(), 1) + ")",
                    ChatColor.YELLOW + "Claim: " + ChatColor.RED + (claimTeamData != null ? claimTeamData.getClaim().getCuboid().getChunks() : "0") + " chunks" + ChatColor.YELLOW + ", " + "Home: " + ChatColor.RED + (claimTeamData == null ? "Not Set" : claimTeamData.getHomeAsString()),
                    ChatColor.YELLOW + "Founded on: " + ChatColor.RED + new SimpleDateFormat("MM/dd/yyyy").format(currentDate) + " at " + new SimpleDateFormat("hh:mm:ss").format(currentDate),
            });

        } else {
            if (claimTeamData != null) {
                final Claim claim = claimTeamData.getClaim();

                player.sendMessage(new String[]{
                        ChatColor.BLUE + team.getFormattedName() + ChatColor.YELLOW + "(" + (claim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")",
                        ChatColor.YELLOW + "Claim: " + ChatColor.RED + claim.getCuboid().toXYZ(),
                });
            } else {
                player.sendMessage(ChatColor.BLUE + team.getFormattedName() + ChatColor.YELLOW + "(" + ChatColor.RED + "Deathban" + ChatColor.YELLOW + ")");
            }

            player.sendMessage(new String[]{
                    ChatColor.YELLOW + "Color: " + team.getGeneralData().getColor() + team.getGeneralData().getColor().name(),
                    ChatColor.YELLOW + "Type: " + ChatColor.WHITE + team.getGeneralData().getType().name(),
                    ChatColor.YELLOW + "Founded on: " + ChatColor.WHITE + new SimpleDateFormat("MM/dd/yyyy").format(currentDate) + " at " + new SimpleDateFormat("hh:mm:ss").format(currentDate),
            });
        }

        player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52));
    }

    @Subcommand(label = "sethome", parentLabel = "faction")
    public void sethome(Player player) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Optional<Team> team = teamModule.findTeam(player);

        if (team.isPresent()) {
            final ClaimTeamData data = team.get().findData(ClaimTeamData.class);

            if (data == null) {
                player.sendMessage(ChatColor.RED + "Your team doesn't have a claim yet.");
                return;
            } else if (!data.getClaim().getCuboid().isLocationInCuboid(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can only set the team's home in your own claim.");
                return;
            }

            final Location location = player.getLocation();
            final PlayerTeamData playerTeamData = team.get().findData(PlayerTeamData.class);

            data.setHome(location);

            if (playerTeamData != null) {
                playerTeamData.broadcast(ChatColor.YELLOW + "The team's HQ has been set at " + ChatColor.LIGHT_PURPLE + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            }
        }
    }

    @Subcommand(label = "home", parentLabel = "faction")
    public void home(Player player) {
        final Optional<Team> team = teamModule.findTeam(player);

        if (team.isPresent()) {
            final ClaimTeamData data = team.get().findData(ClaimTeamData.class);

            if (data == null || data.getHome() == null) {
                player.sendMessage(ChatColor.RED + "Your team doesn't have a home set, set it with /team sethome.");
                return;
            }

            final Optional<CombatTimer> combatTimer = this.timerModule.findTimer(CombatTimer.class);
            final Optional<EnderpearlTimer> enderpearlTimer = this.timerModule.findTimer(EnderpearlTimer.class);

            if (enderpearlTimer.isPresent() && enderpearlTimer.get().isOnCooldown(player)) {
                player.sendMessage(ChatColor.RED + "You are still under an enderpearl cooldown.");
                return;
            } else if (combatTimer.isPresent() && combatTimer.get().isOnCooldown(player)) {
                player.sendMessage(ChatColor.RED + "You are currently in combat");
                return;
            }

            this.timerModule.findTimer(TeleportTimer.class).ifPresent(timer -> timer.start(player));
        }
    }

    @Subcommand(label = "claim", parentLabel = "faction")
    public void claim(Player player) {
        if (!shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final PlayerData playerData = this.playerDataModule.findPlayerData(player.getUniqueId());
        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            if (playerData.hasData(ClaimSelectionData.class)) {
                player.sendMessage(ChatColor.RED + "You are already claiming.");
                return;
            }

            playerData.addData(new ClaimSelectionData(new ClaimSelection(team.get(), false)).startClaim(player));
            if (team.get().hasData(ClaimTeamData.class) && team.get().findData(ClaimTeamData.class).getHome() != null) {
                player.sendMessage(ChatColor.RED + "Note: " + ChatColor.YELLOW + "Your team's HQ will be removed if you make a new claim.");
            }
        }
    }

    @Subcommand(label = "invite", parentLabel = "faction")
    public void invite(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.CAPTAIN)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);

        if(team.isPresent()) {
            final PlayerTeamData playerTeamData = team.get().findData(PlayerTeamData.class);

            if (playerTeamData != null && !playerTeamData.contains(target)) {
                final InviteTeamData inviteTeamData = team.get().findData(InviteTeamData.class);

                if (inviteTeamData.hasInvite(target)) {
                    player.sendMessage(ChatColor.RED + "That player has already been invited to that team.");
                    return;
                }

                if(playerTeamData.getAllMembers().size() > VaporConstants.FACTION_SIZE) {
                    player.sendMessage(ChatColor.RED + "That team already has the max amount of members.");
                    return;
                }

                inviteTeamData.invite(target);
                player.sendMessage(ChatColor.GRAY + "You have invited " + target.getName() + " to your team.");
                target.sendMessage(new String[]{
                        ChatColor.YELLOW + "You have been invited to join " + ChatColor.LIGHT_PURPLE + team.get().getGeneralData().getName(),
                        ChatColor.YELLOW + "Type " + ChatColor.LIGHT_PURPLE + "/team accept " + team.get().getGeneralData().getName() + ChatColor.YELLOW + " to accept the invite."
                });
            }
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
            } else if (playerTeamData.getAllMembers().size() > VaporConstants.FACTION_SIZE) {
                player.sendMessage(ChatColor.RED + "That team already has the max amount of members.");
                return;
            }

            playerTeamData.join(player);
            inviteTeamData.removeInvite(player);
            playerTeamData.broadcast(ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has joined your team.");
        }
    }

    @Subcommand(label = "promote", parentLabel = "faction")
    public void promote(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

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
            data.broadcast(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been promoted to " + ChatColor.LIGHT_PURPLE + data.getRole(target.getUniqueId()).name());
        }
    }

    @Subcommand(label = "demote", parentLabel = "faction")
    public void demote(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

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
            data.broadcast(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been demoted to " + ChatColor.LIGHT_PURPLE + data.getRole(target.getUniqueId()).name());
        }
    }

    @Subcommand(label = "leader", parentLabel = "faction")
    public void leader(Player player, Player target) {
        if (!this.shouldProceed(player, PlayerRole.LEADER)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);

        if(team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

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
            data.broadcast(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " has transferred the team's ownership to " + ChatColor.LIGHT_PURPLE + target.getName());
        }
    }

    @Subcommand(label = "kick", parentLabel = "faction")
    public void kick(Player player, OfflinePlayer target) {
        if (!this.shouldProceed(player, PlayerRole.CO_LEADER)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

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
    }

    @Subcommand(label = "leave", parentLabel = "faction")
    public void leave(Player player) {
        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            final PlayerTeamData data = team.get().findData(PlayerTeamData.class);

            if (data.getRole(player.getUniqueId()).equals(PlayerRole.LEADER)) {
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot leave your own team...");
                player.sendMessage(ChatColor.RED + "Disband your team or transfer your team's leadership.");

                return;
            }

            data.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " has left the team.");
            data.kick(player.getUniqueId());
        }
    }

    @Subcommand(label="deposit", aliases = {"d", "dep"}, parentLabel = "faction")
    public void deposit(Player player, @Parameter(name = "amount", value="2147483647") Integer amount) {
        if (!this.shouldProceed(player, PlayerRole.MEMBER)) {
            return;
        }

        final Optional<Team> team = this.teamModule.findTeam(player);
        final PlayerData playerData = this.playerDataModule.findPlayerData(player.getUniqueId());

        if (playerData.hasData(BalanceData.class) && team.isPresent() && team.get().hasData(PlayerTeamData.class)) {
            final BalanceData balanceData = playerData.findData(BalanceData.class);
            final PlayerTeamData playerTeamData = team.get().findData(PlayerTeamData.class);
            final int subtract = Math.min(balanceData.getBalance(), amount);

            balanceData.setBalance(balanceData.getBalance() - subtract);
            playerTeamData.setBalance(playerTeamData.getBalance() + subtract);

            playerTeamData.broadcast(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.LIGHT_PURPLE + subtract + ChatColor.YELLOW + " to the team's balance.");
        }
    }

    @Subcommand(label="withdraw", aliases = {"w", "wit"}, parentLabel = "faction")
    public void withdraw(Player player, @Parameter(name = "amount", value = "999999") Integer amount) {
        final Optional<Team> team = this.teamModule.findTeam(player);
        final PlayerData playerData = this.playerDataModule.findPlayerData(player.getUniqueId());

        if (playerData.hasData(BalanceData.class) && team.isPresent() && team.get().hasData(PlayerTeamData.class)) {
            final BalanceData balanceData = playerData.findData(BalanceData.class);
            final PlayerTeamData playerTeamData = team.get().findData(PlayerTeamData.class);
            final int subtract = Math.min(playerTeamData.getBalance(), amount);

            balanceData.setBalance(balanceData.getBalance() + subtract);
            playerTeamData.setBalance(playerTeamData.getBalance() - subtract);

            playerTeamData.broadcast(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " has withdrew " + ChatColor.LIGHT_PURPLE + subtract + ChatColor.YELLOW + " to the team's balance.");
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
        final Optional<Team> team = teamModule.findTeam(player);

        if (!team.isPresent()) {
            player.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }

        if (requiredRole.isHigher(team.get().findData(PlayerTeamData.class).getRole(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return false;
        }

        return true;
    }

    /**
     * Format a {@link UUID} to a fully formatted string to display in /f show
     *
     * @param uuid the unique identifier of the player
     * @return the formatted string
     */
    private String formatPlayer(UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        return player == null
                ? ChatColor.GRAY + offlinePlayer.getName()
                : ChatColor.GREEN + player.getName() + ChatColor.YELLOW + "[" + ChatColor.GREEN + player.getStatistic(Statistic.PLAYER_KILLS) + ChatColor.YELLOW + "]";
    }
}