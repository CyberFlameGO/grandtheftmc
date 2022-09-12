package net.grandtheftmc.vice.utils;

import net.grandtheftmc.core.util.ItemStackManager;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Created by Luke Bingham on 06/08/2017.
 */
public class ItemStackUtil {

    public static ItemStack makeStackable(org.bukkit.inventory.ItemStack itemStack, int stacksize) {
        if(itemStack == null || itemStack.getType() == Material.AIR) return itemStack;

        ItemStackManager.STACKABLES.put(itemStack.getType(), stacksize);

        net.minecraft.server.v1_12_R1.ItemStack stack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack);
        stack.getItem().d(stacksize);
        itemStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asBukkitCopy(stack);

        return itemStack;
    }

    public static ItemStack removeStackable(org.bukkit.inventory.ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() == Material.AIR) return itemStack;

        net.minecraft.server.v1_12_R1.ItemStack stack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack);
        stack.getItem().d(1);
        itemStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asBukkitCopy(stack);

        return itemStack;
    }

    /**
     * Creates and returns an ItemStack object with the given Material/Name
     * @param material
     * @param name
     * @return
     */
    public static ItemStack createWithName(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Creates and returns an ItemStack object with the given Material/Name/Lore
     * @param material
     * @param name
     * @param lore
     * @return
     */
    public static ItemStack createWithNameAndLore(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack addTag(ItemStack itemStack, String key, int value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        if (compound == null) compound = new NBTTagCompound();

        compound.set(key, new NBTTagInt(value));
        nmsItem.setTag(compound);
        nmsItem.save(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static boolean hasTag(ItemStack itemStack, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        return nmsItem.hasTag() && nmsItem.getTag() != null && nmsItem.getTag().hasKey(key);
    }

    private static boolean hasTag(net.minecraft.server.v1_12_R1.ItemStack nmsItem, String key) {
        return nmsItem.hasTag() && nmsItem.getTag() != null && nmsItem.getTag().hasKey(key);
    }

    public static int getIntTag(ItemStack itemStack, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItem.getTag();
        return Integer.parseInt(tag.get(key).toString());
    }
}
