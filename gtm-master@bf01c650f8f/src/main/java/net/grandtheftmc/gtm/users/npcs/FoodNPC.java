package net.grandtheftmc.gtm.users.npcs;

import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class FoodNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    private static final String[] FOOD_GAMEITEMS = new String[]{"burrito", "burger", "chocolatechipcookie", "friedchicken", "carrot", "chickensoup", "pie"};

    public FoodNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&c&lBenjamin Baker", "&7&oBuy different food here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5ODM3MDczNjMsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIzNzI3MTZmMDczMTYxOTJjZTg5MTk3YWY5MjQzMjY4MzZiZGQ0OWI1NWE0YmMyMTFiNjczNjg2NjFhMzU2NyJ9fX0=", "BwAEZYJEJRpiipEyWGUiWbWOfRYCxd1B1JDDwfJfXFKjLB7DzT9wAgXVr+WBA1fr6eU8sU8lSHZ9TL8NWRZVoqbyQjX04d18pz2XaBXH8yu02qOK8od3VhAihVId4pLh1W3zwApGGlP5iHQI1xZ3PZsqJxmuGEugHfg+9E1031lls0alA4WLImuocZx0Xjt/gHphAtQgw0K2WLm1U+78kZ2mBl7B+mSgWMz19b1UfkgLoYeusUxPbNXhEwX/A0H1wQS20TZuYabHBxp5yG4jYt8UZoetTtNQ5u927AhoiYbTtd8mWHRxOqc15EsPw91dO8Vy7ZFEQbALX2kFp6ghYo5D6sLoPPk41GXSi/Z0m0md1nKcqH2QRWmaX0KfssHZz1iVPlQtBBDWTVX6/xmUaMnUH2/fyVIDVf2d1zw2JxucSQ3X9k7s6Gm9BekKyMVtX8qwveemGf1/fVH6aTztYXrNoPC0GEcYu0fmQ4UnDuwFeqIAtLOtAwB3jAXxss04Xc4AnH09Q5CdE+OpK2pd949jj65Y84wjbj3Vkelur5xc8WcRBg6uoO/Xx8nQr9vNY9MUvFpIhATS/GHb3gP+U8hGUNYIwG7ff/neD4pSqZqBcTq4Vf7qC4eTsoepWXdGFLyT2qm2EY6XSkndyIuSMWMEI3aKoLKrjOBC8U8OhDY=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        
        if (!GTM.getSettings().canBuy()){
        	player.sendMessage(ChatColor.RED + "Buying things is currently disabled.");
        	return;
        }
        
        new FoodMenu().openInventory(player);
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent npcLeftClickEvent) {

    }

    @Override
    public void onCollide(NPCCollisionEvent event) {
        
    }

    @Override
    public void onPush(NPCPushEvent event) {
        
    }

    private class FoodMenu extends CoreMenu {
        private final int[] FOOD_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
        public FoodMenu() {
            super(6, Utils.f("&c&lFood Seller"), CoreMenuFlag.PHONE_LAYOUT);

            int counter = 0;
            for(String food : FOOD_GAMEITEMS) {
                GameItem item = GTM.getItemManager().getItem(food);
                if(!item.canSell())
                    continue;
                double price = item.getSellPrice()*8;
                ItemStack is = item.getItem();
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&6&l8x " + im.getDisplayName()));
                im.setLore(Collections.singletonList(Utils.f("&6&lPrice: $&a" + price)));
                is.setItemMeta(im);

                addItem(new ClickableItem(FOOD_SPOTS[counter], is, ((player, clickType) -> {
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    if(!user.hasMoney(price)){
                        player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                        return;
                    }
                    user.takeMoney(price);
                    Utils.giveItems(player, item.getItem(8));
                }), false));
                counter++;

            }
        }
    }
}
