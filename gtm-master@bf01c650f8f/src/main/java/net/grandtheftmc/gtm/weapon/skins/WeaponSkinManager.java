package net.grandtheftmc.gtm.weapon.skins;

import java.util.Arrays;
import java.util.Optional;

import net.grandtheftmc.core.util.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j0ach1mmall3.wastedguns.api.events.WeaponDropEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponPickupEvent;

import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.gui.ConfirmationMenu;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class WeaponSkinManager {

    public WeaponSkinManager() {
        Bukkit.getPluginManager().registerEvents(new SkinListener(), GTM.getInstance());
    }

    public void updateWeaponSkin(Player player, Weapon<?> weapon, WeaponSkin skin) {
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        user.equipWeaponSkin(weapon, skin);

        for (ItemStack stack : player.getInventory().getContents()) {
            Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(stack);

            if (weaponOpt.isPresent()) {
                if (weaponOpt.get().getUniqueIdentifier() == weapon.getUniqueIdentifier()) {
                    stack.setDurability(skin.getIdentifier());
                }
            }
        }
    }

    public WeaponSkin getHeldItemWeaponSkin(ItemStack stack) {
        WeaponManager weaponManager = GTM.getWastedGuns().getWeaponManager();

        if (stack != null) {
            Optional<Weapon<?>> weaponOpt = weaponManager.getWeapon(stack);

            if (weaponOpt.isPresent()) {
                Weapon<?> weapon = weaponOpt.get();

                if (weapon.getWeaponSkins() != null) {
                    for (WeaponSkin skin : weapon.getWeaponSkins()) {
                        if (stack.getDurability() == skin.getIdentifier()) {
                            return skin;
                        }
                    }
                }
            }
        }

        return null;
    }

    public WeaponSkin getWeaponSkinFromIdentifier(Weapon<?> weapon, short identifier) {
        for (WeaponSkin skin : weapon.getWeaponSkins()) {
            if ((skin.getIdentifier() - weapon.getWeaponIdentifier()) == identifier) {
                return skin;
            }
        }

        return null;
    }

    public ItemStack createSkinItem(Weapon<?> weapon, WeaponSkin skin) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();

        meta.setLore(Arrays.asList(
                Utils.f("&7Included skin:"),
                Utils.f("&8- &7" + ChatColor.stripColor(Utils.f(skin.getDisplayName())) + " (" + weapon.getName() + ")"))
        );

        String color = "&f";
        short id = (short) (skin.getIdentifier() - weapon.getWeaponIdentifier());
        if (id == 5 || id == 7) color = "&a";
        else if (id == 2 || id == 6) color = "&9";

        meta.setDisplayName(Utils.f(color + "&lWeapon Skin"));
        stack.setItemMeta(meta);

        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound compound = nmsStack.getTag();
        compound.setShort("weapon_id", weapon.getUniqueIdentifier());
        compound.setShort("skin_id", (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private class SkinListener implements Listener {
        @EventHandler
        public void onWeaponPickup(WeaponPickupEvent event) {
            ItemStack stack = event.getItem().getItemStack();
            Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(stack);

            if (weaponOpt.isPresent()) {
            	
            	// grab living entity
            	LivingEntity le = event.getLivingEntity();
            	
            	if (le instanceof Player){
            		Player player = (Player) le;
            		
            		GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
            		
            		if (gtmUser != null){
            			// might not have any skins
            			WeaponSkin weaponSkin = gtmUser.getEquippedWeaponSkin(weaponOpt.get());
            			
            			if (weaponSkin != null){
            				stack.setDurability(weaponSkin.getIdentifier());
            			}
            		}
            	}
            }
        }

        @EventHandler
        public void onWeaponDrop(WeaponDropEvent event) {
            ItemStack stack = event.getItemDrop().getItemStack();
//            if (stack.getType() == Material.ENCHANTED_BOOK) {
//                if (event.getLivingEntity() instanceof Player)
//                    ((Player) event.getLivingEntity()).closeInventory();
//            }

            Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(stack);

            if (weaponOpt.isPresent()) {
                stack.setDurability(weaponOpt.get().getWeaponIdentifier());
            }
        }

        @EventHandler
        public void onItemMove(InventoryMoveItemEvent event) {
            InventoryHolder holder = event.getSource().getHolder();

            if (holder instanceof Player) {
                Player player = (Player) holder;

                if (event.getDestination() != player.getInventory()) {
                    ItemStack stack = event.getItem();
                    Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(stack);

                    if (weaponOpt.isPresent() && stack.getDurability() != 0) {
                        stack.setDurability(weaponOpt.get().getWeaponIdentifier());
                    }
                } else {
                    ItemStack stack = event.getItem();
                    Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(stack);

                    if (weaponOpt.isPresent()) {
                        stack.setDurability(GTM.getUserManager().getLoadedUser(player.getUniqueId()).getEquippedWeaponSkin(weaponOpt.get()).getIdentifier());
                    }
                }
            }
        }

        @EventHandler
        public void onItemRightClick(PlayerInteractEvent event) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack item = event.getItem();

                net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
                NBTTagCompound compound = nmsStack.getTag();

                if (compound != null && compound.hasKey("weapon_id") && compound.hasKey("skin_id") && item.getType() == Material.ENCHANTED_BOOK) {

                    ItemStack updated = event.getPlayer().getInventory().getItemInMainHand();
                    if (updated == null || updated.getType() == Material.AIR)
                        return;

                    ConfirmationMenu menu = new ConfirmationMenu(GTM.getInstance(), event.getItem()) {
                        @Override
                        protected void onConfirm(InventoryClickEvent e, Player p) {
                            ItemStack updated = event.getPlayer().getInventory().getItemInMainHand();
                            if (updated == null || updated.getType() == Material.AIR)
                                return;

                            net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getItem());
                            NBTTagCompound compound = nmsStack.getTag();

                            short weaponID = compound.getShort("weapon_id");
                            short skinID = compound.getShort("skin_id");

                            Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);
                            if (weaponOpt.isPresent()) {
                                Weapon<?> weapon = weaponOpt.get();
                                WeaponSkin skin = getWeaponSkinFromIdentifier(weapon, skinID);

                                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());

                                if (!user.hasSkinUnlocked(weapon, skin)) {
                                    user.unlockWeaponSkin(weapon, skin);

                                    for (int i = 0; i < p.getInventory().getSize(); i++) {
                                        ItemStack found = p.getInventory().getItem(i);
                                        if (found == null || found.getType() == Material.AIR) continue;
                                        if (!found.isSimilar(event.getItem())) continue;

                                        if (found.getAmount() > 1) found.setAmount(found.getAmount() - 1);
                                        else p.getInventory().setItem(i, new ItemStack(Material.AIR));
                                        break;
                                    }
//                                    p.getInventory().remove(event.getItem());
                                    p.updateInventory();

                                    p.sendMessage(Utils.f("&aYou have unlocked a " + skin.getDisplayName() + " Skin &afor &6&l" + weapon.getName() + "&a! Please go to Mr Skinner at spawn to equip it."));
                                } else {
                                    p.sendMessage(Utils.f("&cYou already have this skin unlocked!"));
                                }

                                event.setCancelled(true);
                            }
                        }
                    };

                    menu.open(event.getPlayer());
                }
            }
        }
    }
}