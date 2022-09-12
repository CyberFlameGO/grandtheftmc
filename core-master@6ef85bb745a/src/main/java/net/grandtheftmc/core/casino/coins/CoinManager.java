package net.grandtheftmc.core.casino.coins;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Timothy Lampen on 2017-11-12.
 */
public class CoinManager implements Component<CoinManager, Core>{

    private long lastResetTime = System.currentTimeMillis();
    private double currentMultiplier = 1.0;
    private static final int baseCoinSellPrice = 150, baseCoinBuyPrice = 200;
    private int soldCoins = 0;

    @Override
    public CoinManager onEnable(Core plugin){
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.insertLog(null, "Casino", "resetMultipler", "CHIPS", currentMultiplier + " cMulti", currentMultiplier, 0);
                setCurrentMultiplier(1);
                resetSoldCoins();
                Bukkit.broadcastMessage(Lang.CASINO.f("&aThe casino chip prices have been reset!"));
            }
        }.runTaskTimer(Core.getInstance(), 20*60*60, 20*60*60);
        return this;
    }

    public int getBaseCoinBuyPrice() {
        return baseCoinBuyPrice;
    }

    public int getBaseCoinSellPrice() {
        return baseCoinSellPrice;
    }

    public void addSoldCoins(int coins){
        soldCoins += coins;
        if(soldCoins>=1000) {
            setCurrentMultiplier(currentMultiplier + .1);
            soldCoins = 0;
        }
    }

    private synchronized void resetSoldCoins(){
        this.soldCoins = 0;
    }

    public synchronized double getCurrentMultiplier(){
        return currentMultiplier;
    }

    private synchronized void setCurrentMultiplier(double d){
        currentMultiplier = d;
    }

    public void giveCasinoChips(Player player, final int amount){
        ServerUtil.runTaskAsync(new BukkitRunnable() {
            @Override
            public void run() {
                int dynamicAmount = amount;
                dynamicAmount += getTotalCasinoChips(player);
                for(ItemStack is : player.getInventory().getContents()) {
                    if(is==null)
                        continue;
                    if(isCasinoChip(is))
                        player.getInventory().removeItem(is);
                }
                HashMap<ItemStack, Integer> items = new HashMap<>();
                for(ChipAmount chip : ChipAmount.values()) {
                    while (dynamicAmount >= chip.getAmount()) {
                        ItemStack coin = chip.getItemStack();
                        if (items.containsKey(coin))
                            items.put(coin, items.get(coin) + 1);
                        else
                            items.put(coin, 1);
                        dynamicAmount -= chip.getAmount();
                    }
                }
                List<ItemStack> convertedItems = new ArrayList<ItemStack>();
                items.entrySet().forEach(entry -> {
                    ItemStack key = entry.getKey();
                    key.setAmount(entry.getValue());
                    convertedItems.add(key);
                });
                ServerUtil.runTask(new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(ItemStack is : convertedItems)
                            Utils.giveItems(player, is);
                    }
                });
            }
        });

    }

    public int getTotalCasinoChips(Player player) {
        int amt = 0;
        for(ItemStack is : player.getInventory().getContents()) {
            if(is==null)
                continue;
            if(isCasinoChip(is)) {
                ChipAmount chip = ChipAmount.getChipAmount(is.getItemMeta().getDisplayName()).get();
                amt += chip.getAmount()*is.getAmount();
            }
        }
        return amt;
    }

    public boolean isCasinoChip(ItemStack is){
        if(is==null)
            return false;
        return is.getType()==Material.DIAMOND_SWORD && is.getDurability()==1002;
    }

    public boolean removeCasinoChips(Player player, int amount) {
        if(!hasCasinoChips(player, amount))
            return false;
        int newTotal = getTotalCasinoChips(player)-amount;
        for(ItemStack is : player.getInventory().getContents()) {
            if(!isCasinoChip(is))
                continue;
            player.getInventory().removeItem(is);
        }
        giveCasinoChips(player, newTotal);
        return true;
    }

    public boolean hasCasinoChips(Player player, int amount){
        int invAmount = 0;
        for(ItemStack is : player.getInventory().getContents()) {
            if(!isCasinoChip(is))
                continue;
            ChipAmount chip = ChipAmount.getChipAmount(is.getItemMeta().getDisplayName()).get();
            invAmount += chip.getAmount()*is.getAmount();
        }
        return invAmount>=amount;
    }


}
