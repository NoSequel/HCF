package rip.vapor.hcf.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import rip.vapor.hcf.controller.Controller;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CombatLoggerController implements Controller {

    private final JavaPlugin plugin;
    private final List<CombatLogger> loggers = new ArrayList<>();

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new CombatLoggerListener(), plugin);
    }

}