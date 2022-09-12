package com.j0ach1mmall3.wastedguns.api.attachments;

import com.j0ach1mmall3.jlib.inventory.CustomEnchantment;
import com.j0ach1mmall3.wastedguns.Main;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 13/05/2016
 */
public final class Attachment {
    private final Main plugin;
    private final String identifier;
    private final double meleeDamageFactor;
    private final double fireDelayFactor;
    private final double walkSpeedFactor;
    private final Sound useSound;
    private final double damageFactor;
    private final double accuracyFactor;
    private final double recoilFactor;
    private final double zoomFactor;
    private final int magazineAmount;
    private final double reloadTimeFactor;
    private final double rangeFactor;
    private final boolean pumpkinScope;

    private final Enchantment enchantment;

    public Attachment(Main plugin, String identifier, double meleeDamageFactor, double fireDelayFactor, double walkSpeedFactor, Sound useSound, double damageFactor, double accuracyFactor, double recoilFactor, double zoomFactor, int magazineAmount, double reloadTimeFactor, double rangeFactor, boolean pumpkinScope) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.meleeDamageFactor = meleeDamageFactor;
        this.fireDelayFactor = fireDelayFactor;
        this.walkSpeedFactor = walkSpeedFactor;
        this.useSound = useSound;
        this.damageFactor = damageFactor;
        this.accuracyFactor = accuracyFactor;
        this.recoilFactor = recoilFactor;
        this.zoomFactor = zoomFactor;
        this.magazineAmount = magazineAmount;
        this.reloadTimeFactor = reloadTimeFactor;
        this.rangeFactor = rangeFactor;
        this.pumpkinScope = pumpkinScope;

        CustomEnchantment ce = new CustomEnchantment(this.identifier, new ArrayList<>(), EnchantmentTarget.ALL, 1, 1);
        ce.register();
        this.enchantment = ce.getEnchantment();
    }

//    public String getIdentifier() {
//        return this.identifier;
//    }
//
//    public double getMeleeDamageFactor() {
//        return this.meleeDamageFactor;
//    }
//
//    public double getFireDelayFactor() {
//        return this.fireDelayFactor;
//    }
//
//    public double getWalkSpeedFactor() {
//        return this.walkSpeedFactor;
//    }
//
//    public Sound getUseSound() {
//        return this.useSound;
//    }
//
//    public double getDamageFactor() {
//        return this.damageFactor;
//    }
//
//    public double getAccuracyFactor() {
//        return this.accuracyFactor;
//    }
//
//    public double getRecoilFactor() {
//        return this.recoilFactor;
//    }
//
//    public double getZoomFactor() {
//        return this.zoomFactor;
//    }
//
//    public int getMagazineAmount() {
//        return this.magazineAmount;
//    }
//
//    public double getReloadTimeFactor() {
//        return this.reloadTimeFactor;
//    }
//
//    public double getRangeFactor() {
//        return this.rangeFactor;
//    }
//
//    public boolean isPumpkinScope() {
//        return this.pumpkinScope;
//    }
//
//    public Enchantment getEnchantment() {
//        return this.enchantment;
//    }
//
//    public ItemStack add(ItemStack itemStack) {
//        Set<Attachment> attachments = getAttachments(this.plugin, itemStack);
//        attachments.add(this);
//        return setAttachments(itemStack, attachments);
//    }
//
//    public ItemStack remove(ItemStack itemStack) {
//        Set<Attachment> attachments = getAttachments(this.plugin, itemStack);
//        attachments.remove(this);
//        return setAttachments(itemStack, attachments);
//    }
//
//    public boolean has(ItemStack itemStack) {
//        return getAttachments(this.plugin, itemStack).contains(this);
//    }
//
//    public void apply(Weapon weapon) {
//        weapon.setMeleeDamage(weapon.getMeleeDamage() * this.meleeDamageFactor);
//        weapon.setFireDelay((long) (weapon.getFireDelay() * this.fireDelayFactor));
//        weapon.setWalkSpeed((float) (weapon.getWalkSpeed() * this.walkSpeedFactor));
//        if(this.useSound != null) weapon.setUseSound(this.useSound);
//
//        if(weapon instanceof RangedWeapon) {
//            RangedWeapon rangedWeapon = (RangedWeapon) weapon;
//            rangedWeapon.setDamage(rangedWeapon.getDamage() * this.damageFactor);
//            rangedWeapon.setAccuracy(rangedWeapon.getAccuracy() * this.accuracyFactor);
//            rangedWeapon.setRecoil(rangedWeapon.getRecoil() * this.recoilFactor);
//            rangedWeapon.setZoom((int) (rangedWeapon.getZoom() * this.zoomFactor));
//            rangedWeapon.setMagazine(rangedWeapon.getMagazine() + this.magazineAmount);
//            rangedWeapon.setReloadTime((int) (rangedWeapon.getReloadTime() * this.reloadTimeFactor));
//            rangedWeapon.setRange((int) (rangedWeapon.getRange() * this.rangeFactor));
//            rangedWeapon.setPumpkinScope(this.pumpkinScope);
//        }
//
//        if(weapon instanceof EnergyWeapon) {
//            EnergyWeapon energyWeapon = (EnergyWeapon) weapon;
//            energyWeapon.setDamage(energyWeapon.getDamage() * this.damageFactor);
//            energyWeapon.setRange(energyWeapon.getRange() * this.rangeFactor);
//        }
//    }
//
//    public static Set<Attachment> getAttachments(Main plugin, ItemStack itemStack) {
//        Set<Attachment> attachments = new HashSet<>();
//        itemStack.getEnchantments().keySet().stream().filter(e -> plugin.getAttachment(e.getName()).isPresent()).forEach(e -> attachments.add(plugin.getAttachment(e.getName()).get()));
//        return attachments;
//    }
//
//    public static ItemStack setAttachments(ItemStack itemStack, Set<Attachment> attachments) {
//        ItemStack is = itemStack.clone();
//        is.getEnchantments().forEach((e, i) -> is.removeEnchantment(e));
//        attachments.forEach(a -> is.addUnsafeEnchantment(a.enchantment, 1));
//        return is;
//    }
}
