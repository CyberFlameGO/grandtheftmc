package net.grandtheftmc.core.inventory.example;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public class ExampleUI extends CoreMenu {

    /**
     * Construct a new Menu.
     */
    public ExampleUI() {
        //Without MenuFlags
        super(6, "Example Interface");

        //With MenuFlags
        //super(6, "Example Interface", CoreMenuFlag.CLOSE_ON_NULL_CLICK, CoreMenuFlag.RESET_CURSOR_ON_OPEN);


        //new MenuItem(index, itemstack, allowPickup)
        addItem(new MenuItem(0, new ItemStack(Material.APPLE), false));

        //new ClickableItem(index, itemstack, menuClickAction)
        addItem(new ClickableItem(1, new ItemStack(Material.ENDER_PEARL, 4), (player, clickType) -> {
            player.sendMessage("You clicked the Ender pearl... nice! (" + clickType.name() + ")");
        }));

        //done.
    }

    //How to open this menu.
    //new ExampleUI().openInventory(player);
}
