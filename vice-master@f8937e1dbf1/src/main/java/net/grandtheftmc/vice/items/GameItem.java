package net.grandtheftmc.vice.items;

import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import net.grandtheftmc.vice.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameItem {

    private final ItemType type;
    private final String name;
    private ItemStack item;
    private String weaponOrVecicleOrDrug;
    private String displayName;
    private String shopCategory;
    private double sellPrice;
    private AmmoType ammoType;
    private ArmorUpgrade armorUpgrade;
    private boolean hideDurability;
    private boolean canStack;
    private int machineID;

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

    public GameItem(String name, ItemStack item, String displayName, double sellPrice, boolean hideDurability, boolean canStack, String category) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.shopCategory = category;
        this.item = item;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.canStack = canStack;
    }

    public GameItem(String name, ItemStack item, String displayName, double sellPrice, boolean hideDurability, boolean canStack) {
        this.type = ItemType.ITEMSTACK;
        this.name = name;
        this.item = item;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.canStack = canStack;
    }

    public GameItem(String name, ItemStack item, AmmoType type, String displayName, boolean hideDurability, boolean canStack) {
        this.type = ItemType.AMMO;
        this.name = name;
        this.item = item;
        this.ammoType = type;
        this.displayName = displayName;
        this.hideDurability = hideDurability;
        this.canStack = canStack;
    }

    public GameItem(String name, ItemStack item, AmmoType type, String displayName, double sellPrice, boolean hideDurability) {
        this.type = ItemType.AMMO;
        this.name = name;
        this.item = item;
        this.ammoType = type;
        this.displayName = displayName;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
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

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, double sellPrice) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.sellPrice = sellPrice;
        this.hideDurability = false;
        this.getItem();
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, double sellPrice,
                    boolean hideDurability) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.getItem();
    }

    /**
     * @apiNote used soley for machines.
     */
    public GameItem(String name, int machineID,  String displayName, double sellPrice, boolean hideDurability) {
        this.type = ItemType.MACHINE;
        this.name = name;
        this.displayName = displayName;
        this.machineID = machineID;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.getItem();
    }

    public GameItem(ItemType type, String name, String weaponOrVecicleOrDrug, String displayName, double sellPrice,
                    boolean hideDurability, boolean canStack) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.weaponOrVecicleOrDrug = weaponOrVecicleOrDrug;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.canStack = canStack;
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

    public GameItem(String name, ArmorUpgrade armorUpgrade, String displayName, double sellPrice) {
        this.type = ItemType.ARMOR_UPGRADE;
        this.name = name;
        this.displayName = displayName;
        this.armorUpgrade = armorUpgrade;
        this.sellPrice = sellPrice;
        this.hideDurability = false;
        this.getItem();
    }

    public GameItem(String name, ArmorUpgrade armorUpgrade, String displayName, double sellPrice, boolean hideDurability) {
        this.type = ItemType.ARMOR_UPGRADE;
        this.name = name;
        this.displayName = displayName;
        this.armorUpgrade = armorUpgrade;
        this.sellPrice = sellPrice;
        this.hideDurability = hideDurability;
        this.getItem();
    }//

    public String getName() {
        return this.name;
    }

    public int getMachineID() {
        return this.machineID;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int amount) {
        switch (this.type) {
            case WEAPON: {
                Optional<Weapon<?>> opt = Vice.getWastedGuns().getWeaponManager().getWeapon(this.weaponOrVecicleOrDrug);
                opt.ifPresent(weapon -> {
                    this.item = weapon.createItemStack(1, null);
                });
                break;
            }
            case DRUG: {
                Optional<Drug> drug = ((DrugService) Vice.getDrugManager().getService()).getDrug(this.weaponOrVecicleOrDrug);
                if (drug.isPresent()) {
                    DrugItem drugItem = DrugItem.getByDrug(drug.get());
                    if (drugItem != null) {
                        this.item = ItemStackUtil.makeStackable(drugItem.getItemStack(), 64);
                    }
                }
                else {
                    this.item = Parsing.parseItemStack(this.weaponOrVecicleOrDrug);
                }
                break;
            }
            case MACHINE: {
                if(Vice.getInstance().getMachineManager()==null || Vice.getInstance().getMachineManager().getMachineItemById(this.machineID)==null)
                    this.item = new ItemStack(Material.STONE);
                else
                    this.item = Vice.getInstance().getMachineManager().getMachineItemById(this.machineID);
                break;
            }
            case VEHICLE:
                Optional<VehicleProperties> opt = Vice.getWastedVehicles().getVehicle(this.weaponOrVecicleOrDrug);
                opt.ifPresent(vehicleProperties -> {
                    this.item = vehicleProperties.getItem();
                });
                break;
            case ARMOR_UPGRADE:
                this.item = Utils.createItem(Material.LEATHER, "&e&l" + this.armorUpgrade.getDisplayName() + " Upgrade &a&lBUY&f: &a&l$" + this.armorUpgrade.getPrice());
            default:
                break;
        }
        if (this.item == null) this.item = new ItemStack(Material.STONE);
        if (this.canStack) this.item = ItemStackUtil.makeStackable(this.item, 64);

        if (this.hideDurability) {
            ItemMeta itemMeta = this.item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            this.item.setItemMeta(itemMeta);
        }
        if (this.isScheduled()) {
            ItemMeta itemMeta = this.item.getItemMeta();
            if (itemMeta.hasLore()) {
                if (!Objects.equals(itemMeta.getLore().get(0), this.getSchedule().getDisp())) {
                    List<String> lore = new ArrayList<>();
                    lore.add(this.getSchedule().getDisp());
                    lore.addAll(itemMeta.getLore());
                    itemMeta.setLore(lore);
                }
            } else itemMeta.setLore(Collections.singletonList(this.getSchedule().getDisp()));
            this.item.setItemMeta(itemMeta);
        }
        ItemStack is = this.item.clone();
        is.setAmount(amount);
        return is;
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

    public String getShopCategory() {
        return this.shopCategory;
    }

    public void setDisplayName(String s) {
        this.displayName = s;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public void setSellPrice(double i) {
        this.sellPrice = i;
    }

    public boolean canSell() {
        return this.sellPrice > 0;
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

    public boolean canStack() {
        return canStack;
    }

    public Schedule getSchedule() {
        return Schedule.of(this.name);
    }

    public boolean isScheduled() {
        return this.getSchedule() != Schedule.NONE;
    }

    public enum ItemType {
        ITEMSTACK, WEAPON, VEHICLE, AMMO, ARMOR_UPGRADE, DRUG, MACHINE
    }

}