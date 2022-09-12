package net.grandtheftmc.vice.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TPEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private Player sender;
    private Player target;
    private TPType type;
    private String cancelMessage;
    private Location targetLocation;

    public TPEvent(Player sender, TPType type) {
        this.sender = sender;
        this.type = type;
    }

    public TPEvent(Player sender, Player target, TPType type) {
        this.sender = sender;
        this.target = target;
        this.type = type;
    }


    public TPType getType() {
        return this.type;
    }

    public void setType(TPType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return this.cancelMessage != null;
    }

    public String getCancelMessage() {
        return this.cancelMessage;
    }

    public void setCancelled(String msg) {
        this.cancelMessage = msg;
    }

    public TPEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }

    public Location getTargetLocation() {
        return this.targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public boolean targetLocationIsChanged() {
        return this.targetLocation != null;
    }


    public Player getSender() {
        return this.sender;
    }

    public Player getPlayer() {
        return this.sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }


    public Player getTarget() {
        return this.target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }


    public enum TPType {
        TPA_REQ,
        TPAHERE_REQ,
        TPA_ACCEPT,
        TPAHERE_ACCEPT,
        TP_COMPLETE,
        WARP,
        VEHICLE_SEND_AWAY,
        VEHICLE_CALL,
        HOUSE_ENTER,
        HOUSE_LEAVE,
        PREMIUM_HOUSE_ENTER,
        PREMIUM_HOUSE_LEAVE,
        BACKUP

    }

}
