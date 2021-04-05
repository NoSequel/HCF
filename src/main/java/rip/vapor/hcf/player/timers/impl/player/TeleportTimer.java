package rip.vapor.hcf.player.timers.impl.player;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.player.timers.impl.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public class TeleportTimer extends PlayerTimer {

    private final TeamModule teamModule;

    public TeleportTimer(ModuleHandler handler) {
        super("Home", ChatColor.BLUE + "Home", true, 10000);
        this.teamModule = handler.find(TeamModule.class);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (this.isOnCooldown(player)) {
            final Location to = event.getTo();
            final Location from = event.getFrom();

            if (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()) {
                super.cancel(player);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (this.isOnCooldown(player)) {
            this.cancel(player);
        }
    }

    @Override
    public void handleTick(Player player) {
    }

    @Override
    public void handleEnd(Player player) {
        final Optional<Team> team = this.teamModule.findTeam(player);

        if (team.isPresent()) {
            final ClaimTeamData data = team.get().findData(ClaimTeamData.class);

            if (data != null && data.getHome() != null) {
                player.teleport(data.getHome());
                player.sendMessage(ChatColor.GRAY + "You have been teleported to your team's HQ.");
            }
        }
    }

    @Override
    public void handleCancel(Player player) {
        player.sendMessage(ChatColor.RED + "Your teleport timer has been cancelled.");
    }
}