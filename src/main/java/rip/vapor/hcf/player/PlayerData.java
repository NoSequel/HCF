package rip.vapor.hcf.player;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.data.Data;
import rip.vapor.hcf.data.Loadable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class PlayerData implements Loadable<Data> {

    private UUID uniqueId;
    private List<Data> data = new ArrayList<>();

    private final PlayerDataModule controller = Vapor.getInstance().getHandler().find(PlayerDataModule.class);

    /**
     * Constructor for creating a new Profile
     * This constructor automatically adds the profile to the profile list
     *
     * @param uniqueId the uuid
     */
    public PlayerData(UUID uniqueId) {
        this.uniqueId = uniqueId;

        controller.getPlayerData().add(this);
        controller.load(this);
    }
}