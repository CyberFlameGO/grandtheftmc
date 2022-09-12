package net.grandtheftmc.vice.machine.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.MachineManager;
import net.grandtheftmc.vice.machine.MachineUtil;
import net.grandtheftmc.vice.machine.recipe.misc.MachineRecipeData;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.type.*;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class MachineRecipeManager {

    private final MachineManager machineManager;
    private final HashMap<Integer, List<MachineRecipe>> recipes;
    private final List<MachineRecipeData> recipeData;

    /**
     case 1: return Optional.of(new MachineSmallDryingChamber());
     case 2: return Optional.of(new MachineMediumDryingChamber());
     case 3: return Optional.of(new MachineLargeDryingMachine());
     case 4: return Optional.of(new MachineBeerDistillery());
     case 5: return Optional.of(new MachineVodkaDistillery());
     case 6: return Optional.of(new MachineCocaProcessor());
     case 7: return Optional.of(new MachinePulpCondenser());
     case 8: return Optional.of(new MachineBasicMethProducer());
     case 9: return Optional.of(new MachineAdvancedMethProducer());
     case 10: return Optional.of(new MachineSugarBox());
     */

    public MachineRecipeManager(MachineManager machineManager) {
        this.machineManager = machineManager;
        this.recipes = Maps.newHashMap();
        this.recipeData = Lists.newArrayList();

        this.recipes.put(1, Arrays.asList(new RecipeHumulusLupulusFruit(), new RecipeWeed(), new RecipeDriedMushroomRed(), new RecipeDriedMushroomBrown(), new RecipeAcid()));
        this.recipes.put(2, Arrays.asList(new RecipeHumulusLupulusFruit(), new RecipeWeed(), new RecipeDriedMushroomRed(), new RecipeDriedMushroomBrown(), new RecipeAcid()));
        this.recipes.put(3, Arrays.asList(new RecipeHumulusLupulusFruit(), new RecipeWeed(), new RecipeDriedMushroomRed(), new RecipeDriedMushroomBrown(), new RecipeAcid()));
        this.recipes.put(4, Arrays.asList(new RecipeBeer(), new RecipeCraftBeer()));
        this.recipes.put(5, Arrays.asList(new RecipeVodka(), new RecipeDistilledVodka()));
        this.recipes.put(6, Arrays.asList(new RecipeCrack(), new RecipeCocaine()));
        this.recipes.put(7, Arrays.asList(new RecipeConcentratedMagicMushroom()));
        this.recipes.put(8, Arrays.asList(new RecipeWhiteMeth()));
        this.recipes.put(9, Arrays.asList(new RecipePureMeth()));
        this.recipes.put(10, Arrays.asList(new RecipeLSD()));
    }

    public boolean tryInitRecipe(BaseMachine machine) {
        if (machine.isRecipeActive()) return true;

        Inventory inv = machine.getInventory();
        if (inv == null) return false;
        if (!this.recipes.containsKey(machine.getMachineIdentifier())) return false;

        for(int i = 0; i < machine.getOutputSlots().length; i++)
            if(MachineUtil.isOutputFull(machine.getInventory(), machine.getOutputSlots()[i]))
                return false;

        for (MachineRecipe recipe : this.recipes.get(machine.getMachineIdentifier())) {
            if (!this.hasAllRecipeItems(machine, recipe)) continue;

            this.removeRecipeItems(machine, recipe);
            machine.setRecipeData(new MachineRecipeData(machine, recipe));
            return true;
        }

        return false;
    }

    private boolean hasAllRecipeItems(BaseMachine machine, MachineRecipe recipe) {
        for (RecipeInput input : recipe.getInput()) {
            if (!hasRecipeItem(machine, input))
                return false;
        }

        return true;
    }

    private boolean hasRecipeItem(BaseMachine machine, RecipeInput input) {
        Inventory inv = machine.getInventory();
        if (inv == null) return false;

        for (int i : machine.getOpenSlots()) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            if (!item.isSimilar(input.getItemStack())) continue;
            if (item.getAmount() < input.getAmount()) continue;
            return true;
        }

        return false;
    }

    private void removeRecipeItems(BaseMachine machine, MachineRecipe recipe) {
        Inventory inv = machine.getInventory();
        if (inv == null) return;

        for (RecipeInput input : recipe.getInput()) {
            for (int i : machine.getOpenSlots()) {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getType() == Material.AIR) continue;
                if (!item.isSimilar(input.getItemStack())) continue;
                if (item.getAmount() < input.getAmount()) {
                    inv.setItem(i, new ItemStack(Material.AIR));
                    continue;
                }

                if (item.getAmount() - input.getAmount() < 1) inv.setItem(i, new ItemStack(Material.AIR));
                else item.setAmount(item.getAmount() - input.getAmount());
            }
        }
    }
}
