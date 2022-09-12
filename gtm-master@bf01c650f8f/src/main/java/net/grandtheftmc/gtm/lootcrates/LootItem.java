package net.grandtheftmc.gtm.lootcrates;

import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.items.GameItem;

public class LootItem {

	/** The identifier for this loot item */
	private String identifier;
	/** The name of the item to drop, usually the game item */
    private String itemName;
    private double chance;
    private int min;
    private int max;
    /** Star rating for weapons */
    private int stars;
    private boolean isDrug;

    public LootItem(String identifier, String itemName, double chance, int min, int max, int stars, boolean isDrug) {
        this.identifier = identifier;
    	this.itemName = itemName;
        this.chance = chance;
        this.min = min;
        this.max = max;
        this.stars = stars;
        this.isDrug = isDrug;
    }

    /**
     * Get the identifier for this loot item.
     * <p>
     * This is what the id is for the yml.
     * </p>
     * 
     * @return The identifier for this loot item.
     */
    public String getIdentifier() {
		return identifier;
	}

    /**
     * Set the identifier for this loot item.
     * <p>
     * This is what the id is for the yml.
     * </p>
     * @param identifier - the new identifier
     */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public GameItem getGameItem() {
        if(!isDrug){
            return GTM.getItemManager().getItem(this.itemName);
        } else {
            Optional<Drug> drug = ((DrugService) GTM.getInstance().getDrugManager().getService()).getDrug(itemName);
            if (!drug.isPresent()) {
                return null;
            }
            DrugItem itema = DrugItem.getByDrug(drug.get());
            ItemStack is = itema.getItemStack();
            return new GameItem(itemName, is, is.getItemMeta().getDisplayName());
        }
    }

    public boolean isDrug(){
        return isDrug;
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Get the stars/rarity associated with this loot item.
     * 
     * @return The stars/rarity associated with this loot item.
     */
	public int getStars() {
		return stars;
	}

	/**
	 * Set the stars/rarity associated with this loot item.
	 * 
	 * @param stars - the new stars
	 */
	public void setStars(int stars) {
		this.stars = stars;
	}
}
