package rip.vapor.hcf.team.menu.button;

import io.github.nosequel.menus.button.ButtonBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class TeamInformationButton extends ButtonBuilder {

    /**
     * Constructor to make a new {@link TeamInformationButton} object
     *
     * @param index the index of the button
     * @param team  the team
     */
    public TeamInformationButton(int index, Team team) {
        super(index, Material.BEACON);

        final PlayerTeamData playerData = team.findData(PlayerTeamData.class);
        final ClaimTeamData claimData = team.findData(ClaimTeamData.class);

        this.displayName(ChatColor.AQUA + team.getFormattedName());
        this.action(type -> true);

        if (playerData == null) {
            this.lore(ChatColor.RED + "Unable to load data from", ChatColor.RED + "this team.");
        } else {
            final Date date = new Date(team.getGeneralData().getCreateTime());

            this.lore(
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 24),
                    ChatColor.YELLOW + "Leader: " + ChatColor.RED + Bukkit.getOfflinePlayer(playerData.getLeader()).getName(),
                    ChatColor.YELLOW + "Members: " + playerData.getAllMembers().stream()
                            .filter(uuid -> !playerData.getLeader().equals(uuid))
                            .map(uuid -> (Bukkit.getPlayer(uuid) == null ? ChatColor.RED : ChatColor.GREEN) + Bukkit.getOfflinePlayer(uuid).getName())
                            .collect(Collectors.joining(ChatColor.YELLOW + ", ")),
                    ChatColor.YELLOW + "Slots: " + ChatColor.RED + playerData.getAllMembers().size() + ChatColor.GRAY + ChatColor.BOLD + " ｜ " + ChatColor.RED + VaporConstants.FACTION_SIZE,
                    "",
                    ChatColor.YELLOW + "Claim: " + ChatColor.RED + (claimData == null || claimData.getClaim() == null ? "None" : claimData.getClaim().getCuboid().getChunks() + " chunks."),
                    ChatColor.YELLOW + "Home: " + ChatColor.RED + (claimData == null || claimData.getHome() == null ? ChatColor.RED + "None" : claimData.getHomeAsString()),
                    "",
                    ChatColor.GRAY + "This team was created on " + new SimpleDateFormat("MM/dd/yyyy").format(date) + " at " + new SimpleDateFormat("hh:mm:ss").format(date),
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 24)
            );
        }
    }
}