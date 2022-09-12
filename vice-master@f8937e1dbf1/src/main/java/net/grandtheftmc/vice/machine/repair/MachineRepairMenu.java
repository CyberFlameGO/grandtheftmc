package net.grandtheftmc.vice.machine.repair;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.vice.machine.MachineManager;
import net.grandtheftmc.vice.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public final class MachineRepairMenu extends CoreMenu {

    private static final int[] input = new int[] {10,11,12,13};
    private final MachineManager machineManager;

    public MachineRepairMenu(MachineManager machineManager) {
        super(3, "Machine Mechanic", CoreMenuFlag.RESET_CURSOR_ON_OPEN);
        this.machineManager = machineManager;
        super.setSelfHandle(true);

        //Slot 10 - 13 = Fragment input

        //Slot 15 = Machine output
        super.addItem(new MenuItem(15, new ItemStack(Material.BARRIER, 1), false));

        String[] desc = {
                C.GRAY + C.ITALIC + " Place 4 of the same type",
                C.GRAY + C.ITALIC + " of fragments to construct",
                C.GRAY + C.ITALIC + " a functional Machine!",
        };

        super.addItem(new MenuItem(0, new ItemFactory(Material.STONE_SWORD).setDurability((short) 78)
                .setName(C.YELLOW + C.BOLD + "Machine Mechanic").setLore(desc).setUnbreakable(true)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS).build(), false));

        for (int slot : new int[] {1,2,3,4,5,6,7,8,9,14,16,17,18,19,20,21,22,23,24,25,26}) {
            super.addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE, (byte) 0)
                    .setName(C.YELLOW + C.BOLD + "Machine Mechanic").setLore(desc).build(), false));
        }
    }

    private void generateMachine(int machineId) {
        ItemStack itemStack = this.machineManager.getMachineItemById(machineId);
        if (itemStack.getType() == Material.STONE) {
            super.addItem(new MenuItem(15, new ItemStack(Material.BARRIER, 1), false));
            return;
        }

//        super.addItem(new MenuItem(15, itemStack, true));

        super.addItem(new ClickableItem(15, itemStack, true, (player, clickType) -> {
//            if (player.getInventory().firstEmpty() != -1) {
//                player.getInventory().addItem(itemStack);
//                redeemed = true;
//                player.closeInventory();
//                return;
//            }
//
//            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
//            player.sendMessage(C.RED + "Machine dropped on the floor, your inventory was full.");
//            redeemed = true;
//            player.closeInventory();

            for (int i : input) {
                ItemStack item = super.getInventory().getItem(i);
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                }
                else {
                    super.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(itemStack);
                return;
            }

            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            player.sendMessage(C.RED + "Machine dropped on the floor, your inventory was full.");
        }));
    }

    @Override
    public void selfHandle(InventoryClickEvent event) {
//        super.selfHandle(event);

        CoreMenu menu = (CoreMenu)event.getInventory().getHolder();
        Player player = (Player)event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked!=null && clicked.getType() == Material.CHEST) {
            event.setCancelled(true);
            return;
        }

        MenuItem item = menu.getMenuItem(event.getRawSlot());
        if (item != null && event.getRawSlot() == 15 && this.machineManager.isType(item.getItemStack().getType())) {
            switch (event.getAction()) {
                case PICKUP_ALL:
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case MOVE_TO_OTHER_INVENTORY:
                case COLLECT_TO_CURSOR:
                    if (item instanceof ClickableItem) {
                        event.setCancelled(true);
                        ((ClickableItem)item).getClickAction().onClick(player, event.getClick());
                    }
                    return;

                default:
                    event.setCancelled(true);
                    return;
            }
        }

        menu.onClick(event);
        if (item != null) {
            if (!item.isAllowingPickup()) {
                event.setCancelled(true);
            }

            if (item instanceof ClickableItem) {
                ((ClickableItem)item).getClickAction().onClick(player, event.getClick());
            }
        }
    }

    @Override
    public void onInteract(CoreMenu menu) {
        check(menu.getInventory());
    }

    private void check(Inventory inventory) {
        ServerUtil.runTaskLater(() -> {
            int id = -1;
            boolean stable = true;
            for (int slot : input) {
                ItemStack found = inventory.getItem(slot);
                if (found == null || found.getType() != Material.PRISMARINE_SHARD) {
                    stable = false;
                    break;
                }

                if (!ItemStackUtil.hasTag(found, "machineid")) {
                    stable = false;
                    break;
                }

                if (id == -1) {
                    id = ItemStackUtil.getIntTag(found, "machineid");
                    continue;
                }

                int temp = ItemStackUtil.getIntTag(found, "machineid");
                if (id != temp) {
                    stable = false;
                    break;
                }
            }

            if (!stable) id = -1;
//
//            int amount = -1;
//            for (int slot : input) {
//                ItemStack found = inventory.getItem(slot);
//                if (amount == -1) {
//                    amount = found.getAmount();
//                    continue;
//                }
//
//                if (amount != found.getAmount()) {
//                    stable = false;
//                    break;
//                }
//            }
//
//            if (!stable) id = -1;

            this.generateMachine(id);
        }, 1);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        for (int slot : input) {
            ItemStack found = event.getInventory().getItem(slot);
            if (found == null || found.getType() == Material.AIR) continue;

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(found);
                continue;
            }

            player.getWorld().dropItemNaturally(event.getPlayer().getLocation(), found);
        }
        player.updateInventory();
    }
}
