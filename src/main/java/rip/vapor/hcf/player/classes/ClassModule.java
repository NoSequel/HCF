package rip.vapor.hcf.player.classes;

import rip.vapor.hcf.player.classes.archer.ArcherClass;
import rip.vapor.hcf.player.classes.bard.BardClass;
import rip.vapor.hcf.module.Module;
import lombok.Getter;
import rip.vapor.hcf.player.classes.rogue.RogueClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ClassModule implements Module {

    private final List<Class<?>> classes = new ArrayList<>();

    public ClassModule() {
        classes.addAll(Arrays.asList(
                new BardClass(),
                new ArcherClass(),
                new RogueClass()
        ));
    }

    /**
     * Find a class by a name
     *
     * @param className the name
     * @return the class | or null
     */
    public Class<?> findClass(String className) {
        return this.classes.stream()
                .filter(clazz -> clazz.getClassName().equalsIgnoreCase(className))
                .findFirst().orElse(null);
    }

    /**
     * Find a class by their designated java.lang.Class<T>
     *
     * @param clazz the class
     * @param <T>   the type of the class
     * @return the found class | or null
     */
    public <T extends Class<?>> T findClass(java.lang.Class<T> clazz) {
        return clazz.cast(this.classes.stream()
                .filter($clazz -> $clazz.getClass().equals(clazz))
                .findFirst().orElse(null));
    }

}
