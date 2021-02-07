package rip.vapor.hcf.scoreboard.provider.impl;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class  ClassBoardProvider implements BoardProvider {

    private final ClassModule classController = Vapor.getInstance().getHandler().find(ClassModule.class);

    @Override
    public List<String> getStrings(Player player) {
        return classController.getClasses().stream()
                .filter(clazz -> clazz.getEquipped().contains(player))
                .map(clazz -> clazz.getStrings(player)).findFirst().orElseGet(ArrayList::new);
    }
}