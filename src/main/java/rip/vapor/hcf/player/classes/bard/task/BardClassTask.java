package rip.vapor.hcf.player.classes.bard.task;

import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.player.classes.ClassModule;
import rip.vapor.hcf.player.classes.ability.TickableAbility;
import rip.vapor.hcf.player.classes.bard.BardClass;
import rip.vapor.hcf.player.classes.bard.BardClassData;
import rip.vapor.hcf.util.tasks.Task;

public class BardClassTask extends Task {

    private final BardClass bardClass;

    public BardClassTask(ModuleHandler handler) {
        super(20);
        this.bardClass = handler.find(ClassModule.class).findClass(BardClass.class);
    }

    @Override
    public void tick() {
        bardClass.getEquipped().forEach(player -> {
            if (bardClass.getClassData().containsKey(player)) {
                final BardClassData classData = bardClass.getClassData().get(player);

                if (classData.getEnergy() < 120) {
                    final long newEnergy = classData.getEnergy() + 1;

                    classData.setEnergy(newEnergy);
                }

                bardClass.getAbilities().stream()
                        .filter(ability -> ability instanceof TickableAbility)
                        .forEach(ability -> ((TickableAbility) ability).tick(player));
            }

        });
    }

    @Override
    public String getName() {
        return "BardClass-Task";
    }
}