package net.grandtheftmc.vice.combatlog;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-08-17.
 */
public class CombatLogger {
    private final UUID uuid;
    private final boolean fromSpawn;
    private final List<ItemStack> contents;

    public CombatLogger(UUID uuid, List<ItemStack> contents, boolean fromSpawn) {
        this.uuid = uuid;
        this.fromSpawn = fromSpawn;
        this.contents = contents;
    }

    public List<ItemStack> getContents() {
        return contents;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isFromSpawn() {
        return fromSpawn;
    }
}
