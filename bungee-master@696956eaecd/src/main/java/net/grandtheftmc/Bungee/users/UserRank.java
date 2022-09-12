package net.grandtheftmc.Bungee.users;

import net.grandtheftmc.Bungee.Utils;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;

public enum UserRank {

    DEFAULT("&8"), VIP("&6"), PREMIUM("&a"), ELITE("&b"), SPONSOR("&5"), SUPREME("&c"),
    YOUTUBER("&r"), HELPOP("&d"), MOD("&9"), SRMOD("&9"), BUILDER("&f"), ADMIN("&c"),
    DEV("&9"), MANAGER("&4"), OWNER("&4");

    private final String color;

    UserRank(String color) {
        this.color = color;
    }

    public static UserRank[] getUserRanks() {
        return UserRank.class.getEnumConstants();
    }

    public static UserRank getUserRank(String name) {
        if (name == null) return UserRank.DEFAULT;
        for (UserRank ur : getUserRanks())
            if (ur.getName().equalsIgnoreCase(name))
                return ur;
        return UserRank.DEFAULT;
    }

    public static UserRank getUserRankOrNull(String name) {
        if (name == null)
            return null;
        for (UserRank ur : getUserRanks())
            if (ur.getName().equalsIgnoreCase(name))
                return ur;
        return null;
    }

    public static UserRank getUserRankExact(String name) {
        if (name == null)
            return null;
        for (UserRank ur : getUserRanks())
            if (ur.getName().equalsIgnoreCase(name))
                return ur;
        return null;
    }

    public static UserRank[] getDonorRanks() {
        return new UserRank[] { VIP, PREMIUM, ELITE, SPONSOR, SUPREME };
    }

    public String getName() {
        return this.toString();
    }

    public String getColor() {
        return Utils.f(this.isHigherThan(UserRank.YOUTUBER) ? this.color + ChatColor.BOLD : this.color);
    }

    public String getColoredName() {
        return Utils.f(this == UserRank.YOUTUBER ? "&rYOU&4TUBER" : this.color + this.getName());
    }

    public String getColoredNameBold() {
        return Utils.f(this == UserRank.YOUTUBER ? "&r&lYOU&4&lTUBER" : this.color + "&l" + this.getName());
    }

    public String getTabPrefix() {
        return Utils.f(this == UserRank.YOUTUBER ? "&r&lY&4&lT" : this.color + "&l" + this.getName());
    }

    public String getPrefix() {
        if (!Objects.equals(this.getName(), "DEFAULT"))
            return Utils.f(' ' + this.getColoredNameBold() + "&8&l>");
        return "";
    }

    public UserRank getNext() {
        boolean picknext = false;
        for (UserRank u : getUserRanks()) {
            if (picknext)
                return u;
            if (Objects.equals(u.getName(), this.getName()))
                picknext = true;
        }
        return UserRank.DEFAULT;
    }

    public boolean isHigherThan(UserRank rank) {
        for (UserRank r : getUserRanks())
            if (r == this)
                return false;
            else if (r == rank)
                return true;
        return false;
    }
}
