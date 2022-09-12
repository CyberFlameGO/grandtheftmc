package net.grandtheftmc.vice.lootcrates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LootCrate {

    private Location location;
    private long timer;
    private Hologram hologram;
    private final List<TextLine> textLines = new ArrayList<>();
    private boolean looted = false;

    public LootCrate(Location location) {
        this.location = location;
        this.timer = 60;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTimer() {
        return this.timer;
    }

    public void resetTimer() {
        this.timer = Vice.getCrateManager().getCooldown() * 60L;
    }

    public void tick() {
//        if (this.timer > 0) {
//            this.updateHologram("&7Start a Dropship to refill!");
//            this.timer--;
//        } else if (this.timer == 0)
//            this.restock();

        if (this.looted) {
            this.updateHologram("&f&oStart a Dropship to refill!");
        } else {
            this.updateHologram("&6&lRestocked!");
        }

        this.updateVisibility();
    }

    public void restock(Area.DropType type) {
        this.closeOutViewers(); //Close out inventory viewers before restocking.

        BlockState state = this.location.getBlock().getState();
        if (!(state instanceof Chest)) {
            Vice.log("Loot Chest at location " + Utils.blockLocationToString(this.location) + " is not a Chest!");
            this.updateHologram("&c&lERROR: Please contact an admin!");
//            this.timer = -1;
            return;
        }

        Chest chest = (Chest) state;
//        try {
//            Class<?> craftChestClass = ReflectionUtil.getOBCClass("block.CraftChest");
//            Object craftChest = craftChestClass.cast(chest);
//            Method getTileEntity = craftChestClass.getMethod("getTileEntity");
//            Object tileEntity = getTileEntity.invoke(craftChest);
//            Class<?> tileEntityClass = ReflectionUtil.getNMSClass("TileEntityChest");
//            Method setTitle = tileEntityClass.getMethod("a", String.class);
//            setTitle.invoke(tileEntity, Utils.f("&e&lLoot Crate"));
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
        chest.setCustomName(Utils.f("&e&lLoot Crate"));
        chest.update();

        Inventory inv = chest.getBlockInventory();
        inv.clear();

        List<LootItem> items = Vice.getCrateManager().dropItems.get(type).stream().filter(item -> Utils.calculateChance(item.getChance())).collect(Collectors.toList());
        items.forEach(item -> {
            ItemStack stack = item.getGameItem().getItem();
            stack.setAmount(Utils.randomNumber(item.getMin(), item.getMax()));
            Utils.putItemInInventoryRandomly(inv, stack);
        });

        this.setLooted(false);
//        this.updateHologram("&7Restocked!");
//        this.timer = -1;
    }

    private void updateHologram(String text) {
        if (this.hologram == null) {
            this.hologram = HologramsAPI.createHologram(Vice.getInstance(), this.location.clone().add(0.5, 2, 0.5));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&e&lDropship Crate")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f(text)));
            this.hologram.getVisibilityManager().setVisibleByDefault(false);
        } else
            this.textLines.get(1).setText(Utils.f(text));

    }

    private void updateVisibility() {
        VisibilityManager v = this.hologram.getVisibilityManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(player.getWorld(), this.location.getWorld())
                    && player.getLocation().distanceSquared(this.location) < 225) {
                if (!v.isVisibleTo(player))
                    v.showTo(player);
            } else if (v.isVisibleTo(player))
                v.hideTo(player);
        }
    }

    public void removeHologram() {
        this.hologram.delete();
        this.hologram = null;
        this.textLines.clear();

    }

    private void closeOutViewers() {
        if (this.location == null) return;
        Block block = this.location.getBlock();
        if (block.getType() != Material.CHEST) return;
        Chest chest = (Chest) block.getState();
        chest.getInventory().getViewers().forEach(HumanEntity::closeInventory);
    }

    public void setLooted(boolean looted) {
        this.looted = looted;
    }

    public boolean isLooted() {
        return looted;
    }
}
