package net.grandtheftmc.vice.users.npcs.shopnpc;

/**
 * Created by Timothy Lampen on 1/27/2018.
 */

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @apiNote Main category menu (when you first right click this pops up)
 *
 */
public class CategoryMenu extends CoreMenu {

    private static final Set<String> DISABLED = new HashSet<>(Arrays.asList("clausinator"));
    private static final HashMap<AmmoType, Integer> AMMO_MULTIPLIERS = new HashMap<>();
    private static final int[] ITEM_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
    private int counter = 0;
    private int[] CATEGORY_SLOTS = new int[]{21,22,23,30,32};


    static {
        /**
         * The multiples of which the ammo for the certain ammo type is sold as.
         */
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
    
    /**
     * @apiNote the different categories avaliable.
     */
    private enum Category {
        SELL_DRUGS, BUY_MACHINES, BUY_SUPPLIES, BUY_GUNS, BUY_BLOCKS
    }

    public CategoryMenu() {
        super(6, Utils.f("&c&lChoose Category"), CoreMenuFlag.PHONE_LAYOUT);
        for(Category c : Category.values()) {
            addItem(getCategoryPlaceholder(c));
        }

    }

    /**
     * @param menu the menu that when this item is clicked, the player will be directed to
     * @return a clickable item that does above.
     */
    private ClickableItem generateBackwardSelector(CoreMenu menu){
        ItemStack is  = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta im = (SkullMeta)is.getItemMeta();
        im.setOwner("MHF_ArrowLeft");
        im.setDisplayName(Utils.f("&c&lBack"));
        is.setItemMeta(im);
        return new ClickableItem(46, is, ((player, clickType) -> {
            menu.openInventory(player);
        }));
    }

    /**
     * @param c the category that you want to generate a placeholder for.
     * @return a clickable item that when clicked, directs the player to the correct category.
     */
    private ClickableItem getCategoryPlaceholder(Category c){
        switch (c) {
            case SELL_DRUGS: {
                ItemStack is = Vice.getItemManager().getItem("heroinsyringe").getItem().clone();
                ItemMeta im = is.getItemMeta();
                im.setLore(Collections.emptyList());
                im.setDisplayName(Utils.f("&a&lSell Drugs"));
                is.setItemMeta(im);
                return new ClickableItem(CATEGORY_SLOTS[counter++], is,
                        ((player, clickType) -> {
                            player.closeInventory();
                            Vice.getTrashCanManager().openTrashCan(player);
                        }));
            }
            case BUY_MACHINES: {
                ItemStack is = new ItemStack(Material.FURNACE);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&e&lBuy Machines"));
                is.setItemMeta(im);
                return new ClickableItem(CATEGORY_SLOTS[counter++], is,
                        ((player, clickType) -> new MachineMenu().openInventory(player)));
            }
            case BUY_SUPPLIES:
                return new ClickableItem(CATEGORY_SLOTS[counter++], Utils.createItem(Material.DIAMOND_PICKAXE, "&b&lBuy Supplies"),
                        ((player, clickType) -> new SuppliesMenu().openInventory(player)));
            case BUY_GUNS: {
                ItemStack is = Vice.getItemManager().getItem("smg").getItem();
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&a&lBuy Guns"));
                im.setLore(Collections.emptyList());
                is.setItemMeta(im);
                return new ClickableItem(CATEGORY_SLOTS[counter++],
                        is,
                        ((player, clickType) -> new GunCategoryMenu().openInventory(player)));
            }
            case BUY_BLOCKS:
                return new ClickableItem(CATEGORY_SLOTS[counter++],
                        Utils.createItem(Material.SAND, "&c&lBuy Blocks"),
                        ((player, clickType) -> new BlocksMenu().openInventory(player)));
        }
        return null;
    }

    /**
     *
     *
     * @apiNote Supplies menus
     *
     */
    private class SuppliesMenu extends CoreMenu {

        private int counter = 0;
        private final int page;

        protected SuppliesMenu(){
            this(1);
        }

        protected SuppliesMenu(int page) {
            super(6, Utils.f("&b&lBuy Supplies"), CoreMenuFlag.PHONE_LAYOUT);
            this.page = page;


            LinkedList<GameItem> filtered = Vice.getItemManager().getShopItems().stream().filter(gi -> gi.getShopCategory().equals("SUPPLIES")).collect(Collectors.toCollection(LinkedList::new));

            if(page==1) {
                addItem(generateNextPageSelector());
                addItem(generateBackwardSelector(new CategoryMenu()));
            }
            else if(page*20>=filtered.size())
                addItem(generatePrevPageSelector());
            else {
                addItem(generatePrevPageSelector());
                addItem(generateNextPageSelector());
            }

            int beginningIndex = page * 20 - 20;
            Set<GameItem> subset;
            if (beginningIndex + 20 <= filtered.size())
                subset = filtered.stream().skip(beginningIndex).limit(20).collect(Collectors.toSet());
            else if(beginningIndex <= filtered.size() && filtered.size()-beginningIndex < 20)
                subset = filtered.stream().skip(beginningIndex).limit(filtered.size()-beginningIndex).collect(Collectors.toSet());
            else
                subset = new HashSet<>();

            for(GameItem gi : subset) {
                if(!gi.canSell()) {
                    Core.error("[SuppliesMenu] " + gi.getName() + " is in category supplies, but has no sell price.");
                    continue;
                }
                ItemStack disp = gi.getItem().clone();
                ItemMeta im = disp.getItemMeta();
                im.setLore(Arrays.asList(Utils.f("&6&lBuy x&b&l1 &6&lItem &8(&6Left Click&8): &6$&a" + gi.getSellPrice()), Utils.f("&6&lBuy x&b&l64 &6&lItems &8(&6Right Click&8): &6$&a" + gi.getSellPrice()*64)));
                disp.setItemMeta(im);

                addItem(new ClickableItem(ITEM_SPOTS[counter++], disp, ((player, clickType) -> {
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                    switch (clickType) {
                        case LEFT:
                        case SHIFT_LEFT:
                            if(!user.hasMoney(gi.getSellPrice())){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                                return;
                            }
                            user.takeMoney(gi.getSellPrice());
                            Utils.giveItems(player, gi.getItem());
                            break;
                        case RIGHT:
                        case SHIFT_RIGHT:
                            if(!user.hasMoney(gi.getSellPrice()*64)){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for these items!"));
                                return;
                            }
                            user.takeMoney(gi.getSellPrice()*64);
                            Utils.giveItems(player, gi.getItem(64));
                            break;
                    }
                })));
            }
        }

        /**
         * @return a clickable item that will send player to next page of same category.
         */
        private ClickableItem generateNextPageSelector(){
            ItemStack is  = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            SkullMeta im = (SkullMeta)is.getItemMeta();
            im.setOwner("MHF_ArrowRight");
            im.setDisplayName(Utils.f("&cTo Page: &a" + (page+1)));
            is.setItemMeta(im);
            return new ClickableItem(52, is, ((player, clickType) -> {
                new SuppliesMenu(page+1).openInventory(player);
            }));
        }

        /**
         * @return a clickable item that will send player to previous page of same category.
         */
        private ClickableItem generatePrevPageSelector(){
            ItemStack is  = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            SkullMeta im = (SkullMeta)is.getItemMeta();
            im.setOwner("MHF_ArrowLeft");
            im.setDisplayName(Utils.f("&cTo Page: &a" + (page-1)));
            is.setItemMeta(im);
            return new ClickableItem(47, is, ((player, clickType) -> {
                new SuppliesMenu(page-1).openInventory(player);
            }));
        }
    }

    /**
     *
     *
     * @apiNote Machine buy menu
     *
     */

    private class MachineMenu extends CoreMenu {

        private int counter = 0;

        public MachineMenu() {
            super(6, Utils.f("&e&lBuy Machines"), CoreMenuFlag.PHONE_LAYOUT);
            addItem(generateBackwardSelector(new CategoryMenu()));

            for(BaseMachine machine : Vice.getInstance().getMachineManager().getStatues()) {
                GameItem item = Vice.getItemManager().getItem(machine.getName().replace(" ", ""));
                ItemStack is = item.getItem();
                ItemMeta im = is.getItemMeta();
                im.setLore(Collections.singletonList(Utils.f("&6&lPrice: &6$&a" + item.getSellPrice())));
                is.setItemMeta(im);
                addItem(new ClickableItem(ITEM_SPOTS[counter++], is, ((player, clickType) -> {
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                    if(!user.hasMoney(item.getSellPrice())){
                        player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                        return;
                    }
                    user.takeMoney(item.getSellPrice());
                    Utils.giveItems(player, item.getItem());
                })));
            }
        }


    }

    /**
     *
     *
     * @apiNote Gun Menus
     *
     */

    private class GunCategoryMenu extends CoreMenu {
        private final int[] CATEGORY_SLOTS = new int[]{20,21,22,23,24,29,30,31,32,33};
        private int counter = 0;

        protected GunCategoryMenu() {
            super(6, Utils.f("&c&lChoose Gun ategory"), CoreMenuFlag.PHONE_LAYOUT);
            addItem(getWeaponCategoryPlaceholder(WeaponType.THROWABLE, Utils.f("&7&oRemember: Don't miss!")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.MELEE, Utils.f("&7&oFor when you need to get close and personal.")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.PISTOL, Utils.f("&7&oA basic gun; point, shoot, kill.")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.LMG, Utils.f("&7&oSlow and steady wins the race... so do a lot of bullets.")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.SMG, Utils.f("&7&oFor when you need to get close and personal.")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.SHOTGUN, Utils.f("&7&oDamage? Check! Spread? Check!"), Utils.f("&7&oOverall coolness factor? What more could you want!?")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.ASSAULT, Utils.f("&7&oNow we're talking!")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.LAUNCHER, Utils.f("&7&oWhen you're too lazy to throw...")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.SNIPER, Utils.f("&7&oNot all battles are fought at close range.")));
            addItem(getWeaponCategoryPlaceholder(WeaponType.SPECIAL, Utils.f("&7&oRespect comes in many forms..."), Utils.f("&7&oespecially that of a giant death machine!")));

            addItem(generateBackwardSelector(new CategoryMenu()));

        }

        /**
         * @param type the type of weapon that  you want to generate a placeholder for
         * @param lore the flavor lore that you want to add to the placeholder itemstack.
         * @return a clickable item that when clicked, will open the specified weapon subcategory to the player.
         */
        private ClickableItem getWeaponCategoryPlaceholder(WeaponType type, String... lore){
            ServerUtil.debug("Trying to load weapon category: " + type);
            Optional<Weapon<?>> optWeapon = type == WeaponType.SPECIAL ? Vice.getWastedGuns().getWeaponManager().getWeapon("minigun") : Vice.getWastedGuns().getWeaponManager().getRegisteredWeapons().stream().filter(w -> w.getWeaponType()==type && Vice.getItemManager().getItemFromWeapon(w.getCompactName())!=null && Vice.getItemManager().getItemFromWeapon(w.getCompactName()).canSell()).findFirst();
            ItemStack is = optWeapon.get().getBaseItemStack().clone();
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(Utils.f("&a&l" + type.toString()));
            im.setLore(Arrays.asList(lore));
            is.setItemMeta(im);
            return new ClickableItem(CATEGORY_SLOTS[counter++], is, ((player, clickType) -> {
                new GunSubCategoryMenu(type).openInventory(player);
            }), false);
        }
    }

    private class GunSubCategoryMenu extends CoreMenu {

        protected GunSubCategoryMenu(WeaponType type) {
            super(6, Utils.f("&9&lPurchase " + type.toString()), CoreMenuFlag.PHONE_LAYOUT);
            addItem(generateBackwardSelector(new GunCategoryMenu()));

            int counter = 0;
            Set<Weapon<?>> filtered = GTMGuns.getInstance().getWeaponManager().getRegisteredWeapons().stream().filter(w ->{
                if(w==null)
                    return false;
                if(DISABLED.contains(w.getName().toLowerCase()))
                    return false;
                return w.getWeaponType()==type || (type==WeaponType.SPECIAL && (w.getWeaponType()!=WeaponType.MELEE && w.getWeaponType()!=WeaponType.ASSAULT && w.getWeaponType()!=WeaponType.LAUNCHER && w.getWeaponType()!=WeaponType.THROWABLE && w.getWeaponType()!= WeaponType.SMG && w.getWeaponType()!=WeaponType.LMG && w.getWeaponType()!=WeaponType.SHOTGUN && w.getWeaponType()!=WeaponType.PISTOL && w.getWeaponType()!=WeaponType.SNIPER));
            }).collect(Collectors.toSet());

            for(Weapon<?> w : filtered){
                GameItem item = Vice.getItemManager().getItemFromWeapon(w.getCompactName());
                if(item==null) {
                    Core.error("[GunSubCategory] Unable to load gameitem from weapon: " + w.getCompactName());
                    continue;
                }
                if(!item.canSell()) {
                    ServerUtil.debug("[GunSubCategory] Unable to find sell price for weapon: " + w.getCompactName() + " is it suppose to be like that?");
                    continue;
                }
                ItemStack is = item.getItem();
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(Utils.f("&6" + w.getName()));
                List<String> lore = new ArrayList<>();
                lore.add(Utils.f("&6&lBuy Weapon &8(&6Left Click&8)&6: $&a" + item.getSellPrice()));

                GameItem ammoItem = null;
                if(type!=WeaponType.THROWABLE && type!=WeaponType.MELEE && w.getAmmoType()!= AmmoType.NONE) {
                    if(type==WeaponType.LMG)
                        ammoItem = net.grandtheftmc.vice.items.AmmoType.MG.getGameItem();//because of how the naming of the wastedguns / gtmguns is :(
                    else
                        ammoItem = net.grandtheftmc.vice.items.AmmoType.getAmmoType(w.getAmmoType().toString()).getGameItem();
                    if(ammoItem==null) {
                        Core.error("[GunSubCategory] Unable to load ammo from string: " + w.getAmmoType() + " for weapon: " + w.getName());
                        return;
                    }

                    lore.add(Utils.f("&6&lBuy x&b&l" + AMMO_MULTIPLIERS.get(w.getAmmoType()) + "&6&l Ammo &8(&6Right Click&8)&6: $&a" + (ammoItem.getSellPrice()*AMMO_MULTIPLIERS.get(w.getAmmoType()))));
                }
                im.setLore(lore);
                is.setItemMeta(im);

                final GameItem finalAmmoItem = ammoItem;
                addItem(new ClickableItem(ITEM_SPOTS[counter], is, (player, clickType) -> {
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                    switch (clickType) {
                        case LEFT:
                        case SHIFT_LEFT:
                            if(!user.hasMoney(item.getSellPrice())){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                                return;
                            }
                            user.takeMoney(item.getSellPrice());
                            Utils.giveItems(player, item.getItem());
                            break;
                        case RIGHT:
                        case SHIFT_RIGHT:
                            if(finalAmmoItem ==null)
                                return;
                            if(!user.hasMoney(finalAmmoItem.getSellPrice()*AMMO_MULTIPLIERS.get(w.getAmmoType()))){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this item!"));
                                return;
                            }
                            user.takeMoney(finalAmmoItem.getSellPrice()*AMMO_MULTIPLIERS.get(w.getAmmoType()));
                            user.addAmmo(net.grandtheftmc.vice.items.AmmoType.getAmmoType(w.getAmmoType().toString()), AMMO_MULTIPLIERS.get(w.getAmmoType()));
                            player.sendMessage(Lang.AMMO_ADD.f(AMMO_MULTIPLIERS.get(w.getAmmoType()) + "&7 " + w.getAmmoType().toString()));
                            break;
                    }
                }, false));
                counter++;
            }
        }
    }
}
