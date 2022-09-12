package net.grandtheftmc.vice.weapon.skins;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class WeaponSkinManager {
    public WeaponSkinManager() {
        Bukkit.getPluginManager().registerEvents(new SkinListener(), Vice.getInstance());
    }

    public void updateWeaponSkin(Player player, Weapon<?> weapon, WeaponSkin skin) {
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        user.equipWeaponSkin(weapon, skin);

        for (ItemStack stack : player.getInventory().getContents()) {
            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeapon(stack);

            if (weaponOpt.isPresent()) {
                if (weaponOpt.get().getUniqueIdentifier() == weapon.getUniqueIdentifier()) {
                    stack.setDurability(skin.getIdentifier());
                }
            }
        }
    }

    public WeaponSkin getHeldItemWeaponSkin(ItemStack stack) {
        WeaponManager weaponManager = Vice.getWastedGuns().getWeaponManager();

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

        meta.setLore(Arrays.asList(new String[] {
                Utils.f("&7Included skin:"), Utils.f("&8- &7" + ChatColor.stripColor(Utils.f(skin.getDisplayName())) + " (" + weapon.getName() + ")"),
        }));

        meta.setDisplayName(Utils.f("&9&lWeapon Skin"));
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
            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeapon(stack);

            if (weaponOpt.isPresent()) {
                stack.setDurability(Vice.getUserManager().getLoadedUser(event.getLivingEntity().getUniqueId()).getEquippedWeaponSkin(weaponOpt.get()).getIdentifier());
            }
        }

        @EventHandler
        public void onWeaponDrop(WeaponDropEvent event) {
            ItemStack stack = event.getItemDrop().getItemStack();
            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeapon(stack);

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
                    Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeapon(stack);

                    if (weaponOpt.isPresent() && stack.getDurability() != 0) {
                        stack.setDurability(weaponOpt.get().getWeaponIdentifier());
                    }
                } else {
                    ItemStack stack = event.getItem();
                    Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeapon(stack);

                    if (weaponOpt.isPresent()) {
                        stack.setDurability(Vice.getUserManager().getLoadedUser(player.getUniqueId()).getEquippedWeaponSkin(weaponOpt.get()).getIdentifier());
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
                    ConfirmationMenu menu = new ConfirmationMenu(Vice.getInstance(), event.getItem()) {
                        @Override
                        protected void onConfirm(InventoryClickEvent e, Player p) {
                            net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getItem());
                            NBTTagCompound compound = nmsStack.getTag();

                            short weaponID = compound.getShort("weapon_id");
                            short skinID = compound.getShort("skin_id");

                            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);
                            if (weaponOpt.isPresent()) {
                                Weapon<?> weapon = weaponOpt.get();
                                WeaponSkin skin = getWeaponSkinFromIdentifier(weapon, skinID);

                                ViceUser user = Vice.getUserManager().getLoadedUser(p.getUniqueId());

                                if (!user.hasSkinUnlocked(weapon, skin)) {
                                    user.unlockWeaponSkin(weapon, skin);

                                    p.getInventory().remove(event.getItem());
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