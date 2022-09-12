package net.grandtheftmc.vice.machine.recipe.menu.type;

import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.vice.machine.recipe.menu.RecipeMenu;
import net.grandtheftmc.vice.machine.recipe.type.RecipePureMeth;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public final class RecipeMenuPureMeth extends RecipeMenu {

    public RecipeMenuPureMeth() {
        super(3, "Recipe : Meth Producer", new RecipePureMeth(), CoreMenuFlag.RESET_CURSOR_ON_OPEN, CoreMenuFlag.CLOSE_ON_NULL_CLICK);

        super.setBlockedSlots(0, 1, 2, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 23, 24, 25, 26);
        super.setOpenSlots(3, 5);
        super.setOutputSlots(22);

        super.initRecipeItems();

        String[] description = {
                C.DARK_GRAY + C.ITALIC + " " + super.getTitle(),
                "",
                "",
                C.YELLOW + C.BOLD + "MACHINE RECIPES",
                C.GRAY + " Note, recipes consume fuel and durability.",
                C.GRAY + " Some more than others.",
                "",
                " " + i("霑", 1) + i("霠", 64) + "  霚  " + i("霒", 4),
                ""
        };

        for (int i : super.getBlockedSlots()) {
            super.addItem(new MenuItem(i,
                    new ItemFactory(Material.STAINED_GLASS_PANE)
                            .setDurability((short) 0)
                            .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                            .setLore(description)
                            .setUnbreakable(true)
                            .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                            .build(),
                    false
            ));
        }

        //Machine Frame
        super.addItem(new MenuItem(0,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 80)
                        .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                        .setLore(description)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));

        this.initFuelItems();
        this.initDurabilityItems();

        //Progress Item.
        super.addItem(new MenuItem(11,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability(super.progress)
                        .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                        .setLore(description)
                        .setEnchantment(Enchantment.DURABILITY, 1)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));
    }

    private void initFuelItems() {
        String[] lore = {
                C.GRAY + " Adding burnable items will replenish",
                C.GRAY + " your machine fuel counter.",
                "",
                // fire  -  oak_plank  -  2
                C.RESET + " 霚 " + C.WHITE + "[霡] +" + C.YELLOW + "2" + C.WHITE + " Fuel",
                // fire  -  coal  -  8
                C.RESET + " 霚 " + C.WHITE + "[霖] +" + C.GOLD + "8" + C.WHITE + " Fuel",
                // fire  -  coal_block  -  160
                C.RESET + " 霚 " + C.WHITE + "[霗] +" + C.RED + "80" + C.WHITE + " Fuel",
                // fire  -  lava  -  100
                C.RESET + " 霚 " + C.WHITE + "[霙] +" + C.RED + "100" + C.WHITE + " Fuel",
                "",
                C.GRAY + " Use the command " + C.WHITE + "/recipe" + C.GRAY + " for",
                C.GRAY + " a visual tutorial on drug recipes."
        };

        //Fuel Item.
        super.addItem(new MenuItem(19,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));

        //Fuel Dummy Item.
        super.addItem(new MenuItem(10,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));

        //Fuel Dummy Item.
        super.addItem(new MenuItem(1,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));
    }

    private void initDurabilityItems() {
        String[] lore = {
                C.GRAY + " Once the durability is gone, you will",
                C.GRAY + " need to fix your machine to repair it.",
                ""
        };

        //Fuel Item.
        super.addItem(new MenuItem(25,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));

        //Fuel Dummy Item.
        super.addItem(new MenuItem(16,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));

        //Fuel Dummy Item.
        super.addItem(new MenuItem(7,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                false
        ));
    }
}
