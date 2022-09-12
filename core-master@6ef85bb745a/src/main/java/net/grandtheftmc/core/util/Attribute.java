package net.grandtheftmc.core.util;

/**
 * Created by Liam on 27/10/2016.
 */
public enum Attribute {
    MAX_HEALTH("generic.maxHealth"),
    FOLLOW_RANGE("generic.followRange"),
    KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
    MOVEMENT_SPEED("generic.movementSpeed"),
    ATTACK_DAMAGE("generic.attackDamage"), ARMOR("generic.armor"),
    ARMOR_THOUGHNESS("generic.armorToughness"),
    ATTACK_SPEED("generic.attackSpeed"),
    LUCK("generic.luck"),
    JUMP_STRENGTH("horse.jumpStrength"),
    SPAWN_REINFORCEMENTS("zombie.spawnReinforcements");

    private final String name;

    Attribute(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
