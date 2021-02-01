package rip.vapor.hcf.listeners.claim;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.controller.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataController;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClaimSelectionListener implements Listener, Controllable<PlayerDataController> {

    private final PlayerDataController controller = this.getController();
    private final TeamController teamController = Vapor.getInstance().getHandler().find(TeamController.class);

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = controller.findPlayerData(player.getUniqueId());

        if (playerData != null && playerData.hasData(ClaimSelectionData.class)) {
            final ClaimSelectionData data = playerData.findData(ClaimSelectionData.class);
            final ClaimSelection claimSelection = data.getClaimSelection();
            final Action action = event.getAction();

            switch (action) {
                case LEFT_CLICK_BLOCK: {
                    claimSelection.setLocation1(event.getClickedBlock().getLocation());
                }
                break;

                case RIGHT_CLICK_BLOCK: {
                    claimSelection.setLocation2(event.getClickedBlock().getLocation());
                }
                break;

                case RIGHT_CLICK_AIR: {
                    if (player.isSneaking()) {
                        playerData.getData().remove(data);
                        player.sendMessage(ChatColor.GRAY + "You have cancelled your claiming task.");
                    }
                }
                break;

                case LEFT_CLICK_AIR: {
                    if (player.isSneaking()) {
                        if (claimSelection.getLocation1() == null || claimSelection.getLocation2() == null) {
                            player.sendMessage(ChatColor.GRAY + "One or more positions hasn't been set.");
                            return;
                        }

                        if ((!teamController.findTeam(claimSelection.getLocation1()).getGeneralData().getType().equals(TeamType.WILDERNESS_TEAM) || !teamController.findTeam(claimSelection.getLocation1()).getGeneralData().getType().equals(TeamType.WILDERNESS_TEAM)) && claimSelection.getTeam().getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
                            player.sendMessage(ChatColor.GRAY + "The current selection contains non-wilderness regions.");
                            return;
                        }


                        final Team team = claimSelection.getTeam();
                        final ClaimTeamData oldClaimData = team.findData(ClaimTeamData.class);

                        claimSelection.apply();
                        playerData.getData().remove(data);

                        if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
                            final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);
                            final ClaimTeamData claimTeamData = team.findData(ClaimTeamData.class);

                            playerTeamData.broadcast(ChatColor.GRAY + "Your team now has a claim of " + claimTeamData.getClaim().getCuboid().getChunks() + " chunks.");
                        }

                        if (oldClaimData != null) {
                            final Claim oldClaim = oldClaimData.getClaim();
                            final Claim newClaim = team.findData(ClaimTeamData.class).getClaim();

                            newClaim.setPriority(oldClaim.getPriority());
                        }

                        player.sendMessage(ChatColor.YELLOW + "You have claimed for " + team.getDisplayName(player));
                    }

                }
                break;
            }

            event.setCancelled(true);
        }
    }
}