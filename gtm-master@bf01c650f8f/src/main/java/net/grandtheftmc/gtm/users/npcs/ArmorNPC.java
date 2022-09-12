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
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class ArmorNPC extends CoreNPC implements ClickableNPC, CollideableNPC {
    private static final String[] ARMOR_GAMEITEMS = new String[]{"shirt", "kevlarvest", "ceramicvest", "titaniumvest", "jetpackfuel10", "jetpack", "jetpackfuel100", "nikes", "pants", "baseballcap", "tacticalmask","titaniumhelmet"};
    public ArmorNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&c&lFrank Sheathson", "&7&oBuy armor and clothes here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5ODM2NDk2NjgsInByb2ZpbGVJZCI6IjdjZjc2MTFkYmY2YjQxOWRiNjlkMmQzY2Q4NzUxZjRjIiwicHJvZmlsZU5hbWUiOiJrYXJldGg5OTkiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI4Y2RkZmQxM2Y1NmI3YzJjNGYxZjNkNmI4MjY5Y2UxOTgxYjJjYTlhOWFmMjEzODVhNDQ2YTUzMTEifX19", "f2GaPKFyc2hG+I8+7CnIbDZcxU56RG+gbYZzKt58n2PWifyjoR7USPSsQEV9eDM8EnJuay5vhbSuFODX8O2w1Fkz0M9MyjDg41J3ERwOVD8B7VnAS3P24gISwpAmKSL5QQ8Va8gUZvx4Cgc07XJflfw60YdZ4DpinW4XQh2b1rEMTdHBFilmFj4GVbejcMSRGp9aOY60TXX4+aD4Uww/CVuAaAE7tAcX0V1NkmP/sUBGtKtZLTxzVnUWYsMEjvOTlgy4gd4OsK+pCegG092k2KNoI5q93TG1oMNGRQtpD5Yd4jwA8uO5vUaHBw1tIxe+pn85j3qS3KIB0QDMHIM2NNi+tHKEkLO0+ScILD7Ud2vqfXxI0oO2j8olcVBGivScssKp+8k7p1tHL2kQxYt/K6y+Xiw76H0aYoabWOnwkw7ZobDMHKoWMSBxbxqDjJ4n8itTtIO1Qn5UQZ9zAbEojGwwBZ82fqZmZkKBPX1b2/9yJ2jkVCKmEV3sSUBgovjPtm44ZyONFqf0HkmI1r19cCgzzY1xy13sueokOgevYn0trkLJsitpJEt6Z3pbnZ99k3aqjtsi+FMUqxqHHe8U8Kp7AKxW/BNnZOBFcFArXjy/C9ia8g8QajUWBt+YRr+RiDhEpCDON1WPYGhyq95FWIJf9Uz3/z59mMTNhUZAA9c=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        
        if (!GTM.getSettings().canBuy()){
        	player.sendMessage(ChatColor.RED + "Buying things is currently disabled.");
        	return;
        }
        
        new ArmorMenu().openInventory(player);
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

    private class ArmorMenu extends CoreMenu {
        private final int[] ARMOR_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
        public ArmorMenu() {
            super(6, Utils.f("&8&lArmor Seller"), CoreMenuFlag.PHONE_LAYOUT);

            int counter = 0;
            for(String armor : ARMOR_GAMEITEMS) {
                GameItem item = armor.contains("jetpackfuel") ? GTM.getItemManager().getItem("jetpackfuel") : GTM.getItemManager().getItem(armor);
                if(!item.canBuy())
                    continue;
                int amt = 1;
                if(armor.contains("jetpackfuel")) {
                    amt = Integer.parseInt(armor.replace("jetpackfuel", ""));
                }
                double price = item.getBuyPrice()*amt;
                ItemStack is = item.getItem();
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&6&l" + amt+ "x " + im.getDisplayName()));
                im.setLore(Collections.singletonList(Utils.f("&6&lPrice: $&a" + (price))));
                is.setItemMeta(im);

                int finalAmt = amt;
                addItem(new ClickableItem(ARMOR_SPOTS[counter], is, ((player, clickType) -> {
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    if(!user.hasMoney(price)){
                        player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                        return;
                    }
                    user.takeMoney(price);
                    GTMUtils.updateBoard(player, GTM.getUserManager().getLoadedUser(player.getUniqueId()));
                    Utils.giveItems(player, item.getItem(finalAmt));
                }), false));
                counter++;

            }
        }
    }
}
