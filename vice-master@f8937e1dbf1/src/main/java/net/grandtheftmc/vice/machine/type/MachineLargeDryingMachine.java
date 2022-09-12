package net.grandtheftmc.vice.machine.type;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.data.MachineData;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.misc.MachineProgressMultiplier;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public final class MachineLargeDryingMachine extends BaseMachine implements MachineProgressMultiplier {

    /**
     * Construct a Machine
     */
    public MachineLargeDryingMachine() {
        super(
                3,
                "Large Drying Chamber",
                Material.DROPPER
        );

        // FUEL
        MachineData fuelData = new MachineData(MachineData.DataFlag.UP, 100, 0);
        fuelData.setTextures(0, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2);
        fuelData.setSlots(20);
        super.setData(MachineDataType.FUEL, fuelData);

        // DURABILITY
        MachineData durabilityData = new MachineData(MachineData.DataFlag.UP, 72000 * this.getMultiplier());
        durabilityData.setTextures(0, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27);
        super.setData(MachineDataType.DURABILITY, durabilityData);

        // PROGRESS
        MachineData progressData = new MachineData(MachineData.DataFlag.DOWN, 20, 0);
        progressData.setTextures(0, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52);
        super.setData(MachineDataType.PROGRESS, progressData);

        super.setBlockedSlots(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 23, 24, 25, 26);
        super.setOpenSlots(4);
        super.setOutputSlots(22);

        super.setMachineItem(new ItemFactory(Material.DROPPER).setName(C.WHITE + C.BOLD + super.getName()).setLore(C.GRAY + "Machine").build());

        String[] description = {
                C.DARK_GRAY + C.ITALIC + " " + super.getName(),
                "",
                "",
                C.YELLOW + C.BOLD + "MACHINE RECIPES",
                C.GRAY + " Note, recipes consume fuel and durability.",
                C.GRAY + " Some more than others.",
                "",
                " " + i("需", 1) + "  霚  " + i("霂", 6),
                "",
                " " + i("霢", 1) + "  霚  " + i("霆", 1),
                "",
                " " + i("霟", 1) + "  霚  " + i("霋", 1),
                "",
                " " + i("霞", 1) + "  霚  " + i("霋", 1),
                "",
                " " + i("霁", 1) + "  霚  " + i("霎", 1),
                "",
                C.GRAY + " Use the command " + C.WHITE + "/recipe" + C.GRAY + " for",
                C.GRAY + " a visual tutorial on drug recipes."
        };

        for (int i : super.getBlockedSlots()) {
            super.getInventory().setItem(i,
                    new ItemFactory(Material.STAINED_GLASS_PANE)
                            .setDurability((short) 0)
                            .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                            .setLore(description)
                            .setUnbreakable(true)
                            .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                            .build()
            );
        }

        //Machine Frame
        super.getInventory().setItem(0,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 79)
                        .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                        .setLore(description)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        this.initFuelItems(fuelData);
        this.initDurabilityItems(durabilityData);

        //Progress Item.
        super.getInventory().setItem(11,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) progressData.getTexture())
                        .setName(C.GREEN + C.BOLD + "MACHINE TUTORIAL")
                        .setLore(description)
                        .setEnchantment(Enchantment.DURABILITY, 1)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );
    }

    private void initFuelItems(MachineData data) {
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
                ""
        };

        //Fuel Item.
        super.getInventory().setItem(19,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        //Fuel Dummy Item.
        super.getInventory().setItem(10,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        //Fuel Dummy Item.
        super.getInventory().setItem(1,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.GOLD + C.BOLD + "MACHINE FUEL")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );
    }

    private void initDurabilityItems(MachineData data) {
        String[] lore = {
                C.GRAY + " Once the durability is gone, you will",
                C.GRAY + " need to fix your machine to repair it.",
                ""
        };

        //Fuel Item.
        super.getInventory().setItem(25,
                new ItemFactory(Material.STONE_SWORD)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        //Fuel Dummy Item.
        super.getInventory().setItem(16,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        //Fuel Dummy Item.
        super.getInventory().setItem(7,
                new ItemFactory(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 0)
                        .setName(C.AQUA + C.BOLD + "MACHINE DURABILITY")
                        .setLore(lore)
                        .setUnbreakable(true)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                        .build()
        );
    }

    /**
     * This multiplier will speed up the brewing/ cooking
     * progress by the given amount.
     *
     * @return
     */
    @Override
    public double getMultiplier() {
        return 2;
    }
}
