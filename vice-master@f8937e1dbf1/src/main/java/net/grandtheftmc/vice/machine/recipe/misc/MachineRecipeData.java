package net.grandtheftmc.vice.machine.recipe.misc;

import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.data.MachineData;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.event.MachineRecipeCompleteEvent;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import org.bukkit.Bukkit;

public final class MachineRecipeData {

    private final BaseMachine machine;
    private final MachineRecipe recipe;
    private final MachineData data;

    private long next = -1;

    public MachineRecipeData(BaseMachine machine, MachineRecipe recipe) {
        this.machine = machine;
        this.recipe = recipe;
        this.data = machine.getData(MachineDataType.PROGRESS);
    }

    public MachineRecipe getRecipe() {
        return this.recipe;
    }

    public boolean next() {
//        double progress = this.getProgress();
//        double max = this.getMaxProgress();
//
//        if (progress >= max) {
//            this.data.setCurrent(0);
//        }
//
//        if (progress + amount >= max) {
//            this.data.setCurrent(max);
//
//            MachineRecipeCompleteEvent event = new MachineRecipeCompleteEvent(this.machine, this);
//            Bukkit.getPluginManager().callEvent(event);
//            return;
//        }
//
//        this.data.add(amount);

        if (this.next < 0) {
            this.next = System.currentTimeMillis() + ((this.recipe.getTime() * 1000) / (int) this.data.getMax());
        }

        if (System.currentTimeMillis() > this.next) {
            this.data.add(1);
            this.next = System.currentTimeMillis() + ((this.recipe.getTime() * 1000) / (int) this.data.getMax());

            if (this.data.getCurrent() == this.data.getMax()) {
                MachineRecipeCompleteEvent event = new MachineRecipeCompleteEvent(this.machine, this);
                Bukkit.getPluginManager().callEvent(event);
            }
            return true;
        }

        return false;
    }
}
