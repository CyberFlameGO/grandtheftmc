package com.j0ach1mmall3.wastedcops.api.events;

import com.j0ach1mmall3.wastedcops.api.Cop;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CopEvent extends Event implements Cancellable {
    private final Cop cop;
    private boolean cancelled;

    protected CopEvent(Cop cop) {
        this.cop = cop;
    }

    public Cop getCop() {
        return this.cop;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}