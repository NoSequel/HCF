package rip.vapor.hcf.listeners.claim;

import org.bukkit.Material;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.data.impl.KothTeamData;
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

public class ClaimListeners implements Listener, Controllable<TeamModule> {

    private final TeamModule controller = this.getModule();

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

        if (teamTo != null && teamTo.hasData(KothTeamData.class)) {
            final KothTeamData teamToData = teamTo.findData(KothTeamData.class);

            if (teamToData != null) {
                final Koth koth = teamToData.getKoth();
                final Location location = player.getLocation();
                final Claim capzone = koth.getCapzone();

                if (koth.isRunning() && koth.getCappingUuid() == null && capzone.getCuboid().isLocationInCuboid(location)) {
                    koth.setCappingUuid(player.getUniqueId());
                }
            }
        }

        this.controller.getTeams().stream()
                .filter(team -> team.hasData(KothTeamData.class))
                .map(team -> team.findData(KothTeamData.class).getKoth())
                .filter(koth -> koth.getCappingUuid() != null && koth.getCapzone() != null)
                .filter(koth -> koth.getCappingUuid().equals(player.getUniqueId()) && !koth.getCapzone().getCuboid().isLocationInCuboid(player.getLocation()))
                .forEach(koth -> koth.setCappingUuid(null));
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
        if (event.getClickedBlock() != null && (event.getItem() == null || Arrays.stream(DISALLOWED_BLOCKS).anyMatch(item -> item.equals(event.getItem().getType())))) {
            final Player player = event.getPlayer();
            final Location blockLocation = event.getClickedBlock().getLocation();
            final Team team = controller.findTeam(blockLocation);

            if (team != null && !team.canInteract(player)) {
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