package net.grandtheftmc.gtm.bounties;

import java.util.UUID;

public class BountyPlacer {

    private UUID uuid;
    private String name;
    private double amount;
    private boolean anonymous;
    private boolean console;

    public BountyPlacer(UUID uuid, String name, double amount, boolean anonymous) {
        this.uuid = uuid;
        this.name = name;
        this.amount = amount;
        this.anonymous = anonymous;
    }

    public BountyPlacer(boolean console, double amount) {
        this.console = console;
        this.amount = amount;
        this.anonymous = true;
    }

    public BountyPlacer(int amount, boolean b) {
        this.amount = amount;
        this.anonymous = b;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID u) {
        this.uuid = u;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double i) {
        this.amount = i;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean b) {
        this.anonymous = b;
    }

    public boolean isConsole() {
        return this.console;
    }
}
