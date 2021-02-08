package rip.vapor.hcf.listeners.team;

import org.bukkit.event.EventPriority;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener {

    private final TeamModule teamController = Vapor.getInstance().getHandler().find(TeamModule.class);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            event.setCancelled(true);

            final Player player = event.getPlayer();
            final Optional<Team> team = teamController.findTeam(player);

            event.getRecipients().forEach(recipient -> recipient.sendMessage(this.getTeamString(team.orElse(null), recipient) + event.getFormat()));
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