package net.grandtheftmc.core.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import net.grandtheftmc.core.util.Utils;

public class Menu implements Listener {

    private final String name;
    private final String displayName;
    private final int size;
    private Inventory inventory;

    public Menu(String name, int size, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getSize() {
        return this.size;
    }

    public void openFor(Player player) {
        MenuOpenEvent event = new MenuOpenEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            Inventory inv = Bukkit.createInventory(null, size, Utils.f(this.displayName));
            inv.setContents(event.getContents());
            player.openInventory(inv);
            this.inventory = inv;
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void openFor(Player... p) {
        for (Player player : p) {
            this.openFor(player);
        }
    }
}
