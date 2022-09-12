package net.grandtheftmc.vice.dropship.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.dropship.DropShip;

public final class DropShipStartEvent extends CoreEvent {

    private final DropShip dropShip;

    public DropShipStartEvent(DropShip dropShip) {
        super(false);
        this.dropShip = dropShip;
    }

    public DropShip getDropShip() {
        return dropShip;
    }
}
