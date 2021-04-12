package rip.vapor.hcf.scoreboard.provider;

import io.github.nosequel.scoreboard.element.ScoreboardElement;
import org.bukkit.entity.Player;

import java.util.List;

public interface BoardProvider {

    /**
     * Get the strings of the part
     *
     * @param player  the player
     * @param element the element to add the strings to
     */
    void getStrings(ScoreboardElement element, Player player);

}