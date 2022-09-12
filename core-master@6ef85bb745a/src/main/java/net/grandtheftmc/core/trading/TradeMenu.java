package net.grandtheftmc.core.trading;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Timothy Lampen on 11/1/2017.
 */
public class TradeMenu extends CoreMenu{
    private final HashMap<TradingSlotType, LinkedList<Integer>> slotPairs = new HashMap<>();
    private final Player trader, tradee;
    private double moneyTrader = 0, moneyTradee = 0;
    private TradingStatus status = TradingStatus.WAITING;
    private boolean traderSettingCustomAmt = false, tradeeSettingCustomAmt = false;

    public TradeMenu(Player trader, Player tradee) {
        super(6, generateTitle(trader.getName(), tradee.getName()));
        loadSlotPairs();
        this.tradee = tradee;
        this.trader = trader;

        this.slotPairs.get(TradingSlotType.NOTHING).forEach(slot -> {
            addItem(generateDefaultItem(TradingSlotType.NOTHING, slot));
        });

        this.slotPairs.get(TradingSlotType.ACCEPT).forEach(slot -> {
            addItem(generateDefaultItem(TradingSlotType.ACCEPT, slot));
        });

        this.slotPairs.get(TradingSlotType.DECLINE).forEach(slot -> {
            addItem(generateDefaultItem(TradingSlotType.DECLINE, slot));
        });

        addItem(generateDefaultItem(TradingSlotType.TRADER_MONEY));
        addItem(generateDefaultItem(TradingSlotType.TRADEE_MONEY));
        addItem(generateDefaultItem(TradingSlotType.ADD_HUNDRED));
        addItem(generateDefaultItem(TradingSlotType.ADD_THOUSAND));
        addItem(generateDefaultItem(TradingSlotType.ADD_TEN_THOUSAND));
        addItem(generateDefaultItem(TradingSlotType.SET_CUSTOM_AMOUNT));
        openInventory(trader);
        openInventory(tradee);
    }

    @Override
    public void onClick(InventoryClickEvent event){
       ItemStack cursor = event.getCursor();
        ItemStack item = event.getCurrentItem();
        Player player = (Player)event.getWhoClicked();
        int slot = event.getSlot();

        if(!Core.getTradeManager().getActiveTrades().contains(this))
            return;
        if(!(event.getClick().toString().contains("LEFT") || event.getClick().toString().contains("RIGHT"))) {
            event.setCancelled(true);
            return;
        }
        if(cursor.getType()==Material.AIR && item.getType()==Material.AIR)
            return;
        if(event.getClickedInventory()==null) {
            event.setCancelled(true);
            return;
        }
        if(event.getClickedInventory().equals(player.getInventory())) {
            if(!event.getClick().isShiftClick())
                return;
            if(item==null|| item.getType()==Material.COMPASS || item.getType()== Material.WATCH || item.getType()==Material.CHEST) {
                event.setCancelled(true);
                return;
            }

            if(player.equals(this.trader)) {
                for(Integer i : this.slotPairs.get(TradingSlotType.TRADER_ITEM)) {
                    ItemStack is = event.getInventory().getItem(i);
                    if(is==null || is.getType()==Material.AIR) {
                        event.getInventory().setItem(i, item);
                        event.setCurrentItem(new ItemStack(Material.AIR));
                        if(this.status == TradingStatus.ONE_ACCEPT) {
                            this.status = TradingStatus.WAITING;
                            int[] slots = new int[]{31,40,49};
                            for(int ia : slots)
                                addItem(generateDefaultItem(TradingSlotType.NOTHING, ia));
                        }
                        break;
                    }
                }
                this.trader.updateInventory();
                this.tradee.updateInventory();
                event.setCancelled(true);
            }
            else{
                for(Integer i : this.slotPairs.get(TradingSlotType.TRADEE_ITEM)) {
                    ItemStack is = event.getInventory().getItem(i);
                    if(is==null || is.getType()==Material.AIR) {
                        event.getInventory().setItem(i, item);
                        event.setCurrentItem(new ItemStack(Material.AIR));
                        if(this.status == TradingStatus.ONE_ACCEPT) {
                            this.status = TradingStatus.WAITING;
                            int[] slots = new int[]{4,13,22};
                            for(int ia : slots)
                                addItem(generateDefaultItem(TradingSlotType.NOTHING, ia));
                        }
                        break;
                    }
                }
                this.trader.updateInventory();
                this.tradee.updateInventory();
                event.setCancelled(true);
            }
        }
        else if(this.slotPairs.get(TradingSlotType.TRADER_ITEM).contains(slot)) {
            Core.error("2");
            if(player.equals(this.trader)) {
                if(this.status == TradingStatus.ONE_ACCEPT) {
                    this.status = TradingStatus.WAITING;
                    int[] slots = new int[]{31,40,49};
                    for(int i : slots)
                        addItem(generateDefaultItem(TradingSlotType.NOTHING, i));
                    ServerUtil.runTaskLater(() -> {
                        this.tradee.updateInventory();
                        this.trader.updateInventory();
                    }, 1);
                }
            }
            else {
                event.setCancelled(true);
            }
        }
        else if(this.slotPairs.get(TradingSlotType.TRADEE_ITEM).contains(slot)) {
            if(player.equals(this.tradee)) {
                if(this.status == TradingStatus.ONE_ACCEPT) {
                    this.status = TradingStatus.WAITING;
                    int[] slots = new int[]{4,13,22};
                    for(int i : slots)
                        addItem(generateDefaultItem(TradingSlotType.NOTHING, i));
                    ServerUtil.runTaskLater(() -> {
                        this.tradee.updateInventory();
                        this.trader.updateInventory();
                    }, 1);

                }
            }
            else {
                event.setCancelled(true);

            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event){
        Player player = (Player)event.getPlayer();
        if(!Core.getTradeManager().getActiveTrades().contains(this))
            return;
        if(player.equals(trader)) {
            if (!this.traderSettingCustomAmt) {
                Core.getTradeManager().endTrade(this, false, false);
                this.tradee.closeInventory();
            }
        }
        else {
            if (!this.tradeeSettingCustomAmt) {
                Core.getTradeManager().endTrade(this, false, false);
                this.trader.closeInventory();
            }
        }
    }

    /**
     * @param type the type of item in the inventory that was clicked
     * @param amt the amount to set the trader's money to.
     */
    public boolean setTraderMoney(TradingSlotType type, double amt){
        User user = Core.getUserManager().getLoadedUser(this.trader.getUniqueId());
        if(!user.hasMoney(amt)) {
            addItem(new ClickableItem(this.slotPairs.get(type).get(0), new ItemFactory(Material.WOOL).setDurability((short)14).setName(Utils.f("&cYou do not have enough money!")).setLore(Utils.f("&7Click to try again")).build(), (player, clickType)-> {
                addItem(generateDefaultItem(type));
            }));
            return false;
        }
        this.moneyTrader = amt;
        MenuItem menuItem = generateDefaultItem(TradingSlotType.TRADER_MONEY);
        ItemStack is = menuItem.getItemStack().clone();
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Utils.f("&7+ &a" + Utils.formatMoney(amt)));
        is.setItemMeta(im);
        getInventory().setItem(menuItem.getIndex(), is);
        tradee.updateInventory();
        trader.updateInventory();
        resetStatus();
        return true;
    }

    /**
     * @param type the type of item in the inventory that was clicked
     * @param amt the amount to set the tradee's money to.
     */
    public boolean setTradeeMoney(TradingSlotType type, double amt){
        User user = Core.getUserManager().getLoadedUser(this.tradee.getUniqueId());
        if(!user.hasMoney(amt)) {
            addItem(new ClickableItem(this.slotPairs.get(type).get(0), new ItemFactory(Material.WOOL).setDurability((short)14).setName(Utils.f("&cYou do not have enough money!)")).setLore(Utils.f("&7Click to try again")).build(), (player, clickType)-> {
                addItem(generateDefaultItem(type));
            }));
            return false;
        }
        this.moneyTradee = amt;
        MenuItem menuItem = generateDefaultItem(TradingSlotType.TRADEE_MONEY);
        ItemStack is = menuItem.getItemStack().clone();
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Utils.f("&7+ &a" + Utils.formatMoney(amt)));
        is.setItemMeta(im);
        getInventory().setItem(menuItem.getIndex(), is);
        resetStatus();
        return true;
    }

    /**
     * Used when a player edits the trade.
     */
    private static final int[] DIVIDER_SLOTS = new int[]{4,13,22,31,40,49};
    public void resetStatus(){
        if(this.status == TradingStatus.ONE_ACCEPT) {
            this.status = TradingStatus.WAITING;
            for(int i : DIVIDER_SLOTS)
                addItem(generateDefaultItem(TradingSlotType.NOTHING, i));
            this.tradee.updateInventory();
            this.trader.updateInventory();
        }
    }

    /**
     * @return a hashmap with the type of item in the inventory, and the slot(s) that correspond to that type of item.
     */
    public HashMap<TradingSlotType, LinkedList<Integer>> getSlotPairs() {
        return this.slotPairs;
    }

    /**
     * @return the money the tradee has offered
     */
    public double getMoneyTradee() {
        return moneyTradee;
    }

    /**
     * @return the money the trader has offered
     */
    public double getMoneyTrader() {
        return moneyTrader;
    }

    private MenuItem generateDefaultItem(TradingSlotType type){
        return generateDefaultItem(type, this.slotPairs.get(type).size()>1 ? -1: this.slotPairs.get(type).get(0));
    }

    private MenuItem generateStackableItem(ItemStack is, TradingSlotType type, int slot){
        switch (type) {
            case TRADEE_ITEM:
                return new ClickableItem(slot, is, (playerA, clickTypeA) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!playerA.equals(tradee))
                                return;
                            ServerUtil.debug("is tradee");
                            ItemStack cursor = tradee.getItemOnCursor().clone();
                            MenuItem item = getMenuItem(slot);
                            if(cursor==null || cursor.getType() == Material.AIR) {
                                ServerUtil.debug("cursor null, should put item in hand.");
                                playerA.setItemOnCursor(item.getItemStack());
                                addItem(generateDefaultItem(TradingSlotType.TRADEE_EMPTY_ITEM, slot));
                            }
                            else {
                                ServerUtil.debug("cursor not null, should stack / switch the items");
                                stackItemsInTrade(cursor, item, playerA, type, slot);
                            }
                            ServerUtil.debug("updated the player's inventory.");
                            playerA.updateInventory();
                            resetStatus();
                        }
                    }.runTaskLater(Core.getInstance(), 1);
                });
            case TRADER_ITEM:
                return new ClickableItem(slot, is, (playerA, clickTypeA) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!playerA.equals(trader))
                                return;
                            ServerUtil.debug("is tradee");
                            ItemStack cursor = trader.getItemOnCursor().clone();
                            MenuItem item = getMenuItem(slot);
                            if(cursor==null || cursor.getType() == Material.AIR) {
                                ServerUtil.debug("cursor null, should put item in hand.");
                                playerA.setItemOnCursor(item.getItemStack());
                                addItem(generateDefaultItem(TradingSlotType.TRADER_EMPTY_ITEM, slot));
                            }
                            else {
                                ServerUtil.debug("cursor not null, should stack / switch the items");
                                stackItemsInTrade(cursor, item, playerA, type, slot);
                            }
                            ServerUtil.debug("updated the player's inventory.");
                            playerA.updateInventory();
                            resetStatus();
                        }
                    }.runTaskLater(Core.getInstance(), 1);
                });
        }
        return null;
    }

    private void stackItemsInTrade(ItemStack cursor, MenuItem item, Player playerA, TradingSlotType type, int slot) {
        if(cursor.isSimilar(item.getItemStack())) {
            ServerUtil.debug("they are similar");
            if(cursor.getAmount() + item.getItemStack().getAmount() <= 64) {
                ServerUtil.debug("less than 64, just put them together");
                item.getItemStack().setAmount(item.getItemStack().getAmount() + cursor.getAmount());
                addItem(item);
                playerA.setItemOnCursor(new ItemStack(Material.AIR));
            }
            else {
                ServerUtil.debug("more than 64, so just make the item 64, remove that amount from cursor");
                int needed = 64 - item.getItemStack().getAmount();
                cursor.setAmount(cursor.getAmount() - needed);
                item.getItemStack().setAmount(64);
                addItem(item);
                playerA.setItemOnCursor(cursor);
            }
        }
        else {
            ServerUtil.debug("player is trying to pick the item up.");
            playerA.setItemOnCursor(item.getItemStack());
            addItem(generateStackableItem(cursor, type, slot));
        }
    }


    /**
     * @param type the type of item that is going to be generated
     * @param slot the slot that the item will be placed into
     */
    private MenuItem generateDefaultItem(TradingSlotType type, int slot){
        switch (type) {
            /*case TRADER_ITEM:
                return new ClickableItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE).setDurability((short)8).setName(Utils.f("&6" + this.trader.getName() + "&7's side")).build(), (player, clickType) -> {
                    if(player.equals(this.trader)) {
                        if(this.trader.getItemOnCursor()==null || this.trader.getItemOnCursor().getType()==Material.AIR)
                            return;
                        ServerUtil.debug(player.getItemOnCursor().getType() + "/ " + getMenuItem(slot).getItemStack().getType() + " / 1");
                        addItem(generateStackableItem(player.getItemOnCursor(), TradingSlotType.TRADER_ITEM, slot));
                        this.trader.setItemOnCursor(new ItemStack(Material.AIR));
                        resetStatus();
                        player.updateInventory();
                    }
                });
            case TRADEE_EMPTY_ITEM:
                return new ClickableItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE).setDurability((short)8).setName(Utils.f("&6" + this.tradee.getName() + "&7's side")).build(), (player, clickType) -> {
                    if(player.equals(this.tradee)) {
                        if(this.tradee.getItemOnCursor()==null || this.tradee.getItemOnCursor().getType()==Material.AIR)
                            return;
                        ServerUtil.debug(player.getItemOnCursor().getType() + "/ " + getMenuItem(slot).getItemStack().getType() + " / 2");
                        addItem(generateStackableItem(player.getItemOnCursor(), TradingSlotType.TRADEE_ITEM, slot));
                        this.tradee.setItemOnCursor(new ItemStack(Material.AIR));
                        resetStatus();
                        player.updateInventory();
                    }
                });*/
            case NOTHING:
                return new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE).setDurability((short)15).setName(" ").setLore(Utils.f("&a< &6" + trader.getName() + " &b&l| &6" + tradee.getName() + " &a>")).build(), false);
            case TRADEE_MONEY:
                return new MenuItem(slot, new ItemFactory(Material.PAPER).setUnsafeEnchantment(Enchantment.LURE, 0).addFlags(ItemFlag.HIDE_ENCHANTS).setName(Utils.f("&7+ &a$0")).build(), false);
            case TRADER_MONEY:
                return new MenuItem(slot, new ItemFactory(Material.PAPER).setUnsafeEnchantment(Enchantment.LURE, 0).addFlags(ItemFlag.HIDE_ENCHANTS).setName(Utils.f("&7+ &a$0")).build(), false);
            case ADD_HUNDRED:
                return new ClickableItem(slot, new ItemFactory(Material.IRON_NUGGET).setName(Utils.f("&7Add &a$100")).build(), (player, clickType)-> {
                    if(player.equals(this.trader)) {
                        setTraderMoney(type, this.moneyTrader + 100);
                        return;
                    }
                    setTradeeMoney(type, this.moneyTradee + 100);
                });
            case ADD_THOUSAND:
                return new ClickableItem(slot, new ItemFactory(Material.GOLD_INGOT).setName(Utils.f("&7Add &a$1,000")).build(), (player, clickType)-> {
                    if(player.equals(this.trader)) {
                        setTraderMoney(type, this.moneyTrader + 1000);
                        return;
                    }
                    setTradeeMoney(type, this.moneyTradee + 1000);
                });
            case ADD_TEN_THOUSAND:
                return new ClickableItem(slot, new ItemFactory(Material.GOLD_BLOCK).setName(Utils.f("&7Add &a$10,000")).build(), (player, clickType)-> {
                    if(player.equals(this.trader)) {
                        setTraderMoney(type, this.moneyTrader + 10000);
                        return;
                    }
                    setTradeeMoney(type, this.moneyTradee + 10000);
                });
            case SET_CUSTOM_AMOUNT:
                return new ClickableItem(slot, new ItemFactory(Material.DIAMOND).setName(Utils.f("&6Custom Amount")).build(), (player, clickType)->{
                    updatePlayerSettingCustomAmt(player, true);
                    player.closeInventory();
                    player.sendMessage(Lang.TRADE.f("&7Please insert the amount of money you would like to add to the trade. Or type '&ccancel&7' to return to the trade screen."));
                });
            case DECLINE:
                return new ClickableItem(slot, new ItemFactory(Material.WOOL).setDurability((short)14).setName(Utils.f("&cDecline the trade")).build(), (player, clickType) -> {
                    this.status = TradingStatus.DECLINED;
                    Core.getTradeManager().endTrade(this, false, true);
                });
            case ACCEPT:
                return new ClickableItem(slot, new ItemFactory(Material.WOOL).setDurability((short)5).setName(Utils.f("&aAccept the trade")).build(), (player, clickType)-> {
                    ItemStack newDivider = generateDefaultItem(TradingSlotType.NOTHING).getItemStack();
                    newDivider.setDurability((short)5);
                    int[] slots = player.equals(trader) ? new int[]{4,13,22} : new int[]{31,40,49};
                    for(int i : slots)
                        addItem(new MenuItem(i, newDivider, false));
                    boolean fullyComplete = true;
                    for(int i : new int[]{4,13,22,31,40,49})
                        if(getInventory().getItem(i).getDurability()==15) {
                            fullyComplete = false;
                            break;
                        }
                    if(fullyComplete) {
                        this.status = TradingStatus.ACCPETED;
                        Core.getTradeManager().endTrade(this, true, true);
                    }
                    else
                        this.status = TradingStatus.ONE_ACCEPT;
                });
        }
        return null;
    }



    private void loadSlotPairs(){
        slotPairs.put(TradingSlotType.TRADER_ITEM, new LinkedList<>(Arrays.asList(0,1,2,3,9,10,11,12,18,19,20,21,27,28,29,30,36,37,38)));
        slotPairs.put(TradingSlotType.TRADEE_ITEM, new LinkedList<>(Arrays.asList(5,6,7,8,14,15,16,17,23,24,25,26,32,33,34,35,41,42,43)));
        slotPairs.put(TradingSlotType.TRADER_MONEY, new LinkedList<>(Collections.singletonList(39)));
        slotPairs.put(TradingSlotType.TRADEE_MONEY,new LinkedList<>( Collections.singletonList(44)));
        slotPairs.put(TradingSlotType.ADD_TEN_THOUSAND, new LinkedList<>(Collections.singletonList(47)));
        slotPairs.put(TradingSlotType.ADD_HUNDRED, new LinkedList<>(Collections.singletonList(45)));
        slotPairs.put(TradingSlotType.ADD_THOUSAND, new LinkedList<>(Collections.singletonList(46)));
        slotPairs.put(TradingSlotType.SET_CUSTOM_AMOUNT, new LinkedList<>(Collections.singletonList(48)));
        slotPairs.put(TradingSlotType.NOTHING, new LinkedList<>(Arrays.asList(4,13,22,31,40,49)));
        slotPairs.put(TradingSlotType.ACCEPT, new LinkedList<>(Arrays.asList(50,51)));
        slotPairs.put(TradingSlotType.DECLINE, new LinkedList<>(Arrays.asList(52,53)));
    }

    /**
     * @return the tradee in the trade
     */
    public Player getTradee() {
        return this.tradee;
    }

    /**
     * @return the trader in the trade
     */
    public Player getTrader() {
        return this.trader;
    }

    /**
     * @param player the player that is using the chat function
     * @param bool if they are using the chat custom ammount function.
     */
    public void updatePlayerSettingCustomAmt(Player player, boolean bool) {
        if(player.equals(trader))
            this.traderSettingCustomAmt = bool;
        else
            this.tradeeSettingCustomAmt = bool;
    }

    private static String generateTitle(String origin, String target){
        origin = origin.length() >= 13 ? origin.substring(0, 13) : origin;
        StringBuilder sb = new StringBuilder(origin);
        for(int i = origin.length() ; i<=13; i++){
            sb.append(" ");
        }
        sb.append(" | ");
        target = target.length() >= 12 ? target.substring(0, 12) : target;
        sb.append(target);
        return sb.toString();
    }
}
