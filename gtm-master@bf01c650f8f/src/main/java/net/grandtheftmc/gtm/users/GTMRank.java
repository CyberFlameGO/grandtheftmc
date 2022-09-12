package net.grandtheftmc.gtm.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;

public enum GTMRank {

	HOBO(0, 0, 0, 0, Material.CHEST),
	CRIMINAL(10000, 0, 0, 5.0, Material.WOOD_SWORD),
	HOMIE(25000, 0, 0, 10.0, Material.LEATHER_HELMET),
	THUG(50000, 1, 0, 15.0, Material.WOOD_HOE),
	GANGSTER(75000, 1, 2, 20.0, Material.IRON_HELMET),
	MUGGER(100000, 2, 3, 25.0, Material.WOOD_SWORD),
	HUNTER(250000, 2, 5, 30.0, Material.WOOD_AXE),
	DEALER(500000, 2, 5, 35.0, Material.SUGAR),
	PIMP(1000000, 2, 10, 40.0, Material.IRON_SWORD),
	MOBSTER(2000000, 3, 10, 45.0, Material.IRON_SPADE),
	GODFATHER(5000000, 3, 20, 50.0, Material.DIAMOND_CHESTPLATE);

	private final int price;
	private final List<String> perms;
	private final Material material;
	private final int houses;
	private final int gangMembers;
	/** The additional tax applied to this rank */
	private final double serverTax;

	GTMRank(int price, int houses, int gangMembers, double serverTax, Material material, String... perms) {
		this.price = price;
		this.perms = Arrays.asList(perms);
		this.houses = houses;
		this.material = material;
		this.gangMembers = gangMembers;
		this.serverTax = serverTax;
	}

	public List<String> getAllPerms() {
		List<String> permissions = new ArrayList<>();
		for (GTMRank uc : getGTMRanks()) {
			permissions.addAll(uc.perms);
			if (uc == this)
				return permissions;
		}
		return permissions;
	}

	private List<String> getPerms() {
		return this.perms;
	}

	public int getPrice() {
		return this.price;
	}

	public String getName() {
		if (Core.getSettings().isSister()) {
			if (this == DEALER)
				return "HUSTLER";
			if (this == PIMP)
				return "UNDERBOSS";
		}
		return this.toString();
	}

	public ChatColor getColor() {
		return this == GTMRank.HOBO ? ChatColor.GRAY : ChatColor.YELLOW;
	}

	public String getColoredName() {
		return Utils.f(this.getColor() + this.getName() + "&r");
	}

	public String getColoredNameBold() {
		return Utils.f(this.getColor() + "&l" + this.getName() + "&r");
	}

	public GTMRank getNext() {
		String rankName = this.getName();
		if ("GODFATHER".equalsIgnoreCase(rankName))
			return null;
		int go = 0;

		GTMRank rank = null;
		for (GTMRank r : getGTMRanks())
			if (go == 0) {
				if (Objects.equals(r.getName(), rankName)) {
					go = 1;
				}
			}
			else if (go == 1) {
				rank = r;
				break;
			}
		return rank;
	}

	public static GTMRank[] getGTMRanks() {
		return GTMRank.class.getEnumConstants();
	}

	public static GTMRank fromString(String string) {
		return Arrays.stream(GTMRank.getGTMRanks()).filter(uc -> uc.getName().equalsIgnoreCase(string) || uc.name().equalsIgnoreCase(string)).findFirst().orElse(GTMRank.HOBO);
	}

	public Material getMaterial() {
		return this.material;
	}

	public static GTMRank getRankOrNull(String name) {
		if (name == null)
			return null;
		return Arrays.stream(getGTMRanks()).filter(r -> r.getName().equalsIgnoreCase(name) || r.name().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public boolean isHigherThan(GTMRank rank) {
		if (rank == null)
			return false;
		for (GTMRank r : getGTMRanks())
			if (r == this)
				return false;
			else if (r == rank)
				return true;
		return false;
	}

	public int getHouses() {
		return this.houses;
	}

	public int getGangMembers() {
		return this.gangMembers;
	}

	/**
	 * Get the additional server tax applied for this GTMRank.
	 * <p>
	 * If this player dies, some of their money drops, which can be picked up.
	 * This variable increases or decreases the amount of money they drop.
	 * 
	 * Note: 5.0 represents 5% increase.
	 * </p>
	 * 
	 * @return The additional server tax applied for this GTMRank.
	 */
	public double getServerTax() {
		return serverTax;
	}

}
