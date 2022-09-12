package net.grandtheftmc.core.util;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class AttributeModifier
{
    private String attribute;
    private String name;
    private String slot;
    private int operation;
    private double amount;
    private UUID uuid;

    public AttributeModifier(Attribute attribute, String name, Slot slot, int operation, double amount, UUID uuid)
    {
        this.attribute = attribute.getName();
        this.name = name;
        this.slot = slot.getName();
        this.operation = operation;
        this.amount = amount;
        this.uuid = uuid;
    }

    @Deprecated
    public AttributeModifier(String attribute, String name, String slot, int operation, double amount, UUID uuid)
    {
        this.attribute = attribute;
        this.name = name;
        this.slot = slot;
        this.operation = operation;
        this.amount = amount;
        this.uuid = uuid;
    }

    public AttributeModifier(Object modifier)
    {
        try
        {
            this.attribute = (String)modifier.getClass().getMethod("getString", new Class[] { String.class }).invoke(modifier, "AttributeName");
            this.name = (String)modifier.getClass().getMethod("getString", new Class[] { String.class }).invoke(modifier, "Name");
            this.slot = (String)modifier.getClass().getMethod("getString", new Class[] { String.class }).invoke(modifier, "Slot");
            this.operation = (Integer) modifier.getClass().getMethod("getInt", new Class[]{String.class}).invoke(modifier, "Operation");
            this.amount = (Double) modifier.getClass().getMethod("getDouble", new Class[]{String.class}).invoke(modifier, "Amount");
            this.uuid = new UUID((Long) modifier.getClass().getMethod("getLong", new Class[]{String.class}).invoke(modifier, "UUIDMost"), (Long) modifier.getClass().getMethod("getLong", new Class[]{String.class}).invoke(modifier, "UUIDLeast"));
        }
        catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e)
        {
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
        }
    }

    public Object getNBT()
    {
        try
        {
            Object data = ReflectionUtils.getNMSClass("NBTTagCompound").newInstance();
            if (data != null)
            {
                data.getClass().getMethod("setString", new Class[] { String.class, String.class }).invoke(data, "AttributeName", this.attribute);
                data.getClass().getMethod("setString", new Class[] { String.class, String.class }).invoke(data, "Name", this.name);
                data.getClass().getMethod("setString", new Class[] { String.class, String.class }).invoke(data, "Slot", this.slot);
                data.getClass().getMethod("setInt", new Class[] { String.class, Integer.TYPE }).invoke(data, "Operation", this.operation);
                data.getClass().getMethod("setDouble", new Class[] { String.class, Double.TYPE }).invoke(data, "Amount", this.amount);
                data.getClass().getMethod("setLong", new Class[] { String.class, Long.TYPE }).invoke(data, "UUIDMost", this.uuid.getMostSignificantBits());
                data.getClass().getMethod("setLong", new Class[] { String.class, Long.TYPE }).invoke(data, "UUIDLeast", this.uuid.getLeastSignificantBits());
                return data;
            }
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible Server version! Missing classes.");
            return null;
        }
        catch (InstantiationException|IllegalAccessException|NoSuchMethodException|SecurityException|IllegalArgumentException|InvocationTargetException e)
        {
            Bukkit.getLogger().info("[ItemAttributeAPI] Incompatible server version! Some methods can't be applied.");
        }
        return null;
    }
}
