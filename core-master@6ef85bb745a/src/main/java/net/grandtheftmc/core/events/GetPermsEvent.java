package net.grandtheftmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GetPermsEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final UUID uuid;
    private final List<String> perms = new ArrayList<>();

    public GetPermsEvent(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void addPerm(String perm) {
        if(perm != null && !this.perms.contains(perm)) this.perms.add(perm);
    }

    public List<String> getPerms() {
        return this.perms;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}