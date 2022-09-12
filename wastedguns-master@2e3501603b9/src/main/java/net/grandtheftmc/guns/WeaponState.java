package net.grandtheftmc.guns;

/**
 * Created by Luke Bingham on 24/07/2017.
 */
public enum WeaponState {

    /**
     * This state will be used when the player is reloading their Weapon.
     */
    RELOADING,

    /**
     * This state will be used when the player is shooting their Weapon.
     */
    SHOOTING,
    
    /**
     * This state will be used when the player is bursting with their Weapon.
     */
    BURSTING,

    /**
     * This state will be used when the weapon is not doing anything.
     */
    IDLE,

    /**
     * This state will be used when the Weapon is no longer being used.
     */
    NONE,

    ;
}
