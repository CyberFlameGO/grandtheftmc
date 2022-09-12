package net.grandtheftmc.vice.items;

import net.grandtheftmc.core.util.Utils;

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
        try {
            return (this.amount > 1 ? "&7" + this.amount + "x " : "") + this.item == null ? "ERROR!!!" : this.item.getDisplayName();
        } catch (Exception e) {
            Utils.b("amnt " + this.amount);
            Utils.b("item " + this.item == null ? null : this.item.getName());
            return "ERROR!!! dsqd";
        }
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
