package net.grandtheftmc.gtm.utils;

import net.grandtheftmc.core.util.ItemStackManager;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

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

    public static ItemStack addTag(ItemStack itemStack, String key, int value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        if (compound == null) compound = new NBTTagCompound();

        compound.set(key, new NBTTagInt(value));
        nmsItem.setTag(compound);
        nmsItem.save(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static ItemStack addTag(ItemStack itemStack, String key, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        if (compound == null) compound = new NBTTagCompound();

        compound.set(key, new NBTTagString(value));
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

    public static Object getTag(ItemStack itemStack, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItem.getTag();
        return tag.get(key).toString();
    }
}
