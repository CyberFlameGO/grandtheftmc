package net.grandtheftmc.vice.tasks;

import java.util.UUID;

public class LotteryPlayer {
    private final UUID uuid;
    private final String name;
    private int tickets;

    private double amount;
    private boolean paid;

    public LotteryPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getTickets() {
        return this.tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public void addTickets(int tickets) {
        this.tickets += tickets;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setPaid(boolean b) {
        this.paid = b;
    }

    public boolean isPaid() {
        return this.paid;
    }

    @Override
    public String toString() {
        return this.name;
    }


}
