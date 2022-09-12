package net.grandtheftmc.gtm.items;

import com.j0ach1mmall3.jlib.methods.Parsing;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.GameItem.ItemType;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager {

    private List<GameItem> items = new ArrayList<>();
    private List<Kit> kits = new ArrayList<>();

    public ItemManager() {
        this.loadItems();
        this.loadKits();
    }

    public void m(int i) {
        Bukkit.broadcastMessage(String.valueOf(i));
    }

    public void m(String s) {
        Bukkit.broadcastMessage(s);
    }

    public void loadItems() {
        this.items = new ArrayList<>();
        YamlConfiguration c = GTM.getSettings().getItemsConfig();
        if (c == null)
            return;
        for (String name : c.getKeys(false)) {
            try {
                String displayName = c.getString(name + ".displayName");
                double sellPrice = -1, buyPrice = -1;
                boolean hideDurability = false, stackable = false;
                if (c.get(name + ".sellPrice") != null) sellPrice = c.getDouble(name + ".sellPrice");
                if (c.get(name + ".buyPrice") != null) buyPrice = c.getDouble(name + ".buyPrice");
                if (c.get(name + ".hideDurability") != null) hideDurability = c.getBoolean(name + ".hideDurability");
                if(c.get(name + ".stackable") !=null) stackable = c.getBoolean(name + ".stackable");

                if (displayName.equals("weapon_skin_common") || displayName.equals("weapon_skin_common")
                        || displayName.equals("weapon_skin_common") || displayName.equals("weapon_skin_common")) {
                    this.items.add(new GameItem(ItemType.SKIN, name, "null", displayName, sellPrice, buyPrice, hideDurability));
                    continue;
                }

                if (c.get(name + ".weapon") != null)
                    this.items.add(new GameItem(ItemType.WEAPON, name, c.getString(name + ".weapon"), displayName, sellPrice, buyPrice, hideDurability));
                else if (!Core.getSettings().isSister() && c.get(name + ".drug") != null)
                    this.items.add(new GameItem(ItemType.DRUG, name, c.getString(name + ".drug"), displayName, sellPrice, buyPrice, hideDurability));
                else if (c.get(name + ".vehicle") != null)
                    this.items.add(new GameItem(ItemType.VEHICLE, name, c.getString(name + ".vehicle"), displayName, sellPrice, buyPrice, hideDurability));
                else if (c.get(name + ".ammo") != null)
                    this.items.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")),
                            AmmoType.getAmmoType(c.getString(name + ".ammo")), displayName, sellPrice, buyPrice));
                else if (c.get(name + ".armorupgrade") != null) {
                    ArmorUpgrade upgrade = ArmorUpgrade.getArmorUpgrade(c.getString(name + ".armorupgrade"));
                    if (upgrade == null)
                        GTM.error("Error while loading item " + name + ": " + c.getString(name + ".ammo") + " is not a valid ArmorUpgrade!");
                    else
                        this.items.add(new GameItem(name, upgrade, displayName, sellPrice, buyPrice));
                } else if (c.get(name + ".item") != null)
                    this.items.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")), displayName, sellPrice, buyPrice, hideDurability, stackable));
            } catch (Exception e) {
                GTM.error("Error while loading item " + name + '!');
                e.printStackTrace();
            }
        }
    }

    public void saveItems() {
        YamlConfiguration c = GTM.getSettings().getItemsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (GameItem item : this.items) {
            String name = item.getName();
            c.set(name + ".displayName", item.getDisplayName());
            if (item.getSellPrice() > 0) {
                c.set(name + ".sellPrice", item.getSellPrice());
            }
            if (item.getBuyPrice() > 0) {
                c.set(name + ".buyPrice", item.getBuyPrice());
            }
            if (item.getHideDurability()) {
                c.set(name + ".hideDurability", item.getHideDurability());
            }
            if(item.isStackable())
                c.set(name + ".stackable", item.isStackable());
            if (item.getType() == ItemType.WEAPON) {
                c.set(name + ".weapon", item.getWeaponOrVehicleOrDrug());
            } else if (item.getType() == ItemType.DRUG) {
                c.set(name + ".drug", item.getWeaponOrVehicleOrDrug());
            } else if (item.getType() == ItemType.VEHICLE) {
                c.set(name + ".vehicle", item.getWeaponOrVehicleOrDrug());
            } else if (item.getType() == ItemType.AMMO) {
                c.set(name + ".ammo", item.getAmmoType().toString().toLowerCase());
                c.set(name + ".item", Parsing.parseString(item.getItem()));
            } else if (item.getType() == ItemType.ARMOR_UPGRADE) {
                c.set(name + ".armorupgrade", item.getArmorUpgrade().toString().toLowerCase());
            } else {
                c.set(name + ".item", Parsing.parseString(item.getItem()));
            }
        }
        Utils.saveConfig(c, "items");
    }

    public void loadKits() {
        YamlConfiguration c = GTM.getSettings().getKitsConfig();
        this.kits = new ArrayList<>();
        for (String name : c.getKeys(false)) {
            try {
                double cost = 0;
                CheatCode code = null;
                int delay = 60;
                if(c.get(name + ".cheatcode") !=null)
                    code = CheatCode.valueOf(c.getString(name  + ".cheatcode"));
                if (c.get(name + ".cost") != null)
                    cost = c.getDouble(name + ".cost");
                if (c.get(name + ".delay") != null)
                    delay = c.getInt(name + ".delay");
                List<KitItem> contents = c.getStringList(name + ".contents").stream().map(this::kitItemFromString).collect(Collectors.toList());
                KitItem helmet = this.kitItemFromString(c.getString(name + ".helmet"));
                KitItem chestplate = this.kitItemFromString(c.getString(name + ".chestplate"));
                KitItem leggings = this.kitItemFromString(c.getString(name + ".leggings"));
                KitItem boots = this.kitItemFromString(c.getString(name + ".boots"));
                KitItem offHand = this.kitItemFromString(c.getString(name + ".offHand"));
                String perm = c.getString(name + ".permission");
                this.kits.add(new Kit(name, cost, delay, contents, helmet, chestplate, leggings, boots, offHand, perm, code));
            } catch (Exception e) {
                Core.error("Error while loading kit " + name);
                e.printStackTrace();
            }
        }
    }

    public KitItem kitItemFromString(String s) {
        if (s == null)
            return null;
        String[] a = s.split(":");
        if (a.length == 0)
            return null;
        GameItem item = this.getItem(a[0]);
        try {
            return new KitItem(item, a.length > 1 ? Integer.parseInt(a[1]) : 1);
        } catch (NumberFormatException e) {
            Core.error("Error parsing kititem: " + s);
            return null;
        }

    }

    public void saveKits() {
        YamlConfiguration c = GTM.getSettings().getKitsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (Kit kit : this.kits) {
            String name = kit.getName();
            try {
                if (kit.getCost() > 0)
                    c.set(name + ".cost", kit.getCost());
                if(kit.getCode()!=null)
                    c.set(name + ".cheatcode", kit.getCode().toString());
                if (kit.getDelay() > 0)
                    c.set(name + ".delay", kit.getDelay());
                List<String> contents = kit.getContents().stream().map(this::kitItemToString).collect(Collectors.toList());
                c.set(name + ".contents", contents);
                c.set(name + ".helmet", this.kitItemToString(kit.getHelmet()));
                c.set(name + ".chestplate", this.kitItemToString(kit.getChestPlate()));
                c.set(name + ".leggings", this.kitItemToString(kit.getLeggings()));
                c.set(name + ".boots", this.kitItemToString(kit.getBoots()));
                c.set(name + ".offHand", this.kitItemToString(kit.getOffHand()));
                c.set(name + ".permission", kit.getPermission());
            } catch (Exception e) {
                Core.error("Error while saving kit " + name);
                e.printStackTrace();
            }
        }
        Utils.saveConfig(c, "kits");
    }

    public String kitItemToString(KitItem item) {
        if (item == null || item.getGameItem() == null)
            return null;
        return item.getGameItem().getName() + (item.getAmount() > 1 ? ":" + item.getAmount() : "");
    }

    public GameItem getItem(String itemName) {
        return this.items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName)).findFirst().orElse(null);

    }

    public GameItem getItemFromDisplayName(String itemName) {
        return this.items.stream().filter(item -> ChatColor.stripColor(Utils.f(item.getDisplayName())).equalsIgnoreCase(ChatColor.stripColor(Utils.f(itemName)))).findFirst().orElse(null);
    }

    public GameItem getItem(ItemStack item) {
        if (item != null)
			return this.items.stream().filter(g -> {
                boolean namesMatch = true;
                if(g.getItem().hasItemMeta() && g.getItem().getItemMeta().hasDisplayName() && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if(g.getType() == ItemType.WEAPON && /*g.getItem().getItemMeta().getDisplayName().contains("»") && */g.getItem().getItemMeta().getDisplayName().contains("«")) {
                        String a = ChatColor.stripColor(g.getItem().getItemMeta().getDisplayName()).split("«")[0];
                        String b = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split("«")[0]/*.replace("/0", "")*/;
                        if(!a.equalsIgnoreCase(b)) {
                            namesMatch = false;
                        }
                    }
                }
                return g.getItem().getType() == item.getType() && g.getItem().getDurability() == item.getDurability() && namesMatch;
            }).findFirst().orElse(null);        return null;
    }

    public GameItem getSellableItem(ItemStack is, int range) {
        return this.items.stream().filter(g ->
                        g.canSell() &&
                        g.getType() == ItemType.WEAPON &&
                        g.getItem().getType() == is.getType() &&
                        is.getDurability() > g.getItem().getDurability() &&
                        is.getDurability() < g.getItem().getDurability() + range
        ).findFirst().orElse(null);
    }

    /**
     * @param damagableItem if the item can be damaged during normal use, ex. Armor
     */
    public GameItem getSellableItem(ItemStack is, boolean damagableItem) {
        return this.items.stream().filter(g -> g.canSell() && g.getItem().getType()==is.getType() && (damagableItem || g.getItem().getDurability() == is.getDurability())).findFirst().orElse(null);
    }

    public GameItem getSellableItem(ItemStack is) {
        return getSellableItem(is, false);
    }

    public GameItem getItem(Material material) {
        return this.items.stream().filter(g -> g.getItem().getType() == material).findFirst().orElse(null);
    }

    public GameItem getItemFromWeapon(String s) {
        return this.items.stream().filter(g -> g.getType() == ItemType.WEAPON && g.getWeaponOrVehicleOrDrug().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public GameItem getItemFromVehicle(String s) {
        return this.items.stream().filter(g -> g.getType() == ItemType.VEHICLE && g.getWeaponOrVehicleOrDrug().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public GameItem getItem(ArmorUpgrade upgrade) {
        return this.items.stream().filter(g -> g.getType() == ItemType.ARMOR_UPGRADE && upgrade == g.getArmorUpgrade()).findFirst().orElse(null);
    }


    public List<Kit> getKits() {
        return this.kits;
    }

    public boolean giveKit(Player player, User user, GTMUser gtmUser, String name) {
        if (gtmUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't get a kit in jail!"));
            return true;
        }
        Kit kit = this.getKit(name);
        if (kit == null) {
            player.sendMessage(Utils.f(Lang.KITS.f("&cThat kit does not exist!")));
            return false;
        }
        if (kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
            player.sendMessage(Lang.KITS.f("&7You don't have permission to use this kit!"));
            return false;
        }
        if(kit.getCode()!=null && gtmUser.getCheatCodeState(kit.getCode()).getState() == State.LOCKED) {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You haven't unlocked this cheat code yet!"));
            return false;
        }
        JobMode mode = JobMode.getMode(kit.getName());
        UserRank ur = UserRank.getUserRankOrNull(kit.getName());
        GTMRank rank = GTMRank.getRankOrNull(kit.getName());
        if (ur == null && mode != null && mode != gtmUser.getJobMode()) {
            player.sendMessage(Lang.KITS.f("&7You need to be on the " + mode.getColoredNameBold()
                    + "&7 job to use this kit!"));
            return false;
        } else if (ur != null && !(ur == user.getUserRank()
                || (ur == UserRank.SUPREME && user.getUserRank().isHigherThan(UserRank.SUPREME)))) {
            player.sendMessage(
                    Lang.KITS.f("&7You need to be " + ur.getColoredNameBold() + "&7 to use this kit!"));
            return false;
        } else if (rank != null && !(rank == GTMRank.HOBO || rank == gtmUser.getRank()
                || (rank == GTMRank.CRIMINAL && gtmUser.getRank() != GTMRank.HOBO))) {
            player.sendMessage(Lang.KITS.f("&7You need to be rank " + rank.getColoredNameBold()
                    + "&7 to use this kit!"));
            return false;
        }
        if (kit.getCost() > 0 && !gtmUser.hasMoney(kit.getCost())) {
            player.sendMessage(
                    Lang.KITS.f("&7You do not have the &c$&l" + kit.getCost() + "&7 to pay for this kit!"));
            return false;
        }
        if (!gtmUser.canUseKit(kit.getName())) {
            player.sendMessage(Lang.KITS.f("&7You need to wait &c"
                    + Utils.timeInMillisToText(gtmUser.getKitExpiry(kit.getName()) - System.currentTimeMillis())
                    + "&7 to use this kit again!"));
            return false;
        }
        gtmUser.setKitExpiry(kit.getName(), kit.getDelay());
        if (kit.getCost() > 0) {
            gtmUser.takeMoney(kit.getCost());
            player.sendMessage(Lang.MONEY_TAKE.toString() + kit.getCost());
            GTMUtils.updateBoard(player, user, gtmUser);
        }
        player.sendMessage(Lang.KITS.f("&7You received the kit &b" + kit.getName() + "&7!"));
        this.giveKitItems(player, gtmUser, kit);
        return true;
    }

    public void giveKitItems(Player player, GTMUser gtmUser, Kit kit) {
        List<ItemStack> items = new ArrayList<>(kit.getItems());
        ItemStack helmet = this.kitItemToItemStack(kit.getHelmet());
        ItemStack chestPlate = this.kitItemToItemStack(kit.getChestPlate());
        ItemStack leggings = this.kitItemToItemStack(kit.getLeggings());
        ItemStack boots = this.kitItemToItemStack(kit.getBoots());
        ItemStack offHand = this.kitItemToItemStack(kit.getOffHand());
        if (helmet != null)
            if (player.getInventory().getHelmet() == null)
                player.getInventory().setHelmet(helmet);
            else
                items.add(helmet);
        if (chestPlate != null)
            if (player.getInventory().getChestplate() == null)
                player.getInventory().setChestplate(chestPlate);
            else
                items.add(chestPlate);
        if (leggings != null)
            if (player.getInventory().getLeggings() == null)
                player.getInventory().setLeggings(leggings);
            else
                items.add(leggings);
        if (boots != null)
            if (player.getInventory().getBoots() == null)
                player.getInventory().setBoots(boots);
            else
                items.add(boots);
        if (offHand != null)
            if (player.getInventory().getItemInOffHand() == null)
                player.getInventory().setItemInOffHand(offHand);
            else
                items.add(offHand);
        for (ItemStack stack : new ArrayList<>(items)) {
            AmmoType type = AmmoType.getAmmoType(stack.getType(), stack.getDurability());
            if (type != null && !type.isInInventory()) {
                gtmUser.addAmmo(type, stack.getAmount());
                player.sendMessage(Lang.AMMO_ADD.f(stack.getAmount() + "&7 " + type.getGameItem().getDisplayName()));
                items.remove(stack);
            }
        }
        if (Utils.giveItems(player, Utils.toArray(items)))
            player.sendMessage(
                    Utils.f(Lang.KITS + "&cYour inventory was full so some items were dropped on the ground!"));
    }

    public ItemStack kitItemToItemStack(KitItem item) {
        if (item == null || item.getGameItem() == null)
            return null;
        ItemStack i = item.getGameItem().getItem();
        i.setAmount(item.getAmount());
        return i;
    }

    public Kit getKit(String name) {
        return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public GameItem addItem(GameItem gameItem) {
        this.items.add(gameItem);
        return gameItem;
    }

    public void removeItem(GameItem gi) {
        this.items.remove(gi);

    }

    public void addKit(Kit kit) {
        this.kits.add(kit);
    }

    public List<GameItem> getItems() {
        return this.items;
    }


}
