package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugCraftable;
import net.grandtheftmc.vice.items.ItemManager;
import net.grandtheftmc.vice.utils.recipe.ShapedRegister;
import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import us.myles.ViaVersion.util.ConcurrentList;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Joint extends BaseDrugItem< DrugCraftable > {

    /**
     * Construct a new Drug Item
     */
    public Joint() {
        super("Joint", DrugType.JOINT);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        ShapedRegister recipe = new ShapedRegister(ItemManager.NAMESPACED_KEY, super.getGameItem().getItem().clone());
        recipe.shape("000","010","222");
        ItemStack bud = MarijuanaLeaf.getOutput();
        recipe.setIngredient('1', bud);
        ItemStack paper = new ItemStack(Material.PAPER);
        recipe.setIngredient('2', paper);
        recipe.register();

        setAttribute(new DrugCraftable() {
            @Override
            public boolean isShapeless() {
                return false;
            }

            @Override
            public void onEvent(PrepareItemCraftEvent event) {
                if (event.getInventory() == null) return;
                if (event.getRecipe().getResult() != recipe.getOutput()) return;

                ConcurrentList<ItemStack> matrix = new ConcurrentList<>();
                matrix.addAll(Arrays.stream(event.getInventory().getMatrix()).filter(i -> i != null && i.getType() != Material.AIR).collect(Collectors.toList()));

                int budCount = (int) matrix.stream().filter(item -> {
                    if (item != null && item.equals(bud)) {
                        matrix.remove(item);
                        return true;
                    }
                    return false;
                }).count();
                int paperCount = (int) matrix.stream().filter(item -> {
                    if (item != null && item.getType() == paper.getType()) {
                        matrix.remove(item);
                        return true;
                    }
                    return false;
                }).count();

                if (!matrix.isEmpty()) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }

                if (budCount == 1 && paperCount == 3) {
                    event.getInventory().setResult(recipe.getOutput());
                    System.out.println("" + event.getInventory().getResult().getType().name());
                    return;
                }

                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        });
    }
}
