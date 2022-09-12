package net.grandtheftmc.core.anticheat.check;

import net.grandtheftmc.core.anticheat.trigger.Trigger;

public class Check {

    protected String name;

    public Check(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double analyse(Trigger trigger) {
        return 0D;
    }
}
