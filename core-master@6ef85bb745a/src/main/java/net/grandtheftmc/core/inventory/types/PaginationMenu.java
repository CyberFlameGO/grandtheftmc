package net.grandtheftmc.core.inventory.types;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 1/22/2018.
 * @deprecated in progress.
 */
public abstract class PaginationMenu extends CoreMenu {

    protected final ItemStack[] items = getItems();
    public PaginationMenu(String title, CoreMenuFlag... menuFlags) {
        super(6, title, menuFlags);
    }

    public PaginationMenu(String title){
        super(6, title);
    }

    /**
     * Open the inventory to the specified player.
     *
     * @param player Specified Player
     */
    public void openInventory(Player player, int page) {
        if(hasFlag(CoreMenuFlag.RESET_CURSOR_ON_OPEN)) player.closeInventory();

    }

    protected abstract ItemStack[] getItems();


}
