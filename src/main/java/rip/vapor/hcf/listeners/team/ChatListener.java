package rip.vapor.hcf.listeners.team;

import org.bukkit.event.EventPriority;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final TeamController teamController = Vapor.getInstance().getHandler().find(TeamController.class);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            event.setCancelled(true);

            final Player player = event.getPlayer();
            final Team team = teamController.findTeam(player);

            event.getRecipients().forEach(recipient -> recipient.sendMessage(this.getTeamString(team, recipient) + event.getFormat()));
        }
    }

    /**
     * Get the formatted string of a {@link Team} to display in the chat
     *
     * @param team      the team to format
     * @param recipient the recipient to format it for
     * @return the formatted string
     */
    private String getTeamString(Team team, Player recipient) {
        return ChatColor.GOLD + "[" + (team == null ? ChatColor.RED + "*" : team.getDisplayName(recipient.getPlayer())) + ChatColor.GOLD + "]";
    }
}