package rip.vapor.hcf.tablist;

import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import io.github.nosequel.tab.shared.skin.SkinUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.koth.Koth;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.util.StringUtils;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TablistProvider implements TabElementHandler {

    private final TeamModule teamController;

    /**
     * Constructor to make a new tablist provider instance
     *
     * @param handler the handler to get the modules from
     */
    public TablistProvider(ModuleHandler handler) {
        this.teamController = handler.find(TeamModule.class);
    }

    @Override
    public TabElement getElement(Player player) {
        final TabElement element = new TabElement();
        final Optional<Team> team = teamController.findTeam(player);
        final int increment = team.isPresent() ? 8 : 0;

        element.setHeader(null);
        element.setFooter(null);

        element.add(0, increment, ChatColor.AQUA + "Player Info");
        element.add(0, 1 + increment, ChatColor.WHITE + "Kills: " + player.getStatistic(Statistic.PLAYER_KILLS));
        element.add(0, 2 + increment, ChatColor.WHITE + "Deaths: " + player.getStatistic(Statistic.DEATHS));

        element.add(0, 4 + increment, ChatColor.AQUA + "Location");
        element.add(0, 5 + increment, teamController.findTeam(player.getLocation()).get().getDisplayName(player));
        element.add(0, 6 + increment, ChatColor.WHITE + "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") " + ChatColor.GRAY + "[" + getDirection(player) + ']');

        element.add(1, 0, ChatColor.AQUA + ChatColor.BOLD.toString() + "Vapor");

        if (team.isPresent()) {
            final ClaimTeamData claimTeamData = teamController.findData(ClaimTeamData.class);
            final PlayerTeamData playerTeamData = team.get().findData(PlayerTeamData.class);
            final DTRData dtrData = team.get().findData(DTRData.class);

            element.add(0, 0, ChatColor.AQUA + "Home");
            element.add(0, 1, ChatColor.WHITE + (claimTeamData == null || claimTeamData.getHome() == null ? "None" : claimTeamData.getHomeAsString()));

            element.add(0, 3, ChatColor.AQUA + "Faction Info");
            element.add(0, 4, ChatColor.WHITE + "Online: " + playerTeamData.getOnlineMembers().size() + '/' + playerTeamData.getAllMembers().size());
            element.add(0, 5, ChatColor.WHITE + "DTR: " + dtrData.formatDtr());
            element.add(0, 6, ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + playerTeamData.getBalance());

            element.add(1, 2, ChatColor.DARK_GREEN + team.get().getGeneralData().getName());

            final AtomicInteger index = new AtomicInteger(3);

            playerTeamData.getOnlineMembers().stream()
                    .sorted(Comparator.comparingInt(target -> -playerTeamData.getRole(target.getUniqueId()).priority))
                    .forEach(target -> element.add(
                            1, index.getAndIncrement(),
                            ChatColor.GRAY + playerTeamData.getRole(target.getUniqueId()).astrix + ChatColor.DARK_GREEN + target.getName(),
                            -1, SkinUtil.getSkinDataThrown(target.getUniqueId())
                    ));
        }

        element.add(2, 0, ChatColor.AQUA + "End Portals");
        element.add(2, 1, ChatColor.WHITE + "1000, 1000");

        element.add(2, 3, ChatColor.AQUA + "Map Kit");
        element.add(2, 4, ChatColor.WHITE + "Prot 1, Sharp 1");

        element.add(2, 6, ChatColor.AQUA + "Border");
        element.add(2, 7, ChatColor.WHITE + String.valueOf(VaporConstants.BORDER_SIZE));

        element.add(2, 9, ChatColor.AQUA + "Online Players");
        element.add(2, 10, ChatColor.WHITE.toString() + Bukkit.getOnlinePlayers().size());

        if(!this.teamController.findActiveKoths().isEmpty()) {
            final Koth koth = this.teamController.findActiveKoths().get(0);

            element.add(2, 12, ChatColor.AQUA + "Current Event");
            element.add(2, 13, ChatColor.WHITE + koth.getKothTeam().getDisplayName(player));
            element.add(2, 14, ChatColor.WHITE + (koth.getCapzone() == null ? "None" : koth.getCapzone().getCuboid().getMinXYZ()));

            if (koth.getCappingUuid() != null && Bukkit.getPlayer(koth.getCappingUuid()) != null) {
                element.add(2, 15, ChatColor.AQUA + "Duration");
                element.add(2, 16, StringUtils.getFormattedTime(koth.getKothTimer().getThread().getCurrentDuration(), false));
            }
        }

        return element;
    }

    /**
     * Get the direction of a player
     *
     * @param player the player
     * @return the direction
     */
    private String getDirection(Player player) {
        float angle = player.getLocation().getYaw();
        angle %= 360.0F;

        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        angle += 180.0F;

        if (angle >= 0F && angle <= 360F) {
            return "N";
        }

        if (angle >= 22.5F && angle <= 67.5F) {
            return "NE";
        }
        if (angle >= 67.5F && angle <= 112.5F) {
            return "E";
        }

        if (angle >= 112.5F && angle <= 157.5F) {
            return "SE";
        }

        if (angle >= 157.5F && angle <= 202.5F) {
            return "S";
        }

        if (angle >= 202.5F && angle <= 247.5F) {
            return "SW";
        }

        if (angle >= 247.5F && angle <= 292.45F) {
            return "W";
        }

        if (angle >= 292.45F && angle <= 337.5f) {
            return "NW";
        }

        return "";
    }
}