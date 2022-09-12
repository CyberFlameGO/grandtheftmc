package net.grandtheftmc.core.anticheat.trigger;

import net.grandtheftmc.core.anticheat.data.ClientData;

public class Trigger {

    private ClientData data;
    private double timeCreated;

    /**
     * Construct new Trigger.
     */
    public Trigger(ClientData data, double timeCreated) {
        this.data = data;
        this.timeCreated = timeCreated;
    }

    public double getTimeCreated() {
        return timeCreated;
    }

    public ClientData getData() {
        return data;
    }
}
