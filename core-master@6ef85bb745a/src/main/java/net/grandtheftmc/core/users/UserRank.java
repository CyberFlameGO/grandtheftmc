package net.grandtheftmc.core.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Material;

import java.util.Objects;

public enum UserRank {

    DEFAULT("&8", "DEFAULT"), VIP("&6", "VIP"), PREMIUM("&a", "PREMIUM"), ELITE("&b", "ELITE"), SPONSOR("&5", "SPONSOR"), SUPREME("&c", "SUPREME"), YOUTUBER("&r", "YOUTUBER"),
    BUILDTEAM("&3","BUILDTEAM"), HELPOP("&d", "HELPER"), MOD("&9", "MOD"), SRMOD("&9", "SRMOD"), BUILDER("&f", "BUILDER"), ADMIN("&c", "ADMIN"), DEV("&9", "DEV"), MANAGER("&4", "MANAGER"), OWNER("&4", "OWNER");

    private final String color;
    private final String displayName;
    /** The server_key that this rank is for */
	private String serverKey;

    UserRank(String color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public static UserRank[] getUserRanks() {
        return UserRank.class.getEnumConstants();
    }

    public static UserRank getUserRank(String name) {
        if (name == null)
            return UserRank.DEFAULT;
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

    public static UserRank getUserRank(Material type) {
        if (type == null)
            return null;
        for (UserRank ur : getUserRanks())
            if (ur.getMaterial() == type)
                return ur;
        return null;
    }

    public static UserRank[] getDonorRanks() {
        return new UserRank[]{VIP, PREMIUM, ELITE, SPONSOR, SUPREME};
    }

    public String getName() {
        return this.toString();
    }

    public String getColor() {
        return this.color;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public String getColoredName() {
        return Utils.f(this == UserRank.YOUTUBER ? "&rYOU&4TUBER" : this.color + this.getDisplayName());
    }

    public String getColoredNameBold() {
        return Utils.f(this == UserRank.YOUTUBER ? "&r&lYOU&4&lTUBER" : this.color + "&l" + this.getDisplayName());
    }

    public String getTabPrefix() {
        return Utils.f(this == UserRank.YOUTUBER ? "&r&lY&4&lT" : (this == UserRank.BUILDTEAM ? "&3&lBT" : this.color + "&l" + this.getDisplayName()));
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

    public boolean hasRank(UserRank rank) {
        return this.ordinal() >= rank.ordinal();
    }

    public double getBucksModifier() {
        switch (this) {
            case DEFAULT:
                return 1;
            case VIP:
                return 1.5;
            case PREMIUM:
                return 2;
            case ELITE:
                return 2.5;
            case SPONSOR:
                return 3;
            case SUPREME:
                return 4;
            default:
                return 4;
        }
    }

    public int getNumberOfHomes(){
        switch (this) {
            case DEFAULT:
                return 1;
            case VIP:
                return 2;
            case PREMIUM:
                return 4;
            case ELITE:
                return 7;
            case SPONSOR:
                return 12;
            case SUPREME:
                return 20;
            default:
                return 20;
        }
    }

    public int getHomeTPCooldown(){
        switch (this) {
            case DEFAULT:
                return 10;
            case VIP:
                return 9;
            case PREMIUM:
                return 8;
            case ELITE:
                return 7;
            case SPONSOR:
                return 6;
            case SUPREME:
                return 5;
            default:
                return 5;
        }
    }

    public int getPrice() {
        switch (this) {
            case VIP:
                return 10;
            case PREMIUM:
                return 25;
            case ELITE:
                return 50;
            case SPONSOR:
                return 100;
            case SUPREME:
                return 200;
            default:
                return -1;
        }
    }

    public Material getMaterial() {
        switch (this) {
            case DEFAULT:
                return Material.IRON_INGOT;
            case VIP:
                return Material.GOLD_INGOT;
            case PREMIUM:
                return Material.EMERALD;
            case ELITE:
                return Material.DIAMOND;
            case SPONSOR:
                return Material.NETHER_BRICK_ITEM;
            default:
                return Material.CLAY_BRICK;
        }
    }

    public int getBackpackRows() {
        switch (this) {
            case DEFAULT:
                return 2;
            case VIP:
                return 3;
            case PREMIUM:
                return 5;
            case ELITE:
                return 7;
            case SPONSOR:
                return 9;
            default:
                return 11;
        }
    }

    public int getCompassRadius() {
        switch (this) {
            case DEFAULT:
            case VIP:
                return 30;
            case PREMIUM:
                return 25;
            case ELITE:
                return 20;
            case SPONSOR:
                return 15;
            default:
                return 10;
        }
    }

    public int getMonthlyTokens() {
        switch (this) {
            case DEFAULT:
                return 0;
            case VIP:
                return 100;
            case PREMIUM:
                return 250;
            case ELITE:
                return 500;
            case SPONSOR:
                return 1000;
            default:
                return 2000;

        }
    }

    public int getPetRespawnDelay() {
        switch (this) {
            case DEFAULT:
                return 900;
            case VIP:
                return 720;
            case PREMIUM:
                return 600;
            case ELITE:
                return 480;
            case SPONSOR:
                return 360;
            default:
                return 300;
        }
    }
    
    /**
	 * Get the server key that this rank is for.
	 * <p>
	 * Note: This must be set by the plugin or project for each rank.
	 * </p>
	 * 
	 * @return If a rank is a global one, "GLOBAL" is returned, or if it's a
	 *         per server rank, the server will be returned, i.e. "GTM1".
	 */
	public String getServerKey() {
		
		// if not set, assume per server
		if (serverKey == null || serverKey.isEmpty()){
			return Core.name().toUpperCase();
		}
		
		return serverKey;
	}

	/**
	 * Set the server key that this rank is for.
	 * <p>
	 * Note: This must be set by the plugin or project for each rank.
	 * </p>
	 * If a rank is a global rank, set this as "GLOBAL". If a rank is per server, set this as the name of the server, "GTM1".
	 * 
	 * @param serverKey - the server key for this rank
	 */
	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}


}
