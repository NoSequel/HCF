package rip.vapor.hcf.scoreboard;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
import io.github.nosequel.scoreboard.element.ScoreboardElementHandler;
import lombok.RequiredArgsConstructor;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BoardProviderHandler implements ScoreboardElementHandler {

    private final List<BoardProvider> providers;

    @Override
    public ScoreboardElement getElement(Player player) {
        final ScoreboardElement element = new ScoreboardElement();

        providers.forEach(provider -> provider.getStrings(element, player));

        element.setTitle(ChatColor.AQUA + "Vapor" + ChatColor.GRAY + ChatColor.BOLD + " ｜ " + ChatColor.WHITE + "Squads");

        if (!element.getLines().isEmpty()) {
            element.getLines().add(0, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            element.add("");
            element.add(ChatColor.AQUA + ChatColor.ITALIC.toString() + "vapor.rip");
            element.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        }

        return element;
    }
}