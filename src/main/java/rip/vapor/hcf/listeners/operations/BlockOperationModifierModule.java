package rip.vapor.hcf.listeners.operations;

import lombok.Getter;
import org.bukkit.block.BlockState;
import rip.vapor.hcf.VaporConstants;
import rip.vapor.hcf.listeners.operations.impl.BrewingStandBlockOperation;
import rip.vapor.hcf.listeners.operations.impl.FurnaceBlockOperation;
import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.util.tasks.Task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
public class BlockOperationModifierModule extends Task implements Module {

    private final Set<BlockOperationModifier> modifiers = new HashSet<>();
    private final Set<BlockOperation<?>> operations = new HashSet<>(Arrays.asList(
            new BrewingStandBlockOperation(),
            new FurnaceBlockOperation()
   ));

    /**
     * Constructor to make a new {@link BlockOperationModifierModule} object
     */
    public BlockOperationModifierModule() {
        super(2L);
    }

    @Override
    public void tick() throws Exception {
        this.modifiers.removeIf(modifier -> modifier.getOperation().editTimeCasted(modifier.getBlockState(), VaporConstants.BLOCK_OPERATION_SPEED));
    }

    @Override
    public String getName() {
        return "Block-Operations";
    }

    /**
     * Find a {@link BlockOperation} by a {@link BlockState}
     *
     * @param state the state
     * @return the optional of the block operation
     */
    public Optional<BlockOperation<?>> getOperationType(BlockState state) {
        return this.operations.stream()
                .filter(type -> type.getType().isInstance(state))
                .findFirst();
    }

}
