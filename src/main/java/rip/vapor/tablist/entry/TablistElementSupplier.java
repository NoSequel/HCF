package rip.vapor.tablist.entry;

import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import rip.vapor.tablist.entry.TablistElement;

public interface TablistElementSupplier {

    /**
     * Get the {@link TablistElement} for a {@link Player}
     *
     * @param player the player
     * @return the element
     */
    TablistElement getEntries(Player player);

}