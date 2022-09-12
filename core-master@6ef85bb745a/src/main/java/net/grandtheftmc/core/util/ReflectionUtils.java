package net.grandtheftmc.core.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public final class ReflectionUtils
{
    private ReflectionUtils() {
    }

    public static Class<?> getNMSClass(String name)
    {
        try
        {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + '.' + name);
        }
        catch (ClassNotFoundException e)
        {
            Bukkit.getLogger().info("[Reflection] Can't find NMS Class! (net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + '.' + name + ')');
        }
        return null;
    }

    public static Class<?> getCBClass(String name)
    {
        try
        {
            return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + '.' + name);
        }
        catch (ClassNotFoundException e)
        {
            Bukkit.getLogger().info("[Reflection] Can't find CB Class! (org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + '.' + name + ')');
        }
        return null;
    }

    public static <K, V> K setField(K obj, String field, V value) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            if(!f.isAccessible()) f.setAccessible(true);
            f.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T, E> E getField(T obj, String field, Class<E> clazz) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            if(!f.isAccessible()) f.setAccessible(true);
            E e = (E) f.get(obj);
            if(clazz.isInstance(e))
                return e;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}

