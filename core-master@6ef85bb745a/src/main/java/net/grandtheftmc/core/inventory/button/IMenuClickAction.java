package net.grandtheftmc.core.inventory.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public interface IMenuClickAction {

    /**
     * This is fired when an item is interacted with.
     *
     * @param player    Player who interacted
     * @param clickType interaction type
     */
    void onClick(Player player, ClickType clickType);
}
