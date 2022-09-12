package net.grandtheftmc.vice.machine;

import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MachineUtil {

//    public static Optional<BaseMachine> getMachineByInventory(MachineManager machineManager, Inventory inventory) {
//        if (inventory == null) return Optional.empty();
//        if (!inventory.getTitle().contains("Machine-")) return Optional.empty();
//
//        int id = -1;
//        try {
//            id = Integer.parseInt(inventory.getTitle().split("Machine-")[1]);
//        } catch (NumberFormatException e) {}
//
//        if (id == -1) return Optional.empty();
//
//        return machineManager.getMachineById(id);
//    }
//
//    public static Optional<Block> getBlockByInventory(MachineManager machineManager, Inventory inventory) {
//        if (inventory == null) return Optional.empty();
//        return Optional.of(inventory.getLocation().getBlock());
//    }

    public static int getFuelByType(Material material) {
        switch (material) {
            case LAVA_BUCKET: return 100;
            case COAL_BLOCK:  return 80;
            case COAL:        return 8;
            case WOOD:        return 2;
        }

        return 0;
    }

    public static boolean isFuelType(Material material) {
        return material == Material.LAVA_BUCKET || material == Material.COAL || material == Material.WOOD || material == Material.COAL_BLOCK;
    }

    public static boolean tryAddingItem(BaseMachine machine, Location from, ItemStack itemStack) {
        if (isFuelType(itemStack.getType())) {
            for (int slot : machine.getData(MachineDataType.FUEL).getSlots()) {
                ItemStack found = machine.getInventory().getItem(slot);

                //If null or AIR, place the item.
                if (found == null || found.getType() == Material.AIR) {
                    machine.getInventory().setItem(slot, itemStack);
                    return true;
                }

                if (found.getType() != itemStack.getType()) continue;
                if (found.getAmount() >= found.getMaxStackSize()) continue;

                found.setAmount(found.getAmount() + itemStack.getAmount());
                return true;
            }
        }

        if (isFuelType(itemStack.getType())) return false;

        for (int slot : machine.getOpenSlots()) {
            ItemStack found = machine.getInventory().getItem(slot);

            //If null or AIR, place the item.
            if (found == null || found.getType() == Material.AIR) {
                machine.getInventory().setItem(slot, itemStack);
                return true;
            }

            if (!found.isSimilar(itemStack)) continue;
            if (found.getAmount() >= found.getMaxStackSize()) continue;

            found.setAmount(found.getAmount() + itemStack.getAmount());
            return true;
        }

        return false;
    }

    public static void removeSimilarItem(Inventory inventory, ItemStack itemStack) {
        if (inventory == null) return;

        for (int i = 0; i < inventory.getSize(); i++) {
            int finalI = i;

            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            if (!item.isSimilar(itemStack)) continue;

            if (item.getAmount() <= 1) {
                ServerUtil.runTaskLater(() -> inventory.setItem(finalI, new ItemStack(Material.AIR)), 1L);

                ServerUtil.debug(inventory.getTitle() + " - Item removed.");
            }
            else {
                item.setAmount(item.getAmount() - 1);
                ServerUtil.runTaskLater(() -> inventory.setItem(finalI, item), 1L);

                ServerUtil.debug(inventory.getTitle() + " - Amount reduced.");
            }
        }
    }

    /**
     * @param inventory the inventory of the machine
     * @param slot the output slot of the inventory of the machine
     */
    public static boolean isOutputFull(Inventory inventory, int slot) {
        if(inventory == null) return true;

        ItemStack found = inventory.getItem(slot);
        if(found== null || found.getType()== Material.AIR)
            return false;
        if(found.getAmount() >= 64)
            return true;
        return false;
    }

    public static void addOutput(Inventory inventory, int slot, ItemStack itemStack) {
        if (inventory == null) return;

        ItemStack found = inventory.getItem(slot);
        if (found == null || found.getType() == Material.AIR) {
            inventory.setItem(slot, itemStack);
            return;
        }

        if (!found.isSimilar(itemStack)) return;

        if (found.getAmount() + itemStack.getAmount() >= 64) {
            found.setAmount(64);
            return;
        }

        if (found.getAmount() < 64) {
            found.setAmount(found.getAmount() + itemStack.getAmount());
        }
    }
}
