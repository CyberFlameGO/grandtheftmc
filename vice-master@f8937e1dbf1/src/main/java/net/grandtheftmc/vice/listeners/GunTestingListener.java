package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;

public class GunTestingListener implements Component<GunTestingListener, Vice> {

    private final ItemStack[] states;

    public GunTestingListener() {
        this.states = new ItemStack[] {
                new ItemFactory(Material.STONE_SWORD).setDurability((short) 73).setUnbreakable(true).build(),
                new ItemFactory(Material.STONE_SWORD).setDurability((short) 74).setUnbreakable(true).build(),
                new ItemFactory(Material.STONE_SWORD).setDurability((short) 75).setUnbreakable(true).build(),
        };
        Bukkit.getPluginManager().registerEvents(this, Vice.getInstance());
    }

    @Override
    public GunTestingListener onEnable(Vice plugin) {
        return this;
    }

    @Override
    public GunTestingListener onDisable(Vice plugin) {
        return this;
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setItem(0, states[0]);
        event.getPlayer().updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPlayerMove(PlayerToggleSneakEvent event) {

        if (event.isSneaking()) event.getPlayer().getInventory().setItem(0, states[2]);
        else event.getPlayer().getInventory().setItem(0, states[0]);
        event.getPlayer().updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPlayerMove(PlayerToggleSprintEvent event) {
//        PlayerToggleSprintEvent
//        PlayerToggleSneakEvent

        if (event.isSprinting()) event.getPlayer().getInventory().setItem(0, states[1]);
        else event.getPlayer().getInventory().setItem(0, states[0]);
        event.getPlayer().updateInventory();
    }
}
