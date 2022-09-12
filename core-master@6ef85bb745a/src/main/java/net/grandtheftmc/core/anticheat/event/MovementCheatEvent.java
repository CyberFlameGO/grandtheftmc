package net.grandtheftmc.core.anticheat.event;

import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.data.ClientData;
import net.grandtheftmc.core.events.CoreEvent;
import org.bukkit.event.Cancellable;

public final class MovementCheatEvent<T> extends CoreEvent implements Cancellable {

    private final ClientData playerData;
    private final CheatType cheatType;
    private T obj;
    private boolean cancelled;

    /**
     * Construct a new Event
     */
    public MovementCheatEvent(ClientData playerData, CheatType cheatType) {
        super(true);
        this.playerData = playerData;
        this.cheatType = cheatType;
    }

    /**
     * Construct a new Event
     */
    public MovementCheatEvent(ClientData playerData, CheatType cheatType, T obj) {
        this(playerData, cheatType);
        this.obj = obj;
    }

    public ClientData getPlayerData() {
        return this.playerData;
    }

    public CheatType getCheatType() {
        return this.cheatType;
    }

    public T getObj() {
        return this.obj;
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
