package rip.vapor.hcf.koth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.timers.impl.global.KothTimer;

import java.util.UUID;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
@Setter
public class Koth {

    private final KothTimer kothTimer = new KothTimer(this.getKothName(), this);

    private final String kothName;
    private final long defaultDuration;
    private final Claim capzone;
    private final Claim claim;

    private boolean isRunning;
    private UUID cappingUuid;

    /**
     * Method to handle ticking the cap
     */
    public void tickCap() {
        if (this.cappingUuid == null || Bukkit.getPlayer(this.cappingUuid) == null) {
            this.cappingUuid = null;
        } else if (!this.kothTimer.getThread().isShouldSubtract()) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[King Of The Hill] "
                    + ChatColor.WHITE + Bukkit.getPlayer(this.cappingUuid).getName()
                    + ChatColor.YELLOW + "is now capping the "
                    + ChatColor.YELLOW + this.getKothName() + ChatColor.GOLD + " KoTH");

            this.kothTimer.getThread().setShouldSubtract(true);

            Bukkit.getLogger().log(Level.INFO, this.cappingUuid + " starting capping KoTH " + this.kothName + " (" + this.capzone.getCuboid().toXYZ() + ")");
        }
    }
}