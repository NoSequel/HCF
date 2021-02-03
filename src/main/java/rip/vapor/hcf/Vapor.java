package rip.vapor.hcf;

import rip.vapor.hcf.listeners.EnchantmentLimiterListener;
import rip.vapor.hcf.listeners.EnvironmentListener;
import rip.vapor.hcf.listeners.combatwall.CombatWallListener;
import rip.vapor.hcf.logger.CombatLoggerController;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.commands.SystemTeamCommand;
import rip.vapor.hcf.commands.TeamCommand;
import rip.vapor.hcf.controller.Controller;
import rip.vapor.hcf.controller.ControllerHandler;
import rip.vapor.hcf.listeners.PlayerListeners;
import rip.vapor.hcf.listeners.claim.ClaimListeners;
import rip.vapor.hcf.listeners.claim.ClaimSelectionListener;
import rip.vapor.hcf.listeners.classes.EquipListener;
import rip.vapor.hcf.listeners.team.ChatListener;
import rip.vapor.hcf.listeners.team.DamageListeners;
import rip.vapor.hcf.listeners.team.DeathListeners;
import rip.vapor.hcf.player.PlayerDataController;
import rip.vapor.hcf.scoreboard.BoardProviderHandler;
import rip.vapor.hcf.util.tasks.TaskController;
import rip.vapor.hcf.team.TeamController;
import rip.vapor.hcf.timers.TimerController;
import rip.vapor.hcf.timers.commands.PvPCommand;
import rip.vapor.hcf.timers.commands.TimerCommand;
import rip.vapor.hcf.util.command.CommandController;
import rip.vapor.hcf.util.database.DatabaseController;
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

    private final ControllerHandler handler = new ControllerHandler();

    @Override
    public void onEnable() {
        // register the instance
        instance = this;

        // save default config
        this.saveDefaultConfig();
        this.debugConfig();

        // setup database controller
        final DatabaseController controller = new DatabaseController(
                new MongoDatabaseOption(
                        "127.0.0.1",
                        "",
                        "",
                        "hcteams",
                        27017
                ),
                new MongoDataType()
        );

        controller.setDataHandler(new MongoDataHandler(controller));

        // register controllers
        this.handler.register(controller);
        this.handler.register(new TeamController());
        this.handler.register(new PlayerDataController());
        this.handler.register(new TimerController());
        this.handler.register(new ClassController());
        this.handler.register(new TaskController());
        this.handler.register(new CombatLoggerController(this));

        // register commands
        final CommandController commandController = handler.register(new CommandController("vapor"));
        commandController.registerCommand(
                new TeamCommand(),
                new SystemTeamCommand(),
                new TimerCommand(),
                new PvPCommand()
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

        // setup scoreboard
        new Assemble(this, new BoardProviderHandler()).setTicks(1L);
        new TablistManager(this, new TablistProvider(), 250L);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("The server is shutting down"));

        handler.getControllers().forEach(Controller::disable);
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