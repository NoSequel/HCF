package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class  ClassBoardProvider implements BoardProvider {

    private final ClassController classController = Vapor.getInstance().getHandler().findController(ClassController.class);

    @Override
    public List<String> getStrings(Player player) {
        final List<String> strings = new ArrayList<>();

        classController.getClasses().stream()
                .filter(clazz -> clazz.getEquipped().contains(player))
                .map(clazz -> clazz.getStrings(player))
                .forEach(strings::addAll);

        return strings;
    }
}
