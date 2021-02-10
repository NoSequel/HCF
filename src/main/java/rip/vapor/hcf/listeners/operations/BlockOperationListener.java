package rip.vapor.hcf.listeners.operations;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.vapor.hcf.module.Controllable;

import java.util.Optional;

public class BlockOperationListener implements Listener, Controllable<BlockOperationModifierModule> {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (block != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Optional<BlockOperation<?>> operation = this.getModule().getOperationType(block.getState());

            operation.ifPresent(blockOperation -> this.getModule().getModifiers().add(new BlockOperationModifier(block.getState(), blockOperation)));
        }
    }
}