package net.grandtheftmc.gtm.drugs.events;

import net.grandtheftmc.gtm.drugs.Drug;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Remco on 25-3-2017.
 */
public class DrugUseEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player user;
    private final Drug drug;

    public DrugUseEvent(Player user, Drug drug) {
        this.user = user;
        this.drug = drug;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandler(){
        return handlers;
    }

    public Player getUser() {
        return user;
    }

    public Drug getDrug() {
        return drug;
    }
}
