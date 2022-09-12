package net.grandtheftmc.core.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;

/**
 * Created by Timothy Lampen on 11/1/2017.
 */
public class TradeManager implements Component<TradeManager, Core>, Listener{
	
	/** Whether trading is enabled */
	private boolean enabled;

    @Override
    public TradeManager onEnable(Core plugin) {
    	this.enabled = true;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        return this;
    }

    private final ArrayList<TradeMenu> activeTrades = new ArrayList<>();
    private final HashMap<UUID, UUID> pendingTrades = new HashMap<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<TradeMenu> tradeMenu = getTrade(player);
        if(!tradeMenu.isPresent())
            return;
        endTrade(tradeMenu.get(), false, true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<TradeMenu> tradeMenu = getTrade(player);
        String msg = event.getMessage();
        if(!tradeMenu.isPresent())
            return;
        event.setCancelled(true);
        if(msg.equalsIgnoreCase("cancel")) {
            tradeMenu.get().updatePlayerSettingCustomAmt(player, false);
            tradeMenu.get().openInventory(player);
            return;
        }
        int amt;
        try {
            amt =Integer.parseInt(event.getMessage());
        }catch (NumberFormatException nfe) {
            player.sendMessage(Lang.TRADE.f("&6" + msg + " &7is not a number. Please type a number or type '&ccancel&7' to return to the trade menu."));
            return;
        }
        if(amt <= 0) {
            player.sendMessage(Lang.TRADE.f("&6" + msg + " &7 is not greater than 0. Please type a positive number or type '&ccancel&7' to return to the trade menu."));
            return;
        }
        tradeMenu.get().updatePlayerSettingCustomAmt(player, false);
        tradeMenu.get().openInventory(player);
        if(tradeMenu.get().getTrader().equals(player)) {
            tradeMenu.get().setTraderMoney(TradingSlotType.SET_CUSTOM_AMOUNT, amt);
            return;
        }
        tradeMenu.get().setTradeeMoney(TradingSlotType.SET_CUSTOM_AMOUNT, amt);
    }

    /**
     * @param player the player who is in the trade (the tradee or trader)
     * @return the trademenu that is currently active.
     */
    public Optional<TradeMenu> getTrade(Player player) {
        return activeTrades.stream().filter(trade -> trade.getTrader().equals(player) || trade.getTradee().equals(player)).findFirst();
    }

    /**
     * @param tradee the player being traded (the person who accepts the trade)
     * @param trader the player who initiated the trade
     */
    public void startTrade(Player trader, Player tradee){
        activeTrades.add(new TradeMenu(trader, tradee));
    }

    /**
     * @param  tradeMenu the trademenu that you want to end
     * @param successful if the trade was done successfully
     * Note that the player's inventories cannot be closed here because a loop could be made with the onClose event.
     */
    public void endTrade(TradeMenu tradeMenu, boolean successful, boolean closeInventories) {
        if(!successful) {
            tradeMenu.getSlotPairs().get(TradingSlotType.TRADER_ITEM).forEach(slot -> {
                ItemStack is = tradeMenu.getInventory().getItem(slot);
                if(is==null || is.getType()==Material.AIR || is.getType()== Material.STAINED_GLASS_PANE)
                    return;
                Utils.giveItems(tradeMenu.getTrader(), tradeMenu.getInventory().getItem(slot));
            });
            tradeMenu.getSlotPairs().get(TradingSlotType.TRADEE_ITEM).forEach(slot -> {
                ItemStack is = tradeMenu.getInventory().getItem(slot);
                if(is==null || is.getType()==Material.AIR || is.getType()== Material.STAINED_GLASS_PANE)
                    return;
                Utils.giveItems(tradeMenu.getTradee(), tradeMenu.getInventory().getItem(slot));
            });
            tradeMenu.getTrader().getInventory().addItem(tradeMenu.getTrader().getItemOnCursor());
            tradeMenu.getTrader().setItemOnCursor(null);
            tradeMenu.getTrader().sendMessage(Lang.TRADE.f("&7The trade has been cancelled."));

            tradeMenu.getTradee().getInventory().addItem(tradeMenu.getTradee().getItemOnCursor());
            tradeMenu.getTradee().setItemOnCursor(null);
            tradeMenu.getTradee().sendMessage(Lang.TRADE.f("&7The trade has been cancelled."));

            ServerUtil.runTaskLater(() -> {
                tradeMenu.getTradee().updateInventory();
                tradeMenu.getTrader().updateInventory();
            }, 1);
        }
        else{
            String fault = "";
            User traderUser = Core.getUserManager().getLoadedUser(tradeMenu.getTrader().getUniqueId());
            User tradeeUser = Core.getUserManager().getLoadedUser(tradeMenu.getTradee().getUniqueId());
            if(!(traderUser.getMoney() >= tradeMenu.getMoneyTrader())) {
                fault = tradeMenu.getTrader().getName();
            }
            if(!(tradeeUser.getMoney() >= tradeMenu.getMoneyTradee())) {
                fault = tradeMenu.getTradee().getName();
            }
            if(!fault.equals("")) {//it cancelled somehow
                tradeMenu.getTradee().sendMessage(Lang.TRADE.f("&6" + fault + " &7does not have enough money to complete this trade,"));
                return;
            }
            tradeMenu.getSlotPairs().get(TradingSlotType.TRADER_ITEM).forEach(slot -> {
                ItemStack is = tradeMenu.getInventory().getItem(slot);
                if(is==null || is.getType()==Material.AIR || is.getType()== Material.STAINED_GLASS_PANE)
                    return;
                Utils.giveItems(tradeMenu.getTradee(), tradeMenu.getInventory().getItem(slot));
            });
            tradeMenu.getSlotPairs().get(TradingSlotType.TRADEE_ITEM).forEach(slot -> {
                ItemStack is = tradeMenu.getInventory().getItem(slot);
                if(is==null || is.getType()==Material.AIR || is.getType()== Material.STAINED_GLASS_PANE)
                    return;
                Utils.giveItems(tradeMenu.getTrader(), tradeMenu.getInventory().getItem(slot));
            });
            if(tradeMenu.getMoneyTrader()>0) {
                tradeeUser.addMoney(tradeMenu.getMoneyTrader());
                traderUser.takeMoney(tradeMenu.getMoneyTrader());
                Utils.insertLog(tradeeUser.getUUID(), tradeMenu.getTradee().getName(), "getTradeMoney", "TRADEMENU", "MONEY", tradeMenu.getMoneyTrader(), -1);
                Utils.insertLog(traderUser.getUUID(), tradeMenu.getTrader().getName(), "giveTradeMoney", "TRADEMENU", "MONEY", tradeMenu.getMoneyTrader(), -1);
            }
            if(tradeMenu.getMoneyTradee()>0) {
                traderUser.addMoney(tradeMenu.getMoneyTradee());
                tradeeUser.takeMoney(tradeMenu.getMoneyTradee());
                Utils.insertLog(tradeeUser.getUUID(), tradeMenu.getTradee().getName(), "giveTradeMoney", "TRADEMENU", "MONEY", tradeMenu.getMoneyTradee(), -1);
                Utils.insertLog(traderUser.getUUID(), tradeMenu.getTrader().getName(), "getTradeMoney", "TRADEMENU", "MONEY", tradeMenu.getMoneyTradee(), -1);
            }
            tradeMenu.getTradee().playSound(tradeMenu.getTradee().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
            tradeMenu.getTrader().playSound(tradeMenu.getTrader().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
        }
        this.activeTrades.remove(tradeMenu);
        if(closeInventories) {
            tradeMenu.getTradee().closeInventory();
            tradeMenu.getTrader().closeInventory();
        }
    }

    /**
     * @param origin the player who sent the request
     * @param target the target player who was sent the request
     */
    public void addPendingTrade(UUID origin, UUID target) {
        this.pendingTrades.put(target, origin);
    }

    /**
     * @param target the target player who was sent the request
     * @return the entry of the pending trade with sender (value) and target (key)
     */
    public Optional<Map.Entry<UUID, UUID>> getPendingTrade(UUID target){
        return this.pendingTrades.entrySet().stream().filter(entry -> entry.getKey().equals(target)).findFirst();
    }

    /**
     * @param target the target who was sent the request
     */
    public void removePendingTrade(UUID target) {
        if(this.pendingTrades.containsKey(target))
            this.pendingTrades.remove(target);
    }

    public ArrayList<TradeMenu> getActiveTrades() {
        return activeTrades;
    }

    /**
     * Get whether or not the trade manager is enabled.
     * 
     * @return {@code true} if the trade manager is enabled, {@code false} otherwise.
     */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set whether the trade manager is enabled.
	 * 
	 * @param enabled - {@code true} if the trade manager is enabled, {@code false} otherwise.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
