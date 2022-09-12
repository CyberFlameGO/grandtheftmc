package net.grandtheftmc.guns;

import org.bukkit.plugin.java.JavaPlugin;

import de.slikey.effectlib.EffectManager;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public final class GTMGuns extends JavaPlugin {

    public static final boolean DEBUG = false;
    /** If using the star system */
    public static boolean STAR_SYSTEM = true;
    /** If using the kill count system */
    public static boolean KILL_COUNT_SYSTEM = true;
    /** If using the star system, max number of stars */
    public static int MAX_STARS = 3;

    private static GTMGuns instance;
    private WeaponManager weaponManager;
    private EffectManager effectManager;

    @Override
    public final void onEnable() {
        instance = this;
        this.effectManager = new EffectManager(this);
        this.weaponManager = new WeaponManager();
        WeaponHandler weaponHandler = new WeaponHandler(this, this.weaponManager);
        //new WeaponsListener(this);
    }

    @Override
    public final void onDisable() {
        instance = null;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public static GTMGuns getInstance() {
        return instance;
    }

    public EffectManager getEffectManager() {
        return this.effectManager;
    }
}
