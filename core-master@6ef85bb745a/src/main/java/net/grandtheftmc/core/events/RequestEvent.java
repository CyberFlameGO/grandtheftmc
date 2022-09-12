package net.grandtheftmc.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;
import java.util.HashMap;

public class RequestEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Collection<? extends Player> players;
    private String type;
    private HashMap<Player, Double> result;

    public RequestEvent(Collection<? extends Player> players, String type) {
        this.players = players;
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<Player, Double> getResult() {
        return this.result;
    }

    public void setResult(HashMap<Player, Double> result) {
        this.result = result;
    }

    public Collection<? extends Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Collection<? extends Player> players) {
        this.players = players;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
