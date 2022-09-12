package net.grandtheftmc.core.voting;

import com.j0ach1mmall3.jlib.methods.Parsing;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private final int price;
    private final ItemStack item;
    private final RewardPack pack;

    public ShopItem(String name, int price, String item, RewardPack pack) {
        this.price = price;
        this.item = Parsing.parseItemStack(item);
        this.pack = pack;
    }


    public String getName() {
        return this.pack.getName();
    }

    public int getPrice() {
        return this.price;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public RewardPack getRewardPack() {
        return this.pack;
    }

    public void buy(Player player, User user) {
        if (this.price > 0 && !user.hasTokens(this.price)) {
            player.sendMessage(
                    Lang.TOKEN_SHOP.f("&7You do not have the &e&l" + this.price + " Tokens&7 to pay for this item!"));
            return;
        }
        player.sendMessage(
                Lang.TOKEN_SHOP.f("&7You bought &a" + this.pack.getName() + "&7 for &e&l" + this.price + " Token" + (this.price == 1 ? "" : "s") + "&7!"));
        user.takeTokens(this.price);
        this.pack.give(player, user, "buyShopItem", this.price, true);
        UpdateEvent e = new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS);
        Bukkit.getPluginManager().callEvent(e);
    }
}
