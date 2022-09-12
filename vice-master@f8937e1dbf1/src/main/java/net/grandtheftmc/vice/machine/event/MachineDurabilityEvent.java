package net.grandtheftmc.vice.machine.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.data.MachineData;
import org.bukkit.event.Cancellable;

public final class MachineDurabilityEvent extends CoreEvent implements Cancellable {

    private final BaseMachine machine;
    private final MachineData data;
    private boolean cancelled;

    public MachineDurabilityEvent(BaseMachine machine, MachineData data) {
        super(false);
        this.machine = machine;
        this.data = data;
    }

    public BaseMachine getMachine() {
        return machine;
    }

    public MachineData getData() {
        return data;
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
