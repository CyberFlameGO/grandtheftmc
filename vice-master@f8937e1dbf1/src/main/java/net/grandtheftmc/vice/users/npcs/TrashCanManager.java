package net.grandtheftmc.vice.users.npcs;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 *
 * @apiNote This class is positioned here because it is only used for the drugs menu.
 *
 */
public class TrashCanManager implements Listener {

    private final Set<UUID> inTrashcan = new HashSet<>();

    public void openTrashCan(Player player) {
        if (Vice.getUserManager().getLoadedUser(player.getUniqueId()).isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't sell items in jail!"));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 54, Utils.f("&a&lSell Drugs"));
        int[] paneSlots = new int[] { 9, 10, 11, 12, 13, 14, 15, 16, 17, 27, 28, 29, 30, 31, 32, 33, 34, 35, 45, 46, 47,
                48, 49, 50, 51, 52 };
        for (int i : paneSlots)
            inv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
      //  inv.setItem(52, Utils.createItem(Material.DIAMOND, "&6&lSell Entire Inventory", "&7Total Value: &a$&l" +   getTotalInvPrice(player)));
        inv.setItem(53, Utils.createItem(Material.PAPER, "&a&lConfirm", "&7Total Reward: &a$&l0"));
        inv.setItem(44, Utils.createItem(Material.REDSTONE, "&c&lCancel", "&7Return all items!"));

        this.inTrashcan.add(player.getUniqueId());

        player.openInventory(inv);
    }

    private void m(int i) {
        this.m(String.valueOf(i));
    }

    private void m(String s) {
        Bukkit.broadcastMessage(s);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getView().getTopInventory();
        if (inv == null || !this.inTrashcan.contains(player.getUniqueId()))
            return;

        ItemStack c = e.getCurrentItem();
        if (c != null) {
            switch (c.getType()) {
                case PAPER:
                    e.setCancelled(true);
                    player.closeInventory();
                    return;
                case REDSTONE:
                    e.setCancelled(true);
                    List<ItemStack> items = itemSlots.stream().mapToInt(i -> i).mapToObj(inv::getItem).filter(Objects::nonNull).collect(Collectors.toList());

                    for (int i : itemSlots) {
                        inv.setItem(i, null);
                        inv.setItem(i + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
                    }

                    player.closeInventory();
                    Utils.giveItems(player, items.toArray(new ItemStack[items.size()]));

                    if (c.getType() == Material.REDSTONE)
                        player.sendMessage(Utils.f(Lang.TRASH_CAN + "&7The items have been added back to your inventory."));

                    if (c.getType() == Material.DIAMOND)
                        player.sendMessage(Utils.f(" &c&lERROR&8&l> &cThis feature is currently disabled, try again soon!"));

                    return;

                    /*
                case DIAMOND://TODO, Fix sell-all, It causes alot of lag!
                    e.setCancelled(true);
//                    double price = getTotalInvPrice(player);
//                    if (price == 0) return;
                    player.closeInventory();
//                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
//                    user.setSellInvConfirmAmt(price);
//                    MenuManager.openMenu(player, "sellinvconfirm");

                    //TODO, Remove message after fixing lag.
                    player.sendMessage(Utils.f(" &c&lERROR&8&l> &cThis feature is currently disabled, try again soon!"));
                    return;
                    */

                default:
                    break;
            }
        }

        if (Objects.equals(e.getClickedInventory(), inv) && !itemSlots.contains(e.getSlot())) {
            e.setCancelled(true);
            player.updateInventory();
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline())
                    return;
                updateTrashCan(inv);

            }
        }.runTaskLater(Vice.getInstance(), 1);
    }

    private double calculateItem(Inventory inv, int slot, boolean updateInventory) {
        ItemStack item = inv.getItem(slot);

        if (item == null) {
            if(updateInventory)
                inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
            return 0;
        }
        GameItem gameItem = Vice.getItemManager().getSellableItem(item);
        
        if(gameItem==null || gameItem.getType()!= GameItem.ItemType.DRUG)
            return 0;

        double price = gameItem.getSellPrice() * item.getAmount();
        DecimalFormat df = new DecimalFormat("#.##");
        if(updateInventory)
            inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 13,"&a&lReward: &a$&l" + df.format(price)));
        return price;
    }

    private double updateTrashCan(Inventory inv) {
        List<Integer> itemSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 36, 37, 38, 39, 40, 41, 42, 43);
        if(inv.getViewers().size()==0)
            return 0;
        Player player = (Player) inv.getViewers().get(0);
        if(player==null)
            return 0;

        //inv.setItem(52, Utils.createItem(Material.DIAMOND, "&6&lSell Entire Inventory", "&7Total Value: &a$&l" + getTotalInvPrice(player)));
        DecimalFormat df = new DecimalFormat("#.##");
        double rankMultiplier = ViceUtils.getDrugSellModifier(Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank());

        double totalPrice = itemSlots.stream().mapToDouble(i -> this.calculateItem(inv, i, true)).sum() * rankMultiplier;

        inv.setItem(53, Utils.createItem(Material.PAPER, "&a&lConfirm", "&7Total Reward: &a$&l" + df.format(totalPrice), "&6&lRank Multiplier: &b&l" + rankMultiplier + "x"));
        inv.setItem(44, Utils.createItem(Material.REDSTONE, "&c&lCancel", "&7Return all items!"));
        return totalPrice;
    }

    /*public double getTotalInvPrice(Player player) {
        double invPrice = IntStream.range(0, 36).mapToDouble(i -> this.calculateItem(player.getInventory(), i, false)).sum();
        for(int i =9; i<36; i++){
            ItemStack is = player.getInventory().getItem(i);
            if(is==null || is.getType()==Material.AIR)
                continue;
            GameItem item = Vice.getItemManager().getSellableItem(is);
            if(item==null || item.getType()== GameItem.ItemType.DRUG)
                continue;
            invPrice += item.getSellPrice() * is.getAmount() ;
        }
        return invPrice;
    }*/

    private final List<Integer> itemSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 36, 37, 38, 39, 40, 41, 42, 43);
    public Collection<GameItem> getSellableItems(Inventory inv) {
        Collection<GameItem> items = new ArrayList<>();

        for (int slot : itemSlots) {
            if (inv.getItem(slot) != null) {
                ItemStack itemStack = inv.getItem(slot);
                GameItem gameItem = Vice.getItemManager().getSellableItem(itemStack);
                if (gameItem == null || gameItem.getType()!= GameItem.ItemType.DRUG ) continue;
                gameItem.getItem().setAmount(itemStack.getAmount());
                items.add(gameItem);
            }
        }

        //I think this causes selling problems.
//        itemSlots.stream().forEachOrdered(i -> {
//            if (inv.getItem(i) != null) {
//                ItemStack itemStack = inv.getItem(i);
//                GameItem gameItem = Vice.getItemManager().getSellableItem(itemStack, ViceUtils.isArmor(itemStack.getType()));
//                if (gameItem == null || gameItem.getType()== GameItem.ItemType.DRUG) return;
//                gameItem.getItem().setAmount(itemStack.getAmount());
//                items.add(gameItem);
//            }
//        });

        return items;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (!this.inTrashcan.contains(player.getUniqueId()))
            return;
        double totalPrice = this.updateTrashCan(inv);
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());


        /*for (ItemStack itemStack : inv.getStorageContents()) {
            if (itemStack == null) continue;
            if (!itemStack.hasItemMeta()) continue;
            if (itemStack.getItemMeta().getDisplayName() == null) continue;
            Optional<DrugItem> drugItem = ((DrugService) Vice.getDrugManager().getService()).getDrugItem(itemStack.getItemMeta().getDisplayName());
            if (drugItem.isPresent()) {
                Optional<DrugDealerItem> drugDealerItem = DrugDealerItem.byDrugItem(drugItem.get());
                if (!drugDealerItem.isPresent()) continue;
                drugDealerItem.get().setStockRemaining(drugDealerItem.get().getStockRemaining() + 1);
            }
        }*/

        if (totalPrice > 0) {
            user.addMoney(totalPrice);
            ViceUtils.updateBoard(player, user);
            player.sendMessage(Utils.f(Lang.MONEY_ADD.toString() + Math.round(totalPrice)));
        }

        this.inTrashcan.remove(player.getUniqueId());
    }
}
