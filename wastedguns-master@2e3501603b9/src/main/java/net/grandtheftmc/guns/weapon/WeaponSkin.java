package net.grandtheftmc.guns.weapon;

import net.grandtheftmc.core.util.Similarity;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public class WeaponSkin implements Similarity<WeaponSkin> {

    private final WeaponType weaponType;
    private final short identifier;
    private final String displayName;

    public WeaponSkin(WeaponType weaponType, short identifier, String displayName) {
        this.weaponType = weaponType;
        this.identifier = identifier;
        this.displayName = displayName;
    }

    public final WeaponType getWeaponType() {
        return weaponType;
    }

    public final short getIdentifier() {
        return identifier;
    }

    public final String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean isSimilar(WeaponSkin weaponSkin) {
        return this.weaponType == weaponSkin.weaponType && this.identifier == weaponSkin.identifier;
    }
}
