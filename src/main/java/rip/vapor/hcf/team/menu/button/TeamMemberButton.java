package rip.vapor.hcf.team.menu.button;

import io.github.nosequel.menus.button.ButtonBuilder;
import io.github.nosequel.menus.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.vapor.hcf.team.data.impl.player.PlayerRole;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;

import java.util.UUID;

public class TeamMemberButton extends ButtonBuilder {

    /**
     * Constructor to make a new {@link TeamMemberButton} object
     *
     * @param index  the index of the button to display in
     * @param data   the data of the team
     * @param target the target to make the button for
     */
    public TeamMemberButton(int index, PlayerTeamData data, UUID target, Menu menu) {
        super(index, Material.SKULL_ITEM);

        if (data.getAllMembers().contains(target)) {
            this.displayName(ChatColor.GOLD + Bukkit.getOfflinePlayer(target).getName());
            this.lore(
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 24),
                    ChatColor.YELLOW + "Role: " + ChatColor.RED + data.getRole(target).fancyName,
                    ChatColor.YELLOW + "Online: " + (Bukkit.getPlayer(target) == null ? ChatColor.RED + "No" : ChatColor.GREEN + "Yes"),
                    "",
                    ChatColor.GRAY + "Left-click to promote this member",
                    ChatColor.GRAY + "Right-click to demote this member",
                    ChatColor.GRAY + "Shift Left-click to kick this member",
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 24)
            );

            this.action(type -> {
                if (!target.equals(data.getLeader()) && data.getRole(menu.getPlayer().getUniqueId()).isHigher(PlayerRole.CAPTAIN)) {
                    if (type.isRightClick() && !type.isShiftClick()) {
                        data.demotePlayer(target);
                    } else if (type.isRightClick()) {
                        data.kick(target);
                    } else if (type.isLeftClick() && !data.getRole(target).isHigher(PlayerRole.CAPTAIN)) {
                        data.promotePlayer(target);
                    }
                }

                menu.updateMenu();
                return true;
            });
        }
    }
}