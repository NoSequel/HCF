package rip.vapor.hcf.listeners.claim;

import org.bukkit.Material;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.team.claim.selection.ClaimSelection;
import rip.vapor.hcf.team.data.impl.KothTeamData;
import rip.vapor.hcf.team.data.impl.claim.ClaimTeamData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.enums.TeamType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClaimSelectionListener implements Listener, Controllable<PlayerDataModule> {

    private final PlayerDataModule controller = this.getModule();
    private final TeamModule teamController = Vapor.getInstance().getHandler().find(TeamModule.class);

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = controller.findPlayerData(player.getUniqueId());

        if (playerData != null && playerData.hasData(ClaimSelectionData.class) && (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR))) {
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

                        playerData.getData().remove(data);

                        if (!claimSelection.isKothCapzone()) {
                            claimSelection.apply();

                            if (team.getGeneralData().getType().equals(TeamType.PLAYER_TEAM)) {
                                final PlayerTeamData playerTeamData = team.findData(PlayerTeamData.class);
                                final ClaimTeamData claimTeamData = team.findData(ClaimTeamData.class);

                                playerTeamData.broadcast(ChatColor.GRAY + "Your team now has a claim of " + claimTeamData.getClaim().getCuboid().getChunks() + " chunks.");
                            }

                            if (oldClaimData != null) {
                                final Claim oldClaim = oldClaimData.getClaim();

                                if (oldClaim != null) {
                                    final Claim newClaim = team.findData(ClaimTeamData.class).getClaim();

                                    newClaim.setPriority(oldClaim.getPriority());
                                }
                            }
                        }

                        if (team.getGeneralData().getType().equals(TeamType.KOTH_TEAM) && claimSelection.isKothCapzone()) {
                            final KothTeamData kothTeamData = team.findData(KothTeamData.class);

                            kothTeamData.getKoth().setCapzone(claimSelection.toClaim());
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