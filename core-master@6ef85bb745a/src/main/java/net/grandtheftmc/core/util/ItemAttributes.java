package net.grandtheftmc.core.util;


import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ItemAttributes {
    private Object modifiers;

    public ItemAttributes() {
        try {
            this.modifiers = ReflectionUtils.getNMSClass("NBTTagList").newInstance();
            if (this.modifiers == null) {
                Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible Server version! Missing classes.");
            }
        } catch (InstantiationException | IllegalAccessException e) {
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
        }
    }

    public void addModifier(AttributeModifier modifier) {
        if (this.modifiers != null) {
            try {
                this.modifiers.getClass().getMethod("add", new Class[]{ReflectionUtils.getNMSClass("NBTBase")}).invoke(this.modifiers, modifier.getNBT());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
            }
        }
    }

    public void removeModifier(int i) {
        if (this.modifiers != null) {
            try {
                try {
                    this.modifiers.getClass().getMethod("a", new Class[]{Integer.TYPE}).invoke(this.modifiers, i);
                } catch (NoSuchMethodException e) {
                    this.modifiers.getClass().getMethod("remove", new Class[]{Integer.TYPE}).invoke(this.modifiers, i);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
            }
        }
    }

    public void removeModifier(AttributeModifier modifier) {
        if (this.modifiers != null) {
            try {
                int size = (Integer) this.modifiers.getClass().getMethod("size", new Class[0]).invoke(this.modifiers);
                for (int i = 0; i < size; i++) {
                    if (this.modifiers.getClass().getMethod("get", new Class[]{Integer.TYPE}).invoke(this.modifiers, new Object[]{Integer.valueOf(i)}).equals(modifier.getNBT())) {
                        try {
                            this.modifiers.getClass().getMethod("a", new Class[]{Integer.TYPE}).invoke(this.modifiers, i);
                        } catch (NoSuchMethodException e) {
                            this.modifiers.getClass().getMethod("remove", new Class[]{Integer.TYPE}).invoke(this.modifiers, i);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
                Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
            }
        }
    }

    public List<AttributeModifier> getModifiers() {
        if (this.modifiers != null) {
            try {
                List<AttributeModifier> modifiers = new ArrayList();
                int size = (Integer) this.modifiers.getClass().getMethod("size", new Class[0]).invoke(this.modifiers);
                for (int i = 0; i < size; i++) {
                    modifiers.add(new AttributeModifier(this.modifiers.getClass().getMethod("get", new Class[]{Integer.TYPE}).invoke(this.modifiers, i)));
                }
                return modifiers;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
                return null;
            }
        }
        return null;
    }

    public ItemStack apply(ItemStack item) {
        try {
            Object itemNMS = ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, item);
            itemNMS.getClass().getMethod("a", new Class[]{String.class, ReflectionUtils.getNMSClass("NBTBase")}).invoke(itemNMS, "AttributeModifiers", this.modifiers);
            return (ItemStack) ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", new Class[]{ReflectionUtils.getNMSClass("ItemStack")}).invoke(null, itemNMS);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
        }
        return null;
    }

    public void getFromStack(ItemStack item) {
        try {
            Object itemNMS = ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, item);
            Object itemNMSTag = itemNMS.getClass().getMethod("getTag", new Class[0]).invoke(itemNMS);
            this.modifiers = itemNMSTag.getClass().getMethod("getList", new Class[]{String.class, Integer.TYPE}).invoke(itemNMSTag, "AttributeModifiers", 10);
            if (this.modifiers == null) {
                this.modifiers = ReflectionUtils.getNMSClass("NBTTagList").newInstance();
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
        }
    }

    public String getVersion() {
        return "1.1,Release";
    }
}
