package rip.vapor.hcf.util.command;

import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import rip.vapor.hcf.util.command.annotation.Command;
import rip.vapor.hcf.util.command.annotation.Subcommand;
import rip.vapor.hcf.util.command.data.CommandData;
import rip.vapor.hcf.util.command.data.SubcommandData;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import rip.vapor.hcf.util.command.adapter.defaults.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommandModule implements Module {

    @Getter
    private static CommandModule instance;

    private final String fallbackPrefix;

    private final List<TypeAdapter<?>> typeAdapters = new ArrayList<>();
    private final List<CommandData> commands = new ArrayList<>();

    /**
     * Create a new instance of the CommandModule class
     *
     * @param fallbackPrefix the fallback prefix
     */
    public CommandModule(String fallbackPrefix, ModuleHandler handler) {
        instance = this;

        this.fallbackPrefix = fallbackPrefix;
        this.typeAdapters.addAll(Arrays.asList(
                new UUIDTypeAdapter(),
                new OfflinePlayerTypeAdapter(),
                new LongTypeAdapter(),
                new IntegerTypeAdapter(),
                new TeamTypeAdapter(handler),
                new PlayerTypeAdapter(),
                new TimerTypeAdapter(handler),
                new KothTypeAdapter(handler)
        ));
    }

    /**
     * Register commands
     *
     * @param objects the commands
     */
    public void registerCommand(Object... objects) {
        Arrays.stream(objects).forEach(this::registerCommand);
    }

    /**
     * Method to register a {@link Object} as a command
     *
     * @param object the object
     */
    public void registerCommand(Object object) {
        final List<Method> commandMethods = this.getMethods(Command.class, object);
        final List<Method> subcommands = this.getMethods(Subcommand.class, object);

        commandMethods.stream()
                .map(method -> new CommandData(object, method))
                .forEach(this::register);

        subcommands.stream()
                .filter(method -> commands.stream().anyMatch(data -> data.isParentOfSubCommand(method.getAnnotation(Subcommand.class))))
                .forEach(method -> commands.stream()
                        .filter(data -> data.isParentOfSubCommand(method.getAnnotation(Subcommand.class)))
                        .forEach(parent -> parent.getSubcommands().add(new SubcommandData(object, method))));
    }

    /**
     * Register a new {@link CommandData} object as a command
     *
     * @param data the command data object
     */
    private void register(CommandData data) {
        if(this.getCommandMap() == null) {
            throw new RuntimeException("commandMap field is null");
        }

        this.commands.add(data);
        this.getCommandMap().register("basics", new CommandExecutable(data));
    }

    /**
     * Get all methods annotated with a {@link Annotation} in an object
     *
     * @param annotation the annotation which the method must be annotated with
     * @param object     the object with the methods
     * @param <T>        the type of the annontation
     * @return the list of methods
     */
    private <T extends Annotation> List<Method> getMethods(Class<T> annotation, Object object) {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }


    /**
     * Find a converter by a class type
     *
     * @param type the type
     * @param <T>  the return type
     * @return the found type adapter
     */
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> findConverter(Class<T> type) {
        return (TypeAdapter<T>) this.typeAdapters.stream()
                .filter(converter -> converter.getType().equals(type))
                .findAny().orElse(null);
    }

    /**
     * Gets the bukkit commandmap
     *
     * @return the commandmap
     */
    private CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }
}