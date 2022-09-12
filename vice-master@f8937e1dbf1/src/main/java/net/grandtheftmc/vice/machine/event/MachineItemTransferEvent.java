package net.grandtheftmc.vice.machine.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.vice.machine.BaseMachine;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class MachineItemTransferEvent extends CoreEvent implements Cancellable {

    private final BaseMachine machine;
    private final Inventory from, to;
    private final TransferType transferType;
    private final ItemStack itemStack;

    private boolean cancelled, transferred;

    public MachineItemTransferEvent(BaseMachine machine, Inventory from, Inventory to, TransferType transferType, ItemStack itemStack) {
        super(false);
        this.machine = machine;
        this.from = from;
        this.to = to;
        this.transferType = transferType;
        this.itemStack = itemStack;
    }

    public BaseMachine getMachine() {
        return machine;
    }

    public Inventory getFrom() {
        return from;
    }

    public Inventory getTo() {
        return to;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }

    public static enum TransferType {
        TO_MACHINE,
        FROM_MACHINE,
        ;
    }
}
