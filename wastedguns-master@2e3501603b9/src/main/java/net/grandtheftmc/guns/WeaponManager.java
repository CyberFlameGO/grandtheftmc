package net.grandtheftmc.guns;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;
import com.j0ach1mmall3.wastedguns.commands.GiveWeaponCommandHandler;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.guns.cache.PlayerCache;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public final class WeaponManager {
    protected final ConcurrentHashMap<UUID, PlayerCache> playerCacheMap;
    protected final Set<Entity> entityQueue;
    protected final Set<Material> ignoredBlocks;
    protected final Set<Weapon<?>> weapons;

    public WeaponManager() {
        this.playerCacheMap = new ConcurrentHashMap<UUID, PlayerCache>();
        this.entityQueue = Sets.newHashSet();
        this.ignoredBlocks = Sets.newHashSet();
        this.weapons = Sets.newHashSet();

        //Register giveweapon command.
        new GiveWeaponCommandHandler(this);
    }

    public ConcurrentHashMap<UUID, PlayerCache> getPlayerCacheMap() {
        return playerCacheMap;
    }

    public Set<Entity> getEntityQueue() {
        return entityQueue;
    }

    public Set<Material> getIgnoredBlocks() {
        return ignoredBlocks;
    }

    public Set<Weapon<?>> getRegisteredWeapons() {
        return weapons;
    }

    public void registerWeapons(List<Weapon<?>> list) {
        weapons.addAll(list);
    }

    public Optional<Weapon<?>> getWeapon(short durability) {
        return this.weapons.stream().filter(weapon -> {
            if (weapon.getWeaponSkins() != null) {
                for (WeaponSkin skin : weapon.getWeaponSkins()) {
                    if (skin.getIdentifier() == durability) {
                        return true;
                    }
                }
            }

            return weapon.getWeaponIdentifier() == durability;
        }).findFirst();
    }

    public Optional<Weapon<?>> getWeaponFromUniqueIdentifier(short uniqueIdentifier) {
        return this.weapons.stream().filter(weapon -> {
            return weapon.getUniqueIdentifier() == uniqueIdentifier;
        }).findFirst();
    }

    public Optional<Weapon<?>> getWeapon(String name){
        return this.weapons.stream().filter(weapon -> weapon.getName().equalsIgnoreCase(name) || weapon.getCompactName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Get the weapon based off the itemstack representation.
     * <p>
     * This is a costly operation and iterates over the whole set 
     * and compares each weapon's base itemstack to this one 
     * and possible skin lookups.
     * </p>
     * 
     * @param is - the itemstack to get the weapon for
     * 
     * @return The weapon, that has the base representation as this item, if it exists.
     */
    public Optional<Weapon<?>> getWeapon(ItemStack is){	
    	if (is == null){
    		return Optional.empty();
    	}
    	
    	for (Weapon weapon : this.weapons){
    		ItemStack base = weapon.getBaseItemStack();
    		
    		if (base != null){
    			
    			// if the same type
    			if (is.getType() == base.getType() && is.getTypeId() == base.getTypeId()){
    				
    				// if same durability
    				if (is.getDurability() == base.getDurability()){
    					return Optional.of(weapon);
    				}
    				
    				if (weapon.getWeaponSkins() != null){
        				for (WeaponSkin skin : weapon.getWeaponSkins()){
        					if (is.getDurability() == skin.getIdentifier()){
        						return Optional.of(weapon);
        					}
        				}
    				}
    			}
    		}
    	}
    	return Optional.empty();
        //return this.weapons.stream().filter(weapon -> weapon.getBaseItemStack().isSimilar(is)).findFirst();
    }

    public Weapon<?> getWeaponInHand(LivingEntity livingEntity) {
        return getWeaponByItem(livingEntity.getEquipment().getItemInMainHand());
    }

    public Weapon<?> getWeaponByItem(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD) return null;
        Optional<Weapon<?>> optional = getWeapon(itemStack.getDurability());
        return optional.orElse(null);
    }

    public PlayerCache getPlayerCache(UUID uuid) {
        return this.playerCacheMap.computeIfAbsent(uuid, k -> new PlayerCache());
    }

    public void giveWeapon(Player player, Weapon<?> weapon, boolean freshWeapon, short... skin) {
//        ItemMeta itemMeta = weapon.getItemStack().getItemMeta();
//        if(freshWeapon) {
//            if (weapon instanceof RangedWeapon)
//                itemMeta.setDisplayName(Utils.f("&6" + weapon.getName() + " &8«&f" + ((RangedWeapon<?>) weapon).getMagazineSize() + "&8»"));
//            else
//                itemMeta.setDisplayName(Utils.f("&6" + weapon.getName()));
//        }
//        itemMeta.setUnbreakable(true);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
//        weapon.getItemStack().setItemMeta(itemMeta);

        player.getInventory().addItem(weapon.createItemStack());
//        if(skin != null && skin.length > 0) setSkin(player, weapon, skin[0]);
        player.updateInventory();
    }

    public Weapon<?> isWeapon(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD) return null;

        Optional<Weapon<?>> optional = weapons.stream().filter(weapon -> {
            if (weapon.getWeaponSkins() != null) {
                for (WeaponSkin skin : weapon.getWeaponSkins()) {
                    if (skin.getIdentifier() == itemStack.getDurability()) {
                        return true;
                    }
                }
            }

            return weapon.getWeaponIdentifier() == itemStack.getDurability();
        }).findFirst();
        
        return optional.orElse(null);
    }

    /**
     * @deprecated This method does nothing, as createItemStack returns an ItemStack.
     */
    @Deprecated
	public void setSkin(Player player, Weapon<?> weapon, String skinName) {
        WeaponSkin[] weaponSkins = null;
        if(weapon instanceof RangedWeapon)
            weaponSkins = ((RangedWeapon) weapon).getWeaponSkins();

        if(weapon instanceof MeleeWeapon)
            weaponSkins = ((MeleeWeapon) weapon).getWeaponSkins();

        if(weaponSkins == null) return;

        for(WeaponSkin skin : weaponSkins) {
            if(!skinName.equalsIgnoreCase(skin.getDisplayName())) continue;
            
            weapon.createItemStack(1, skin);
        }
    }

    /**
     * @deprecated This method does nothing, as createItemStack returns an ItemStack.
     */
    @Deprecated
    public void setSkin(Player player, Weapon<?> weapon, short id) {
        WeaponSkin[] weaponSkins = null;
        if(weapon instanceof RangedWeapon)
            weaponSkins = ((RangedWeapon) weapon).getWeaponSkins();

        if(weapon instanceof MeleeWeapon)
            weaponSkins = ((MeleeWeapon) weapon).getWeaponSkins();

        if(weaponSkins == null) return;

        for(WeaponSkin skin : weaponSkins) {
            if(weapon.getWeaponIdentifier() + id != skin.getIdentifier()) continue;
            //weapon.getItemStack().setDurability(skin.getIdentifier());
        }
    }

    public void updateOldWeapon(Player player, ItemStack item) {
        if(player == null || (item == null || item.getType() == Material.AIR)) return;
        if(!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) return;

        if(item.getItemMeta().getDisplayName()!=null && item.getItemMeta().getDisplayName().contains("Devil's Snowball"))
            return;
        
        // grab core user
        User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        
        if (user == null || user.hasEditMode()){
        	return;
        }

        Weapon<?> w = null;
        for(Weapon<?> weapon : this.weapons) {
            if(weapon.getOldItemStack().getType() == Material.FLINT_AND_STEEL && item.getType() == Material.FLINT_AND_STEEL) {
                if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Katana")) {
                    if(weapon.getName().equalsIgnoreCase("Katana")) {
                        w = weapon;
                        break;
                    }
                }

                if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Flamethrower")) {
                    if(weapon.getName().equalsIgnoreCase("Flamethrower")) {
                        w = weapon;
                        break;
                    }
                }
            }
            else {
                if(weapon.getOldItemStack().getType() == item.getType() &&  weapon.getOldItemStack().getData().getData() == item.getData().getData()) {
                    if(item.getDurability() == 0) {
                        w = weapon;
                        break;
                    }
                    else {
                        if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Minigun")) {
                            if(weapon.getName().equalsIgnoreCase("Gold Minigun")) {
                                w = weapon;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(w == null) return;
        ItemStack newItem = w.getBaseItemStack().clone();
        item.setType(newItem.getType());
        item.setDurability(newItem.getDurability());
        item.setData(newItem.getData());
        item.setItemMeta(newItem.getItemMeta());

//        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        NBTTagCompound compound;
//        if (nmsItem.getTag() == null) compound = new NBTTagCompound();
//        else compound = nmsItem.getTag();
//
//        compound.set("stack_fix", new NBTTagString(UUID.randomUUID().toString()));
//        compound.set("weapon_type", new NBTTagString(optional.get().getWeaponType().name()));
//
//        compound.set("weapon_range", new NBTTagDouble(((RangedWeapon<?>) optional.get()).getRange()));
//        compound.set("weapon_accuracy", new NBTTagDouble(((RangedWeapon<?>) optional.get()).getAccuracy()));
//        compound.set("weapon_melee-damage", new NBTTagDouble(((MeleeWeapon) optional.get()).getMeleeDamage()));
//
//        if(optional.get() instanceof RangedWeapon<?>)
//            compound.set("weapon_damage", new NBTTagDouble(((RangedWeapon<?>) optional.get()).getDamage()));
//
//        if(optional.get() instanceof WeaponRPM)
//            compound.set("weapon_rpm", new NBTTagInt(((WeaponRPM) optional.get()).getRpm()));
//
//        nmsItem.setTag(compound);
//        item.setItemMeta(CraftItemStack.asBukkitCopy(nmsItem).getItemMeta());
    }
}
