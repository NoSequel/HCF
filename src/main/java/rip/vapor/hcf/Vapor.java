package rip.vapor.hcf;

import io.github.nosequel.menus.MenuHandler;
import io.github.nosequel.scoreboard.ScoreboardHandler;
import io.github.nosequel.tab.shared.TabHandler;
import io.github.nosequel.tab.v1_7_r4.v1_7_R4TabAdapter;
import rip.vapor.hcf.commands.EcoCommand;
import rip.vapor.hcf.commands.KothCommand;
import rip.vapor.hcf.listeners.operations.BlockOperationListener;
import rip.vapor.hcf.listeners.operations.BlockOperationModifierModule;
import rip.vapor.hcf.player.timers.commands.SOTWCommand;
import rip.vapor.hcf.scoreboard.provider.impl.ClassBoardProvider;
import rip.vapor.hcf.scoreboard.provider.impl.TimerBoardProvider;
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
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rip.vapor.hcf.tablist.TablistProvider;

import java.util.Arrays;
import java.util.logging.Level;

@Getter
public class Vapor extends JavaPlugin {

    private final ModuleHandler handler = new ModuleHandler();

    @Override
    public void onEnable() {
        // save default config
        this.saveDefaultConfig();
        this.debugConfig();

        // setup menu api
        new MenuHandler(this);

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
        this.handler.register(new TimerModule(this.handler));
        this.handler.register(new TeamModule());
        this.handler.register(new PlayerDataModule());
        this.handler.register(new ClassModule(this));
        this.handler.register(new BlockOperationModifierModule());
        this.handler.register(new TaskModule(this.handler));
        this.handler.register(new CombatLoggerModule(this));

        // register commands
        final CommandModule commandController = handler.register(new CommandModule("hcf", this.handler));
        commandController.registerCommand(
                new TeamCommand(this.handler),
                new SystemTeamCommand(this.handler),
                new TimerCommand(this.handler),
                new PvPCommand(this.handler),
                new SOTWCommand(this.handler),
                new KothCommand(this.handler),
                new EcoCommand(this.handler)
        );

        // register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ClaimListeners(this.handler), this);
        pluginManager.registerEvents(new ClaimSelectionListener(this.handler), this);
        pluginManager.registerEvents(new PlayerListeners(this.handler), this);
        pluginManager.registerEvents(new DamageListeners(this.handler), this);
        pluginManager.registerEvents(new DeathListeners(this.handler), this);
        pluginManager.registerEvents(new ChatListener(this.handler), this);
        pluginManager.registerEvents(new EquipListener(this), this);
        pluginManager.registerEvents(new EnvironmentListener(this.handler), this);
        pluginManager.registerEvents(new CombatWallListener(this.handler), this);
        pluginManager.registerEvents(new EnchantmentLimiterListener(), this);
        pluginManager.registerEvents(new KothListener(this.handler), this);
        pluginManager.registerEvents(new BorderListener(), this);
        pluginManager.registerEvents(new BlockOperationListener(this.handler), this);

        // setup scoreboard
        new ScoreboardHandler(this, new BoardProviderHandler(Arrays.asList(
                new TimerBoardProvider(this.handler),
                new ClassBoardProvider(this.handler)
        )), 2L);

        new TabHandler(new v1_7_R4TabAdapter(), new TablistProvider(this.handler), this, 5L);
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