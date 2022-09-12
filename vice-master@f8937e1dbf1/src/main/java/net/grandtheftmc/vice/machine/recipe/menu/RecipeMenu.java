package net.grandtheftmc.vice.machine.recipe.menu;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import org.bukkit.inventory.ItemStack;

public abstract class RecipeMenu extends CoreMenu {

    public final short progress = 65;
    private final MachineRecipe recipe;

    private int[] openSlots, outputSlots, blockedSlots;

    public RecipeMenu(int rows, String title, MachineRecipe recipe, CoreMenuFlag... menuFlags) {
        super(rows, title, menuFlags);
        this.recipe = recipe;
    }

    public int[] getOpenSlots() {
        return openSlots;
    }

    public void setOpenSlots(int... openSlots) {
        this.openSlots = openSlots;
    }

    public void setOutputSlots(int... outputSlots) {
        this.outputSlots = outputSlots;
    }

    public int[] getOutputSlots() {
        return outputSlots;
    }

    public void setBlockedSlots(int... blockedSlots) {
        this.blockedSlots = blockedSlots;
    }

    public int[] getBlockedSlots() {
        return blockedSlots;
    }

    protected String i(String icon, int amount) {
        return C.RESET + icon + C.GRAY + "x" + amount + C.RESET;
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }

    public void initRecipeItems() {
        for (int i = 0; i < this.openSlots.length; i++) {
            RecipeInput input = this.recipe.getInput()[i];
            ItemStack item = input.getItemStack().clone();
            item.setAmount(input.getAmount());
            super.addItem(new MenuItem(this.openSlots[i], item, false));
        }

        for (int i = 0; i < this.outputSlots.length; i++) {
            RecipeOutput output = this.recipe.getOutput()[i];
            ItemStack item = output.getItemStack().clone();
            item.setAmount(output.getAmount());
            super.addItem(new MenuItem(this.outputSlots[i], output.getItemStack().clone(), false));
        }
    }
}
