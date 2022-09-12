package net.grandtheftmc.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class MenuCloseEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Menu menu;

    public MenuCloseEvent(Player player, Menu menu) {
        super(player);
        this.menu = menu;
    }

    public Menu getMenu() {
        return this.menu;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
