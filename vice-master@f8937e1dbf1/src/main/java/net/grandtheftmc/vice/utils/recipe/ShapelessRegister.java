package net.grandtheftmc.vice.utils.recipe;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessRegister {

    private final NamespacedKey key;
    private final ItemStack output;
    private final List<ItemStack> ingredients = new ArrayList<>();

    /**
     * Create a shapeless recipe to craft the specified ItemStack. The
     * constructor merely determines the result and type; to set the actual
     * recipe, you'll need to call the appropriate methods.
     *
     * @param result The item you want the recipe to create.
     * @see ShapelessRecipe#addIngredient(Material)
     * @see ShapelessRecipe#addIngredient(int, Material)
     * @see ShapelessRecipe#addIngredient(ItemStack)
     * @see ShapelessRecipe#addIngredient(int, ItemStack)
     */
    public ShapelessRegister(NamespacedKey key, ItemStack result) {
        this.key = key;
        output = new ItemStack(result);
    }

    public ItemStack getOutput() {
        return output.clone();
    }

    /**
     * Adds the specified ingredient.
     *
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRegister addIngredient(Material mat) {
        return addIngredient(1, mat);
    }

    /**
     * Adds multiples of the specified ingredient.
     *
     * @param count      How many to add (can't be more than 9!)
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRegister addIngredient(int count, Material mat) {
        return addIngredient(count, new ItemStack(mat, 1));
    }

    /**
     * Adds the specified ingredient.
     *
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRegister addIngredient(ItemStack item) {
        return addIngredient(1, item);
    }

    /**
     * Adds multiples of the specified ingredient.
     *
     * @param count      How many to add (can't be more than 9!)
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRegister addIngredient(int count, ItemStack item) {
        Validate.isTrue(ingredients.size() + count <= 9, "Shapeless recipes cannot have more than 9 ingredients");

        while (count-- > 0)
            ingredients.add(item);

        return this;
    }

    /**
     * Removes an ingredient from the list. If the ingredient occurs multiple
     * times, only one instance of it is removed.
     *
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRegister removeIngredient(Material mat) {
        return removeIngredient(1, mat);
    }

    /**
     * Removes multiple instances of an ingredient from the list. If there are
     * less instances then specified, all will be removed.
     *
     * @param count      The number of copies to remove.
     * @param ingredient The ingredient to remove.
     * @return The changed recipe.
     */
    public ShapelessRegister removeIngredient(int count, Material mat) {
        return removeIngredient(count, new ItemStack(mat, 1));
    }

    /**
     * Removes an ingredient from the list. If the ingredient occurs multiple
     * times, only one instance of it is removed.
     *
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRegister removeIngredient(ItemStack item) {
        return removeIngredient(1, item);
    }

    /**
     * Removes multiple instances of an ingredient from the list. If there are
     * less instances then specified, all will be removed.
     *
     * @param count      The number of copies to remove.
     * @param ingredient The ingredient to remove.
     * @return The changed recipe.
     */
    public ShapelessRegister removeIngredient(int count, ItemStack item) {
        Iterator<ItemStack> iterator = ingredients.iterator();
        while (count > 0 && iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.isSimilar(item)) {
                iterator.remove();
                count--;
            }
        }
        return this;
    }

    /**
     * Registers the Recipe in the server.
     */
    public void register() {
        ShapelessRecipe sr = new ShapelessRecipe(key, output);
        try {
            Field f = sr.getClass().getDeclaredField("ingredients");
            f.setAccessible(true);
            f.set(sr, ingredients);
            Bukkit.addRecipe(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
