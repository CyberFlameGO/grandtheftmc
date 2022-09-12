package net.grandtheftmc.vice.machine;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.vice.machine.data.MachineData;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.recipe.misc.MachineRecipeData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public abstract class BaseMachine {

    private final HashMap<MachineDataType, MachineData> machineData;

    private int[] openSlots;
    private int[] outputSlots;
    private int[] blockedSlots;

    // Main data
    private int uniqueIdentifier = -1;
    private final int machineIdentifier;
    private Location location = null;
    private boolean enabled = false;

    private final String name;
    private final Material material;
    private final Inventory inventory;
    private ItemStack machineItem;
    private ItemStack[] contents;

    private MachineRecipeData recipeData = null;

    /**
     * Construct a Machine
     */
    public BaseMachine(int machineIdentifier, String name, Material material) {
        this(machineIdentifier, name, material, 3);
    }

    /**
     * Construct a Machine
     */
    public BaseMachine(int machineIdentifier, String name, Material material, int inventorySize) {
        this.machineIdentifier = machineIdentifier;
        this.name = name;
        this.material = material;

        this.machineData = Maps.newHashMap();

        this.inventory = Bukkit.createInventory(null, inventorySize * 9, Utils.f("&e" + name));
    }

    public int getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(int uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public int getMachineIdentifier() {
        return machineIdentifier;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ItemStack getMachineItem() {
        return machineItem;
    }

    public void setMachineItem(ItemStack machineItem) {
        this.machineItem = machineItem;
    }

    public ItemStack[] getContents() {
        this.contents = this.inventory.getContents();
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
        this.inventory.setContents(contents);
    }

    public int[] getOpenSlots() {
        return openSlots;
    }

    public void setOpenSlots(int... openSlots) {
        this.openSlots = openSlots;
    }

    public void setOutputSlots(int... outputSlots) {
        this.outputSlots = outputSlots;
    }

    public int[] getOutputSlots() {
        return outputSlots;
    }

    public boolean isOutputSlot(int slot) {
        for (int i : this.outputSlots)
            if (i == slot) return true;
        return false;
    }

    public int[] getBlockedSlots() {
        return blockedSlots;
    }

    public void setBlockedSlots(int... blockedSlots) {
        this.blockedSlots = blockedSlots;
    }

    public boolean isBlockedSlot(int slot) {
        for (int i : this.blockedSlots)
            if (i == slot) return true;
        return false;
    }

    public void setData(MachineDataType type, MachineData data) {
        this.machineData.put(type, data);
    }

    public MachineData getData(MachineDataType type) {
        return this.machineData.getOrDefault(type, null);
    }

    public void updateFuel() {
        MachineData data = this.getData(MachineDataType.FUEL);
        if (data == null) return;
        if(this.inventory.getItem(19)==null) {
            Core.error("there is a broken inventory at " + this.location);
            return;
        }
        this.inventory.getItem(19).setDurability((short) data.getTexture());
//        ServerUtil.debug("FUEL: " + this.fuel + " - " + this.getFuelByte());
    }

    public void updateDurability() {
        MachineData data = this.getData(MachineDataType.DURABILITY);
        if (data == null) return;

        this.inventory.getItem(25).setDurability((short) data.getTexture());
//        ServerUtil.debug("DURABILITY: " + this.durability + " - " + this.getDurabilityByte());
    }

    public void updateProgress() {
        MachineData data = this.getData(MachineDataType.PROGRESS);
        if (data == null) return;

        if(this.inventory.getItem(11)==null) {
            Core.error("there is a broken inventory at " + this.location);
            return;
        }

        this.inventory.getItem(11).setDurability((short) data.getTexture());
//        ServerUtil.debug("PROGRESS: " + this.progress + " - " + this.getProgressByte());
    }

    public void updateAllItems() {
        updateFuel();
        updateDurability();
        updateProgress();
    }

    public MachineRecipeData getRecipeData() {
        return recipeData;
    }

    public void setRecipeData(MachineRecipeData recipeData) {
        this.recipeData = recipeData;
    }

    public boolean isRecipeActive() {
        return this.recipeData != null;
    }

    public String i(String icon, int amount) {
        return C.RESET + icon + C.GRAY + "x" + amount + C.RESET;
    }
}
