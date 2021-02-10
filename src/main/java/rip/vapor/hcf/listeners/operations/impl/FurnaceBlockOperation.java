package rip.vapor.hcf.listeners.operations.impl;

import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import rip.vapor.hcf.listeners.operations.BlockOperation;

public class FurnaceBlockOperation implements BlockOperation<Furnace> {

    @Override
    public boolean editTimeCasted(BlockState state, int duration) {
        return this.editTime(this.cast(state), duration);
    }
    @Override
    public boolean editTime(Furnace furnace, int duration) {
        if (furnace.getInventory().getItem(0) != null) {
            if (furnace.getCookTime() > 0 || furnace.getBurnTime() > 0) {
                furnace.setCookTime((short) (furnace.getCookTime() + duration));
                furnace.setBurnTime((short) (furnace.getBurnTime() + duration));
            }
        } else {
            if (furnace.getInventory().getViewers().isEmpty()) {
                return true;
            }

            furnace.setCookTime((short) 0);
            furnace.setBurnTime((short) 0);
        }

        return false;
    }

    @Override
    public Furnace cast(BlockState state) {
        return this.getType().cast(state);
    }

    @Override
    public Class<Furnace> getType() {
        return Furnace.class;
    }
}
