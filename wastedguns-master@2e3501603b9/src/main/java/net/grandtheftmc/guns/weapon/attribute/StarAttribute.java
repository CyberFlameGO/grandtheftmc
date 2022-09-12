package net.grandtheftmc.guns.weapon.attribute;

import org.bukkit.inventory.ItemStack;

/**
 * @deprecated - Unused as these methods were turned to static in Weapon.
 */
@Deprecated
public interface StarAttribute {
	
	/**
	 * Get the amount of stars assigned to this attribute.
	 * <p>
	 * Generally, stars are indicative of the power for this attribute.
	 * </p>
	 * @param is - the itemstack representation
	 * 
	 * @return The number of stars for this attribute, -1 if not found.
	 */
	int getStars(ItemStack is);
	
	/**
	 * Set the number of stars assigned to this attribute.
	 * 
	 * @param is - the itemstack representation
	 * @param stars - the new star amount
	 * 
	 * @return {@code true} if the stars was set for this item, {@code false} otherwise.
	 */
	boolean setStars(ItemStack is, int stars);

}
