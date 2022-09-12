package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.events.ArmorEquipEvent;
import net.grandtheftmc.vice.events.EquipArmorType;
import net.grandtheftmc.vice.items.ArmorType;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.items.Kit;
import net.grandtheftmc.vice.lootcrates.LootCrate;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.grandtheftmc.vice.utils.ParticleColor;
import net.grandtheftmc.vice.world.ZoneFlag;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Interact implements Listener {
    private static final Class BLOCK_POSITION_CLASS = ReflectionAPI.getNmsClass("BlockPosition");

    @EventHandler
    protected final void onNPCInteract(NPCRightClickEvent event) {
        switch (ChatColor.stripColor(event.getNPC().getName()).toLowerCase()) {
            case "carl": {
                Player player = event.getClicker();
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.isCop()) {
                    player.sendMessage(Lang.COPS.f("&7Hey! You're not a cop, why should I pay you?"));
                    return;
                }
                Vice.getItemManager().giveKit(player, u, user, "cop");
                if (user.getLastCopSalary() + 86400000 > System.currentTimeMillis()) {
                    player.sendMessage(Lang.COPS.f("&7Please wait &a" + Utils.timeInMillisToText(user.getLastCopSalary() + 86400000 - System.currentTimeMillis()) + "&7 before claiming your daily salary!"));
                    return;
                }
                user.setLastCopSalary(System.currentTimeMillis());
                user.addMoney(user.getCopRank().getSalary());
                player.sendMessage(Lang.COPS.f("&7You received your daily salary of &a$&l" + user.getCopRank().getSalary() + "&7!"));
            }
            default:
                break;
        }
    }

    @EventHandler
    protected final void onNPCDamage(NPCDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    protected final void onNPCDamage(NPCDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void armorEquipRunner(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = e.getPlayer();
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if (newArmorType != null) {
                if (newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, EquipArmorType.matchType(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if (armorEquipEvent.isCancelled()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        ItemStack item = player.getInventory().getItemInMainHand();
        if ((item != null && item.getType() == Material.FIREWORK) || (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.FIREWORK)) {
            e.setCancelled(true);
            return;
        }

        if ((e.getItem() != null && item != null) && e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK && e.getItem().equals(item)) {
            GameItem gameItem = Vice.getItemManager().getItem(item);
            if (gameItem == null || !item.hasItemMeta() || !item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE))
                return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 3, 2, false, false), true);//so if player tries to use drug as shovel, it doesn't work.
        }

        if (item != null) {
            switch (item.getType()) {
                case GOLD_RECORD:
                    if (e.getClickedBlock() == null) return;
                    if (e.getClickedBlock().getType() == Material.JUKEBOX) return;
                    break;
                case WATCH:
                    MenuManager.openMenu(player, "phone");
                    return;
                case RECORD_5:
                    if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Vice.getItemManager().getItem("bong").getItem().getType()) {
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                            player.getInventory().setItemInMainHand(item);
                        } else {
                            player.getInventory().remove(item);
                        }
                        Optional<Drug> weed = ((DrugService) Vice.getDrugManager().getService()).getDrug("weed");
                        if (weed.isPresent()) {
                            weed.get().apply(player);
                        } else {
                            return;
                        }
                        Location blockLoc = e.getClickedBlock().getLocation();
                        long startTime = System.currentTimeMillis();
                        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (startTime + (1000 * 5) <= System.currentTimeMillis())
                                    this.cancel();
                                double locX = blockLoc.getX() + (ThreadLocalRandom.current().nextDouble(-.15, .15) + .5);
                                double locZ = blockLoc.getZ() + (ThreadLocalRandom.current().nextDouble(-.15, .15) + .5);
                                double locY = blockLoc.getY() + 1.2;
                                ParticleColor color = ParticleColor.AQUA;
                                player.spigot().playEffect(new Location(blockLoc.getWorld(), locX, locY, locZ), Effect.SPELL, 1, 1, color.getRed(), color.getGreen(), color.getBlue(), 1, 1, 10);
                            }
                        }.runTaskTimerAsynchronously(Vice.getInstance(), 0, 1);
                    }
                    return;
                default:
                    break;
            }
        }
        if (e.getClickedBlock() == null)
            return;
        BlockState block = e.getClickedBlock().getState();
        if (viceUser.isCop() && ViceUtils.canPlantOn(ViceUtils.getSeedVersionOfMaterial(item.getType()), block.getType()) && Vice.getItemManager().getReplacedVanilla().keySet().stream().anyMatch(vanilla -> ViceUtils.getSeedVersionOfMaterial(vanilla.getType()) == item.getType())) {
            e.setCancelled(true);
            player.sendMessage(Utils.f(Lang.COP + "&7You can't plant drugs as a cop!"));
            return;
        }
        switch (block.getType()) {
            case CHEST:

                Inventory inventory = ((Chest) block).getBlockInventory();

                if (ChatColor.stripColor(inventory.getTitle()).equals("Backpack")) {
                    e.setCancelled(true);
                    return;
                }

                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.getBooleanFromStorage(BooleanStorageType.ADDING_LOOTCRATE)) {
                    try {
                        Chest chest = (Chest) block;
//                        Class<?> craftChestClass = ReflectionUtil.getOBCClass("block.CraftChest");
//                        Object craftChest = craftChestClass.cast(chest);
//                        Method getTileEntity = craftChestClass.getMethod("getTileEntity");
//                        Object tileEntity = getTileEntity.invoke(craftChest);
//                        Class<?> tileEntityClass = ReflectionUtil.getNMSClass("TileEntityChest");
//                        Method setTitle = tileEntityClass.getMethod("a", String.class);
//                        setTitle.invoke(tileEntity, Utils.f("&e&lLoot Crate"));
                        chest.setCustomName(Utils.f("&e&lLoot Crate"));
                        chest.update();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Vice.getCrateManager().addCrate(block.getLocation());
                    player.sendMessage(Lang.LOOTCRATES.f("&7You added a loot crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7!"));
                    user.setBooleanToStorage(BooleanStorageType.ADDING_LOOTCRATE, false);
                    e.setCancelled(true);
                    return;
                } else if (user.getBooleanFromStorage(BooleanStorageType.REMOVING_LOOTCRATE)) {
                    LootCrate crate = Vice.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setBooleanToStorage(BooleanStorageType.REMOVING_LOOTCRATE, false);
                        return;
                    }
                    Vice.getCrateManager().removeCrate(block.getLocation());
                    player.sendMessage(Lang.LOOTCRATES.f("&7You removed a loot crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7!"));
                    user.setBooleanToStorage(BooleanStorageType.REMOVING_LOOTCRATE, false);
                    e.setCancelled(true);
                    return;
                } else if (user.getBooleanFromStorage(BooleanStorageType.CHECKING_LOOTCRATE)) {
                    LootCrate crate = Vice.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setBooleanToStorage(BooleanStorageType.CHECKING_LOOTCRATE, false);
                        return;
                    }
                    player.sendMessage(Lang.LOOTCRATES.f("&7The Loot Crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7 will restock in &a"
                            + Utils.timeInSecondsToText(crate.getTimer()) + "&7! (+- 10s)"));
                    user.setBooleanToStorage(BooleanStorageType.CHECKING_LOOTCRATE, false);
                    e.setCancelled(true);
                    return;
                } else if (user.getBooleanFromStorage(BooleanStorageType.RESTOCKING_LOOTCRATE)) {
                    LootCrate crate = Vice.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setBooleanToStorage(BooleanStorageType.RESTOCKING_LOOTCRATE, false);
                        return;
                    }
                    crate.restock(Area.DropType.DEFAULT);
                    player.sendMessage(Lang.LOOTCRATES.f("&7The Loot Crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7 was restocked."));
                    user.setBooleanToStorage(BooleanStorageType.RESTOCKING_LOOTCRATE, false);
                    e.setCancelled(true);
                    return;
                }
                LootCrate crate = Vice.getCrateManager().getCrate(block.getLocation());
                if (crate == null) return;
                if (!crate.isLooted()) {
                    crate.setLooted(true);

                    int money = Utils.randomNumber(20, 150);
                    user.addMoney(money);
                    player.sendMessage(Lang.MONEY_ADD.f(money + ".00"));
                }
                return;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;//so it only gets triggered once
        Player player = e.getPlayer();
        Entity en = e.getRightClicked();
        switch (en.getType()) {
            case PLAYER: {
                ItemStack i = player.getInventory().getItemInMainHand();
                GameItem gi = i == null ? null : Vice.getItemManager().getItem(i);
                if (gi == null || !"handcuffs".equalsIgnoreCase(gi.getName())) return;
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                Player victim = (Player) en;
                UUID victimUUID = victim.getUniqueId();
                ViceUser victimViceUser = Vice.getUserManager().getLoadedUser(victimUUID);
                if (!viceUser.isCop()) return;
                if (!Objects.equals("spawn", victim.getWorld().getName()) || ViceUtils.isInSpawnRange(victim, 10)) {
                    player.sendMessage(Lang.COPS.f("&7You have no jurisdiction in this area!"));
                    return;
                }
                if (victimViceUser.hasTeleportProtection()) {
                    e.setCancelled(true);
                    player.sendMessage(Lang.COMBATTAG.f("&7That player has teleport protection for &c&l" + Utils.timeInMillisToText(victimViceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
                    return;
                }
                if (viceUser.hasTeleportProtection()) {
                    e.setCancelled(true);
                    player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(viceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
                    return;
                }
                if (victimViceUser.isCop()) {
                    e.setCancelled(true);
                    player.sendMessage(Utils.f(Lang.HEY + "&cYou can't arrest cops!"));
                    return;
                }
                int timeInJail = ViceUtils.getTimeInJailForDrugs(victim);
                if (timeInJail == 0) return;
                ItemStack chestPlate = player.getInventory().getChestplate();
                if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals during flight!"));
                    return;
                }
                if (player.getVehicle() != null) {
                    player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals while in a Vehicle!"));
                    return;
                }
                if (Vice.getWorldManager().getZones(player.getLocation()).stream().anyMatch(zone -> zone.getFlags().contains(ZoneFlag.COP_CANT_ARREST))) {
                    player.sendMessage(Lang.COP_MODE.f("&7You may not arrest criminals in this area!"));
                    return;
                }
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                User victimUser = Core.getUserManager().getLoadedUser(victimUUID);
                victimViceUser.jail(timeInJail, player);
                player.sendMessage(Lang.COP_MODE.f("&7You arrested &a" + victimUser.getColoredName(victim)
                        + "&7! He will go to jail for &a" + Utils.timeInSecondsToText(timeInJail) + "&7!"));
                Utils.broadcastExcept(player, Lang.COP_MODE.f("&a" + victimUser.getColoredName(victim) + "&7 was arrested by &a"
                        + user.getColoredName(player) + "&7!"));
                victimViceUser.addDeaths(1);
                victimViceUser.setLastTag(-1);
                victimViceUser.setKillStreak(0);
                if (Vice.getWorldManager().getWarpManager().cancelTaxi(victim, victimViceUser))
                    victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));
                victim.setHealth(victim.getMaxHealth());
                victim.spigot().respawn();
                victim.setFireTicks(0);
                victim.setGameMode(GameMode.SPECTATOR);
                victim.setFlying(true);
                victim.getActivePotionEffects().clear();
                victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
                victim.setFoodLevel(20);
                victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
                victim.setFlySpeed(0);
                ViceUtils.removeBoard(victim);
//                victimUser.removeCosmetics(victim);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player victim = Bukkit.getPlayer(victimUUID);
                        if (victim == null)
                            return;
                        User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                        ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
                        victim.sendMessage(Lang.JAIL.f("&7You were arrested and have to stay in jail for &a"
                                + Utils.timeInSecondsToText(timeInJail) + "&7!"));
                        victim.teleport(Vice.getWorldManager().getWarpManager().getJail().getLocation());
                        victim.setGameMode(GameMode.SURVIVAL);
                        victim.getActivePotionEffects().clear();
                        victim.setFoodLevel(20);
                        victim.setFlying(false);
                        victim.setFlySpeed(0.1F);
                        ViceUtils.giveGameItems(victim);
                        ViceUtils.updateBoard(victim, victimGameUser);
//                        victimUser.loadLastCosmetics(victim);
                    }
                }.runTaskLater(Vice.getInstance(), 150);
                HashSet<ItemStack> bannedItems = new HashSet<>();
                for (ItemStack is : victim.getInventory().getContents()) {
                    if (Vice.getItemManager().getItem(is) != null && Vice.getItemManager().getItem(is).isScheduled()) {
                        bannedItems.add(is);
                        victim.getInventory().removeItem(is);
                    }
                }
                ViceUtils.giveGameItems(victim);
                for (ItemStack item : bannedItems)
                    Utils.giveItems(player, item);
                Utils.sendTitle(victim, "&c&lBUSTED", "&7Arrested by " + player.getName(), 80, 50, 20);
                ViceUtils.updateBoard(player, user, viceUser);
                return;
            }
            case ITEM_FRAME:
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.hasEditMode()) {
                    e.setCancelled(false);
                    return;
                }
                if (user.isInTutorial()) {
                    e.setCancelled(true);
                    return;
                }

                e.setCancelled(true);
                ItemFrame frame = (ItemFrame) en;
                if (!Objects.equals("spawn", frame.getWorld().getName())) return;
                ItemStack item = frame.getItem();
                if (item == null)
                    return;
                switch (item.getType()) {
                    case PAPER: {
                        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                        if (viceUser.isArrested()) {
                            if (viceUser.getJailTimer() < 5) {
                                player.sendMessage(Lang.BRIBE.f("&7You are already being released!"));
                                return;
                            }
                            Player cop = Bukkit.getPlayer(viceUser.getJailCop());
                            ViceUser copUser = cop == null ? null : Vice.getUserManager().getLoadedUser(cop.getUniqueId());
                            if (cop == null || !copUser.isCop()) {
                                player.sendMessage(Lang.BRIBE.f("&7The cop who arrested you (&3&l" + viceUser.getJailCopName() + "&7) is off duty!"));
                                return;
                            }
                            viceUser.setBooleanToStorage(BooleanStorageType.BRIBING, true);
                            player.sendMessage(Lang.BRIBE.f("&7Please type the amount you would like to offer as a bribe to &3&l" + viceUser.getJailCopName() + "&7 or type &a\"/quit\"&7!"));
                            return;
                        }
                        frame.setRotation(Rotation.NONE);
                        MenuManager.openMenu(player, "bank");
                        break;
                    }
                    case ENDER_PEARL:
                        frame.setRotation(Rotation.NONE);
                        player.performCommand("/rtp");
                        break;
                    case IRON_FENCE:
                        if (ViceUtils.getJailedPlayers().isEmpty()) {
                            player.sendMessage(Lang.JAIL.f("&7There are currently no prisoners in jail!"));
                            return;
                        }
                        MenuManager.openMenu(player, "jail");
                        break;
                    case STORAGE_MINECART:
                        MenuManager.openMenu(player, "taxi");
                        break;
                    case MINECART: {
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
                        if (name.startsWith("Buy Vehicle: "))
                            name = name.replace("Buy Vehicle: ", "");
                        else if (name.startsWith("Vehicle Shop: "))
                            name = name.replace("Vehicle Shop: ", "");
                        else if (name.startsWith("Buy "))
                            name = name.replace("Buy ", "");
                        else break;
                        GameItem gameItem = Vice.getItemManager().getItemFromDisplayName(name);
                        if (gameItem == null || gameItem.getType() != GameItem.ItemType.VEHICLE) return;
                        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                        viceUser.setActionVehicle(gameItem.getWeaponOrVehicleOrDrug());
                        MenuManager.openMenu(player, "vehicleshop");
                        return;
                    }
                    case LEATHER: {
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? ChatColor.stripColor(frame.getItem().getItemMeta().getDisplayName()).toLowerCase() : null;
                        if (name != null && name.contains("upgrade buy: $")) {
                            Vice.getShopManager().buyArmorUpgrade(player, name);
                            return;
                        }
                        break;
                    }
                    case EMPTY_MAP:
                    case MAP:
                        frame.setRotation(Rotation.NONE);
                        MenuManager.openMenu(player, "lottery");
                        break;

                    default:
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? ChatColor.stripColor(frame.getItem().getItemMeta().getDisplayName()) : null;
                        if (name != null && Objects.equals("spawn", frame.getWorld().getName()) && name.startsWith("Floor")) {
                            if (name.toLowerCase().endsWith("up")) {
                                Location loc = player.getLocation();
                                if (ViceUtils.ascendLevel(player)) {
                                    player.sendMessage(Lang.VICE.f("&7Ascended a level."));
                                } else {
                                    player.sendMessage(Lang.VICE.f("&7No free spot above you found."));
                                }
                                break;
                            } else if (name.toLowerCase().endsWith("down")) {
                                Location loc = player.getLocation();
                                if (ViceUtils.descendLevel(player)) {
                                    player.sendMessage(Lang.VICE.f("&7Descended a level."));
                                } else {
                                    player.sendMessage(Lang.VICE.f("&7No free spot below you found."));
                                }
                                break;
                            }
                        }
                        if (name != null && name.startsWith("Preview Kit: ")) {
                            Kit kit = Vice.getItemManager().getKit(name.replace("Preview Kit: ", ""));
                            if (kit != null)
                                Vice.getBackpackManager().kitPreview(player, kit);
                            return;
                        }
                        Vice.getShopManager().buy(player, frame.getItem());
                        break;
                }
                return;
            case VILLAGER:
                if (Vice.getCombatLogManager().getCombatLogNPCs().contains((NPC) e.getRightClicked())) {
                    e.setCancelled(true);

                    return;
                }
            default:
                break;
        }
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        Entity en = e.getRightClicked();
        if (en == null || e.getHand() != EquipmentSlot.HAND) {
            e.setCancelled(true);
            return;
        }//so it only triggers once
        switch (en.getType()) {
            case ARMOR_STAND:
                if (user.hasEditMode())
                    break;
                ArmorStand armorStand = (ArmorStand) e.getRightClicked();
                String name = ChatColor.stripColor(armorStand.getCustomName());
                if (name == null) break;
                switch (name.toLowerCase()) {
                    case "mechanic":
                        MenuManager.openMenu(player, "mechanic");
                        return;
                    case "cab driver":
                        MenuManager.openMenu(player, "taxi");
                        return;
                    case "warden":
                        MenuManager.openMenu(player, "jail");
                        return;
                    case "arms dealer":
                        MenuManager.openMenu(player, "weapons");
                        return;
                    case "head salesman":
                        ItemStack item = player.getInventory().getItemInMainHand();
                        MenuManager.openMenu(player, item != null && item.getType() == Material.SKULL_ITEM ? "auctionhead" : "heads");
                        return;
                    default:
                        return;
                }

            case VILLAGER: {
                Villager villager = (Villager) en;
                if (villager.hasMetadata("loggedplayer")) {
                    e.setCancelled(true);
                    return;
                }
                if (player.isSneaking() && viceUser.getCheatCodeState(CheatCode.VILLAGERJOB).getState() == State.ON) {
                    e.setCancelled(true);
                    if (user.isOnCooldown("villager_job_cc")) {
                        player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + user.getFormattedCooldown("villager_job_cc") + " &7before using this cheatcode again!"));
                        return;
                    }
                    Faction cFac = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

                    if (fPlayer.getRelationTo(cFac) == Relation.ENEMY) {
                        player.sendMessage(Lang.CHEAT_CODES.f("&7You cannot change the job of a villager in an enemy's faction!"));
                        return;
                    }
                    viceUser.setChangingJob(villager);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            MenuManager.openMenu(player, "choose_villager_type");
                        }
                    }.runTaskLater(Vice.getInstance(), 1);
                    return;
                }

//                if (villager.getRecipes() == null || villager.getRecipes().isEmpty()) return;
//                List<MerchantRecipe> filtered = new ArrayList<>();
//                for (int i = 0; i < villager.getRecipes().size(); i++) {
//                    MerchantRecipe recipe = villager.getRecipe(i);
//                    switch (recipe.getResult().getType()) {
//                        case WOOD_SWORD:
//                        case STONE_SWORD:
//                        case IRON_SWORD:
//                        case GOLD_SWORD:
//                        case DIAMOND_SWORD:
//
//                        case LEATHER_BOOTS:
//                        case LEATHER_LEGGINGS:
//                        case LEATHER_CHESTPLATE:
//                        case LEATHER_HELMET:
//                        case CHAINMAIL_BOOTS:
//                        case CHAINMAIL_LEGGINGS:
//                        case CHAINMAIL_CHESTPLATE:
//                        case CHAINMAIL_HELMET:
//                        case IRON_BOOTS:
//                        case IRON_LEGGINGS:
//                        case IRON_CHESTPLATE:
//                        case IRON_HELMET:
//                        case GOLD_BOOTS:
//                        case GOLD_LEGGINGS:
//                        case GOLD_CHESTPLATE:
//                        case GOLD_HELMET:
//                        case DIAMOND_BOOTS:
//                        case DIAMOND_LEGGINGS:
//                        case DIAMOND_CHESTPLATE:
//                        case DIAMOND_HELMET:
//
//                        case FLINT_AND_STEEL:
//                            break;
//                        default:
//                            filtered.add(recipe);
//                            break;
//                    }
//                }
//
//                villager.setRecipes(filtered);
//
//                if (villager.getInventory() != null)
//                    player.openInventory(villager.getInventory());
//                break;
            }
            default:
                break;
        }
    }

    @EventHandler
    protected final void onSpawnerChange(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getItem() != null &&
                event.getClickedBlock().getType() == Material.MOB_SPAWNER && event.getItem().getType() == Material.MONSTER_EGG) {
            event.setCancelled(true);
        }
    }

    // TODO remove as this is just to stop a dupe glitch of
    // stacking similarly named items onto eachother to dupe them
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilInteract(PlayerInteractEvent event) {

        // grab event variables
        Player p = event.getPlayer();
        Action a = event.getAction();
        Block block = event.getClickedBlock();

        if (block != null && block.getType() == Material.ANVIL) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
        }
    }
}
