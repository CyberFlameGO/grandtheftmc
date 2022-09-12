package net.grandtheftmc.gtm.users;

import net.grandtheftmc.core.users.UserRank;

import java.util.Arrays;

public enum LockedWeapon {

    MARKSMANPISTOL(GTMRank.HOMIE, UserRank.VIP),
    HEAVYSHOTGUN(GTMRank.THUG, UserRank.VIP),
    CHAINSAW(GTMRank.GANGSTER, UserRank.PREMIUM),
    GUSENBERGSWEEPER(GTMRank.GANGSTER, UserRank.PREMIUM),
    RPG(GTMRank.MUGGER, UserRank.PREMIUM),
    HEAVYSNIPER(GTMRank.HUNTER, UserRank.ELITE),
    SPECIALCARBINE(GTMRank.DEALER, UserRank.ELITE),
    GRENADELAUNCHER(GTMRank.PIMP, UserRank.SPONSOR),
    COMBATMG(GTMRank.MOBSTER, UserRank.SPONSOR),
    HOMINGLAUNCHER(GTMRank.GODFATHER, UserRank.SUPREME),
    MINIGUN(GTMRank.GODFATHER, UserRank.SUPREME);

    private final GTMRank rank;
    private final UserRank userRank;

    LockedWeapon(GTMRank rank, UserRank userRank) {
        this.rank = rank;
        this.userRank = userRank;
    }

    public static boolean canUseWeapon(String identifier, GTMRank rank, UserRank userRank) {
        LockedWeapon w = getWeapon(identifier);
        return w == null || w.canUseWeapon(rank, userRank);
    }

    public static LockedWeapon getWeapon(String identifier) {
        return Arrays.stream(LockedWeapon.values()).filter(w -> w.toString().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }

    public GTMRank getGTMRank() {
        return this.rank;
    }

    public UserRank getUserRank() {
        return this.userRank;
    }

//    public boolean canUseWeapon(GTMRank rank, UserRank userRank) {
//        return this.rank == rank || rank.isHigherThan(this.rank) || this.userRank == userRank || userRank.isHigherThan(this.userRank);
//    }

    public boolean canUseWeapon(GTMRank rank, UserRank userRank) {
        if(rank == null || userRank == null) return false;
        return this.rank == rank || rank.isHigherThan(this.rank) || this.userRank == userRank || userRank.isHigherThan(this.userRank);
    }
}
