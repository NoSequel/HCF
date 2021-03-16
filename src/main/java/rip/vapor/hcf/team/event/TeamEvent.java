package rip.vapor.hcf.team.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamEvent extends Event {
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
