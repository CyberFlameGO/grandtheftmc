package net.grandtheftmc.gtm.items;

public class KitItem {

    private final GameItem item;
    private int amount;

    public KitItem(GameItem item) {
        this.item = item;
        this.amount = 1;
    }

    public KitItem(GameItem item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public GameItem getItem() {
        return this.item;
    }

    public GameItem getGameItem() {
        return this.item;
    }

    public String getDescription() {
        return (this.amount > 1 ? "&7" + this.amount + "x " : "") + this.item.getDisplayName();
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
