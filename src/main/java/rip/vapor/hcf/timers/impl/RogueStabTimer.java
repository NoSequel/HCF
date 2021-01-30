package rip.vapor.hcf.timers.impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.hcf.timers.Timer;

public class RogueStabTimer extends Timer {

    public RogueStabTimer() {
        super("RogueStab", ChatColor.RED + "Rogue Stab", true, 15000);
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
