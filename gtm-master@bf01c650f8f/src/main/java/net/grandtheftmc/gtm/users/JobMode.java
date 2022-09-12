package net.grandtheftmc.gtm.users;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Material;

import java.util.Arrays;

public enum JobMode {

    CRIMINAL("e"),
    COP("3", GTMRank.GANGSTER, UserRank.PREMIUM),
    HITMAN("8", GTMRank.HUNTER, UserRank.ELITE),
    ;

    private final String color;
    private final GTMRank rank;
    private final UserRank userRank;

    JobMode(String color) {
        this.color = color;
        this.rank = null;
        this.userRank = null;
    }

    JobMode(String color, GTMRank rank, UserRank userRank) {
        this.color = color;
        this.rank = rank;
        this.userRank = userRank;
    }

    public boolean canUse(GTMRank rank, UserRank userRank) {
        return (this.userRank == null && this.rank == null) || rank == this.rank || rank.isHigherThan(this.rank) || userRank == this.userRank || userRank.isHigherThan(this.userRank);
    }

    public String getName() {
        return this.toString();
    }

    public static JobMode getMode(String string) {
        return Arrays.stream(getJobModes()).filter(mode -> mode.toString().equalsIgnoreCase(string)).findFirst().orElse(JobMode.CRIMINAL);
    }

    public static JobMode getModeOrNull(String string) {
        return Arrays.stream(getJobModes()).filter(mode -> mode.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public static JobMode[] getJobModes() {
        return JobMode.class.getEnumConstants();
    }

    public String getColoredName() {
        return Utils.f('&' + this.color + this.getName());
    }

    public String getColoredNameBold() {
        return Utils.f('&' + this.color + "&l" + this.getName());
    }

    public String getColor() {
        return '&' + this.color;
    }

    public Material getMaterial() {
        switch (this) {
            case COP:
                return Material.LEATHER_CHESTPLATE;
            case HITMAN:
                return Material.SKULL_ITEM;
            default:
                return Material.WOOD_SWORD;
        }
    }

    public String getColorChar() {
        return this.color;
    }

    public GTMRank getRank() {
        return this.rank;
    }

    public UserRank getUserRank() {
        return this.userRank;
    }
}
