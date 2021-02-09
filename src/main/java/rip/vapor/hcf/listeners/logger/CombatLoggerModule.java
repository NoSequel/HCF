package rip.vapor.hcf.listeners.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import rip.vapor.hcf.module.Module;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CombatLoggerModule implements Module {

    private final JavaPlugin plugin;
    private final List<CombatLogger> loggers = new ArrayList<>();

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new CombatLoggerListener(), plugin);
    }

}