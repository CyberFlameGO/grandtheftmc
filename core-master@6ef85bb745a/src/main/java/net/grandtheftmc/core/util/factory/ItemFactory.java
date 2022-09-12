package net.grandtheftmc.core.util.factory;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public class ItemFactory extends Factory<ItemStack> implements CloneableFactory<ItemFactory> {

    private ItemMeta itemMeta;

    public ItemFactory(Material material, byte data) {
        this.object = new ItemStack(material, 1, data);
        this.itemMeta = object.getItemMeta();
    }

    public ItemFactory(Material material, short data) {
        this.object = new ItemStack(material, 1);
        this.object.setDurability(data);
        this.itemMeta = object.getItemMeta();
    }

    public ItemFactory(Material material) {
        this(material, (byte) 0);
    }

    public ItemFactory(ItemStack itemStack) {
        this.object = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public final ItemFactory setAmount(int amount) {
        if (amount > 64) amount = 64;
        object.setAmount(amount);
        return this;
    }

    public final ItemFactory setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public final ItemFactory setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public final ItemFactory setLore(Queue<String> lore) {
        itemMeta.setLore((LinkedList<String>) lore);
        return this;
    }

    public final ItemFactory setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public final ItemFactory setOwner(String name) {
        if (object.getType().equals(Material.SKULL_ITEM)) {
            SkullMeta meta = (SkullMeta) itemMeta;
            meta.setOwner(name);
        }
        return this;
    }

    public final ItemFactory setDurability(short durability) {
        object.setDurability(durability);
        return this;
    }

    public final ItemStack build() {
        object.setItemMeta(itemMeta);
        return object;
    }

    public final ItemFactory setData(byte data) {
        object.setDurability(data);
        return this;
    }

    public final ItemFactory setUnsafeEnchantment(Enchantment enchantment, int level) {
        object.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public final ItemFactory setEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public final ItemFactory addFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

//    public <T> ItemFactory setNBT(String key, T value) {
//        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(object);
//        NBTTagCompound compoundTag = new NBTTagCompound();
//        nmsCopy.c(compoundTag);
//        compoundTag.set(key, );
//        nmsCopy.f(compoundTag);
//    }

    public ItemFactory setUnbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    @Override
    public final ItemFactory clone() {
        ItemFactory clone = new ItemFactory(object.getType(), object.getData().getData());
        clone.itemMeta = this.itemMeta;
        return clone;
    }
}
