package rip.vapor.hcf.timers.impl.global;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.koth.Koth;
import rip.vapor.hcf.module.Controllable;
import rip.vapor.hcf.timers.TimerModule;
import rip.vapor.hcf.timers.impl.GlobalTimer;

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
            Bukkit.broadcastMessage(ChatColor.YELLOW + "yay capped by " + Bukkit.getPlayer(koth.getCappingUuid()).getName());
        }

        koth.setRunning(false);
        koth.setCappingUuid(null);
        this.getThread().setActive(false);
    }
}