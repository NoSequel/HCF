package rip.vapor.hcf.team.menu;

import io.github.nosequel.menus.button.Button;
import io.github.nosequel.menus.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import rip.vapor.hcf.team.Team;
import rip.vapor.hcf.team.data.impl.player.PlayerTeamData;
import rip.vapor.hcf.team.menu.button.TeamInformationButton;
import rip.vapor.hcf.team.menu.button.TeamMemberButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GeneralTeamMenu extends PaginatedMenu {

    private final Team team;

    /**
     * Constructor to make a new {@link GeneralTeamMenu} object
     *
     * @param player the player
     * @param team   the team
     */
    public GeneralTeamMenu(Player player, Team team) {
        super(player, "Team Information", 9);
        this.team = team;
    }

    @Override
    public List<Button> getButtonsInRange() {
        final List<Button> buttons = super.getButtonsInRange();

        buttons.add(new TeamInformationButton(4, this.team));

        return buttons;
    }

    @Override
    public List<Button> getButtons() {
        final AtomicInteger index = new AtomicInteger();
        final PlayerTeamData playerData = this.team.findData(PlayerTeamData.class);

        return playerData == null ? Collections.emptyList() : playerData.getAllMembers().stream()
                .map(uuid -> new TeamMemberButton(index.getAndIncrement(), playerData, uuid, this))
                .collect(Collectors.toList());
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }
}
