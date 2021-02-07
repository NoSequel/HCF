package rip.vapor.hcf.team.enums;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public enum TeamType {

    PLAYER_TEAM(false) {
        @Override
        public String formatName(Team team, Player player) {
            final Team team1 = (Vapor.getInstance().getHandler().find(TeamModule.class).findTeam(player));

            return (team1 != null && team1.equals(team) ? ChatColor.DARK_GREEN : ChatColor.YELLOW) + team.getFormattedName();
        }
    },

    SAFEZONE_TEAM(false) {
        @Override
        public String formatName(Team team, Player player) {
            return (team.getGeneralData().getColor() == null ? ChatColor.GREEN : team.getGeneralData().getColor()) + team.getFormattedName();
        }
    },

    WILDERNESS_TEAM(true) {
        @Override
        public String formatName(Team team, Player player) {
            return (team.getGeneralData().getColor() == null ? ChatColor.GRAY : team.getGeneralData().getColor()) + "The " + team.getFormattedName();
        }
    },

    KOTH_TEAM(false) {
        @Override
        public String formatName(Team team, Player player) {
            return (team.getGeneralData().getColor() == null ? ChatColor.AQUA : team.getGeneralData().getColor()) + team.getFormattedName() + ChatColor.GOLD + " KOTH";
        }
    },

    ROAD_TEAM(false) {
        @Override
        public String formatName(Team team, Player player) {
            return (team.getGeneralData().getColor() == null ? ChatColor.GOLD : team.getGeneralData().getColor()) + team.getFormattedName().replace("Road", " Road");
        }
    },

    SYSTEM_TEAM(false) {
        @Override
        public String formatName(Team team, Player player) {
            return (team.getGeneralData().getColor() == null ? ChatColor.WHITE : team.getGeneralData().getColor()) + team.getFormattedName();
        }
    };

    public final boolean canInteract;

    /**
     * Format the team name by the type
     *
     * @param team   the team
     * @param player the player
     * @return the formatted team
     */
    public abstract String formatName(Team team, Player player);
}