package rip.vapor.hcf.util.tasks.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.util.tasks.Task;

public class BackupTask extends Task {

    private final PlayerDataModule playerDataModule;
    private final TeamModule teamModule;

    public BackupTask(ModuleHandler handler) {
        super(1300 * 1000);
        this.playerDataModule = handler.find(PlayerDataModule.class);
        this.teamModule = handler.find(TeamModule.class);
    }

    @Override
    public void tick() throws Exception {
        Bukkit.broadcastMessage(ChatColor.GOLD + "[Backup] " + ChatColor.YELLOW + "Saving all team and player data... this might cause lag.");

        // note! we are not actually disabling the plugins, however both of these modules only save their data in the disable method!
        this.playerDataModule.disable();
        this.teamModule.disable();
    }

    @Override
    public String getName() {
        return "Backup";
    }
}
