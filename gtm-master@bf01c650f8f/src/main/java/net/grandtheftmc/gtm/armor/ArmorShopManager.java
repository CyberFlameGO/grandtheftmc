package net.grandtheftmc.gtm.armor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;

import com.google.common.collect.Lists;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.ItemManager;
import net.grandtheftmc.gtm.users.GTMUser;

public class ArmorShopManager implements Component<ArmorShopManager, GTM> {

    private final ItemManager itemManager;

    public ArmorShopManager(JavaPlugin plugin, ItemManager itemManager) {
        this.itemManager = itemManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        ServerUtil.runTaskLater(() -> {
            List<ShopStatue> list = Lists.newArrayList();
            final World spawn = Bukkit.getWorld("spawn");
            list.addAll(Arrays.asList(
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.3, 0), VisualType.NAME);
                            clickable.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            clickable.setCustomNameVisible(true);

                            ArmorStand armor = spawnEntity(origin.clone().add(-0.1, 0.1, 0), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(42.0f), 0f, 0f));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -371.5, 25.5, 268.5, -90.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "baseballcap";
                        }
                    }, //Leather Helmet
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.4, 0), VisualType.NAME);
                            clickable.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            clickable.setCustomNameVisible(true);

                            ArmorStand armor = spawnEntity(origin.clone().add(-0.1, 0.5, 0), VisualType.NONE);
                            armor.setChestplate(gameItem.getItem().clone());
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -371.5, 25.5, 267.5, -90.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "shirt";
                        }
                    }, //Leather Chestplate
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.1, 0), VisualType.NAME);
                            clickable.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            clickable.setCustomNameVisible(true);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 1, 0), VisualType.NONE);
                            armor.setLeggings(gameItem.getItem().clone());
                            armor.setLeftLegPose(new EulerAngle(AngleUtil.getRadianFromDegree(343.0), 0f, AngleUtil.getRadianFromDegree(350.0)));
                            armor.setRightLegPose(new EulerAngle(AngleUtil.getRadianFromDegree(3.0), 0f, AngleUtil.getRadianFromDegree(7.0)));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -371.5, 25.5, 266.5, -90.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "pants";
                        }
                    }, //Leather Leggings
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 2.1, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.6, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 1.5, 0), VisualType.NONE);
                            armor.setBoots(gameItem.getItem().clone());
                            armor.setLeftLegPose(new EulerAngle(AngleUtil.getRadianFromDegree(343.0), 0f, AngleUtil.getRadianFromDegree(350.0)));
                            armor.setRightLegPose(new EulerAngle(AngleUtil.getRadianFromDegree(3.0), 0f, AngleUtil.getRadianFromDegree(7.0)));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -371.5, 25.5, 265.5, -75.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "nikes";
                        }
                    }, //Leather Boots
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 0.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(42.0), 0f, 0f));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -362.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "tacticalmask";
                        }
                    }, //Iron Helmet
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 0.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(42.0), 0f, 0f));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -360.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "titaniumhelmet";
                        }
                    }, //Diamond Helmet
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 0.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 0.15, 0), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(42.0), 0f, 0f));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -358.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "pimpcrown";
                        }
                    }, //Golden Helmet
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 1.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 1.5, 0.05), VisualType.NONE);
                            armor.setChestplate(gameItem.getItem().clone());
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -361.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "ceramicvest";
                        }
                    }, //Iron Chestplate
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 1.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 1.5, 0.05), VisualType.NONE);
                            armor.setChestplate(gameItem.getItem().clone());
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -359.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "titaniumvest";
                        }
                    }, //Diamond Chestplate
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 1.6, 0), VisualType.NAME);
                            name.setCustomName(C.GREEN + "$" + gameItem.getBuyPrice());
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, 1.5, 0.1), VisualType.NONE);
                            armor.setChestplate(gameItem.getItem().clone());
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -357.5, 25.5, 263.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "jetpack";
                        }
                    }, //Jetpack
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 2.2, 0), VisualType.NAME);
                            name.setCustomName(C.WHITE + "x" + C.BOLD + this.getAmount() + C.RESET + " " + C.GREEN + "$" + (gameItem.getBuyPrice() * this.getAmount()));
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.6, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, -0.3, 0.1), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(10.0), 0f, 0f));
                            armor.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -356.5, 25.5, 270.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "jetpackfuel";
                        }

                        @Override
                        public int getAmount() {
                            return 10;
                        }
                    }, //Jetpack Fuel x10
                    new ShopStatue() {
                        @Override
                        public Location spawnVisual(World world) {
                            GameItem gameItem = itemManager.getItem(this.getGameItem());
                            if (gameItem == null) return null;

                            Location origin = this.getOrigin(world);

                            ArmorStand name = spawnEntity(origin.clone().add(0, 2.2, 0), VisualType.NAME);
                            name.setCustomName(C.WHITE + "x" + C.BOLD + this.getAmount() + C.RESET + " " + C.GREEN + "$" + (gameItem.getBuyPrice() * this.getAmount()));
                            name.setCustomNameVisible(true);
                            name.setMarker(true);

                            ArmorStand clickable = spawnEntity(origin.clone().add(0, 0.6, 0), VisualType.NAME);

                            ArmorStand armor = spawnEntity(origin.clone().add(0, -0.3, -0.1), VisualType.NONE);
                            armor.setHelmet(gameItem.getItem().clone());
                            armor.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(10.0), 0f, 0f));
                            armor.setMarker(true);

                            ArmorStand armor2 = spawnEntity(origin.clone().add(0.2, -0.28, 0.1), VisualType.NONE);
                            armor2.setHelmet(gameItem.getItem().clone());
                            armor2.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(10.0), 0f, 0f));
                            armor2.setMarker(true);

                            ArmorStand armor3 = spawnEntity(origin.clone().add(-0.2, -0.25, 0), VisualType.NONE);
                            armor3.setHelmet(gameItem.getItem().clone());
                            armor3.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(10.0), 0f, 0f));
                            armor3.setMarker(true);
                            return null;
                        }

                        @Override
                        public Location getOrigin(World world) {
                            return new Location(spawn, -358.5, 25.5, 270.5, 0.0f, 0.0f);
                        }

                        @Override
                        public String getGameItem() {
                            return "jetpackfuel";
                        }

                        @Override
                        public int getAmount() {
                            return 64;
                        }
                    } //Jetpack Fuel x64
            ));

            for (ShopStatue statue : list) {
                Location origin = statue.getOrigin(spawn);
                if (!origin.getChunk().isLoaded()) origin.getChunk().load();
                for (Entity entity : origin.getWorld().getNearbyEntities(origin, 2, 10, 2)) {
                    if (entity.getType() != EntityType.ARMOR_STAND) continue;
                    entity.remove();
                }
            }

            ServerUtil.runTaskLater(() -> {
                for (ShopStatue statue : list) {
                    statue.spawnVisual(spawn);
                }
            }, 20 * 15);
        }, 40L);
    }

    @EventHandler
    protected final void onArmorstandInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() == null) return;
        if (!event.getRightClicked().hasMetadata("armor-statue")) return;
        event.setCancelled(true);

        ShopStatue statue = (ShopStatue) event.getRightClicked().getMetadata("armor-statue").get(0).value();
        if (statue == null) return;

        Player player = event.getPlayer();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        
        if (!GTM.getSettings().canBuy()){
        	player.sendMessage(ChatColor.RED + "Buying things is currently disabled.");
        	return;
        }

        GameItem gi = itemManager.getItem(statue.getGameItem());
        double price = gi.getBuyPrice() * statue.getAmount();
        if (user.hasMoney(price)) {
            user.takeMoney(price);
            player.sendMessage(Lang.MONEY_TAKE.f(String.valueOf(price)));
        } else {
            if (user.hasBank(price)) {
                user.takeBank(price);
                player.sendMessage(Lang.BANK_TAKE.f(String.valueOf(price)));
            } else {
                player.sendMessage(Lang.MONEY.f("&7You do not have enough money!"));
                return;
            }
        }
        GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
        switch (gi.getType()) {
//            case AMMO:
//                int amount = statue.getAmount();
//                AmmoType type = statue.getGameItem().getAmmoType();
//                if (type != null)
//                    user.addAmmo(type, amount);
//                player.sendMessage(Lang.SHOP.f("&7You bought " + (amount > 1 ? "&a&l" + amount + "&7x " : "")
//                        + statue.getGameItem().getDisplayName() + "&7 for &a$&l" + price + "&7!"));
//                return;
            case ITEMSTACK:
                ItemStack stack = gi.getItem();
                stack.setAmount(statue.getAmount());
                Utils.giveItems(player, stack);
                player.sendMessage(Lang.SHOP.f("&7You bought " + (statue.getAmount() > 1 ? "&a&l" + statue.getAmount() + "&7x " : "")
                        + gi.getDisplayName() + "&7 for &a$&l" + price + "&7!"));
            default:
                break;
        }
    }
}
