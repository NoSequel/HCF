package rip.vapor.hcf;

import rip.vapor.hcf.commands.EcoCommand;
import rip.vapor.hcf.commands.KothCommand;
import rip.vapor.hcf.team.koth.KothListener;
import rip.vapor.hcf.listeners.BorderListener;
import rip.vapor.hcf.listeners.EnchantmentLimiterListener;
import rip.vapor.hcf.listeners.EnvironmentListener;
import rip.vapor.hcf.listeners.combatwall.CombatWallListener;
import rip.vapor.hcf.listeners.logger.CombatLoggerModule;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.commands.SystemTeamCommand;
import rip.vapor.hcf.commands.TeamCommand;
import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.listeners.PlayerListeners;
import rip.vapor.hcf.listeners.claim.ClaimListeners;
import rip.vapor.hcf.listeners.claim.ClaimSelectionListener;
import rip.vapor.hcf.listeners.classes.EquipListener;
import rip.vapor.hcf.listeners.team.ChatListener;
import rip.vapor.hcf.listeners.team.DamageListeners;
import rip.vapor.hcf.listeners.team.DeathListeners;
import rip.vapor.hcf.player.PlayerDataModule;
import rip.vapor.hcf.scoreboard.BoardProviderHandler;
import rip.vapor.hcf.util.tasks.TaskModule;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.player.timers.TimerModule;
import rip.vapor.hcf.player.timers.commands.PvPCommand;
import rip.vapor.hcf.player.timers.commands.TimerCommand;
import rip.vapor.hcf.util.command.CommandModule;
import rip.vapor.hcf.util.database.DatabaseModule;
import rip.vapor.hcf.util.database.handler.data.MongoDataHandler;
import rip.vapor.hcf.util.database.options.impl.MongoDatabaseOption;
import rip.vapor.hcf.util.database.type.mongo.MongoDataType;
import io.github.thatkawaiisam.assemble.Assemble;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rip.vapor.tablist.TablistManager;
import rip.vapor.tablist.provider.TablistProvider;

import java.util.logging.Level;

@Getter
public class Vapor extends JavaPlugin {

    @Getter
    private static Vapor instance;

    private final ModuleHandler handler = new ModuleHandler();

    @Override
    public void onEnable() {
        // register the instance
        instance = this;

        // save default config
        this.saveDefaultConfig();
        this.debugConfig();

        // setup database controller
        final DatabaseModule databaseModule = new DatabaseModule(
                new MongoDatabaseOption(
                        "127.0.0.1",
                        "",
                        "",
                        "hcteams",
                        27017
                ),
                new MongoDataType()
        );

        databaseModule.setDataHandler(new MongoDataHandler(databaseModule));

        // register controllers
        this.handler.register(databaseModule);
        this.handler.register(new TimerModule());
        this.handler.register(new TeamModule());
        this.handler.register(new PlayerDataModule());
        this.handler.register(new ClassModule());
        this.handler.register(new TaskModule());
        this.handler.register(new CombatLoggerModule(this));

        // register commands
        final CommandModule commandController = handler.register(new CommandModule("vapor"));
        commandController.registerCommand(
                new TeamCommand(),
                new SystemTeamCommand(),
                new TimerCommand(),
                new PvPCommand(),
                new KothCommand(),
                new EcoCommand()
        );

        // register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ClaimListeners(), this);
        pluginManager.registerEvents(new ClaimSelectionListener(), this);
        pluginManager.registerEvents(new PlayerListeners(), this);
        pluginManager.registerEvents(new DamageListeners(), this);
        pluginManager.registerEvents(new DeathListeners(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new EquipListener(), this);
        pluginManager.registerEvents(new EnvironmentListener(), this);
        pluginManager.registerEvents(new CombatWallListener(), this);
        pluginManager.registerEvents(new EnchantmentLimiterListener(), this);
        pluginManager.registerEvents(new KothListener(), this);
        pluginManager.registerEvents(new BorderListener(), this);

        // setup scoreboard
        new Assemble(this, new BoardProviderHandler()).setTicks(1L);
        new TablistManager(this, new TablistProvider(), 500L);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("The server is shutting down"));

        handler.getModules().values().forEach(Module::disable);
    }

    /**
     * Debug the config to the console
     */
    private void debugConfig() {
        Bukkit.getLogger().log(Level.INFO, "Loading config.yml");
        Bukkit.getLogger().log(Level.INFO, "    [ Kitmap Enabled: " + VaporConstants.KITMAP_ENABLED + " ]");


        // potion limits
        VaporConstants.ENCHANTMENT_LIMITS
                .forEach(((enchantment, integer) -> Bukkit.getLogger().log(Level.INFO, "    [ Enchantment Limit " + enchantment.getName() + ": " + integer + " ]")));
    }
}