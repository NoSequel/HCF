package rip.vapor.hcf.player;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.controller.Controller;
import rip.vapor.hcf.data.Data;
import rip.vapor.hcf.data.DataController;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.player.data.CombatLoggerData;
import rip.vapor.hcf.player.data.SpawnProtectionData;
import rip.vapor.hcf.player.data.deathban.impl.PlayerDeathbanData;
import rip.vapor.hcf.player.data.deathban.impl.natural.NaturalDeathbanData;
import rip.vapor.hcf.util.database.DatabaseController;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerDataController implements Controller, DataController<PlayerData, Data> {

    private final List<PlayerData> playerData = new ArrayList<>();
    private final List<? extends Data> registeredData = new ArrayList<>(Arrays.asList(
            new ClaimSelectionData(),
            new PlayerDeathbanData(),
            new NaturalDeathbanData(),
            new SpawnProtectionData(),
            new CombatLoggerData()
    ));

    /**
     * Find a {@link PlayerData} instance by an UUID
     *
     * @param uuid the uuid
     * @return the found player data | or null
     */
    public PlayerData findPlayerData(UUID uuid) {
        return this.playerData.stream()
                .filter($playerData -> $playerData.getUniqueId().equals(uuid))
                .findFirst().orElse(null);
    }

    /**
     * Get a {@link PlayerData} object by a {@link UUID}
     * If a {@link PlayerData} object could not be found, it will create a new object.
     *
     * @param uuid the unique identifier of the profile
     * @return the profile
     */
    public PlayerData findOrElseMake(UUID uuid) {
        return this.playerData.stream()
                .filter(data -> data.getUniqueId().equals(uuid))
                .findFirst().orElseGet(() -> new PlayerData(uuid));
    }

    @Override
    public void disable() {
        final DatabaseController controller = Vapor.getInstance().getHandler().find(DatabaseController.class);

        playerData.forEach(loadable -> controller.getDataHandler().save(loadable, "profiles"));
    }

    @Override
    public void load(PlayerData loadable) {
        final DatabaseController controller = Vapor.getInstance().getHandler().find(DatabaseController.class);

        controller.getDataHandler().load(this, loadable, "profiles");
    }
}