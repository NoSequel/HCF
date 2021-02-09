package rip.vapor.hcf.player.timers.impl.global;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.hcf.team.koth.Koth;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.impl.GlobalTimer;

public class KothTimer extends GlobalTimer implements Controllable<TimerModule> {

    private final Koth koth;

    /**
     * Constructor for creating a new timer
     *
     * @param name the name of the timer
     * @param koth the koth it's designated to
     */
    public KothTimer(String name, Koth koth) {
        super(name, ChatColor.DARK_PURPLE + koth.getKothName(), false, koth.getDefaultDuration(), false);
        this.koth = koth;
        this.getModule().registerTimer(this);
    }

    @Override
    public void handleTick() {
        if (this.koth.isRunning()) {
            this.koth.tickCap();
        }
    }

    @Override
    public void handleEnd() {
        if (koth.getCappingUuid() != null) {
            final Player player = Bukkit.getPlayer(koth.getCappingUuid());

            Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] " + ChatColor.WHITE + player.getName() + ChatColor.YELLOW + " has successfully captured the " + koth.getKothTeam().getDisplayName(player));
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Rewards:");
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + "AIR");
        }

        koth.setRunning(false);
        koth.setCappingUuid(null);
        this.getThread().setActive(false);
    }
}