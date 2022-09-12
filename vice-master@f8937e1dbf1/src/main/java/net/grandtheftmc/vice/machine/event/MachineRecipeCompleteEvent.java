package net.grandtheftmc.vice.machine.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.recipe.misc.MachineRecipeData;
import org.bukkit.event.Cancellable;

public final class MachineRecipeCompleteEvent extends CoreEvent implements Cancellable {

    private final BaseMachine machine;
    private final MachineRecipeData recipeData;
    private boolean cancelled;

    public MachineRecipeCompleteEvent(BaseMachine machine, MachineRecipeData recipeData) {
        super(false);
        this.machine = machine;
        this.recipeData = recipeData;
    }

    public BaseMachine getMachine() {
        return machine;
    }

    public MachineRecipeData getRecipeData() {
        return recipeData;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
