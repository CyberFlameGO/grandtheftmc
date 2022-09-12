package net.grandtheftmc.vice.users.npcs.shopnpc;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Timothy Lampen on 1/27/2018.
 */
public class BlocksMenu extends CoreMenu {
    private int counter = 0;
    private static final int[] ITEM_SPOTS = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
    private int[] CATEGORY_SLOTS = new int[]{21,23,30,32};

    protected BlocksMenu() {
        super(6, Utils.f("&c&lBuy Blocks"), CoreMenuFlag.PHONE_LAYOUT);

        for(Category c : Category.values()) {
            addItem(getCategoryPlaceholder(c));
        }

        addItem(generateBackwardSelector(new CategoryMenu()));
    }

    private enum Category {
        COLORFUL, REDSTONE, BASIC, MISC
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
        return new ClickableItem(47, is, ((player, clickType) -> {
            menu.openInventory(player);
        }));
    }

    private ClickableItem getCategoryPlaceholder(Category c){
        String gameItem = "", displayName = "";
        switch (c) {
            case MISC: {
                gameItem = "rednetherbrick";
                displayName = "&c&lMiscellaneous Blocks";
                break;
            }
            case REDSTONE: {
                gameItem = "redstone";
                displayName = "&c&lRedstone Blocks";
                break;
            }
            case BASIC: {
                gameItem = "dirt";
                displayName = "&c&lBasic Blocks";
                break;
            }
            case COLORFUL: {
                gameItem = "lightbluewool";
                displayName = "&c&lColorful Blocks";
                break;
            }
        }
        ItemStack is = Vice.getItemManager().getItem(gameItem).getItem();
        ItemMeta im = is.getItemMeta();
        im.setLore(Collections.emptyList());
        im.setDisplayName(Utils.f(displayName));
        is.setItemMeta(im);
        return new ClickableItem(CATEGORY_SLOTS[counter++], is,
                ((player, clickType) -> {
                    new SubCategoryMenu(c, 1).openInventory(player);
                }));
    }


    private class SubCategoryMenu extends CoreMenu{
        private int counter = 0;
        private final int page;
        private final Category category;

        public SubCategoryMenu(Category category, int page){
            super(6, Utils.f("&c&lBuy Blocks"), CoreMenuFlag.PHONE_LAYOUT);
            this.page = page;
            this.category = category;

            LinkedList<GameItem> filtered = Vice.getItemManager().getShopItems().stream().filter(gi -> gi.getShopCategory().equals("BLOCKS_" + category.toString())).collect(Collectors.toCollection(LinkedList::new));

            if(page==1) {
                addItem(generateBackwardSelector(new BlocksMenu()));
                if(filtered.size()>20)
                    addItem(generateNextPageSelector());
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
                    Core.error("[BlocksMenu] " + gi.getName() + " is in category blocks, but has no sell price.");
                    continue;
                }
                ServerUtil.debug("loading item " + gi.getName() + " / " + (gi.getItem()==null) + "  / " + gi.getSellPrice());
                ItemStack disp = gi.getItem().clone();
                ItemMeta im = disp.getItemMeta();

                ServerUtil.debug((im==null) + " / " + (disp==null) + " / " + (gi.getSellPrice()));
                im.
                        setLore(
                                Arrays.asList(Utils.f("&6&lBuy x&b&l1 &6&lBlock &8(&6Left Click&8): &6$&a" +
                                                gi.getSellPrice()),
                                        Utils.f("&6&lBuy x&b&l64 &6&lBlocks &8(&6Right Click&8): &6$&a" + gi.getSellPrice()*64)));
                disp.setItemMeta(im);

                addItem(new ClickableItem(ITEM_SPOTS[counter++], disp, ((player, clickType) -> {
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                    switch (clickType) {
                        case LEFT:
                        case SHIFT_LEFT:
                            if(!user.hasMoney(gi.getSellPrice())){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for this block!"));
                                return;
                            }
                            user.takeMoney(gi.getSellPrice());
                            Utils.giveItems(player, gi.getItem());
                            break;
                        case RIGHT:
                        case SHIFT_RIGHT:
                            if(!user.hasMoney(gi.getSellPrice()*64)){
                                player.sendMessage(Lang.SHOP.f("&cYou do not have enough money for these blocks!"));
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
                new SubCategoryMenu(this.category, this.page+1).openInventory(player);
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
                new SubCategoryMenu(this.category, this.page-1).openInventory(player);
            }));
        }
    }

}