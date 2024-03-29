package rip.vapor.hcf.player.data.deathban.impl.natural;

public enum NaturalDeathbanType {

    FALL_DAMAGE("died by fall damage"),
    MONSTER("have been killed by a monster"),
    UNDEFINED("have died because of a natural cause");

    public String reason;

    /**
     * Constructor for creating a new NaturalDeathbanType
     *
     * @param reason the reason why the player had died
     */
    NaturalDeathbanType(String reason) {
        this.reason = reason;
    }
}
