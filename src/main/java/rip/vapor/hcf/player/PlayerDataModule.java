package rip.vapor.hcf.player;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.data.Data;
import rip.vapor.hcf.data.DataController;
import rip.vapor.hcf.player.data.BalanceData;
import rip.vapor.hcf.player.data.ClaimSelectionData;
import rip.vapor.hcf.player.data.CombatLoggerData;
import rip.vapor.hcf.player.data.SpawnProtectionData;
import rip.vapor.hcf.player.data.deathban.impl.PlayerDeathbanData;
import rip.vapor.hcf.player.data.deathban.impl.natural.NaturalDeathbanData;
import rip.vapor.hcf.util.database.DatabaseModule;
import lombok.Getter;

import java.util.*;

@Getter
public class PlayerDataModule implements Module, DataController<PlayerData, Data> {

    private final List<PlayerData> playerData = new ArrayList<>();
    private final List<? extends Data> registeredData = new ArrayList<>(Arrays.asList(
            new ClaimSelectionData(),
            new PlayerDeathbanData(),
            new NaturalDeathbanData(),
            new SpawnProtectionData(),
            new CombatLoggerData(),
            new BalanceData()
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
                .findFirst().orElseGet(() -> new PlayerData(uuid, Collections.emptyList()));
    }

    @Override
    public void disable() {
        final DatabaseModule controller = Vapor.getInstance().getHandler().find(DatabaseModule.class);

        playerData.forEach(loadable -> controller.getDataHandler().save(loadable, "profiles"));
    }

    @Override
    public void load(PlayerData loadable) {
        final DatabaseModule controller = Vapor.getInstance().getHandler().find(DatabaseModule.class);

        controller.getDataHandler().load(this, PlayerData.class, loadable.getUniqueId(), "profiles");
    }
}