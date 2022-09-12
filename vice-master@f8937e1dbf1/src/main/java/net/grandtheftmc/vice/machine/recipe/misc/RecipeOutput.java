package net.grandtheftmc.vice.machine.recipe.misc;

import org.bukkit.inventory.ItemStack;

public final class RecipeOutput {

    private final ItemStack itemStack;
    private final int amount;

    public RecipeOutput(ItemStack itemStack, int amount) {
        this.itemStack = itemStack;
        this.amount = amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }
}
