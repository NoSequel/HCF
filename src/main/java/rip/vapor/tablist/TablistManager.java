package rip.vapor.tablist;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rip.vapor.tablist.entry.TablistElementSupplier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class TablistManager implements Listener {

    public static TablistManager INSTANCE;

    private final JavaPlugin plugin;
    private final Map<UUID, Tablist> tablists;
    private final TablistElementSupplier supplier;

    /**
     * Constructor for making a new {@link TablistManager} object
     *
     * @param plugin     the plugin to register it to
     * @param supplier   the supplier to get the tablist from
     * @param updateTime the time it takes to update the tablist
     */
    public TablistManager(JavaPlugin plugin, TablistElementSupplier supplier, long updateTime) {
        TablistManager.INSTANCE = this;

        this.tablists = new ConcurrentHashMap<>();
        this.supplier = supplier;
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new TablistUpdateTask(updateTime));
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getOnlinePlayers().forEach(this::findOrMake);
    }

    /**
     * Find a {@link Tablist} or make a new tablist if it could not be found.
     *
     * @param player the player to find it by
     * @return the tablist
     */
    public Tablist findOrMake(Player player) {
        return this.tablists.containsKey(player.getUniqueId())
                ? this.tablists.get(player.getUniqueId())
                : this.tablists.put(player.getUniqueId(), new Tablist(player));
    }

    /**
     * Find a {@link Tablist} by a {@link Player}
     *
     * @param player the player
     * @return the found tablist or null
     */
    public Tablist find(Player player) {
        return this.tablists.get(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> this.findOrMake(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Tablist tablist = tablists.remove(player.getUniqueId());

        if (tablist != null) {
            tablist.hideFakePlayers().clear();
        }
    }
}