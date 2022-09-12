package net.grandtheftmc.vice.items;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.LockedWeapon;
import net.grandtheftmc.vice.users.ViceUserDAO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ShopManager {

    public ShopManager() {
        this.load();
        this.startSchedule();
    }


    private List<Head> heads = new ArrayList<>();

    private void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Head> heads = ViceUserDAO.getHeads();
//                try (ResultSet rs = Core.sql.query("select * from " + Core.name() + "_heads;")) {
//                    while (rs.next()) {
//                        UUID sellerUUID = null;
//                        UUID bidderUUID = null;
//                        try {
//                            sellerUUID = rs.getString("sellerUUID") == null ? null : UUID.fromString(rs.getString("sellerUUID"));
//                            bidderUUID = rs.getString("bidderUUID") == null ? null : UUID.fromString(rs.getString("bidderUUID"));
//                        } catch (Exception ignored) {
//                        }
//                        heads.add(new Head(sellerUUID, rs.getString("sellerName"),
//                                rs.getString("head"), rs.getLong("expiry"), rs.getBoolean("done"), rs.getBoolean("paid"), rs.getBoolean("gaveHead"), bidderUUID,
//                                rs.getString("bidderName"), rs.getDouble("bid")));
//                    }
//                    rs.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vice.getShopManager().setHeads(heads);
                    }
                }.runTask(Vice.getInstance());
            }
        }.runTaskAsynchronously(Vice.getInstance());
    }

    private void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                new ArrayList<>(Vice.getShopManager().getHeads()).forEach(Head::update);
            }
        }.runTaskTimer(Vice.getInstance(), 200L, 200L);
    }


    public void setHeads(List<Head> list) {
        this.heads = list;
    }

    public List<Head> getHeads() {
        return this.heads;
    }

    public void removeHead(Head head) {
        this.heads.remove(head);
    }

    public List<Head> getNonExpiredHeads() {
        return this.heads.stream().filter(h -> !h.hasExpired()).collect(Collectors.toList());
    }

    public Set<Head> getNonExpiredHeadsByBid() {
        HashMap<Head, Double> unsortMap = new HashMap<>();
        this.heads.stream().filter(h -> !h.hasExpired()).collect(Collectors.toList()).forEach(h -> unsortMap.put(h, h.getBid()));
        return sort(unsortMap).keySet();
    }

    public static Map<Head, Double> sort(Map<Head, Double> unsortMap) {
        List<Map.Entry<Head, Double>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Comparator.comparing(obj -> obj.getValue()));
        Map<Head, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Head, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Head getHead(String head) {
        return this.heads.stream().filter(h -> h.getHead().equalsIgnoreCase(head)).findFirst().orElse(null);
    }

    public Head getHead(String head, long expiry) {
        return this.heads.stream().filter(h -> h.getHead().equalsIgnoreCase(head) && h.getExpiry() == expiry).findFirst().orElse(null);
    }

    public Head auctionHead(Player seller, ViceUser user) {
        ItemStack item = seller.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.SKULL_ITEM || item.getDurability() != 3) {
            seller.sendMessage(Lang.HEAD_AUCTION.f("&7That's not a player head!"));
            return null;
        }
        if (item.getAmount() > 1) {
            seller.sendMessage(Lang.HEAD_AUCTION.f("&7Please sell auction head at a time!"));
            return null;
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta.getOwner() == null) {
            seller.sendMessage(Lang.HEAD_AUCTION.f("&7That's not a player head!"));
            return null;
        }
        Head head = new Head(seller.getUniqueId(), seller.getName(), meta.getOwner());
        this.heads.add(head);
        seller.sendMessage(Lang.HEAD_AUCTION.f("&7You received &a$&l10,000&7 for putting up &e&l" + head.getHead() + "'s Head&7 for auction."));
        user.addMoney(10000);
        ViceUtils.updateBoard(seller, user);
        seller.getInventory().setItemInMainHand(null);
        return head;
    }

    public void buy(Player player, ItemStack i) {
        if (i == null || !i.hasItemMeta() || !i.getItemMeta().hasDisplayName())
            return;
        String disp = ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase();
        if (!disp.contains("buy: $"))
            return;
        disp = disp.replace("buy: $", "");
        String[] array = disp.split(" ");
        if (array.length == 1)
            return;
        int amount = 1;
        double buyPrice;
        boolean hasAmount = array[0].endsWith("x");
        StringBuilder itemName = new StringBuilder(array[hasAmount ? 1 : 0]);
        for (int n = hasAmount ? 2 : 1; n < (array.length - 1); n++)
            itemName.append(' ').append(array[n]);
        try {
            if (hasAmount)
                amount = Integer.parseInt(array[0].replaceAll("x", ""));
            buyPrice = Double.parseDouble(array[array.length - 1]);
        } catch (NumberFormatException e) {
            for (String anArray : array) player.sendMessage(anArray);
            player.sendMessage(Utils.f(Lang.SHOP
                    + "&cThere was an error while parsing the prices for this shop. Please contact a staff member."));
            return;
        }
        GameItem item = Vice.getItemManager().getItemFromDisplayName(itemName.toString());
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't buy items in jail!"));
            return;
        }
        if(item==null)
            return;
        if (item.getType() == GameItem.ItemType.WEAPON) {
            LockedWeapon l = LockedWeapon.getWeapon(item.getWeaponOrVehicleOrDrug());
            if (l != null && !l.canUseWeapon(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + l.getViceRank().getColoredNameBold() + "&7 or donate for " + l.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use the " + item.getDisplayName() + "&7!"));
                return;
            }
        } else if (item.getType() == GameItem.ItemType.ARMOR_UPGRADE) {
            player.sendMessage(Lang.HEY.f("&7There is an error with this shop. Please contact an administrator."));
            return;
        }
        if (user.hasMoney(buyPrice)) {
            user.takeMoney(buyPrice);
        } else {

                player.sendMessage(Lang.MONEY.f("&7You do not have enough money!"));
                return;

        }
        ViceUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
        switch (item.getType()) {
            case VEHICLE:
                user.setActionVehicle(item.getWeaponOrVehicleOrDrug());
                MenuManager.openMenu(player, "vehicleshop");
                return;
            case AMMO:
                AmmoType type = item.getAmmoType();
                if (type != null)
                    user.addAmmo(type, amount);
                player.sendMessage(Lang.SHOP.f("&7You bought " + (amount > 1 ? "&a&l" + amount + "&7x " : "")
                        + item.getDisplayName() + "&7 for &a$&l" + buyPrice + "&7!"));
                return;
            case WEAPON:
            case ITEMSTACK:
                ItemStack stack = item.getItem();
                stack.setAmount(amount);
                Utils.giveItems(player, stack);
                player.sendMessage(Lang.SHOP.f("&7You bought " + (amount > 1 ? "&a&l" + amount + "&7x " : "")
                        + item.getDisplayName() + "&7 for &a$&l" + buyPrice + "&7!"));
                break;
            default:
                break;
        }
    }

    public void buyArmorUpgrade(Player player, String disp) {
        disp = disp.toLowerCase();
        disp = ChatColor.stripColor(disp);
        ArmorUpgrade upgrade = ArmorUpgrade.getArmorUpgradeFromDisplayName(disp.split(" upgrade")[0]);
        if (upgrade == null) return;
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (!upgrade.canUseUpgrade(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
            player.sendMessage(Lang.HEY.f("&7You need to rank up to " + upgrade.getViceRank().getColoredNameBold() + "&7 or donate for " + upgrade.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        GameItem gameItem = item == null ? null : Vice.getItemManager().getItem(item.getType());
        if (item == null || gameItem == null || !upgrade.canBeUsedOn(gameItem.getName())) {
            player.sendMessage(Lang.HEY.f("&7The &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7 can be applied to the following types of items: " + upgrade.getTypesString() + "&7!"));
            return;
        }
        for (Enchantment e : item.getEnchantments().keySet())
            Utils.b(e.getName());
        if (ArmorUpgrade.getArmorUpgrades(item).contains(upgrade)) {
            player.sendMessage(Lang.ARMOR_UPGRADE.f("&7That piece of armor already has the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
            return;
        }
        if (!user.hasMoney(upgrade.getPrice())) {
            player.sendMessage(Lang.ARMOR_UPGRADE.f("&7You can't afford the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
            return;
        }
        user.setBuyingArmorUpgrade(upgrade);
        MenuManager.openMenu(player, "armorupgrade");
    }

    public void addShop(Player player, String itemName, int amount, double buyPrice) {
        GameItem item = Vice.getItemManager().getItem(itemName);
        if (item == null) {
            player.sendMessage(Lang.SHOP.f("&cThat items does not exist!"));
            return;
        }
        if (buyPrice < 0) {
            player.sendMessage(Lang.SHOP.f("&7The price must be 0 or higher!"));
            return;
        }
        ItemStack i = item.getItem().clone();
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(Utils.f("&a&l" + amount + "&7x " + item.getDisplayName() + " &a&lBUY&f: &a$&l" + buyPrice));
        i.setItemMeta(meta);
        i.setAmount(1);
        player.getInventory().addItem(i);
        player.sendMessage(Lang.SHOP.f("&7Please add the items into an itemframe to create a shop!"));
    }

    public void addArmorUpgradeShop(Player player, String name) {
        ArmorUpgrade armorUpgrade = ArmorUpgrade.getArmorUpgrade(name);
        if (armorUpgrade == null) {
            player.sendMessage(Lang.SHOP.f("&cThat armor upgrade does not exist!"));
            return;
        }
        GameItem item = Vice.getItemManager().getItem(armorUpgrade);
        if (item == null) {
            player.sendMessage(Lang.SHOP.f("&cThat game items does not exist!"));
            return;
        }
        ItemStack i = item.getItem().clone();
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(Utils.f("&b&lArmor Upgrade: &a&l" + armorUpgrade.getDisplayName()));
        i.setItemMeta(meta);
        i.setAmount(1);
        player.getInventory().addItem(i);
        player.sendMessage(Lang.SHOP.f("&7Please add the items into an itemframe to create a shop!"));
    }


}
