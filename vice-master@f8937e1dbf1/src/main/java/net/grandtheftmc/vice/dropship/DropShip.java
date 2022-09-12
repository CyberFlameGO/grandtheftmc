package net.grandtheftmc.vice.dropship;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.lootcrates.LootCrate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class DropShip {

    private final JavaPlugin plugin;
    private final DropShipManager dropShipManager;

    private final DropShipTask dropShipTask;
    private final Player caller;
    private final Area settlement;
    private final Area.DropType dropType;

    private DropShipState dropState = DropShipState.IDLE;

    public DropShip(JavaPlugin plugin, DropShipManager dropShipManager, Player caller, Area settlement, boolean major) {
        this.plugin = plugin;
        this.dropShipManager = dropShipManager;
        this.dropShipTask = new DropShipTask(major, dropShipManager, this);
        this.caller = caller;
        this.settlement = settlement;
        this.dropType = major ? Area.DropType.MAJOR : Area.DropType.MINOR;
    }

    public void start() {
        this.dropShipTask.runTaskTimer(this.plugin, 0, 20L);
        this.setDropState(DropShipState.IN_PROGRESS);
    }

    public void stop() {
        this.setDropState(DropShipState.ENDING);
        this.dropShipTask.stop();
        this.dropShipManager.reset();
    }

    protected void restock() {
        System.out.println("Settlement '" + this.settlement.getName() + "' has " + this.settlement.getChests().size() + " Chests.");
        for (Chest chest : this.settlement.getChests()) {
            if (!chest.getChunk().isLoaded())
                chest.getChunk().load();

            refill(chest).ifPresent(lootCrate -> lootCrate.restock(this.dropType));
        }
    }

    private Optional<LootCrate> refill(Chest chest) {
        for (LootCrate crate : Vice.getCrateManager().getCrates()) {
            if (crate.getLocation().getBlock().getLocation().toString().equals(chest.getLocation().toString())) {
                return Optional.of(crate);
            }
        }

        Vice.getCrateManager().addCrate(chest.getLocation());
        return Optional.of(Vice.getCrateManager().getCrate(chest.getLocation()));
    }

    public DropShipTask getDropShipTask() {
        return dropShipTask;
    }

    public DropShipState getDropState() {
        return dropState;
    }

    public void setDropState(DropShipState dropState) {
        this.dropState = dropState;
    }

    public Player getCaller() {
        return caller;
    }

    public Area getSettlement() {
        return settlement;
    }
}
