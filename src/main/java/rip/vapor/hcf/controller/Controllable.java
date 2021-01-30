package rip.vapor.hcf.controller;

import rip.vapor.hcf.Vapor;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public interface Controllable<T extends Controller> {

    /**
     * Get the controller which controls the object
     *
     * @return the controller
     */
    @SuppressWarnings("unchecked")
    default T getController() {
        final ParameterizedType interfaceClass = (ParameterizedType) Arrays.stream(this.getClass().getGenericInterfaces())
                .filter(type -> type.getTypeName().contains(Controllable.class.getSimpleName()))
                .findFirst().orElse(null);

        if (interfaceClass != null) {
            return Vapor.getInstance().getHandler().findController((Class<T>) interfaceClass.getActualTypeArguments()[0]);
        }

        throw new IllegalStateException("No interface by name Controllable found.");
    }
}