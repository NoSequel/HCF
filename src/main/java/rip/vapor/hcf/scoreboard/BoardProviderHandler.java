package rip.vapor.hcf.scoreboard;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
import io.github.nosequel.scoreboard.element.ScoreboardElementHandler;
import rip.vapor.hcf.scoreboard.provider.impl.ClassBoardProvider;
import rip.vapor.hcf.scoreboard.provider.impl.TimerBoardProvider;
import rip.vapor.hcf.scoreboard.provider.BoardProvider;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BoardProviderHandler implements ScoreboardElementHandler {

    private final List<BoardProvider> providers = new ArrayList<>(Arrays.asList(
            new TimerBoardProvider(),
            new ClassBoardProvider()
    ));

    @Override
    public ScoreboardElement getElement(Player player) {
        final ScoreboardElement element = new ScoreboardElement();
        final List<String> lines = providers.stream().map(provider -> provider.getStrings(player)).flatMap(Collection::stream).collect(Collectors.toList());

        element.setTitle(ChatColor.AQUA + "Vapor" + ChatColor.GRAY + ChatColor.BOLD + " ｜ " + ChatColor.WHITE + "Squads");
        element.add(" ");
        element.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");

        if (!lines.isEmpty()) {
            lines.forEach(element::add);

            element.add("");
            element.add(ChatColor.AQUA + ChatColor.ITALIC.toString() + "vapor.rip");
            element.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        } else {
            element.getLines().clear();
        }

        return element;
    }
}