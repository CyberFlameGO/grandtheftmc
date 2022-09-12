package net.grandtheftmc.guns.weapon;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public enum AmmoType {
    NONE("NONE"),
    MELEE("MELEE"),

    //RANGED
    PISTOL("PISTOL"),
    SMG("SMG"),
    SHOTGUN("SHOTGUN"),
    LMG("MG"),
    SNIPER("SNIPER"),
    LAUNCHER("ROCKET"),
    ASSAULT_RIFLE("ASSAULT_RIFLE"),
    ROCKET("ROCKET"),

    MINIGUN("MINIGUN"),

    //EXTRA
    EXPLOSIVE("EXPLOSIVE"),
    ENERGY("ENERGY"),
    GRENADE("GRENADE"),

    FUEL("FUEL"),
    ;

    private String type;

    AmmoType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
