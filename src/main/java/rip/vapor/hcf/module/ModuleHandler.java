package rip.vapor.hcf.module;

import lombok.Getter;

import java.util.*;

@Getter
public class ModuleHandler {

    private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

    /**
     * Find a {@link Module} by a {@link Class}
     *
     * @param clazz the class
     * @param <T>   the type of the controller
     * @return the controller
     */
    public <T extends Module> T find(Class<T> clazz) {
        return clazz.cast(this.modules.get(clazz));
    }

    /**
     * Register a new controller
     *
     * @param module the controller
     * @param <T>        the type of the controller
     * @return the registered controller | or the previously registered controller.
     */
    @SuppressWarnings("unchecked")
    public <T extends Module> T register(Module module) {
        this.modules.put(module.getClass(), module);
        module.enable();

        return (T) module;
    }

    /**
     * Unregister a controller
     *
     * @param controller the controller to get unregistered
     */
    public void unregister(Class<? extends Module> controller) {
        if (this.find(controller) != null) {
            this.modules.remove(this.find(controller).getClass());
        }

        System.out.println("Tried unregistering controller which isn't registered");
    }
}