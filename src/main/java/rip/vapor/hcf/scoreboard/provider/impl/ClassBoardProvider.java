package rip.vapor.hcf.scoreboard.provider.impl;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
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
     * Get the strings of the part
     *
     * @param element the element to add the strings to
     * @param player  the player
     */
    @Override
    public void getStrings(ScoreboardElement element, Player player) {
        classModule.getClasses().stream()
                .filter(clazz -> clazz.getEquipped().contains(player))
                .forEach(clazz -> clazz.getStrings(element, player));
    }
}