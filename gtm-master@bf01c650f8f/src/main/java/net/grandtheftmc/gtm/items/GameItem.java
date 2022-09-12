package net.grandtheftmc.gtm.items;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import net.grandtheftmc.gtm.utils.RandomUtil;
import net.grandtheftmc.gtm.weapon.melee.Dildo;
import net.grandtheftmc.gtm.weapon.ranged.special.GoldMinigun;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.utils.ItemStackUtil;
import net.grandtheftmc.guns.weapon.Weapon;

public class GameItem {

    private final ItemType type;
    private final String name;
    private ItemStack item;
    private String weaponOrVecicleOrDrug;
    private String displayName;
    private double sellPrice, buyPrice;
    private AmmoType ammoType;
    private ArmorUpgrade armorUpgrade;
    private boolean stackable = false;
    private boolean hideDurability;

    public GameItem(String name, ItemStack item, String displayName) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.item = item;
        this.displayName = displayName;
        this.hideDurability = false;
    }

    public GameItem(String name, ItemStack item, String displayName, double sellPrice) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.item = item;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.hideDurability = false;
    }

    public GameItem(String name, ItemStack item, String displayName, double sellPrice, double buyPrice) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.item = item;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = false;
    }

    public GameItem(String name, ItemStack item, String displayName, double sellPrice, double buyPrice, boolean hideDurability, boolean stackable) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.item = item;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = hideDurability;
        this.stackable = stackable;
    }

    public GameItem(String name, ItemStack item, AmmoType type, String displayName) {
        this.type = ItemType.AMMO;
        this.name = name;
        this.item = item;
        this.ammoType = type;
        this.displayName = displayName;
        this.hideDurability = false;
    }

    public GameItem(String name, ItemStack item, AmmoType type, String displayName, double sellPrice, double buyPrice) {
        this.type = ItemType.AMMO;
        this.name = name;
        this.item = item;
        this.ammoType = type;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = false;
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.hideDurability = false;
        this.getItem();
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, boolean hideDurability) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.hideDurability = hideDurability;
        this.getItem();
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, double sellPrice, double buyPrice) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = false;
        this.getItem();
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, double sellPrice, double buyPrice,
                    boolean hideDurability) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = hideDurability;
        this.getItem();
    }

    public GameItem(String name, ArmorUpgrade armorUpgrade, String displayName) {
        this.type = ItemType.ARMOR_UPGRADE;
        this.name = name;
        this.armorUpgrade = armorUpgrade;
        this.displayName = displayName;
        this.hideDurability = false;
        this.getItem();
    }

    public GameItem(String name, ArmorUpgrade armorUpgrade, String displayName, double sellPrice, double buyPrice) {
        this.type = ItemType.ARMOR_UPGRADE;
        this.name = name;
        this.displayName = displayName;
        this.armorUpgrade = armorUpgrade;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.hideDurability = false;
        this.getItem();
    }

    public boolean isStackable() {
        return stackable;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItem() {
        return this.getItem(1);
    }

    public ItemStack getItem(int amount) {
        switch (this.type) {
            case WEAPON: {
//                Optional<Weapon> opt = GTM.getWastedGuns().getWeapon(this.weaponOrVecicleOrDrug);
//                opt.ifPresent(weapon -> this.item = weapon.getItemStack());
                Optional<Weapon<?>> opt = GTM.getWastedGuns().getWeaponManager().getWeapon(this.weaponOrVecicleOrDrug);
                opt.ifPresent(weapon -> {
                    switch (weapon.getWeaponType()) {
                        case THROWABLE:
                        case DROPPABLE:
                            this.item = weapon.createItemStack();
                            break;

                            default:
                                this.item = ItemStackUtil.addTag(weapon.createItemStack(), "stackFix", UUID.randomUUID().toString());
                                break;
                    }
                });
                break;
            }
            case DRUG: {
                Optional<Drug> drug = ((DrugService) GTM.getDrugManager().getService()).getDrug(this.weaponOrVecicleOrDrug);
                if (drug.isPresent()) {
                    DrugItem drugItem = DrugItem.getByDrug(drug.get());
                    if (drugItem != null) {
                        this.item = drugItem.getItemStack();
                    }
                }
                break;
            }

            case VEHICLE:
                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(this.weaponOrVecicleOrDrug);
                opt.ifPresent(vehicleProperties -> this.item = vehicleProperties.getItem());
                break;

            case SKIN:
                Random random = RandomUtil.RANDOM;
                List<Weapon<?>> weapons = GTMGuns.getInstance().getWeaponManager().getRegisteredWeapons().stream()
                        .filter(weapon -> !(weapon instanceof Dildo) && !(weapon instanceof GoldMinigun)).collect(Collectors.toList());

                Weapon<?> randomWeapon = weapons.get(random.nextInt(weapons.size()));
                if (randomWeapon == null) break;

                WeaponSkin randomSkin = null;

                if(randomWeapon.getWeaponSkins() != null && randomWeapon.getWeaponSkins().length > 1) {
                    short[] commonSkins = {
                            5, 7
                    };

                    short[] rareSkins = {
                            2, 6
                    };

                    if (this.displayName.equals("weapon_skin_common")) {
                        randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, commonSkins[random.nextInt(commonSkins.length)]);
                    }
                    else if (this.displayName.equals("weapon_skin_rare")) {
                        randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, rareSkins[random.nextInt(rareSkins.length)]);
                    }

                    if (randomSkin == null) {
                        break;
                    }

                    this.item = GTM.getWeaponSkinManager().createSkinItem(randomWeapon, randomSkin);
                }
                break;

            case ARMOR_UPGRADE:
                this.item = Utils.createItem(Material.LEATHER, "&e&l" + this.armorUpgrade.getDisplayName() + " Upgrade &a&lBUY&f: &a&l$" + this.armorUpgrade.getPrice());
            default:
                break;
        }
        if (hideDurability) {
            ItemMeta itemMeta = this.item.getItemMeta();
            itemMeta.spigot().setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            this.item.setItemMeta(itemMeta);
        }
        if(stackable)
            this.item = ItemStackUtil.makeStackable(item,64);
        if (item != null)
            item.setAmount(amount);
        return this.item == null ? new ItemStack(Material.STONE) : this.item.clone();
    }

    public String getWeaponOrVehicleOrDrug() {
        return this.weaponOrVecicleOrDrug;
    }

    public String getDisplayName() {
        if (this.displayName != null)
            return this.displayName;
        String name = (this.item.hasItemMeta() && this.item.getItemMeta().hasDisplayName()) ? this.item.getItemMeta().getDisplayName()
                : this.item.getType().name();
        String amnt = this.item.getAmount() > 1 ? " &7x &a" + this.item.getAmount() : "";
        return name + amnt;
    }

    public void setDisplayName(String s) {
        this.displayName = s;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public double getBuyPrice() {
        return this.buyPrice;
    }

    public void setSellPrice(double i) {
        this.sellPrice = i;
    }

    public boolean canSell() {
        return this.sellPrice > 0;
    }

    public boolean canBuy() {
        return this.buyPrice > 0;
    }

    public ItemType getType() {
        return this.type;
    }

    public AmmoType getAmmoType() {
        return this.ammoType;
    }

    public ArmorUpgrade getArmorUpgrade() {
        return this.armorUpgrade;
    }

    public boolean getHideDurability() {
        return this.hideDurability;
    }

    public enum ItemType {
        ITEMSTACK, WEAPON, VEHICLE, AMMO, ARMOR_UPGRADE, DRUG, SKIN
    }

}