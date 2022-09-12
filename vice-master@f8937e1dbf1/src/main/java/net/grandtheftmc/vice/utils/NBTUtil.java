package net.grandtheftmc.vice.utils;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ThatAbstractWolf on 2017-08-02.
 */
public class NBTUtil {

	public static ItemStack setNBTTag(ItemStack itemStack, String key, NBTBase value) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound;

		if (item.getTag() == null) {
			compound = new NBTTagCompound();
		} else {
			compound = item.getTag();
		}

		compound.set(key, value);
		item.setTag(compound);

		return CraftItemStack.asBukkitCopy(item);
	}

	public static Object getNBTTag(ItemStack itemStack, String key) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

		NBTTagCompound compound = item.getTag();

		if(compound == null) {
			return false;
		}

		return compound.get(key);
	}

	public static boolean hasNBTTag(ItemStack itemStack, String key) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

		NBTTagCompound compound = item.getTag();

		if(compound == null) {
			return false;
		}

		return compound.get(key) != null;
	}
}
