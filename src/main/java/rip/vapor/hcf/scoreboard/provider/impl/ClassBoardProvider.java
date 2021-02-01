package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassController;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class  ClassBoardProvider implements BoardProvider {

    private final ClassController classController = Vapor.getInstance().getHandler().find(ClassController.class);

    @Override
    public List<String> getStrings(Player player) {
        return classController.getClasses().stream()
                .filter(clazz -> clazz.getEquipped().contains(player))
                .map(clazz -> clazz.getStrings(player)).findFirst().orElseGet(ArrayList::new);
    }
}