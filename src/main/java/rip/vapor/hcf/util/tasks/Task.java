package rip.vapor.hcf.util.tasks;

import rip.vapor.hcf.Vapor;
import lombok.SneakyThrows;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Task extends BukkitRunnable {

    public Task(long duration) {
        this.runTaskTimer(Vapor.getInstance(), 0, duration);
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            this.tick();
        } catch (Exception e) {
            Exception exception = new RuntimeException("Error occured in " + getName() + "");
            exception.setStackTrace(e.getStackTrace());

            throw exception;
        }
    }

    /**
     * Called upon tick of the task
     *
     * @throws Exception all exceptions thrown
     */
    public abstract void tick() throws Exception;

    /**
     * Get the name of the task
     *
     * @return the name
     */
    public abstract String getName();

}