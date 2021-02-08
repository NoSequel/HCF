package rip.vapor.hcf.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import rip.vapor.hcf.VaporConstants;

public class BorderListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location to = event.getTo();
        final int size = VaporConstants.BORDER_SIZE;

        if (this.checkLocation(to, size)) {
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage(ChatColor.RED + "You can't walk past the border.");
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        final Location to = event.getTo();
        final int size = VaporConstants.BORDER_SIZE;

        if (this.checkLocation(to, size)) {
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage(ChatColor.RED + "You can't walk past the border.");
        }
    }

    /**
     * Check if a {@link Location} has surpassed the border radius
     *
     * @param to   the location
     * @param size the size of the border
     * @return whether it has surpassed th eborder
     */
    private boolean checkLocation(Location to, int size) {
        return to.getBlockX() >= size || to.getBlockX() <= -size || to.getBlockZ() >= size || to.getBlockZ() <= -size;
    }

}
