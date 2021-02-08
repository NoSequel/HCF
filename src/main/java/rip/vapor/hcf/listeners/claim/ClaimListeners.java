package rip.vapor.hcf.listeners.claim;

import org.bukkit.Material;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
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

import java.util.Arrays;
import java.util.Optional;

public class ClaimListeners implements Listener, Controllable<TeamModule> {

    private final TeamModule teamModule = this.getModule();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final Location to = event.getTo();
        final Location from = event.getFrom();
        final Optional<Team> teamTo = teamModule.findTeam(to);
        final Optional<Team> teamFrom = teamModule.findTeam(from);

        if (teamTo.isPresent() && teamFrom.isPresent() && !teamTo.get().equals(teamFrom.get())) {
            final Claim teamToClaim = teamTo.get().findData(ClaimTeamData.class).getClaim();
            final Claim teamFromClaim = teamFrom.get().findData(ClaimTeamData.class).getClaim();

            player.sendMessage(new String[]{
                    ChatColor.YELLOW + "Leaving: " + teamFrom.get().getDisplayName(player) + ChatColor.YELLOW + "(" + (teamFromClaim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")",
                    ChatColor.YELLOW + "Entering: " + teamTo.get().getDisplayName(player) + ChatColor.YELLOW + "(" + (teamToClaim.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ")"
            });

        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Location blockLocation = event.getBlock().getLocation();
        final Optional<Team> team = teamModule.findTeam(blockLocation);

        if (team.isPresent() && !team.get().canInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Location blockLocation = event.getBlock().getLocation();
        final Optional<Team> team = teamModule.findTeam(blockLocation);

        if (team.isPresent() && !team.get().canInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && (event.getItem() == null || Arrays.stream(DISALLOWED_BLOCKS).anyMatch(item -> item.equals(event.getItem().getType())))) {
            final Player player = event.getPlayer();
            final Location blockLocation = event.getClickedBlock().getLocation();
            final Optional<Team> team = teamModule.findTeam(blockLocation);

            if (team.isPresent() && !team.get().canInteract(player)) {
                event.setCancelled(true);
            }
        }
    }

    private final Material[] DISALLOWED_BLOCKS = new Material[]{
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.STONE_PLATE,
            Material.FENCE_GATE,
            Material.WOOD_DOOR,
            Material.IRON_DOOR,
    };
}