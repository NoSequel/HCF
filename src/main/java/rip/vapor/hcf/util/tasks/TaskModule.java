package rip.vapor.hcf.util.tasks;

import rip.vapor.hcf.Vapor;
import rip.vapor.hcf.listeners.operations.BlockOperationModifierModule;
import rip.vapor.hcf.player.classes.bard.task.BardClassTask;
import rip.vapor.hcf.module.Module;
import rip.vapor.hcf.util.tasks.impl.BackupTask;
import rip.vapor.hcf.util.tasks.impl.DTRTask;
import org.bukkit.scheduler.BukkitRunnable;
import rip.vapor.hcf.util.tasks.impl.NametagTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskModule implements Module {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void enable() {
        tasks.addAll(Arrays.asList(
                new DTRTask(),
                new BardClassTask(),
                new NametagTask(),
                new BackupTask(),
                Vapor.getInstance().getHandler().find(BlockOperationModifierModule.class)
        ));
    }

    @Override
    public void disable() {
        tasks.forEach(BukkitRunnable::cancel);
    }

}
