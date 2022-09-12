package net.grandtheftmc.core.inventory.button;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public class ClickableItem extends MenuItem {

    private IMenuClickAction clickAction;

    /**
     * Construct a new MenuItem
     *
     * @param index       menu slot index
     * @param itemStack   menu itemstack
     */
    public ClickableItem(int index, ItemStack itemStack, IMenuClickAction clickAction) {
        super(index, itemStack, false);
        this.clickAction = clickAction;
    }

    /**
     * Construct a new MenuItem
     *
     * @param index       menu slot index
     * @param itemStack   menu itemstack
     */
    public ClickableItem(int index, ItemStack itemStack, boolean allowPickup, IMenuClickAction clickAction) {
        super(index, itemStack, allowPickup);
        this.clickAction = clickAction;
    }

    public ClickableItem(int index, ItemStack itemStack, IMenuClickAction clickAction, boolean pickup) {
        super(index, itemStack, pickup);
        this.clickAction = clickAction;
    }

    public IMenuClickAction getClickAction() {
        return clickAction;
    }
}
