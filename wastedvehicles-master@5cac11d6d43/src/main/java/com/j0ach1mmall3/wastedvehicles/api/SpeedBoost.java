package com.j0ach1mmall3.wastedvehicles.api;

import org.bukkit.inventory.ItemStack;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 15/05/2016
 */
public final class SpeedBoost {
    private final ItemStack itemStack;
    private final double speedBoost;
    private final int duration;

    public SpeedBoost(ItemStack itemStack, double speedBoost, int duration) {
        this.itemStack = itemStack;
        this.speedBoost = speedBoost;
        this.duration = duration;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public double getSpeedBoost() {
        return this.speedBoost;
    }

    public int getDuration() {
        return this.duration;
    }
}
