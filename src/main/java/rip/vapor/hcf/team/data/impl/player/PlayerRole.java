package rip.vapor.hcf.team.data.impl.player;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlayerRole {

    MEMBER(1, ""),
    CAPTAIN(2, "*"),
    CO_LEADER(3, "**"),
    LEADER(4, "***");

    public final int priority;
    public final String astrix;

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
