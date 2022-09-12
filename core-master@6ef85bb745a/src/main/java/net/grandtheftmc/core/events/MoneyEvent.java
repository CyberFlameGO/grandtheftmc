package net.grandtheftmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class MoneyEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private UUID uuid;
    private MoneyEventType type;
    private double amount;
    private double balance;
    private boolean successful;

    public MoneyEvent(UUID uuid) {
        this.uuid = uuid;
        this.type = MoneyEventType.BALANCE;
    }

    public MoneyEvent(UUID uuid, double amount) {
        this.uuid = uuid;
        if (amount < 0) {
            this.amount = -amount;
            this.type = MoneyEventType.TAKE;
        } else {
            this.amount = amount;
            this.type = MoneyEventType.ADD;
        }
    }

    public MoneyEvent(UUID uuid, MoneyEventType type, double amount) {
        this.uuid = uuid;
        this.type = type;
        this.amount = amount;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public MoneyEventType getType() {
        return this.type;
    }

    public void setType(MoneyEventType type) {
        this.type = type;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double d) {
        this.balance = d;
    }

    // TODO please fix spelling (http://www.dictionary.com/browse/successful)
    public boolean isSuccessfull() {
        return this.successful;
    }

    public void setSuccessfull() {
        this.successful = true;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum MoneyEventType {
        TAKE,
        ADD,
        BALANCE
    }
}
