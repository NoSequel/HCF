package rip.vapor.hcf.controller;

import lombok.Getter;

import java.util.*;

@Getter
public class ControllerHandler {

    private final Set<Controller> controllers = new HashSet<>();

    /**
     * Find a {@link Controller} by a {@link Class}
     *
     * @param clazz the class
     * @param <T>   the type of the controller
     * @return the controller
     */
    public <T extends Controller> T find(Class<T> clazz) {
        return clazz.cast(this.controllers.stream()
                .filter(controller -> controller.getClass().equals(clazz))
                .findFirst().orElse(null));
    }

    /**
     * Register a new controller
     *
     * @param controller the controller
     * @param <T>        the type of the controller
     * @return the registered controller | or the previously registered controller.
     */
    @SuppressWarnings("unchecked")
    public <T extends Controller> T register(Controller controller) {
        if (this.controllers.add(controller)) {
            controller.enable();
            return (T) controller;
        }

        return (T) this.find(controller.getClass());
    }

    /**
     * Unregister a controller
     *
     * @param controller the controller to get unregistered
     */
    public void unregister(Class<? extends Controller> controller) {
        if (this.find(controller) != null) {
            this.controllers.remove(this.find(controller));
        }

        System.out.println("Tried unregistering controller which isn't registered");
    }
}