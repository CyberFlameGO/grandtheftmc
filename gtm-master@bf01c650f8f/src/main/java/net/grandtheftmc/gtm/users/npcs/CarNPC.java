package net.grandtheftmc.gtm.users.npcs;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class CarNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public CarNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&d&lSlimy Patrick", "&7&oBest vehicles in the world!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5ODM1NDI2ODAsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdiYmUxMjdlMmFiYjQxNTNkMmVlZDQ0M2M3OGM0ZmVlNmQzOWVjNjZjMTA4MDkyNTBlYmIwNzQyZjYyMWVmNSJ9fX0=", "Reqe3QaLLd+i19gsDZZv8RrFgeOHb0sNvX/aqw/fEcp65YNV4rMCJWP0aDb60rt53mAedi3h4CR9BKIEgBEk1R2zbFJNwGQnHi5CH0yggiaSdjFzc90oAY/QPvsqya9Il/WQ+YutVQXTG3iMG62jfAm5xsX9I098eHkEqoWGhY4MNjFkTuX5u6XTi5knXj2dkWP8bD+6wwFRpyou5arwyZS3cVjMsvBUwZv34kLL7asUfEg+Ud3k1RNjJrwz92+YW2DqRH2R2ZFwhofSvawi3a2uPf8wpefARIkzM1AZ1MW3mj9CplUw1FHCz8tiu0eM2O4fyrqgVIfNsJoCpYqEDvSByMQo8svwhXLZr/JaQjYneYqTueJ2QGwhbVwKq8862VQLJRaRMMYgKRdPaBTpmvYOHN/LMhWBLVYsMqT7RE5jYybAcXR1Ad9/wtR66/BtXPgd0/0t7GAAA8KY2xw80k7XXfdSJCS6snVMPxuzvc0mGmGPq+FG8kb6/NQcGTtTyUVP5HA/qW0b+yQ2qlYRe/F6D//LVWM/1+Qo8U9z9eqHM+G7c/i+bQPa8NxYR8H2B4Hf/dQMnpMIgiclCuX6OcqPQr8ikDpJ8AQCmA1FR8S9uqxE92je5hFMy1+84jMfGLUKecs/wxFy+uUYHYbs2FH0eALMsP/XkbYfyOJmidc=");    
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        new CarMenu(player).openInventory(player);
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {

    }

    @Override
    public void onCollide(NPCCollisionEvent event) {
        
    }

    @Override
    public void onPush(NPCPushEvent event) {
        
    }

    private class CarMenu extends CoreMenu{

        private final String[] CARS = new String[]{"zentorno", "entity_xf", "9f", "armoredkuruma", "primo", "attackmaverick", "maverick", "bmx", "dinghy", "hydra", "rhino"};
        private final int[] CAR_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
        public CarMenu(Player p) {
            super(6, Utils.f("&d&lPurchase Cars"), CoreMenuFlag.PHONE_LAYOUT);
            int counter = 0;
            GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
            for(String s : CARS){
                GameItem item = GTM.getItemManager().getItem(s);
                if(item==null){
                    Core.error("[CarMenu] Unable to load car: " + s + " from game item.");
                    continue;
                }
                ItemStack is = item.getItem();
                if(user.hasVehicle(s)){
                    ItemMeta im = is.getItemMeta();
                    List<String> lore = im.getLore();
                    lore.add(Utils.f("&a&lPurchased"));
                    im.setLore(lore);
                    is.setItemMeta(im);
                }
                addItem(new ClickableItem(CAR_SPOTS[counter], is, (player, clickType) -> {
                    user.setActionVehicle(s);
                    player.closeInventory();
                    MenuManager.openMenu(player, "vehicleshop");
                }, false));
                counter++;
            }
        }
    }
}
