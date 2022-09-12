package net.grandtheftmc.gtm.listeners;

import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.items.ArmorType;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.Kit;
import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;
import net.grandtheftmc.gtm.lootcrates.LootCrate;
import net.grandtheftmc.gtm.users.ChatAction;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.TaxiTarget;
import net.grandtheftmc.gtm.utils.ParticleColor;
import net.grandtheftmc.gtm.utils.ReflectionUtil;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Interact implements Listener {
    private static final Class BLOCK_POSITION_CLASS = ReflectionAPI.getNmsClass("BlockPosition");

    public Interact() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(GTM.getInstance(), PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_DESTROY, PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Client.ENTITY_ACTION) {
//            @Override
//            public void onPacketReceiving(PacketEvent event) {
////                super.onPacketReceiving(event);
//                if (event.getPacket().getType() == PacketType.Play.Server.ENTITY_METADATA) {
//                    PacketPlayOutEntityMetadata
//                }
//                ServerUtil.debug(event.getPlayer().getName() + "  ->  " + event.getPacket().toString());
//            }
//
//            @Override
//            public void onPacketSending(PacketEvent event) {
////                super.onPacketSending(event);
//                ServerUtil.debug(event.getPlayer().getName() + "  ->  " + event.getPacket().toString());
//            }
//        });
    }

    @EventHandler
    public void armorEquipRunner(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = e.getPlayer();
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if (newArmorType != null) {
                if (newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, EquipArmorType.matchType(e.getItem()), null, e.getItem(), null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if (armorEquipEvent.isCancelled()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractHigh(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack is = e.getItem();

        if (is == null) return;
        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        Optional<Weapon<?>> weapon = GTM.getWastedGuns().getWeaponManager().getWeapon(is);
        if (weapon.isPresent()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(Lang.CHEAT_CODES.f("&7Your invisibility was removed because you used a weapon!"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (item != null) {
            switch (item.getType()) {
                case GOLD_RECORD:
                    if (e.getClickedBlock() == null) return;
                    if (e.getClickedBlock().getType() == Material.JUKEBOX) return;
                    return;
                case WATCH:
                    MenuManager.openMenu(player, "phone");
                    return;
                case COMPASS:
                    MenuManager.openMenu(player, "gps");
                    return;
                case RECORD_5:
                    if (e.getClickedBlock() != null && e.getClickedBlock().getType() == GTM.getItemManager().getItem("bong").getItem().getType()) {
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                            player.getInventory().setItemInMainHand(item);
                        } else {
                            player.getInventory().remove(item);
                        }
                        Optional<Drug> weed = ((DrugService) GTM.getInstance().getDrugManager().getService()).getDrug("weed");
                        if (weed.isPresent()) {
                            weed.get().apply(player);
                        } else {
                            return;
                        }
                        final Location blockLoc = e.getClickedBlock().getLocation();
                        final long startTime = System.currentTimeMillis();
                        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (startTime + (1000 * 5) <= System.currentTimeMillis())
                                    cancel();
                                double locX = blockLoc.getX() + (ThreadLocalRandom.current().nextDouble(-.15, .15) + .5);
                                double locZ = blockLoc.getZ() + (ThreadLocalRandom.current().nextDouble(-.15, .15) + .5);
                                double locY = blockLoc.getY() + 1.2;
                                ParticleColor color = ParticleColor.AQUA;
                                player.spigot().playEffect(new Location(blockLoc.getWorld(), locX, locY, locZ), Effect.SPELL, 1, 1, color.getRed(), color.getGreen(), color.getBlue(), 1, 1, 10);
                            }
                        }.runTaskTimerAsynchronously(GTM.getInstance(), 0, 1);
                    }
                    return;
                default:
                    break;
            }
        }
        if (e.getClickedBlock() == null)
            return;

        if (e.getClickedBlock().getType() == Material.CAULDRON) {
            e.setCancelled(true);
            return;
        }

        BlockState block = e.getClickedBlock().getState();
        switch (block.getType()) {
            case CHEST:
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.isAddingLootCrate()) {
                    try {
                        Chest chest = (Chest) block;
                        Class<?> craftChestClass = ReflectionUtil.getOBCClass("block.CraftChest");
                        Object craftChest = craftChestClass.cast(chest);
                        Method getTileEntity = craftChestClass.getMethod("getTileEntity");
                        Object tileEntity = getTileEntity.invoke(craftChest);
                        Class<?> tileEntityClass = ReflectionUtil.getNMSClass("TileEntityChest");
                        Method setTitle = tileEntityClass.getMethod("a", String.class);
                        setTitle.invoke(tileEntity, Utils.f("&e&lLoot Crate"));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    GTM.getCrateManager().addCrate(block.getLocation());
                    player.sendMessage(Lang.LOOTCRATES.f("&7You added a loot crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7!"));
                    user.setAddingLootCrate(false);
                    e.setCancelled(true);
                    return;
                } else if (user.isRemovingLootCrate()) {
                    LootCrate crate = GTM.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setRemovingLootCrate(false);
                        return;
                    }
                    GTM.getCrateManager().removeCrate(block.getLocation());
                    player.sendMessage(Lang.LOOTCRATES.f("&7You removed a loot crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7!"));
                    user.setRemovingLootCrate(false);
                    e.setCancelled(true);
                    return;
                } else if (user.isCheckingLootCrate()) {
                    LootCrate crate = GTM.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setCheckingLootCrate(false);
                        return;
                    }
                    player.sendMessage(Lang.LOOTCRATES.f("&7The Loot Crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7 will restock in &a"
                            + Utils.timeInSecondsToText(crate.getTimer()) + "&7! (+- 10s)"));
                    user.setCheckingLootCrate(false);
                    e.setCancelled(true);
                    return;
                } else if (user.isRestockingLootCrate()) {
                    LootCrate crate = GTM.getCrateManager().getCrate(block.getLocation());
                    if (crate == null) {
                        player.sendMessage(Lang.LOOTCRATES.f("&7This is not a Loot Crate!"));
                        user.setRestockingLootCrate(false);
                        return;
                    }
                    crate.restock();
                    player.sendMessage(Lang.LOOTCRATES.f("&7The Loot Crate at location &a"
                            + Utils.blockLocationToString(block.getLocation()) + "&7 was restocked."));
                    user.setRestockingLootCrate(false);
                    e.setCancelled(true);
                    return;
                }
                LootCrate crate = GTM.getCrateManager().getCrate(block.getLocation());
                if (crate == null) return;
                if (crate.getTimer() <= 0) {
                    crate.resetTimer();

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
        Player player = e.getPlayer();
        Entity en = e.getRightClicked();
        switch (en.getType()) {
            case ARMOR_STAND:
                if (GTM.getDrugManager().getDrugDealer().isDrugDealer(en)) {
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    if (user.getJobMode() == JobMode.COP) {
                        user.setMoney(user.getMoney() + 10000);
                        player.sendMessage(Lang.GTM + "" + ChatColor.GREEN + "You have arrested a drug dealer and recieved $10,000!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        en.remove();
                        return;
                    }
                }
                break;
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
                ItemStack item = frame.getItem();
                if (item == null)
                    return;
                switch (item.getType()) {
                    case PAPER: {
                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                        if (gtmUser.isArrested()) {
                            if (gtmUser.getJailTimer() < 5) {
                                player.sendMessage(Lang.BRIBE.f("&7You are already being released!"));
                                return;
                            }
                            Player cop = Bukkit.getPlayer(gtmUser.getJailCop());
                            GTMUser copUser = cop == null ? null : GTM.getUserManager().getLoadedUser(cop.getUniqueId());
                            if (cop == null || copUser.getJobMode() != JobMode.COP) {
                                player.sendMessage(Lang.BRIBE.f("&7The cop who arrested you (&3&l" + gtmUser.getJailCopName() + "&7) is off duty!"));
                                return;
                            }
                            gtmUser.setCurrentChatAction(ChatAction.BRIBING, 0);
                            player.sendMessage(Lang.BRIBE.f("&7Please type the amount you would like to offer as a bribe to &3&l" + gtmUser.getJailCopName() + "&7 or type &a\"/quit\"&7!"));
                            return;
                        }
                        frame.setRotation(Rotation.NONE);
                        MenuManager.openMenu(player, "bank");
                        break;
                    }
                    case ENDER_PEARL:
                        frame.setRotation(Rotation.NONE);
                        GTM.getWarpManager().warp(player, user, GTM.getUserManager().getLoadedUser(player.getUniqueId()),
                                new TaxiTarget(GTM.getWarpManager().getRandomWarp()), 0, user.isPremium() ? 1 : 10);
                        break;
                    case IRON_FENCE:
                        if (GTMUtils.getJailedPlayers().isEmpty()) {
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
                        GameItem gameItem = GTM.getItemManager().getItemFromDisplayName(name);
                        if (gameItem == null || gameItem.getType() != GameItem.ItemType.VEHICLE) return;
                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                        gtmUser.setActionVehicle(gameItem.getWeaponOrVehicleOrDrug());
                        MenuManager.openMenu(player, "vehicleshop");
                        return;
                    }
                    case LEATHER: {
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? ChatColor.stripColor(frame.getItem().getItemMeta().getDisplayName()).toLowerCase() : null;
                        Core.log(name);
                        if (name != null && name.contains("upgrade buy: $")) {
                            GTM.getShopManager().buyArmorUpgrade(player, name);
                            return;
                        }
                        break;
                    }
                    case EMPTY_MAP:
                    case MAP: {
                        frame.setRotation(Rotation.NONE);
                        MenuManager.openMenu(player, "lottery");
                        break;
                    }
                    default:
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? ChatColor.stripColor(frame.getItem().getItemMeta().getDisplayName()) : null;
                        if (name != null && name.startsWith("Preview Kit: ")) {
                            Kit kit = GTM.getItemManager().getKit(name.replace("Preview Kit: ", ""));
                            if (kit != null)
                                GTM.getBackpackManager().kitPreview(player, kit);
                            return;
                        }
                        GTM.getShopManager().buy(player, frame.getItem());
                        break;
                }
                return;
            default:
                break;
        }
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        Entity en = e.getRightClicked();
        switch (en.getType()) {
            case ARMOR_STAND:
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                if (Core.getSettings().isUseEditMode()
                        && user.hasEditMode())
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
                    case "drug dealer":
                        MenuManager.openMenu(player, "drugdealer");
                        return;
                    case "arms dealer":
                        MenuManager.openMenu(player, "weapons");
                        return;
                    case "head salesman":
                        ItemStack item = player.getInventory().getItemInMainHand();
                        MenuManager.openMenu(player, item != null && item.getType() == Material.SKULL_ITEM ? "auctionhead" : "heads");
                        return;
                    default:
                        JobMode mode = JobMode.getModeOrNull(name);
                        if (mode == null) return;
                        GTMUtils.chooseJobMode(player, user,
                                GTM.getUserManager().getLoadedUser(player.getUniqueId()), mode);
                        return;
                }
            default:
                break;
        }
    }
}
