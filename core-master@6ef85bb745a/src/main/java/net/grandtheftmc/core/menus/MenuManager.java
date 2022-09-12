package net.grandtheftmc.core.menus;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuManager implements Component<MenuManager, Core> {
    private static final List<Menu> MENUS = new ArrayList<>();

    public static void openMenu(Player player, String menuName) {
        Menu menu = getMenu(menuName);
        if (menu == null)
            return;
        menu.openFor(player);
    }

    @Override
    public MenuManager onDisable(Core plugin) {
        MENUS.clear();
        return this;
    }

    public static void updateMenu(Player player, String menuName) {
        InventoryView view = player.getOpenInventory();
        if (view == null)
            return;
        Menu menu = getMenu(menuName);
        if (menu == null || !Objects.equals(Utils.f(menu.getDisplayName()), view.getTitle()))
            return;
        MenuOpenEvent event = new MenuOpenEvent(player, menu);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            view.getTopInventory().setContents(event.getContents());
            player.updateInventory();
        }
    }

    public static void updateMenu(String menuName) {
        for (Player player : Bukkit.getOnlinePlayers())
            updateMenu(player, menuName);
    }

    public static Menu getMenu(String menuName) {
        return MENUS.stream().filter(menu -> menu.getName().equalsIgnoreCase(menuName)).findFirst().orElse(null);
    }

    public static Menu getMenuFromTitle(String title) {
        return MENUS.stream().filter(menu -> Utils.f(menu.getDisplayName()).equalsIgnoreCase(title)).findFirst().orElse(null);
    }

    public static List<Menu> getMenus() {
        return MENUS;
    }

    public static void addMenu(Menu menu) {
        MENUS.add(menu);
    }

    public static void addMenu(String name, int size, String title) {
        MENUS.add(new Menu(name, size, title));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Menu menu = getMenuFromTitle(e.getInventory().getTitle());
        if (menu == null || e.getClickedInventory() == null || !Objects.equals(e.getClickedInventory(), e.getInventory()))
            return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if (Core.getUserManager().getLoadedUser(p.getUniqueId()).isInTutorial()) return;
        MenuClickEvent event = new MenuClickEvent(p, menu, e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), e);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Menu menu = getMenuFromTitle(e.getInventory().getTitle());
        if (menu == null) return;
        Player player = (Player) e.getPlayer();
        MenuCloseEvent event = new MenuCloseEvent(player, menu);
        Bukkit.getPluginManager().callEvent(event);
    }
}
