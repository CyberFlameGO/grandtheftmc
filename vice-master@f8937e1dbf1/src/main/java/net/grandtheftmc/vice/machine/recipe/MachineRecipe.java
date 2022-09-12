package net.grandtheftmc.vice.machine.recipe;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public abstract class MachineRecipe {

    /** Unique Identifier of the Recipe */
    private final int identifier;

    /** Array of input items, size depends on the machine type */
    private final RecipeInput[] input;

    /** Output of the complete Recipe */
    private final RecipeOutput[] output;

    /** Amount of Fuel this Recipe should consume */
    private final int fuelUsage;

    /** Amount of durability this Recipe should consume */
    private final int durabilityUsage;

    /** Time to cook / brew in seconds */
    private final long time;

    /** Recipe string tutorial */
    protected String recipeText;

    /**
     * Construct a new Machine Recipe.
     *
     * @param identifier - Unique Identifier of the Recipe.
     * @param input - Array of input items, size depends on the machine type.
     * @param output - Output of the complete Recipe.
     * @param fuelUsage - Amount of Fuel this Recipe should consume.
     * @param durabilityUsage - Amount of durability this Recipe should consume.
     * @param time - Time to cook / brew in seconds.
     */
    public MachineRecipe(int identifier, RecipeInput[] input, RecipeOutput[] output, int fuelUsage, int durabilityUsage, int time) {
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.fuelUsage = fuelUsage;
        this.durabilityUsage = durabilityUsage;
        this.time = time;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public RecipeInput[] getInput() {
        return this.input;
    }

    public RecipeOutput[] getOutput() {
        return this.output;
    }

    public int getFuelUsage() {
        return this.fuelUsage;
    }

    public int getDurabilityUsage() {
        return this.durabilityUsage;
    }

    public long getTime() {
        return this.time;
    }

    public String getRecipeText() {
        return this.recipeText;
    }

    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }

    public String _(String icon, int amount) {
        return C.RESET + icon + C.GRAY + "x" + amount + C.RESET;
    }
}
