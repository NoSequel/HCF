package rip.vapor.hcf.listeners.operations;

import org.bukkit.block.BlockState;

public interface BlockOperation<T extends BlockState> {

    /**
     * Edit the time of a block
     * 
     * @param state    the block
     * @param duration the duration
     * @return whether it should remove it from the list of operations
     */
    boolean editTimeCasted(BlockState state, int duration);
    
    /**
     * Edit the time of the block
     *
     * @param block    the block
     * @param duration the duration
     */
    boolean editTime(T block, int duration);

    /**
     * Cast a {@link BlockState} to {@link T}
     *
     * @param state the state
     * @return the casted block state
     */
    T cast(BlockState state);

    /**
     * Get the type of the {@link BlockOperation} object
     *
     * @return the type
     */
    Class<T> getType();

}
