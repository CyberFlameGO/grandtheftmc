package net.grandtheftmc.core.util.nbt;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ThatAbstractWolf on 2017-08-04.
 */
public class NBTUtil1_12_2 implements CoreNbt {

	private ItemStack item;

	public NBTUtil1_12_2(ItemStack item) {
		this.item = item;
	}

	public ItemStack setNBTTag(String key, NBTBase base) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(this.item);

		NBTTagCompound compound;

		if (item.getTag() == null) {
			compound = new NBTTagCompound();
		} else {
			compound = item.getTag();
		}

		compound.set(key, base);
		item.setTag(compound);

		return CraftItemStack.asBukkitCopy(item);
	}

	@Override
	public Object getNBTTag(String key) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(this.item);

		NBTTagCompound compound = item.getTag();

		if (compound == null) {
			return false;
		}

		return compound.get(key);
	}

	@Override
	public boolean hasNBTTag(String key) {

		net.minecraft.server.v1_12_R1.ItemStack item = CraftItemStack.asNMSCopy(this.item);

		NBTTagCompound compound = item.getTag();

		if (compound == null) {
			return false;
		}

		return compound.get(key) != null;
	}
}
