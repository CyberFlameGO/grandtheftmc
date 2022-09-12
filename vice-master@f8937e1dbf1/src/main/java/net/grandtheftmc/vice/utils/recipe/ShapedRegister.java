package net.grandtheftmc.vice.utils.recipe;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ShapedRegister {

    private ItemStack output;
    private String[] rows;
    private Map<Character, ItemStack> ingredients = new HashMap<>();

    private final NamespacedKey key;

    /**
     * Create a shaped recipe to craft the specified ItemStack. The
     * constructor merely determines the result and type; to set the actual
     * recipe, you'll need to call the appropriate methods.
     *
     * @param result The item you want the recipe to create.
     * @see ShapedRecipe#shape(String...)
     * @see ShapedRecipe#setIngredient(char, Material)
     * @see ShapedRecipe#setIngredient(char, ItemStack)
     */
    public ShapedRegister(NamespacedKey key, ItemStack result) {
        this.key = key;
        output = new ItemStack(result);
    }

    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Set the shape of this recipe to the specified rows. Each character
     * represents a different ingredient; exactly what each character
     * represents is set separately. The first row supplied corresponds with
     * the upper most part of the recipe on the workbench e.g. if all three
     * rows are supplies the first string represents the top row on the
     * workbench.
     *
     * @param shape The rows of the recipe (up to 3 rows).
     * @return The changed recipe, so you can chain calls.
     */
    public ShapedRegister shape(String... shape) {
        Validate.notNull(shape, "Must provide a shape");
        Validate.isTrue(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2, 3 rows, not ", shape.length);

        for (String row : shape) {
            Validate.notNull(row, "Shape cannot have null rows");
            Validate.isTrue(row.length() > 0 && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());
            Map<Character, ItemStack> newIng = new HashMap<>();

            for (char c : row.toCharArray())
                newIng.put(c, ingredients.get(c));

            ingredients = newIng;
        }

        rows = shape;

        return this;
    }

    /**
     * Sets the Material that a character in the recipe shape refers to.
     *
     * @param key        The character that represents the ingredient in the shape.
     * @param ingredient The ingredient.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapedRegister setIngredient(char key, Material mat) {
        return setIngredient(key, new ItemStack(mat, 1));
    }

    /**
     * Sets the ItemStack that a character in the recipe shape refers to.
     *
     * @param key        The character that represents the ingredient in the shape.
     * @param ingredient The ingredient.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapedRegister setIngredient(char key, ItemStack item) {
        Validate.isTrue(ingredients.containsKey(key), "Symbol does not appear in the shape:", key);
        ingredients.put(key, item);
        return this;
    }

    public ItemStack getOutput() {
        return output.clone();
    }

    /**
     * Registers the Recipe in the server.
     */
    public void register() {
        ShapedRecipe sr = new ShapedRecipe(key, output);
        sr.shape(rows);
        try {
            Field f = sr.getClass().getDeclaredField("ingredients");
            f.setAccessible(true);
            f.set(sr, ingredients);
            Bukkit.addRecipe(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void registerShapedRecipe(ItemStack result, Object... data) {
//        String s = "";
//        int index = 0;
//        int height = 0;
//        int width = 0;
//        if (data[index] instanceof String[]) {
//            String[] strings = (String[]) data[index++];
//
//            for (String shapedRecipes : strings) {
//                ++width;
//                height = shapedRecipes.length();
//                s = s + shapedRecipes;
//            }
//        } else {
//            while (data[index] instanceof String) {
//                String str = (String) data[index++];
//                ++width;
//                height = str.length();
//                s = s + str;
//            }
//        }
//
//        HashMap<Character, net.minecraft.server.v1_12_R1.ItemStack> charMap;
//        for (charMap = Maps.newHashMap(); index < data.length; index += 2) {
//            Character c = (Character) data[index];
//            net.minecraft.server.v1_12_R1.ItemStack stack = null;
//            if (data[index + 1] instanceof ItemStack)
//                stack = CraftItemStack.asNMSCopy((ItemStack) data[index + 1]);
//            else if (data[index + 1] instanceof net.minecraft.server.v1_12_R1.Item)
//                stack = new net.minecraft.server.v1_12_R1.ItemStack((net.minecraft.server.v1_12_R1.Item) data[index + 1]);
//            else if (data[index + 1] instanceof net.minecraft.server.v1_12_R1.Block)
//                stack = new net.minecraft.server.v1_12_R1.ItemStack((net.minecraft.server.v1_12_R1.Block) data[index + 1], 1, Short.MAX_VALUE);
//            else if (data[index + 1] instanceof net.minecraft.server.v1_12_R1.ItemStack)
//                stack = (net.minecraft.server.v1_12_R1.ItemStack) data[index + 1];
//
//
//            charMap.put(c, stack);
//        }
//
//        net.minecraft.server.v1_12_R1.ItemStack[] ingredients = new net.minecraft.server.v1_12_R1.ItemStack[height * width];
//
//        for (int j = 0; j < height * width; ++j) {
//            char c = s.charAt(j);
//            if (charMap.containsKey(c))
//                ingredients[j] = charMap.get(c).cloneItemStack();
//            else
//                ingredients[j] = null;
//        }
//
//        net.minecraft.server.v1_12_R1.ShapedRecipes recipe = new net.minecraft.server.v1_12_R1.ShapedRecipes("test", height, width, ingredients, CraftItemStack.asNMSCopy(result)) {
//            @Override public boolean a(net.minecraft.server.v1_12_R1.InventoryCrafting inventory, net.minecraft.server.v1_12_R1.World world) {
//                for (int i = 0; i < ingredients.length; i++) {
//                    net.minecraft.server.v1_12_R1.ItemStack ingredient = ingredients[i];
//                    net.minecraft.server.v1_12_R1.ItemStack found = inventory.getItem(i);
//                    if (ingredient == null) {
//                        if (found == null)
//                            continue;
//                        else
//                            return false;
//                    }
//
//                    if (found == null)
//                        return false;
//
//                    if (ingredient.getItem() != found.getItem() || ingredient.getData() != found.getData())
//                        return false;
//
//                    if (ingredient.hasTag()) {
//                        if (!found.hasTag())
//                            return false;
//
//                        if (!ingredient.getTag().equals(found.getTag()))
//                            return false;
//                    }
//                }
//
//                return true;
//            }
//        };
//
//        CraftingManager.a(new MinecraftKey(ItemManager.NAMESPACED_KEY.getKey()), recipe);
//    }
}
