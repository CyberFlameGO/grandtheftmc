package com.j0ach1mmall3.wastedcops.api;

import java.util.Collections;
import java.util.List;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 4/06/2016
 */
public final class CopProperties {
    private final String identifier;
    private final String entity;
    private final double health;
    private final int weaponDropChance;
    private final String name;
    private final int killReward;
    private final List<String> weapons;

    public CopProperties(String identifier, String entity, double health, int weaponDropChance, String name, int killReward, List<String> weapons) {
        this.identifier = identifier;
        this.entity = entity;
        this.health = health;
        this.weaponDropChance = weaponDropChance;
        this.name = name;
        this.killReward = killReward;
        this.weapons = weapons;
        Collections.shuffle(this.weapons);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getEntity() {
        return this.entity;
    }

    public double getHealth() {
        return this.health;
    }

    public int getWeaponDropChance() {
        return this.weaponDropChance;
    }

    public String getName() {
        return this.name;
    }

    public int getKillReward() {
        return this.killReward;
    }

    public List<String> getWeapons() {
        return this.weapons;
    }
}
