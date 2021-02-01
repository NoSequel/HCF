package rip.vapor.hcf.listeners.claim;

import rip.vapor.hcf.controller.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ClaimListeners implements Listener, Controllable<TeamController> {

    private final TeamController controller = this.getController();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final Location to = event.getTo();
        final Location from = event.getFrom();
        final Team teamTo = controller.findTeam(to);
        final Team teamFrom = controller.findTeam(from);

        if (teamTo != null && teamFrom != null && !teamTo.equals(teamFrom)) {
            final Claim teamToClaim = teamTo.findData(ClaimTeamData.class).getClaim();
            final Claim teamFromClaim = teamFrom.findData(ClaimTeamData.class).getClaim();

            player.sendMessage(new String[]{
                    ChatColor.YELLOW + "Leaving: " + teamFrom.getDisplayName(player) + ChatColor.YELLOW + "(" + (teamFromClaim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")",
                    ChatColor.YELLOW + "Entering: " + teamTo.getDisplayName(player) + ChatColor.YELLOW + "(" + (teamToClaim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")"
            });
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Location blockLocation = event.getBlock().getLocation();
        final Team team = controller.findTeam(blockLocation);

        if (team != null && !team.canInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Location blockLocation = event.getBlock().getLocation();
        final Team team = controller.findTeam(blockLocation);

        if (team != null && !team.canInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            final Player player = event.getPlayer();
            final Location blockLocation = event.getClickedBlock().getLocation();
            final Team team = controller.findTeam(blockLocation);

            if (team != null && !team.canInteract(player)) {
                event.setCancelled(true);
            }
        }
    }
}