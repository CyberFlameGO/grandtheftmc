package net.grandtheftmc.vice.weapon.skins.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;

public class SkinsMenu extends CoreMenu {
    private final Player    holder;
    private final Weapon<?> weapon;

    public SkinsMenu(Player holder, Weapon<?> weapon) {
        super(5, "Weapon Skins");

        this.holder = holder;
        this.weapon = weapon;

        this.setup();
    }

    @SuppressWarnings("deprecation")
    protected void setup() {
        ViceUser user = Vice.getUserManager().getLoadedUser(this.holder.getUniqueId());
        List<WeaponSkin> unlockedSkins = user.getUnlockedWeaponSkins().get(this.weapon.getUniqueIdentifier());

        for (int i = 0; i < 45; i++) {
            ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) DyeColor.BLACK.getWoolData());
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("");
            stack.setItemMeta(meta);

            this.addItem(new MenuItem(i, stack, false));
        }

        for (int i = 0; i < this.weapon.getWeaponSkins().length; i++) {
            WeaponSkin skin = this.weapon.getWeaponSkins()[i];

            if ((unlockedSkins != null && unlockedSkins.contains(skin)) || (skin.getIdentifier() - this.weapon.getWeaponIdentifier()) == 0) {
                WeaponSkin currentSkin = user.getEquippedWeaponSkin(this.weapon);
                boolean selected = false;

                if (currentSkin != null) {
                    if (skin == currentSkin) {
                        selected = true;
                    }
                }

                if (i >= 0 && i <= 2) {
                    if (!selected) {
                        this.addItem(new ClickableItem(i + 12, this.createSkinStack(skin, selected), (player, action) -> {
                            Vice.getWeaponSkinManager().updateWeaponSkin(this.holder, this.weapon, skin);

                            new SkinsMenu(this.holder, this.weapon).open();
                        }));
                    } else {
                        this.addItem(new MenuItem(i + 12, this.createSkinStack(skin, selected), false));
                    }
                } else {
                    if (!selected) {
                        this.addItem(new ClickableItem(22, this.createSkinStack(skin, selected), (player, action) -> {
                            Vice.getWeaponSkinManager().updateWeaponSkin(this.holder, this.weapon, skin);

                            new SkinsMenu(this.holder, this.weapon).open();
                        }));
                    } else {
                        this.addItem(new MenuItem(22, this.createSkinStack(skin, selected), false));
                    }
                }
            } else {
                if (i >= 0 && i <= 2) {
                    this.addItem(new MenuItem(i + 12, this.createLockedSkinStack(), false));
                } else {
                    this.addItem(new MenuItem(22, this.createLockedSkinStack(), false));
                }
            }
        }

        this.addItem(new ClickableItem(40, this.createBackStack(), (player, action) -> {
            new MainMenu(player).open();
        }));
    }

    private ItemStack createBackStack() {
        ItemStack stack = new ItemStack(Material.REDSTONE, 1);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lBack"));
        meta.setLore(Arrays.asList(new String[] {
                ChatColor.translateAlternateColorCodes('&', "&7Return to the home page!")
        }));

        stack.setItemMeta(meta);

        return stack;
    }

    private ItemStack createSkinStack(WeaponSkin skin, boolean selected) {
        ItemStack stack = this.weapon.createItemStack(skin);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', skin.getDisplayName()));

        if (selected) {
            meta.setLore(Arrays.asList(new String[] {
                    ChatColor.translateAlternateColorCodes('&', "&7This is the currently selected skin"), ChatColor.translateAlternateColorCodes('&', "&7for this weapon.")
            }));
        } else {
            meta.setLore(Arrays.asList(new String[] {
                    ChatColor.translateAlternateColorCodes('&', "&7Click to change the selected skin.")
            }));
        }

        if (selected) {
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);

        return stack;
    }

    private ItemStack createLockedSkinStack() {
        ItemStack stack = new ItemStack(Material.STRING);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&l????"));
        meta.setLore(Arrays.asList(new String[] {
                ChatColor.translateAlternateColorCodes('&', "&7Unknown skin. Find it at the following places:"), ChatColor.translateAlternateColorCodes('&', "&8- &7Skin Crates"), ChatColor.translateAlternateColorCodes('&', "&8- &7Loot Chests"),
                ChatColor.translateAlternateColorCodes('&', "&8- &7Crowbar Crates")
        }));

        stack.setItemMeta(meta);

        return stack;
    }

    public void open() {
        this.openInventory(this.holder);
    }

    protected Player getHolder() {
        return this.holder;
    }

    protected Weapon<?> getWeapon() {
        return this.weapon;
    }
}