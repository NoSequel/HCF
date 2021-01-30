package rip.vapor.hcf;

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
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Vapor extends JavaPlugin {

    @Getter
    private static Vapor instance;

    private final ControllerHandler handler = new ControllerHandler();

    @Override
    public void onEnable() {
        // register the instance
        instance = this;


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
        this.handler.registerController(controller);
        this.handler.registerController(new TeamController());
        this.handler.registerController(new PlayerDataController());
        this.handler.registerController(new TimerController());
        this.handler.registerController(new ClassController());
        this.handler.registerController(new TaskController());
        this.handler.registerController(new CombatLoggerController(this));

        // register commands
        final CommandController commandController = handler.registerController(new CommandController("hcteams"));
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

        // setup scoreboard
        new Assemble(this, new BoardProviderHandler()).setAssembleStyle(AssembleStyle.MODERN);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("The server is shutting down"));

        handler.getControllers().forEach(Controller::disable);
    }
}