package rip.vapor.hcf.listeners.operations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.BlockState;

@RequiredArgsConstructor
@Getter
public class BlockOperationModifier {

    private final BlockState blockState;
    private final BlockOperation<?> operation;

}
