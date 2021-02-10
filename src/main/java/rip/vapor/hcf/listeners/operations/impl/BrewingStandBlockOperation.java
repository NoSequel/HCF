package rip.vapor.hcf.listeners.operations.impl;

import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import rip.vapor.hcf.listeners.operations.BlockOperation;

public class BrewingStandBlockOperation implements BlockOperation<BrewingStand> {

    @Override
    public boolean editTimeCasted(BlockState state, int duration) {
        return this.editTime(this.cast(state), duration);
    }

    @Override
    public boolean editTime(BrewingStand brewingStand, int duration) {
        if (brewingStand.getInventory().getViewers().isEmpty() && brewingStand.getInventory().getItem(3) == null) {
            return true;
        }

        if (brewingStand.getBrewingTime() > 1) {
            brewingStand.setBrewingTime(Math.max(1, brewingStand.getBrewingTime() - duration));
        }

        return false;
    }

    @Override
    public BrewingStand cast(BlockState state) {
        return this.getType().cast(state);
    }

    @Override
    public Class<BrewingStand> getType() {
        return BrewingStand.class;
    }
}
