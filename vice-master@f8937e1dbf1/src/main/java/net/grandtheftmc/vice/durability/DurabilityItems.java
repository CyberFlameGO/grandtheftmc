package net.grandtheftmc.vice.durability;

import org.bukkit.Material;

/**
 * Created by ThatAbstractWolf on 2017-08-02.
 */
public enum DurabilityItems {

	LEATHER_HELMET(Material.LEATHER_HELMET, 56),
	LEATHER_CHESTPLATE(Material.LEATHER_CHESTPLATE, 81),
	LEATHER_LEGGINGS(Material.LEATHER_LEGGINGS, 76),
	LEATHER_BOOTS(Material.LEATHER_BOOTS, 66),

	GOLD_HELMET(Material.GOLD_HELMET, 78),
	GOLD_CHESTPLATE(Material.GOLD_CHESTPLATE, 113),
	GOLD_LEGGINGS(Material.GOLD_LEGGINGS, 106),
	GOLD_BOOTS(Material.GOLD_BOOTS, 92),

	CHAINMAIL_HELMET(Material.CHAINMAIL_HELMET, 166),
	CHAINMAIL_CHESTPLATE(Material.CHAINMAIL_CHESTPLATE, 241),
	CHAINMAIL_LEGGINGS(Material.CHAINMAIL_LEGGINGS, 226),
	CHAINMAIL_BOOTS(Material.CHAINMAIL_BOOTS, 196),

	IRON_HELMET(Material.IRON_HELMET, 166),
	IRON_CHESTPLATE(Material.IRON_CHESTPLATE, 241),
	IRON_LEGGINGS(Material.IRON_LEGGINGS, 226),
	IRON_BOOTS(Material.IRON_BOOTS, 66),

	DIAMOND_HELMET(Material.DIAMOND_HELMET, 364),
	DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, 529),
	DIAMOND_LEGGINGS(Material.DIAMOND_LEGGINGS, 496),
	DIAMOND_BOOTS(Material.DIAMOND_BOOTS, 430),

	JETPACK(Material.GOLD_CHESTPLATE, 50, "Jetpack")
	;

	private Material material;
	private int maximumDurability;

	private String displayName;

	DurabilityItems(Material material, int maximumDurability) {
		this.material = material;
		this.maximumDurability = maximumDurability;
	}

	DurabilityItems(Material material, int maximumDurability, String displayName) {
		this.material = material;
		this.maximumDurability = maximumDurability;
		this.displayName = displayName;
	}

	public Material getMaterial() {
		return material;
	}

	public int getMaximumDurability() {
		return maximumDurability;
	}

	public String getDisplayName() {
		return displayName;
	}
}
