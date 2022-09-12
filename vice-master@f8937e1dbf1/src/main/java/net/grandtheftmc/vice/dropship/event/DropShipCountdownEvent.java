package net.grandtheftmc.vice.dropship.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.dropship.DropShip;

public final class DropShipCountdownEvent extends CoreEvent {

    private final DropShip dropShip;
    private final int timer;

    public DropShipCountdownEvent(DropShip dropShip, int timer) {
        super(false);
        this.dropShip = dropShip;
        this.timer = timer;
    }

    public DropShip getDropShip() {
        return dropShip;
    }

    public int getTimer() {
        return timer;
    }
}
