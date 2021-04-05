package rip.vapor.hcf.scoreboard.provider.impl;

import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ClassBoardProvider implements BoardProvider {

    private final ClassModule classModule;

    /**
     * Constructor to make a new class board provider object
     *
     * @param handler the handler to get the modules from
     */
    public ClassBoardProvider(ModuleHandler handler) {
        this.classModule = handler.find(ClassModule.class);
    }

    @Override
    public List<String> getStrings(Player player) {
        return classModule.getClasses().stream()
                .filter(clazz -> clazz.getEquipped().contains(player))
                .map(clazz -> clazz.getStrings(player)).findFirst().orElseGet(ArrayList::new);
    }
}