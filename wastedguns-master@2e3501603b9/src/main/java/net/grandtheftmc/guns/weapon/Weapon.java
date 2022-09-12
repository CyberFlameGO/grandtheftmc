package net.grandtheftmc.guns.weapon;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.WeaponCooldown;
import net.grandtheftmc.guns.WeaponState;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public abstract class Weapon<T extends Weapon> {

	/**
	 * Extra colour codes to make it unique, extra c, d and l standing for
	 * custom damage lore
	 */
	public static final String CUSTOM_DAMAGE_LORE_PREFIX = "\247c\247d\247l\2477";

	/**
	 * Damage prefix on the lore line.
	 */
	public static final String DAMAGE_PREFIX = "" + ChatColor.RESET + ChatColor.GRAY + "Stars: " + ChatColor.WHITE;

	private final short uniqueIdentifier;
	private final short weaponIdentifier;

	private final String name;
	private final WeaponType weaponType;
	private final AmmoType ammoType;
	private ItemStack itemStack;
	protected ItemStack oldItemStack;
	private final Sound[] sounds;
	protected WeaponSkin[] weaponSkins;
	protected Map<EntityDamageEvent.DamageCause, String> deathMessages;
	private String[] description;

	protected double walkSpeed = 0.2;
	/** The time, in ticks, that must wait before firing again. */
	protected int delay = 0;

	private WeaponCooldown weaponCooldown;
	protected WeaponState weaponState = WeaponState.IDLE;

	/**
	 * Construct a new Weapon.
	 */
	public Weapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds) {
		this.uniqueIdentifier = uniqueIdentifier;
		this.weaponIdentifier = itemStack.getDurability();
		
		this.name = name;
		this.weaponType = weaponType;
		this.ammoType = ammoType;
		this.itemStack = itemStack;
		this.sounds = sounds;
	}

	@Override
	public abstract T clone();

	public void onRightClick(Player player) {
	}

	public void onLeftClick(Player player) {
	}

	public void onSneak(Player player, boolean sneaking) {
	}
	
	public short getUniqueIdentifier() {
		return this.uniqueIdentifier;
	}

	public short getWeaponIdentifier() {
		return this.weaponIdentifier;
	}

	public String getName() {
		return this.name;
	}

	public String getCompactName() {
		return this.name.replace(" ", "");
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public AmmoType getAmmoType() {
		return this.ammoType;
	}

	/**
	 * Get the base representation of this weapon, without any lore, values, or
	 * data.
	 * 
	 * @return The basic itemstack representation for this weapon.
	 */
	public ItemStack getBaseItemStack() {
		return this.itemStack;
	}

	/**
	 * Get the weapon lore for this itemstack.
	 * <p>
	 * Note: ItemMeta is required because we're still building rules from
	 * getItemStack() above.
	 * </p>
	 * 
	 * @param is - the current itemstack
	 * @param im - the current item meta for the itemstack
	 * 
	 * @return The lore for the weapon.
	 */
	public List<String> getWeaponLore(ItemStack is, ItemMeta im) {

		// do not include weapon lore for vice
		if (Core.getSettings().getType() == ServerType.VICE)
			return Lists.newArrayList();

		List<String> lore = Lists.newArrayList();

		// if we have a star system
		if (GTMGuns.STAR_SYSTEM) {
			int rarity = getRarity(is);

			// add lore for the "star" system
			if (rarity > 0) {
				String builder = "" + ChatColor.GOLD;
				
				// add each "star" rarity
				for (int i = 0; i < rarity; i++){
					builder += "✮";
				}
				
				builder += "" + ChatColor.DARK_GRAY;
				
				// for each star not on the item
				for (int i = 0; i < (GTMGuns.MAX_STARS - rarity); i++){
					builder += "✩";
				}
				
				lore.add(builder);
				lore.add("");
			}
		}
		
		if (GTMGuns.KILL_COUNT_SYSTEM) {
            lore.add(ChatColor.GRAY + "Kills: " + getKills(is));
            lore.add("");
        }

		if(this.description != null) {
		    for (String desc : this.description)
	            lore.add(Utils.f("&7&o" + desc));
		}
		
		lore.add("");
		lore.addAll(Arrays.asList(this.getStatsBar()));

		return lore;
	}

	/**
	 * Create a new ItemStack for this weapon.
	 * <p>
	 * This will clone the ItemStack representation of this weapon, and add
	 * default star rating and default skins if applicable.
	 * </p>
	 * 
	 * @return The newly created ItemStack that represents this weapon.
	 */
	public ItemStack createItemStack() {
	    
		// clone the initial itemstack
		ItemStack result = this.itemStack.clone();

		// if item has no rarity
		if (!hasRarity(result)) {

			// if we have a star system
			if (GTMGuns.STAR_SYSTEM) {
				// default is 1 rarity (1 star)
				result = setRarity(result, 1);
			}
		}
		
		// If item has no kill counter
        if(!hasKills(result)) {
            if(GTMGuns.KILL_COUNT_SYSTEM) {
                result = setKills(result, 0);
            }
        }

		// get the item meta
		ItemMeta itemMeta = result.getItemMeta();

		if (this instanceof RangedWeapon)
			itemMeta.setDisplayName(Utils.f("&6" + getName() + " &8«&f" + ((RangedWeapon<?>) this).getMagazineSize() + "&8»"));
		else
			itemMeta.setDisplayName(Utils.f("&6" + getName()));

		// add the weapon lore
		List<String> lore = getWeaponLore(result, itemMeta);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		result.setItemMeta(itemMeta);

		return result;
	}
	
	/**
     * Create a new ItemStack for this weapon with a skin.
     * <p>
     * This will clone the ItemStack representation of this weapon, and add
     * default star rating and default skins if applicable.
     * </p>
     * 
     * @param skin The skin
     * @return The newly created ItemStack that represents this weapon.
     */
    public ItemStack createItemStack(WeaponSkin skin) {
        // clone the initial itemstack
        ItemStack result = this.itemStack.clone();

        // if item has no rarity
        if (!hasRarity(result)) {

            // if we have a star system
            if (GTMGuns.STAR_SYSTEM) {
                // default is 1 rarity (1 star)
                result = setRarity(result, 1);
            }
        }
        
        // If item has no kill counter
        if(!hasKills(result)) {
            if(GTMGuns.KILL_COUNT_SYSTEM) {
                result = setKills(result, 0);
            }
        }
        
        // Set the skin
        if(skin != null) {
            result.setDurability(skin.getIdentifier());
        }

        // get the item meta
        ItemMeta itemMeta = result.getItemMeta();

        if (this instanceof RangedWeapon)
            itemMeta.setDisplayName(Utils.f("&6" + getName() + " &8«&f" + ((RangedWeapon<?>) this).getMagazineSize() + "&8»"));
        else
            itemMeta.setDisplayName(Utils.f("&6" + getName()));

        // add the weapon lore
        List<String> lore = getWeaponLore(result, itemMeta);
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        result.setItemMeta(itemMeta);

        return result;
    }

	/**
	 * Create a new ItemStack for this weapon, based off the old ItemStack
	 * parameter.
	 * <p>
	 * This methods attempts to copy some meta values off the old ItemStack and
	 * create them on the new ItemStack.
	 * </p>
	 * 
	 * @param old - the old itemstack that needs to be replaced
	 * 
	 * @return The newly created ItemStack that represents this weapon.
	 */
	public ItemStack createItemStack(ItemStack old) {

		ItemStack result = getBaseItemStack().clone();

		// if we have a star system
		if (GTMGuns.STAR_SYSTEM) {
			// get rarity of old "itemstack"
			int prevStars = getRarity(old);
			if (prevStars > 1) {
				result = setRarity(result, prevStars);
			}
			else {
				result = setRarity(result, 1);
			}
		}
		
		// If item has no kill counter
        if(GTMGuns.KILL_COUNT_SYSTEM) {
            result = setKills(result, getKills(old));
        }

		// get the base item meta
		ItemMeta itemMeta = result.getItemMeta();

		// set weapon skin here based off old
		result.setDurability(old.getDurability());

		if (this instanceof RangedWeapon)
			itemMeta.setDisplayName(Utils.f("&6" + getName() + " &8«&f" + ((RangedWeapon<?>) this).getMagazineSize() + "&8»"));
		else
			itemMeta.setDisplayName(Utils.f("&6" + getName()));

		// add the weapon lore
		List<String> lore = getWeaponLore(result, itemMeta);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		result.setItemMeta(itemMeta);

		return result;
	}

	/**
	 * Create a new ItemStack for this weapon.
	 * <p>
	 * This methods creates a baseline representation for an item with the
	 * specified star value and skin.
	 * </p>
	 * 
	 * @param stars - the number of stars associated with this weapon
	 * @param skin - the skin for the weapon
	 * 
	 * @return The newly created ItemStack that represents this weapon.
	 */
	public ItemStack createItemStack(int stars, WeaponSkin skin) {

		// get the base itemstack representation for this weapon
		ItemStack representation = getBaseItemStack().clone();

		// if we have a star system
		if (GTMGuns.STAR_SYSTEM) {
			representation = setRarity(representation, stars);
		}
		
		// If item has no kill counter
        if(!hasKills(representation)) {
            if(GTMGuns.KILL_COUNT_SYSTEM) {
                representation = setKills(representation, 0);
            }
        }
        
		// get the base item meta
		ItemMeta itemMeta = representation.getItemMeta();
		
		// Set weapon skin
        if(skin != null) {
            representation.setDurability(skin.getIdentifier());
        }

		if (this instanceof RangedWeapon)
			itemMeta.setDisplayName(Utils.f("&6" + getName() + " &8«&f" + ((RangedWeapon<?>) this).getMagazineSize() + "&8»"));
		else
			itemMeta.setDisplayName(Utils.f("&6" + getName()));

		// add the weapon lore
		List<String> lore = getWeaponLore(representation, itemMeta);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		representation.setItemMeta(itemMeta);

		return representation;
	}

	public abstract String[] getStatsBar();

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemStack getOldItemStack() {
		return oldItemStack;
	}

	public void setOldItemStack(ItemStack oldItemStack) {
		this.oldItemStack = oldItemStack;
	}

	public Sound[] getSounds() {
		return this.sounds;
	}

	public Map<EntityDamageEvent.DamageCause, String> getDeathMessages() {
		return this.deathMessages;
	}

	public Weapon addDeathMessage(EntityDamageEvent.DamageCause damageCause, String message) {
		if (this.deathMessages == null)
			this.deathMessages = Maps.newHashMap();
		this.deathMessages.put(damageCause, message);
		return this;
	}

	/**
	 * Get the walk speed attribute of the Weapon
	 *
	 * @return walk speed value
	 */
	public double getWalkSpeed() {
		return this.walkSpeed;
	}

	public WeaponSkin[] getWeaponSkins() {
		return this.weaponSkins;
	}

	/**
	 * Get the delay that is required to be met before the weapon can be used again.
	 * <p>
	 * This is in ticks, as the value ends up getting multiplied by 50, 
	 * representing a "Minecraft tick" in milliseconds.
	 * 
	 * Therefore, if this value is 5, that means 250 milliseconds must elapse before firing again.
	 * </p>
	 *
	 * @return The time, in ticks, required for this weapon to be fired again.
	 */
	public int getDelay() {
		return this.delay;
	}

	public WeaponCooldown getWeaponCooldown() {
		return weaponCooldown;
	}

	public void setWeaponCooldown(WeaponCooldown weaponCooldown) {
		this.weaponCooldown = weaponCooldown;
	}

	public WeaponState getWeaponState() {
		return weaponState;
	}

	public void setWeaponState(WeaponState weaponState) {
		this.weaponState = weaponState;
	}

	public String[] getDescription() {
		return description;
	}

	public void setDescription(String... description) {
		this.description = description;
	}

	/**
	 * Get whether or not this weapon has a rarity associated with it.
	 * 
	 * @param is - the itemstack representation of the weapon
	 * 
	 * @return {@code true} if the item has a rarity associated with it,
	 *         {@code false} otherwise.
	 */
	public static boolean hasRarity(ItemStack is) {

		// convert to NMS itemstack
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);

		// if tag system exists
		if (nmsStack.getTag() != null) {
			NBTTagCompound tag = nmsStack.getTag();

			// if key exists, this has a rarity
			if (tag.hasKey("rarity")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the rarity of the specified weapon, given the itemstack
	 * representation.
	 * 
	 * @param is - the itemstack representation of the weapon
	 * 
	 * @return The rarity, in a rating system, where the lower the number the
	 *         lower the rarity. -1 if there is no rarity.
	 */
	public static int getRarity(ItemStack is) {

		// can get rarity if it exists
		if (hasRarity(is)) {

			// convert to NMS itemstack
			net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);

			// if tag system exists
			if (nmsStack.getTag() != null) {
				NBTTagCompound tag = nmsStack.getTag();

				try {
					return Integer.parseInt(tag.get("rarity").toString());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return -1;
	}

	/**
	 * Set the rarity of the specified weapon.
	 * 
	 * @param is - the itemstack representation of the weapon
	 * @param value - the higher the value, the more rare it is
	 * 
	 * @return The ItemStack that was newly created and copied.
	 */
	public static ItemStack setRarity(ItemStack is, int value) {

		// convert to NMS itemstack
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.set("rarity", new NBTTagInt(value));

		nmsStack.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	/**
     * Get whether or not this weapon has kills associated with it.
     * 
     * @param is - the itemstack representation of the weapon
     * 
     * @return {@code true} if the item has kills associated with it,
     *         {@code false} otherwise.
     */
    public static boolean hasKills(ItemStack is) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
        
        if (nmsStack.getTag() != null) {
            NBTTagCompound tag = nmsStack.getTag();
            
            if (tag.hasKey("weapon_kills")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Set the kills of the specified weapon.
     * 
     * @param is - the itemstack representation of the weapon
     * @param value - the amount of kills
     * 
     * @return The ItemStack that was newly created and copied.
     */
    public static ItemStack setKills(ItemStack is, int value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        compound.set("weapon_kills", new NBTTagInt(value));
        nmsStack.setTag(compound);
        
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    /**
     * Get the kills of the specified weapon, given the itemstack
     * representation.
     * 
     * @param is - the itemstack representation of the weapon
     * 
     * @return The amount of kills
     */
    public static int getKills(ItemStack is) {
        if (hasKills(is)) {
            net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
            
            if (nmsStack.getTag() != null) {
                NBTTagCompound tag = nmsStack.getTag();

                try {
                    return Integer.parseInt(tag.get("weapon_kills").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

    /**
     * Updates the lore of the specified weapon.
     * 
     * @param is - the itemstack representation of the weapon
     */
    public static void updateLore(ItemStack is) {
        Weapon<?> weapon = GTMGuns.getInstance().getWeaponManager().getWeaponByItem(is);

        ItemMeta meta = is.getItemMeta();
        meta.setLore(weapon.getWeaponLore(is, meta));
        
        is.setItemMeta(meta);
    }
}