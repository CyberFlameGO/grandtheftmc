package net.grandtheftmc.gtm.items;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Attribute;
import net.grandtheftmc.core.util.AttributeModifier;
import net.grandtheftmc.core.util.ItemAttributes;
import net.grandtheftmc.gtm.users.GTMRank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;


/**
 * Created by Liam on 25/10/2016.
 */
public enum ArmorUpgrade {

    LIGHT(2000, GTMRank.THUG, UserRank.VIP, ArmorType.CHESTPLATE),
    DURABLE(2000, GTMRank.GANGSTER, UserRank.VIP, ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS),
    ULTRA_LIGHT(5000, GTMRank.MUGGER, UserRank.PREMIUM, ArmorType.CHESTPLATE),
    TANK(3000, GTMRank.HUNTER, UserRank.PREMIUM, ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS),
    REINFORCED(2500, GTMRank.DEALER, UserRank.ELITE, ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.JETPACK, ArmorType.WINGSUIT),
    BOMB_SQUAD(3000, GTMRank.PIMP, UserRank.ELITE, ArmorType.HELMET, ArmorType.CHESTPLATE),
    EXOSKELETON(5000, GTMRank.MOBSTER, UserRank.SPONSOR, ArmorType.CHESTPLATE),
    ENHANCED(50000, GTMRank.GODFATHER, UserRank.SUPREME, ArmorType.CHESTPLATE),
    NEON(5000, null, UserRank.VIP, ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS, ArmorType.JETPACK, ArmorType.WINGSUIT),
    LEAD_LINED(5000, null, null, ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS, ArmorType.JETPACK, ArmorType.WINGSUIT);


    private final double price;
    private final GTMRank rank;
    private final UserRank userRank;
    private final ArmorType[] types;

    ArmorUpgrade(double price, GTMRank rank, UserRank userRank, ArmorType... types) {
        this.price = price;
        this.rank = rank;
        this.userRank = userRank;
        this.types = types;
    }

    public double getPrice() {
        return this.price;
    }

  /*  public double getPrice(GameItem item) {
        Core.log(item.getSellPrice() + " / " + this.price);
        return this.price * item.getSellPrice();
    }*/

    public GTMRank getGTMRank() {
        return this.rank;
    }

    public UserRank getUserRank() {
        return this.userRank;
    }

    public boolean canUseUpgrade(GTMRank rank, UserRank userRank) {
        return this.rank == rank || rank.isHigherThan(this.rank) || this.userRank == userRank || userRank.isHigherThan(this.userRank) || (userRank==null && rank==null);
    }

    public String getDisplayName() {
        String[] a = this.toString().split("_");
        String s = "";
        for (int i = 0; i < a.length; ++i) {
            s = s + a[i].charAt(0) + a[i].substring(1).toLowerCase() + (i == a.length - 1 ? "" : " ");
        }
        return s;
    }

    public ArmorType[] getTypes() {
        return this.types;
    }

    public String getTypesString() {
        String s = "";
        int length = this.types.length;
        for (int i = 0; i < length; i++) {
            ArmorType type = this.types[i];
            s += type.getName() + (i == length - 1 ? "" : i == length - 2 ? " or " : ", ");
        }
        return s;
    }

    public static boolean isArmorUpgrade(String s) {
        try {
            ArmorUpgrade.valueOf(s);
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean playerHasArmorUpgrade(Player victim, ArmorUpgrade upgrade){
        ItemStack[] armor = victim.getInventory().getArmorContents();
        for (ItemStack itemStack : armor) {
            if(itemStack==null)
                continue;
            HashSet<ArmorUpgrade> upgrades = ArmorUpgrade.getArmorUpgrades(itemStack);
            if (upgrades.stream().anyMatch(up -> up == upgrade)) {
                return true;
            }
        }
        return false;
    }

    //Does not return null when no upgrades, returns an empty set.
    public static HashSet<ArmorUpgrade> getArmorUpgrades(ItemStack is) {
        HashSet<ArmorUpgrade> returnSet = new HashSet<>();
        if (is==null || is.getItemMeta() == null || is.getItemMeta().getLore() == null)
            return returnSet;
        List<String> lore = is.getItemMeta().getLore();
        lore.stream().forEach(line -> {
            line = ChatColor.stripColor(line);
            Core.log(line);
            if (line.equalsIgnoreCase("") || !ArmorUpgrade.isArmorUpgrade(line.toUpperCase().replace(" ", "_")))
                return;
            returnSet.add(ArmorUpgrade.valueOf(line.toUpperCase().replace(" ", "_")));
        });

        return returnSet;
    }

    public boolean canBeUsedOn(ArmorType type) {
        return Arrays.stream(this.types).anyMatch(t -> t == type);
    }

    public boolean canBeUsedOn(String gameItem) {
        return Arrays.stream(this.types).anyMatch(t -> t.hasGameItem(gameItem));
    }

    public HashSet<PotionEffect> getPotionEffects() {
        HashSet<PotionEffect> effects = new HashSet<>();
        switch (this) {
            case ULTRA_LIGHT:
                effects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
                break;
            case EXOSKELETON:
                effects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
                effects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                break;
            case NEON:
                effects.add(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
        }

        return effects;
    }

    public ItemStack getUpgradedItem(GameItem gameItem, ItemStack item) {
        if (gameItem == null) return item;
        ArmorType type = ArmorType.getArmorType(gameItem.getName());
        if (type == null) return item;
        ItemAttributes att = new ItemAttributes();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.hasLore() ? meta.getLore() : Collections.singletonList(""));
        if (ArmorUpgrade.getArmorUpgrades(item).size() == 0) {
            lore.add(Utils.f("&7Upgrades:"));
            lore.add(" ");
        }
        lore.add(Utils.f("&b&l" + this.getDisplayName()));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch (this) {
            case LIGHT:
                att.getFromStack(item);
                att.addModifier(new AttributeModifier(Attribute.MOVEMENT_SPEED, "LightSpeed", type.getSlot(), 0, 0.02, UUID.randomUUID()));
                item = att.apply(item);
                meta.addEnchant(Enchantment.DEPTH_STRIDER, 1, true);
                break;
            case ULTRA_LIGHT:
                att.getFromStack(item);
                att.addModifier(new AttributeModifier(Attribute.MOVEMENT_SPEED, "UltraLightSpeed", type.getSlot(), 0, 0.04, UUID.randomUUID()));
                item = att.apply(item);
                meta.addEnchant(Enchantment.DEPTH_STRIDER, meta.getEnchantLevel(Enchantment.DEPTH_STRIDER) + 2, true);
                meta.addEnchant(Enchantment.DIG_SPEED, meta.getEnchantLevel(Enchantment.DIG_SPEED) + 1, true);
                break;
            case TANK: {
                att.getFromStack(item);
                att.addModifier(new AttributeModifier(Attribute.MOVEMENT_SPEED, "TankSpeed", type.getSlot(), 0, -0.04, UUID.randomUUID()));
                att.addModifier(new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE, "TankKnockback", type.getSlot(), 0, .5d, UUID.randomUUID()));
                item = att.apply(item);
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 9, true);
                break;
            }
            case REINFORCED: {
                att.getFromStack(item);
                item = att.apply(item);
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                break;
            }
            case DURABLE:
                att.getFromStack(item);
                item = att.apply(item);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.spigot().setUnbreakable(true);
                break;
            case BOMB_SQUAD:
                att.getFromStack(item);
                att.addModifier(new AttributeModifier(Attribute.MOVEMENT_SPEED, "BombSquadSpeed", type.getSlot(), 0, -0.02, UUID.randomUUID()));
                att.addModifier(new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE, "BombSquadKnockback", type.getSlot(), 0, 0.5, UUID.randomUUID()));
                item = att.apply(item);
                meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 5, true);
                meta.addEnchant(Enchantment.PROTECTION_FIRE, 5, true);
                break;
        }
        item.setItemMeta(meta);
        att.removeModifier(new AttributeModifier(Attribute.ARMOR));
        att.removeModifier(new AttributeModifier(Attribute.ARMOR_THOUGHNESS));
        att.addModifier(new AttributeModifier(Attribute.ARMOR, "darmor", type.getSlot(), 0, getDefaultArmorAttribute(item.getType()), UUID.randomUUID()));
        att.addModifier(new AttributeModifier(Attribute.ARMOR_THOUGHNESS, "darmorthoughness", type.getSlot(), 0, getDefaultArmorToughness(item.getType()), UUID.randomUUID()));
        item = att.apply(item);
        //Utils.b(this.enchantment == null ? "null" : this.enchantment.getName());
        // item.addUnsafeEnchantment(this.enchantment, 1);
        // for (Enchantment e : item.getEnchantments().keySet())
        //     Utils.b(e.getName());
        return item;
    }

    public static ArmorUpgrade getArmorUpgrade(String s) {
        return Arrays.stream(ArmorUpgrade.values()).filter(u -> u.toString().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public static ArmorUpgrade getArmorUpgradeFromDisplayName(String s) {
        return Arrays.stream(ArmorUpgrade.values()).filter(u -> u.toString().replace("_", " ").equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    private static int getDefaultArmorAttribute(Material material){
        switch (material){
            case IRON_BOOTS:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
                return 2;
            case GOLD_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_LEGGINGS:
                return 5;
            case GOLD_LEGGINGS:
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
            case LEATHER_CHESTPLATE:
                return 3;
            case CHAINMAIL_LEGGINGS:
                return 4;
            case LEATHER_BOOTS:
            case LEATHER_HELMET:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case GOLD_BOOTS:
                return 1;
            case IRON_CHESTPLATE:
            case DIAMOND_LEGGINGS:
                return 6;
            case DIAMOND_CHESTPLATE:
                return 8;
        }
        return 0;
    }

    private static int getDefaultArmorToughness(Material material){
        switch (material){
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
                return 2;
        }
        return 0;
    }

}
