package net.grandtheftmc.vice.weapon.skins.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.ViceUser;

public class MainMenu extends CoreMenu {
    private final Player holder;
    private final int    page;

    public MainMenu(Player holder) {
        super(5, "Weapon Skins");

        this.holder = holder;
        this.page = 0;

        this.setup();
    }

    private MainMenu(Player holder, int page) {
        super(5, "Weapon Skins");

        this.holder = holder;
        this.page = page;

        this.setup();
    }

    @SuppressWarnings("deprecation")
    protected void setup() {
        for (int i = 0; i < 45; i++) {
            ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) DyeColor.BLACK.getWoolData());
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("");
            stack.setItemMeta(meta);

            this.addItem(new MenuItem(i, stack, false));
        }

        this.createMenuContent();
    }

    private void createMenuContent() {
        List<Weapon<?>> weapons = new ArrayList<Weapon<?>>();
        List<Weapon<?>> pageWeapons = null;

        for (GameItem gameItem : Vice.getItemManager().getItems()) {
            if (gameItem.getType() == GameItem.ItemType.WEAPON) {
                Optional<Weapon<?>> optional = Vice.getWastedGuns().getWeaponManager().getWeapon(gameItem.getWeaponOrVehicleOrDrug());

                if (optional.isPresent()) {
                    Weapon<?> weapon = optional.get();

                    if ((weapon instanceof RangedWeapon || weapon instanceof MeleeWeapon) && weapon.getWeaponSkins().length > 1) {
                        weapons.add(weapon);
                    }
                }
            }
        }

        try {
            pageWeapons = weapons.subList(this.page * 15, (this.page * 15) + 15);
        } catch (IndexOutOfBoundsException e) {
            pageWeapons = weapons.subList(this.page * 15, weapons.size() - 1);
        }

        int index = 11;
        for (int i = 0; i < pageWeapons.size(); i++) {
            Weapon<?> weapon = weapons.get(i + (this.page * 15));

            this.addItem(new ClickableItem(index, this.createSkinButton(weapon), (player, action) -> {
                new SkinsMenu(player, weapon).open();
            }));

            if (i == 4 || i == 9) {
                index += 5;
            } else {
                index++;
            }
        }

        this.createNextPageButtons((int) Math.ceil(((double) weapons.size()) / 15.0));
        this.createPreviousPageButtons();
    }

    @SuppressWarnings("deprecation")
    private void createNextPageButtons(int numPages) {
        for (int i = 0; i < 3; i++) {
            if (numPages > 1 && (this.page + 1) < numPages) {
                ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) DyeColor.LIME.getWoolData());
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName("Next Page");
                stack.setItemMeta(meta);

                this.addItem(new ClickableItem((i * 9) + 17, stack, (player, action) -> {
                    new MainMenu(this.holder, this.page + 1).open();
                }));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void createPreviousPageButtons() {
        for (int i = 0; i < 3; i++) {
            if (this.page != 0) {
                ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) DyeColor.RED.getWoolData());
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName("Next Page");
                stack.setItemMeta(meta);

                this.addItem(new ClickableItem((i * 9) + 9, stack, (player, action) -> {
                    new MainMenu(this.holder, this.page - 1).open();
                }));
            }
        }
    }

    private ItemStack createSkinButton(Weapon<?> weapon) {
        ItemStack stack = weapon.createItemStack();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&l" + weapon.getName()));

        int unlockedSkins = 0;
        ViceUser user = Vice.getUserManager().getLoadedUser(this.holder.getUniqueId());

        if (user.getUnlockedWeaponSkins(weapon) != null) {
            unlockedSkins = user.getUnlockedWeaponSkins(weapon).size();
        }

        meta.setLore(Arrays.asList(new String[] {
                ChatColor.translateAlternateColorCodes('&', "&6Unlocked:&r " + (1 + unlockedSkins) + "/4"), " ", ChatColor.translateAlternateColorCodes('&', "&7Click to view the skins for this weapon.")
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
}