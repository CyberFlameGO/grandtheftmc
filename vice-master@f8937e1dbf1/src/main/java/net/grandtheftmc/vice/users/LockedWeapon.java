package net.grandtheftmc.vice.users;

import net.grandtheftmc.core.users.UserRank;

import java.util.Arrays;

public enum LockedWeapon {

    // TODO assign to ranks
    MARKSMANPISTOL(ViceRank.FALCON, UserRank.VIP),
    HEAVYSHOTGUN(ViceRank.THUG, UserRank.VIP),
    CHAINSAW(ViceRank.DEALER, UserRank.PREMIUM),
    GUSENBERGSWEEPER(ViceRank.DEALER, UserRank.PREMIUM),
    RPG(ViceRank.GROWER, UserRank.PREMIUM),
    HEAVYSNIPER(ViceRank.SMUGGLER, UserRank.ELITE),
    SPECIALCARBINE(ViceRank.SMUGGLER, UserRank.ELITE),
    GRENADELAUNCHER(ViceRank.CHEMIST, UserRank.SPONSOR),
    COMBATMG(ViceRank.DRUGLORD, UserRank.SPONSOR),
    HOMINGLAUNCHER(ViceRank.KINGPIN, UserRank.SUPREME),
    MINIGUN(ViceRank.KINGPIN, UserRank.SUPREME),
    GOLDMINIGUN(ViceRank.KINGPIN, UserRank.SUPREME);

    private final ViceRank rank;
    private final UserRank userRank;

    LockedWeapon(ViceRank rank, UserRank userRank) {
        this.rank = rank;
        this.userRank = userRank;
    }

    public static boolean canUseWeapon(String identifier, ViceRank rank, UserRank userRank) {
        LockedWeapon w = getWeapon(identifier);
        return w == null || w.canUseWeapon(rank, userRank);
    }

    public ViceRank getViceRank() {
        return this.rank;
    }

    public UserRank getUserRank() {
        return this.userRank;
    }

    public boolean canUseWeapon(ViceRank rank, UserRank userRank) {
        if(rank == null || userRank == null) return false;
        return this.rank == rank || rank.isHigherThan(this.rank) || this.userRank == userRank || userRank.isHigherThan(this.userRank);
    }

    public static LockedWeapon getWeapon(String identifier) {
        return Arrays.stream(LockedWeapon.values()).filter(w -> w.toString().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }
}
