package rip.vapor.hcf.timers.impl.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.hcf.timers.impl.PlayerTimer;

public class ArcherSpeedTimer extends PlayerTimer {

    public ArcherSpeedTimer() {
        super("ArcherSpeed", ChatColor.AQUA + "Archer Speed", true, 60*1000);
    }

    @Override
    public void handleTick(Player player) {

    }

    @Override
    public void handleEnd(Player player) {

    }

    @Override
    public void handleCancel(Player player) {

    }
}
