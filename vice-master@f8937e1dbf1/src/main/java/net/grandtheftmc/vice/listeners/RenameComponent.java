package net.grandtheftmc.vice.listeners;

import com.google.common.collect.Sets;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class RenameComponent implements Component<RenameComponent, Vice> {

    private static final Set<Material> BLACKLIST;

    static {
        BLACKLIST = Sets.newHashSet();
        BLACKLIST.add(Material.MOB_SPAWNER);
        BLACKLIST.add(Material.CHEST);
        BLACKLIST.add(Material.DIAMOND_SWORD);
    }

    public RenameComponent(Vice vice) {
        Bukkit.getPluginManager().registerEvents(this, vice);
    }

    @EventHandler
    protected final void onItemRename(PrepareAnvilEvent event) {
        if (event.getInventory() == null) return;
        if (event.getResult() == null) return;
        if (event.getResult().getType() == Material.AIR) return;

        if (BLACKLIST.contains(event.getResult().getType())) {
            event.setResult(new ItemStack(Material.AIR));
        }

    }
}
