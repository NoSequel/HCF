package rip.vapor.hcf.team.data.impl.player;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlayerRole {

    MEMBER(1, "", "Member"),
    CAPTAIN(2, "*", "Captain"),
    CO_LEADER(3, "**", "Co Leader"),
    LEADER(4, "***", "Leader");

    public final int priority;
    public final String astrix;
    public final String fancyName;

    /**
     * Check if a {@link PlayerRole} is higher than a different role
     *
     * @param role the player role
     * @return whether its higher or not
     */
    public boolean isHigher(PlayerRole role) {
        return priority > role.priority;
    }

}
