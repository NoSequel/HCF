package rip.vapor.hcf.koth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import rip.vapor.hcf.team.claim.Claim;
import rip.vapor.hcf.timers.impl.global.KothTimer;

import java.util.UUID;

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

    public void tickCap() {
        if (this.cappingUuid == null || Bukkit.getPlayer(this.cappingUuid) == null) {
            this.cappingUuid = null;
        } else {

        }
    }
}