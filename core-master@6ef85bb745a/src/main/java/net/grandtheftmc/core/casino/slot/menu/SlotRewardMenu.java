package net.grandtheftmc.core.casino.slot.menu;

import org.bukkit.Material;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;

public class SlotRewardMenu extends CoreMenu {

	/**
	 * Create a new SlotRewardMenu that tells players the rewards for winning.
	 */
    public SlotRewardMenu() {
        super(3, "Rewards");
    
        addItem(new MenuItem(10, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 3x " + C.GOLD + C.BOLD + "'7's").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$75,000-$60,000,000").build(), false));
        addItem(new MenuItem(11, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 3x " + C.GOLD + C.BOLD + "'Cherry's").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$15,000-$12,000,000").build(), false));
        addItem(new MenuItem(12, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 3x " + C.GOLD + C.BOLD + "'Dildos's").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$7,500-$2,400,000").build(), false));
        addItem(new MenuItem(13, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 3x " + C.GOLD + C.BOLD + "'LSD's").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$1,500-$1,200,000").build(), false));
        addItem(new MenuItem(14, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 3x " + C.GOLD + C.BOLD + "'MDMA's").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$750-$600,000").build(), false));
        addItem(new MenuItem(15, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Roll 2 " + C.GOLD + C.BOLD + "'Identicals").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$450-$360,000").build(), false));
        addItem(new MenuItem(16, new ItemFactory(Material.GOLD_INGOT).setName(C.WHITE + C.BOLD + "Per " + C.GOLD + C.BOLD + "Cherry").setLore("", C.WHITE + "Reward: " + C.YELLOW + "$300-$240,000").build(), false));
    }
}

