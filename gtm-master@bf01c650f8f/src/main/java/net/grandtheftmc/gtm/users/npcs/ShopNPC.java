package net.grandtheftmc.gtm.users.npcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.trashcan.TrashCanManager;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class ShopNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    private static final Set<String> DISABLED = new HashSet<>(Arrays.asList("clausinator"));
    private static final HashMap<AmmoType, Integer> AMMO_MULTIPLIERS = new HashMap<>();
    private final WeaponManager weaponManager;

    static {
        AMMO_MULTIPLIERS.put(AmmoType.ASSAULT_RIFLE, 50);
        AMMO_MULTIPLIERS.put(AmmoType.GRENADE, 5);
        AMMO_MULTIPLIERS.put(AmmoType.ROCKET, 1);
        AMMO_MULTIPLIERS.put(AmmoType.LAUNCHER, 1);
        AMMO_MULTIPLIERS.put(AmmoType.MINIGUN, 600);
        AMMO_MULTIPLIERS.put(AmmoType.PISTOL, 20);
        AMMO_MULTIPLIERS.put(AmmoType.SMG, 60);
        AMMO_MULTIPLIERS.put(AmmoType.LMG, 40);
        AMMO_MULTIPLIERS.put(AmmoType.SHOTGUN, 12);
        AMMO_MULTIPLIERS.put(AmmoType.SNIPER, 10);
        AMMO_MULTIPLIERS.put(AmmoType.FUEL, 64);
    }

    public ShopNPC(WeaponManager weaponManager, Location loc) {
        super(loc, EntityType.PLAYER, "&9&lGary McNaggins", "&7&oBuy the best guns and ammo here!");
        this.weaponManager = weaponManager;
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MDc2MTM5NzI2NDUsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyYjUwYzI4NDQ1YmQyOWE2OGRmZTIyM2M0YzlmZDYyZTYyOWNkNWU1ZDdjMTc3NzIwNmU4YjRkNjMxIn19fQ==", "gza3eHED3BMmxRjZmDDUQQliH10Q4e8U5uKNv0RaGfOKdPOxMToH2rqSpyNeS+odXQvAq6cDZulKk5LZgcs89kpv+Jkb3sfWdUb6HnJvqkeA+4iTw3n9BxRZpoC0lyBmiJSQPlSwywgjmd9cybGPtgX3+WpbExRDYy90X8ii3iN9dlFlNWFiInNZjBUUjslcqnD8VEkItonJwNbPbXkgvHu0qmiBon6bWmnI81cO0DekrxOGAbQQynNosnGVbV7oGTAtN87G9zM7McNvMXK+1BJqAxdqad3U2Jfnu3PHDZ1pDCJIA+5yQiiTblQPzYx9Fp73E2NpS51239/P5B0bWOa8MWGK2fKCznxRy/lZTd/3Ewojxu9guWann0ALLeYyvXA/FDY1vY6clRF50JyhgBR6Tf58lOF8kkq964gdpYlhtldI1ZWf8jn/inK//b3rNmqu046oKQLuhYjxVNoV4lrzzb+pzjjKx2/iBXqzxnWTjrTLZv6n6jLS9aFghryaLbUXc4IETj+MsZ5Z9WdPCG02V3f3Z+5aFZfMg2zkj1qQxDVhrdJr/87lE23ZupYDV1szocx39JF1gtwbKhTugVKlDV4UQZHokFdcFRtMLSpX7zJwNLiVK/+aMN1YbGQzdwII9CFXN2DtgawzTnQQafEBwNiyp3GAcPTE9VqffFY=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        
        if (!GTM.getSettings().canBuy()){
        	player.sendMessage(ChatColor.RED + "Buying things is currently disabled.");
        	return;
        }
        
        new CategoryMenu().openInventory(player);
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

    private class CategoryMenu extends CoreMenu {
        private final int[] CATEGORY_SLOTS = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        private int counter = 0;

        protected CategoryMenu() {
            super(6, Utils.f("&c&lChoose Category"), CoreMenuFlag.PHONE_LAYOUT);
            addItem(getCategoryPlaceholder(WeaponType.THROWABLE, Utils.f("&7&oRemember: Don't miss!")));
            addItem(getCategoryPlaceholder(WeaponType.MELEE, Utils.f("&7&oFor when you need to get close and personal.")));
            addItem(getCategoryPlaceholder(WeaponType.PISTOL, Utils.f("&7&oA basic gun; point, shoot, kill.")));
            addItem(getCategoryPlaceholder(WeaponType.LMG, Utils.f("&7&oSlow and steady wins the race... so do a lot of bullets.")));
            addItem(getCategoryPlaceholder(WeaponType.SMG, Utils.f("&7&oFor when you need to get close and personal.")));
            addItem(getCategoryPlaceholder(WeaponType.SHOTGUN, Utils.f("&7&oDamage? Check! Spread? Check!"), Utils.f("&7&oOverall coolness factor? What more could you want!?")));
            addItem(getCategoryPlaceholder(WeaponType.ASSAULT, Utils.f("&7&oNow we're talking!")));
            addItem(getCategoryPlaceholder(WeaponType.LAUNCHER, Utils.f("&7&oWhen you're too lazy to throw...")));
            addItem(getCategoryPlaceholder(WeaponType.SNIPER, Utils.f("&7&oNot all battles are fought at close range.")));
            addItem(getCategoryPlaceholder(WeaponType.SPECIAL, Utils.f("&7&oRespect comes in many forms..."), Utils.f("&7&oespecially that of a giant death machine!")));

            addItem(getSellWeaponsButton());
        }

        private ClickableItem getCategoryPlaceholder(WeaponType type, String... lore) {
            Optional<Weapon<?>> optWeapon = type == WeaponType.SPECIAL ? weaponManager.getWeapon("minigun") : weaponManager.getRegisteredWeapons().stream().filter(w -> w.getWeaponType() == type && GTM.getItemManager().getItemFromWeapon(w.getCompactName()) != null && GTM.getItemManager().getItemFromWeapon(w.getCompactName()).canBuy()).findFirst();
            if (!optWeapon.isPresent()) {
                return new ClickableItem(CATEGORY_SLOTS[counter++], new ItemStack(Material.STONE), ((player, clickType) -> {
                    new ShopMenu(type).openInventory(player);
                }), false);
            }

            ItemStack is = optWeapon.get().getBaseItemStack().clone();
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(Utils.f("&a&l" + type.toString()));
            im.setLore(Arrays.asList(lore));
            im.setUnbreakable(true);
            im.setUnbreakable(true);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            is.setItemMeta(im);
            return new ClickableItem(CATEGORY_SLOTS[counter++], is, ((player, clickType) -> {
                new ShopMenu(type).openInventory(player);
            }), false);
        }

        private ClickableItem getSellWeaponsButton() {
            ItemStack is = new ItemStack(Material.PAPER);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(Utils.f("&a&lSell Weapons"));
            is.setItemMeta(im);

            return new ClickableItem(49, is, ((player, clickType) -> {
                player.closeInventory();
                TrashCanManager.openTrashCan(player);
            }));
        }
    }

    private class ShopMenu extends CoreMenu {

        private final int[] WEAPON_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};

        /**
         * PAGE LEGEND
         * 1 - GRENADES
         * 2 - MELEE
         * 3 - PISTOLS
         * 4 - SMGS
         * 5 - LMGS
         * 6 - SHOTGUNS
         * 7 - ASSAULT
         * 8 - LAUNCHER
         * 9 - SPECIAL
         */
        protected ShopMenu(WeaponType type) {
            super(6, Utils.f("&9&lPurchase " + type.toString()), CoreMenuFlag.PHONE_LAYOUT);
            addItem(getBackwardSelector());

            int counter = 0;
            Set<Weapon<?>> filtered = weaponManager.getRegisteredWeapons().stream().filter(w -> {
                if (w == null)
                    return false;
                if (DISABLED.contains(w.getName().toLowerCase()))
                    return false;
                return w.getWeaponType() == type || (type == WeaponType.SPECIAL && (w.getWeaponType() != WeaponType.MELEE && w.getWeaponType() != WeaponType.ASSAULT && w.getWeaponType() != WeaponType.LAUNCHER && w.getWeaponType() != WeaponType.THROWABLE && w.getWeaponType() != WeaponType.SMG && w.getWeaponType() != WeaponType.LMG && w.getWeaponType() != WeaponType.SHOTGUN && w.getWeaponType() != WeaponType.PISTOL && w.getWeaponType() != WeaponType.SNIPER));
            }).collect(Collectors.toSet());

            for (Weapon<?> w : filtered) {
                GameItem item = GTM.getItemManager().getItemFromWeapon(w.getCompactName());
                if (item == null) {
                    Core.error("[ShopNPC] Unable to load gameitem from weapon: " + w.getCompactName());
                    continue;
                }
                if (!item.canBuy()) {
                    ServerUtil.debug("[ShopNPC] Unable to find buy price for weapon: " + w.getCompactName() + " is it suppose to be like that?");
                    continue;
                }
                ItemStack is = item.getItem();
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&6" + w.getName()));
                List<String> lore = new ArrayList<>();
                lore.add(Utils.f("&6&lBuy Weapon &8(&6Left Click&8)&6: $&a" + item.getBuyPrice()));

                GameItem ammoItem = null;
                if (type != WeaponType.THROWABLE && type != WeaponType.MELEE && w.getAmmoType() != AmmoType.NONE) {
                    if (type == WeaponType.LMG)
                        ammoItem = net.grandtheftmc.gtm.items.AmmoType.MG.getGameItem();//because of how the naming of the wastedguns / gtmguns is :(
                    else
                        ammoItem = net.grandtheftmc.gtm.items.AmmoType.getAmmoType(w.getAmmoType().toString()).getGameItem();
                    if (ammoItem == null) {
                        Core.error("[ShopNPC] Unable to load ammo from string: " + w.getAmmoType() + " for weapon: " + w.getName());
                        return;
                    }

                    lore.add(Utils.f("&6&lBuy x&b&l" + AMMO_MULTIPLIERS.get(w.getAmmoType()) + "&6&l Ammo &8(&6Right Click&8)&6: $&a" + (ammoItem.getBuyPrice() * AMMO_MULTIPLIERS.get(w.getAmmoType()))));
                }
                im.setLore(lore);
                is.setItemMeta(im);

                final GameItem finalAmmoItem = ammoItem;
                
                addItem(new ClickableItem(WEAPON_SPOTS[counter], is, (player, clickType) -> {
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    switch (clickType) {
                        case LEFT:
                        case SHIFT_LEFT:
                            if (!user.hasMoney(item.getBuyPrice())) {
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                                return;
                            }
                            user.takeMoney(item.getBuyPrice());
                            Utils.giveItems(player, item.getItem());
                            break;
                        case RIGHT:
                        case SHIFT_RIGHT:
                        	
                            if (finalAmmoItem == null)
                                return;
                            if (!user.hasMoney(finalAmmoItem.getBuyPrice() * AMMO_MULTIPLIERS.get(w.getAmmoType()))) {
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                                return;
                            }
                            
                            net.grandtheftmc.gtm.items.AmmoType ammoType = net.grandtheftmc.gtm.items.AmmoType.getAmmoType(w.getAmmoType().getType());
                            
                            // for some reason this can be null
                            if (ammoType == null){
                            	return;
                            }
                            
                            user.takeMoney(finalAmmoItem.getBuyPrice() * AMMO_MULTIPLIERS.get(w.getAmmoType()));
                            user.addAmmo(ammoType, AMMO_MULTIPLIERS.get(w.getAmmoType()));
                            player.sendMessage(Lang.AMMO_ADD.f(AMMO_MULTIPLIERS.get(w.getAmmoType()) + "&7 " + w.getAmmoType().toString()));
                            break;
                    }
                }, false));
                counter++;
            }
        }


        private ClickableItem getBackwardSelector() {
            ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner("MHF_ArrowLeft");
            im.setDisplayName(Utils.f("&cBack"));
            is.setItemMeta(im);
            return new ClickableItem(47, is, ((player, clickType) -> {
                new CategoryMenu().openInventory(player);
            }));
        }
    }
}
