package net.grandtheftmc.vice.machine.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.data.MachineData;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;

public final class MachineFuelEvent extends CoreEvent implements Cancellable {

    private final BaseMachine machine;
    private final MachineData data;
    private final Material fuelType;
    private final int slot;

    private boolean cancelled;

    public MachineFuelEvent(BaseMachine machine, MachineData data, Material fuelType, int slot) {
        super(false);
        this.machine = machine;
        this.data = data;
        this.fuelType = fuelType;
        this.slot = slot;
    }

    public BaseMachine getMachine() {
        return machine;
    }

    public MachineData getData() {
        return data;
    }

    public Material getFuelType() {
        return fuelType;
    }

    public int getSlot() {
        return slot;
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
