package rip.vapor.hcf.timers.impl.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.hcf.timers.PlayerTimer;

public class RogueStabTimer extends PlayerTimer {

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
