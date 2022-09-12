package net.grandtheftmc.gtm.trashcan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dropper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;

public class TrashCanManager implements Listener {

    public static void openTrashCan(Player player) {
    	
    	GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
    	
    	if (gtmUser == null){
    		player.sendMessage(Lang.TRASH_CAN.f("&7Error opening trash can! Please rejoin!"));
            return;
    	}
    	
    	if (gtmUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't sell items in jail!"));
            return;
        }
    	
        Inventory inv = Bukkit.createInventory(null, 54, Utils.f("&8&lTrash Can"));
        int[] paneSlots = new int[] { 9, 10, 11, 12, 13, 14, 15, 16, 17, 27, 28, 29, 30, 31, 32, 33, 34, 35, 45, 46, 47,
                48, 49, 50, 51, 52 };
        for (int i : paneSlots)
            inv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
      //  inv.setItem(52, Utils.createItem(Material.DIAMOND, "&6&lSell Entire Inventory", "&7Total Value: &a$&l" +   getTotalInvPrice(player)));
        inv.setItem(53, Utils.createItem(Material.PAPER, "&a&lConfirm", "&7Total Reward: &a$&l0"));
        inv.setItem(44, Utils.createItem(Material.REDSTONE, "&c&lCancel", "&7Return all items!"));
        player.openInventory(inv);
    }

    private void m(int i) {
        this.m(String.valueOf(i));
    }

    private void m(String s) {
        Bukkit.broadcastMessage(s);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        BlockState block = e.getClickedBlock().getState();
        if (block.getType() != Material.DROPPER) return;
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("spawn")) {
            e.setCancelled(false);
            return;
        }

        if (e.getPlayer().isSneaking()) return;
        Dropper dropper = (Dropper) block;
        e.setCancelled(true);

        if (Objects.equals("Trash Can", ChatColor.stripColor(dropper.getInventory().getTitle())))
            openTrashCan(e.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getView().getTopInventory();
        if (inv == null || e.getClickedInventory() == null || inv.getType() != InventoryType.CHEST
                || !"Trash Can".equalsIgnoreCase(ChatColor.stripColor(inv.getTitle())))
            return;

        ItemStack c = e.getCurrentItem();
        if (c != null) {
            switch (c.getType()) {
                case PAPER:
                    e.setCancelled(true);
                    player.closeInventory();
                    return;

                case DIAMOND:
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
//                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
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
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null)
                    return;
                Inventory inv = p.getOpenInventory().getTopInventory();
                if (inv != null && Objects.equals("Trash Can", ChatColor.stripColor(inv.getTitle()))) {
                    TrashCanManager.this.updateTrashCan(inv);
                }

            }
        }.runTaskLater(GTM.getInstance(), 1);
    }


    private double calculateItem(Inventory inv, int slot, boolean updateInventory) {
        ItemStack item = inv.getItem(slot);
        if (item == null) {
            if(updateInventory)
                inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
            return 0;
        }
        if(item.getType() == Material.SKULL_ITEM) {
            return 0;
        }
        GameItem gameItem = GTMUtils.isArmor(item.getType()) ? GTM.getItemManager().getSellableItem(item, true) : GTM.getItemManager().getSellableItem(item);

        if (gameItem == null) {
            gameItem = GTM.getItemManager().getSellableItem(item, 8);
            if (gameItem == null) {
                if(updateInventory)
                    inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
                return 0;
            }
        }

        if (gameItem.getType() == GameItem.ItemType.DRUG || gameItem.getType() == GameItem.ItemType.VEHICLE) {
            if(updateInventory)
                inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a"));
            return 0;
        }

        if(!gameItem.canSell()) {
            if(updateInventory)
                inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 14, "&c&lReward: &c$&l0"));
            return 0;
        }

        double price = gameItem.getSellPrice();
        if(GTMUtils.isArmor(item.getType())) {
            price *= (double)(item.getType().getMaxDurability() - item.getDurability()) / item.getType().getMaxDurability();
        }

        price *= item.getAmount();
        DecimalFormat df = new DecimalFormat("#.##");
        if(updateInventory)
            inv.setItem(slot + 9, Utils.createItem(Material.STAINED_GLASS_PANE, 13,"&a&lReward: &a$&l" + df.format(price)));
        return price;
    }

    private double updateTrashCan(Inventory inv) {
        Optional<HumanEntity> viewer = inv.getViewers().stream().findFirst();
        Player player = (Player) viewer.get();
        List<Integer> itemSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 36, 37, 38, 39, 40, 41, 42, 43);
        double totalPrice = itemSlots.stream().mapToDouble(i -> this.calculateItem(inv, i, true)).sum();
        //inv.setItem(52, Utils.createItem(Material.DIAMOND, "&6&lSell Entire Inventory", "&7Total Value: &a$&l" + getTotalInvPrice(player)));
        DecimalFormat df = new DecimalFormat("#.##");

        inv.setItem(53, Utils.createItem(Material.PAPER, "&a&lConfirm", "&7Total Reward: &a$&l" + df.format(totalPrice)));
        inv.setItem(44, Utils.createItem(Material.REDSTONE, "&c&lCancel", "&7Return all items!"));
        return totalPrice;
    }

    public double getTotalInvPrice(Player player) {
        double invPrice = IntStream.range(0, 36).mapToDouble(i -> this.calculateItem(player.getInventory(), i, false)).sum();
        for(int i =9; i<36; i++){
            ItemStack is = player.getInventory().getItem(i);
            if(is==null || is.getType()==Material.AIR)
                continue;
            GameItem item = GTM.getItemManager().getSellableItem(is);
            if(item==null || item.getType()== GameItem.ItemType.DRUG)
                continue;
            invPrice += item.getSellPrice() * is.getAmount() ;
        }
        return invPrice;
    }

    private final List<Integer> itemSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 36, 37, 38, 39, 40, 41, 42, 43);
    public Collection<GameItem> getSellableItems(Inventory inv) {
        Collection<GameItem> items = new ArrayList<>();

        for (int slot : itemSlots) {
            if (inv.getItem(slot) != null) {
                ItemStack itemStack = inv.getItem(slot);
                GameItem gameItem = GTM.getItemManager().getSellableItem(itemStack, GTMUtils.isArmor(itemStack.getType()));
                if (gameItem == null || gameItem.getType()== GameItem.ItemType.DRUG) continue;
                gameItem.getItem().setAmount(itemStack.getAmount());
                items.add(gameItem);
            }
        }

        //I think this causes selling problems.
//        itemSlots.stream().forEachOrdered(i -> {
//            if (inv.getItem(i) != null) {
//                ItemStack itemStack = inv.getItem(i);
//                GameItem gameItem = GTM.getItemManager().getSellableItem(itemStack, GTMUtils.isArmor(itemStack.getType()));
//                if (gameItem == null || gameItem.getType()== GameItem.ItemType.DRUG) return;
//                gameItem.getItem().setAmount(itemStack.getAmount());
//                items.add(gameItem);
//            }
//        });

        return items;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
    	
    	// grab event variables
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        
        // skip improper menu/inventories
        if (e.getInventory().getType() != InventoryType.CHEST || !"Trash Can".equalsIgnoreCase(ChatColor.stripColor(inv.getTitle()))){
            return;
        }
        
        GTMUser user = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (user == null){
        	player.sendMessage(Lang.TRASH_CAN.f("&7Error selling contents in trash can! Please rejoin!"));
        	return;
        }
        
        Collection<GameItem> sellableItems = getSellableItems(inv);
        double totalPrice = this.updateTrashCan(inv);

        if (!sellableItems.isEmpty()) {
            Collection<String> items = new ArrayList<>();
            sellableItems.forEach(gameItem -> items.add(gameItem.getName()));

            String itemsSold = StringUtils.join(items, ", ");

            GTMUtils.log("trashcan", player.getName() + " sold the following for $" + totalPrice
                    + ": " + itemsSold);
        }

        /*for (ItemStack itemStack : inv.getStorageContents()) {
            if (itemStack == null) continue;
            if (!itemStack.hasItemMeta()) continue;
            if (itemStack.getItemMeta().getDisplayName() == null) continue;
            Optional<DrugItem> drugItem = ((DrugService) GTM.getDrugManager().getService()).getDrugItem(itemStack.getItemMeta().getDisplayName());
            if (drugItem.isPresent()) {
                Optional<DrugDealerItem> drugDealerItem = DrugDealerItem.byDrugItem(drugItem.get());
                if (!drugDealerItem.isPresent()) continue;
                drugDealerItem.get().setStockRemaining(drugDealerItem.get().getStockRemaining() + 1);
            }
        }*/

        if (totalPrice > 0) {
            user.addMoney(totalPrice);
            GTMUtils.updateBoard(player, user);
            player.sendMessage(Utils.f(Lang.MONEY_ADD.toString() + Math.round(totalPrice)));
        }
    }
}
