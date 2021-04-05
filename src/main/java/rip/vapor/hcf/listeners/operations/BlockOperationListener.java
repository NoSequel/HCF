package rip.vapor.hcf.listeners.operations;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.vapor.hcf.module.ModuleHandler;

import java.util.Optional;

@RequiredArgsConstructor
public class BlockOperationListener implements Listener {

    private final BlockOperationModifierModule modifierModule;

    /**
     * Constructor to make a new block operation listener instance
     *
     * @param handler the handler to get the modules from
     */
    public BlockOperationListener(ModuleHandler handler) {
        this.modifierModule = handler.find(BlockOperationModifierModule.class);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (block != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Optional<BlockOperation<?>> operation = this.modifierModule.getOperationType(block.getState());

            operation.ifPresent(blockOperation -> this.modifierModule.getModifiers().add(new BlockOperationModifier(block.getState(), blockOperation)));
        }
    }
}