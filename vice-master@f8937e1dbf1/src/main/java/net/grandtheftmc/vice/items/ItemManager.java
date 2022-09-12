package net.grandtheftmc.vice.items;

import com.j0ach1mmall3.jlib.methods.Parsing;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.GameItem.ItemType;
import net.grandtheftmc.vice.items.recipes.BottleCraftingRecipe;
import net.grandtheftmc.vice.items.recipetypes.BrewingRecipeItem;
import net.grandtheftmc.vice.items.recipetypes.CraftingRecipeItem;
import net.grandtheftmc.vice.items.recipetypes.RecipeItem;
import net.grandtheftmc.vice.users.CopRank;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ItemManager {

    private List<GameItem> items = new ArrayList<>();
    private List<Kit> kits = new ArrayList<>();
    private HashMap<ItemStack, RecipeItem> customRecipes = new HashMap<>();
    private HashMap<ItemStack, ItemStack> replacedVanilla = new HashMap<>();
    private List<Material> bannedCraftingRecipes = new ArrayList<>();
    private HashSet<ItemStack> ingredients = new HashSet<>();
    public final static NamespacedKey NAMESPACED_KEY = new NamespacedKey(Vice.getInstance(), Vice.getInstance().getDescription().getName());
    private LinkedList<GameItem> shopItems = new LinkedList<>();

    public ItemManager() {
        this.loadItems();
        this.loadKits();
        this.loadReplacedVanilla();
        this.loadBannedCraftingRecipes();
        this.loadCraftingRecipes();
    }

    public void m(int i) {
        Bukkit.broadcastMessage(String.valueOf(i));
    }

    public void m(String s) {
        Bukkit.broadcastMessage(s);
    }

    private void loadCraftingRecipes(){
        registerCustomRecipe(new BottleCraftingRecipe());
    }

    private void loadBannedCraftingRecipes() {
        this.bannedCraftingRecipes.add(Material.LEATHER_LEGGINGS);
        this.bannedCraftingRecipes.add(Material.LEATHER_BOOTS);
        this.bannedCraftingRecipes.add(Material.CHAINMAIL_LEGGINGS);
        this.bannedCraftingRecipes.add(Material.CHAINMAIL_BOOTS);
        this.bannedCraftingRecipes.add(Material.GOLD_LEGGINGS);
        this.bannedCraftingRecipes.add(Material.GOLD_BOOTS);
        this.bannedCraftingRecipes.add(Material.IRON_LEGGINGS);
        this.bannedCraftingRecipes.add(Material.IRON_BOOTS);
        this.bannedCraftingRecipes.add(Material.DIAMOND_LEGGINGS);
        this.bannedCraftingRecipes.add(Material.DIAMOND_BOOTS);
        this.bannedCraftingRecipes.add(Material.WOOD_SWORD);
        this.bannedCraftingRecipes.add(Material.STONE_SWORD);
        this.bannedCraftingRecipes.add(Material.IRON_SWORD);
        this.bannedCraftingRecipes.add(Material.GOLD_SWORD);
        this.bannedCraftingRecipes.add(Material.DIAMOND_SWORD);
        this.bannedCraftingRecipes.add(Material.SHIELD);
    }

    private void loadReplacedVanilla() {
        //this.replacedVanilla.put(new ItemStack(Material.CARROT_ITEM), getItem("opiumpoppies").getItem()); OLD VICE
        this.replacedVanilla.put(new ItemStack(Material.SUGAR_CANE), getItem("marijuanaleaf").getItem());
        this.replacedVanilla.put(new ItemStack(Material.MELON), getItem("cocaleaf").getItem());
        this.replacedVanilla.put(new ItemStack(Material.CACTUS), getItem("humuluslupulusfruit").getItem());
        this.replacedVanilla.put(new ItemStack(Material.BEETROOT), getItem("ephedrasinica").getItem());
        this.replacedVanilla.put(new ItemStack(Material.NETHER_STALK), getItem("ergotfungi").getItem());
        this.replacedVanilla.put(new ItemStack(Material.BROWN_MUSHROOM), getItem("magicmushroombrown").getItem());
        this.replacedVanilla.put(new ItemStack(Material.RED_MUSHROOM), getItem("magicmushroomred").getItem());
        //this.replacedVanilla.put(new ItemStack(Material.CHORUS_FRUIT), getItem("safrole_oil").getItem()); OLD VICE
    }


    public void loadItems() {
        this.items = new ArrayList<>();
        this.shopItems = new LinkedList<>();
        YamlConfiguration c = Vice.getSettings().getItemsConfig();
        if (c == null)
            return;
        for (String name : c.getKeys(false)) {
            try {
                String displayName = c.getString(name + ".displayName");
                double price = -1;
                boolean hideDurability = false, canStack = false;
                String shopCategory = "";

                if (c.get(name + ".canStack") != null) canStack = c.getBoolean(name + ".canStack");
                if (c.get(name + ".sellPrice") != null) price = c.getDouble(name + ".sellPrice");
                if (c.get(name + ".hideDurability") != null) hideDurability = c.getBoolean(name + ".hideDurability");
                if(c.get(name + ".shopCategory") !=null) shopCategory = c.getString(name + ".shopCategory");
                if(c.get(name + ".machineID") !=null )
                    this.items.add(new GameItem(name, c.getInt(name + ".machineID"), displayName, price, hideDurability));
                if (c.get(name + ".weapon") != null)
                    this.items.add(new GameItem(ItemType.WEAPON, name, c.getString(name + ".weapon"), displayName, price, hideDurability, canStack));
                else if (c.get(name + ".drug") != null)
                    this.items.add(new GameItem(ItemType.DRUG, name, c.getString(name + ".drug"), displayName, price, hideDurability, canStack));
                else if (c.get(name + ".vehicle") != null)
                    this.items.add(new GameItem(ItemType.VEHICLE, name, c.getString(name + ".vehicle"), displayName, price, hideDurability, canStack));
                else if (c.get(name + ".ammo") != null)
                    this.items.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")),
                            AmmoType.getAmmoType(c.getString(name + ".ammo")), displayName, price, hideDurability));
                else if (c.get(name + ".armorupgrade") != null) {
                    ArmorUpgrade upgrade = ArmorUpgrade.getArmorUpgrade(c.getString(name + ".armorupgrade"));
                    if (upgrade == null)
                        Vice.error("Error while loading items " + name + ": " + c.getString(name + ".ammo") + " is not a valid ArmorUpgrade!");
                    else
                        this.items.add(new GameItem(name, upgrade, displayName, price, hideDurability));
                }
                else if(!shopCategory.equals("")) {//This is done so not all items have to be searched when generating the shop.
                    this.shopItems.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")), displayName, price, hideDurability, canStack, shopCategory));
                    this.items.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")), displayName, price, hideDurability, canStack, shopCategory));
                }
                else if (c.get(name + ".item") != null)
                    this.items.add(new GameItem(name, Parsing.parseItemStack(c.getString(name + ".item")), displayName, price, hideDurability, canStack));
            } catch (Exception e) {
                Vice.error("Error while loading item " + name + '!');
                e.printStackTrace();
            }
        }
    }

    public void saveItems() {
        YamlConfiguration c = Vice.getSettings().getItemsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (GameItem item : this.items) {
            String name = item.getName();
            if (item.canStack()) c.set(name + ".canStack", item.canStack());
            c.set(name + ".displayName", item.getDisplayName());
            if (item.getSellPrice() > 0)                 c.set(name + ".sellPrice", item.getSellPrice());

            if (item.getHideDurability())                 c.set(name + ".hideDurability", item.getHideDurability());

            if(item.getShopCategory()!=null) c.set(name + ".shopCategory", item.getShopCategory());

            if(item.getType() == ItemType.MACHINE) {
                c.set(name + ".machineID", item.getMachineID());
            }
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
        YamlConfiguration c = Vice.getSettings().getKitsConfig();
        this.kits = new ArrayList<>();
        for (String name : c.getKeys(false)) {
            try {
                double cost = 0;
                int delay = 60;
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
                this.kits.add(new Kit(name, cost, delay, contents, helmet, chestplate, leggings, boots, offHand, perm));
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
        YamlConfiguration c = Vice.getSettings().getKitsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (Kit kit : this.kits) {
            String name = kit.getName();
            try {
                if (kit.getCost() > 0)
                    c.set(name + ".cost", kit.getCost());
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

    public LinkedList<GameItem> getShopItems() {
        return this.shopItems;
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
                    if(g.getType()== ItemType.WEAPON && g.getItem().getItemMeta().getDisplayName().contains("»") && g.getItem().getItemMeta().getDisplayName().contains("«")) {
                        String a = ChatColor.stripColor(g.getItem().getItemMeta().getDisplayName());
                        String b = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("/0", "");
                        if(!a.equalsIgnoreCase(b)) {
                            namesMatch = false;
                        }
                    }
                }
                return g.getItem().getType() == item.getType() && g.getItem().getDurability() == item.getDurability() && namesMatch;
            }).findFirst().orElse(null);
        return null;
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

    /**
     * @param damagableItem if the item can be damaged during normal use, ex. Armor
     */
    public GameItem getSellableItem(ItemStack is, boolean damagableItem) {
        return this.items.stream().filter(g -> g.canSell() && g.getItem().getType()==is.getType() && (damagableItem || g.getItem().getDurability() == is.getDurability())).findFirst().orElse(null);
    }

    public GameItem getSellableItem(ItemStack is) {
        return getSellableItem(is, false);
    }



    public List<Kit> getKits() {
        return this.kits;
    }

    public boolean giveKit(Player player, User user, ViceUser viceUser, String kit){
        return giveKit(player, user, viceUser, this.getKit(kit));
    }

    public boolean giveKit(Player player, User user, ViceUser viceUser, Kit kit) {
        if (viceUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't get a kit in jail!"));
            return true;
        }
        if (kit == null) {
            player.sendMessage(Utils.f(Lang.KITS.f("&cThat kit does not exist!")));
            return false;
        }
        if (kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
            player.sendMessage(Lang.KITS.f("&7You don't have permission to use this kit!"));
            return false;
        }
        UserRank ur = UserRank.getUserRankOrNull(kit.getName());
        ViceRank rank = ViceRank.getRankOrNull(kit.getName());
        CopRank copRank = CopRank.getRankOrNull(kit.getName());
        if (ur == null && copRank != null && !viceUser.isCopRank(copRank)) {
            player.sendMessage(Lang.KITS.f("&7You need to be a " + copRank.getColoredNameBold()
                    + "&7 to use this kit!"));
            return false;
        } else if (ur != null && !(ur == user.getUserRank()
                || (ur == UserRank.SUPREME && user.getUserRank().isHigherThan(UserRank.SUPREME)))) {
            player.sendMessage(
                    Lang.KITS.f("&7You need to be " + ur.getColoredNameBold() + "&7 to use this kit!"));
            return false;
        } else if (rank != null && !(rank == ViceRank.JUNKIE || rank == viceUser.getRank())) {
            player.sendMessage(Lang.KITS.f("&7You need to rank up to " + rank.getColoredNameBold()
                    + "&7 to use this kit!"));
            return false;
        }
        if (kit.getCost() > 0 && !viceUser.hasMoney(kit.getCost())) {
            player.sendMessage(
                    Lang.KITS.f("&7You do not have the &c$&l" + kit.getCost() + "&7 to pay for this kit!"));
            return false;
        }
        if (!viceUser.canUseKit(kit.getName())) {
            player.sendMessage(Lang.KITS.f("&7You need to wait &c"
                    + Utils.timeInMillisToText(viceUser.getKitExpiry(kit.getName()) - System.currentTimeMillis())
                    + "&7 to use this kit again!"));
            return false;
        }
        viceUser.setKitExpiry(kit.getName(), kit.getDelay());
        if (kit.getCost() > 0) {
            viceUser.takeMoney(kit.getCost());
            player.sendMessage(Lang.MONEY_TAKE.toString() + kit.getCost());
            ViceUtils.updateBoard(player, user, viceUser);
        }
        player.sendMessage(Lang.KITS.f("&7You received the kit &b" + kit.getName() + "&7!"));
        this.giveKitItems(player, viceUser, kit);
        return true;
    }

    public void giveKitItems(Player player, ViceUser viceUser, Kit kit) {
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
                viceUser.addAmmo(type, stack.getAmount());
                player.sendMessage(Lang.AMMO_ADD.f(stack.getAmount() + "&7 " + type.getGameItem().getDisplayName()));
                items.remove(stack);
            }
        }
        if (Utils.giveItems(player, Utils.toArray(items)))
            player.sendMessage(Utils.f(Lang.KITS + "&cYour inventory was full so some items were dropped on the ground!"));
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

    public HashMap<ItemStack, RecipeItem> getCustomRecipes() {
        return this.customRecipes;
    }

    public HashMap<ItemStack, ItemStack> getReplacedVanilla() {
        return this.replacedVanilla;
    }


    @Deprecated
    public void registerCustomRecipe(RecipeItem item) {
        switch (item.getType()) {
            case SHAPED_CRAFTING:
            case FURNACE:
            case SHAPELESS_CRAFTING:
                //shaped / shapeless crafting doesnt work.
                this.customRecipes.put(((CraftingRecipeItem) item).getRecipe().getResult(), item);
                break;
            case BREWING:
                this.customRecipes.put(((BrewingRecipeItem) item).getResult(), item);
                this.ingredients.add(((BrewingRecipeItem)item).getIngredient());
                break;
        }
    }

    public Optional<RecipeItem> getCraftingRecipe(ItemStack[] matrix) {
        if (matrix == null)
            return Optional.empty();
        return this.customRecipes.values().stream().filter(recipeItem -> {
            if (!(recipeItem instanceof CraftingRecipeItem)) {
                return false;
            }
            CraftingRecipeItem recipe = (CraftingRecipeItem) recipeItem;
            return recipe.validate(matrix);
        }).findFirst();
    }

    public List<Material> getBannedCraftingRecipes() {
        return bannedCraftingRecipes;
    }

    public HashSet<ItemStack> getPotionIngredients() {
        return ingredients;
    }
}
