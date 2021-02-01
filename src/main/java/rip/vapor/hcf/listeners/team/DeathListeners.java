package rip.vapor.hcf.listeners.team;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.player.PlayerData;
import rip.vapor.hcf.player.PlayerDataController;
import rip.vapor.hcf.player.data.deathban.DeathbanData;
import rip.vapor.hcf.player.data.deathban.impl.PlayerDeathbanData;

import rip.vapor.hcf.player.data.deathban.impl.natural.NaturalDeathbanData;
import rip.vapor.hcf.player.data.deathban.impl.natural.NaturalDeathbanType;

import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.team.data.impl.player.DTRData;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListeners implements Listener {

    private final PlayerDataController controller = Vapor.getInstance().getHandler().find(PlayerDataController.class);
    private final TeamController teamController = Vapor.getInstance().getHandler().find(TeamController.class);

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerData playerData = controller.findPlayerData(player.getUniqueId());
        final Team team = teamController.findTeam(player);

        if (!VaporConstants.KITMAP_ENABLED) {
            final DeathbanData data = player.getKiller() == null
                    ? new NaturalDeathbanData(NaturalDeathbanType.UNDEFINED, 30000)
                    : new PlayerDeathbanData(player.getKiller().getUniqueId(), 30000);

            if (teamController.findTeam(player) != null) {
                final PlayerTeamData teamData = team.findData(PlayerTeamData.class);
                final DTRData dtrData = team.findData(DTRData.class);

                teamData.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE + player.getName() + ChatColor.YELLOW + " (" + dtrData.getDtr() + " -> " + NumberUtil.round(dtrData.getDtr() - 1.0D, 1) + ")");
                dtrData.setDtr(dtrData.getDtr() - 1.0D);
            }

            playerData.addData(data);
            data.kickPlayer(player);
        }

        player.getInventory().clear();
        event.setDeathMessage(null);
    }
}