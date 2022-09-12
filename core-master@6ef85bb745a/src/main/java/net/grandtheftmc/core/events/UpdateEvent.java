package net.grandtheftmc.core.events;

import net.grandtheftmc.core.users.Pref;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class UpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final UpdateReason reason;
    private Pref pref;

    public UpdateEvent(Player player, UpdateReason reason) {
        super(player);
        this.reason = reason;
    }

    public UpdateEvent(Player player, Pref pref) {
        super(player);
        this.reason = UpdateReason.PREF;
        this.pref = pref;
    }

    public UpdateReason getReason() {
        return this.reason;
    }

    public Pref getPref() {
        return this.pref;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum UpdateReason {
        BOARD,
        BUCKS,
        MONEY,
        TOKENS,
        OTHER,
        RANK,
        CROWBARS,
        PREF
    }
}