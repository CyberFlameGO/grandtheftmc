package net.grandtheftmc.gtm.weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.weapon.explosive.Grenade;
import net.grandtheftmc.gtm.weapon.explosive.MolotovCocktail;
import net.grandtheftmc.gtm.weapon.explosive.ProximityMine;
import net.grandtheftmc.gtm.weapon.explosive.StickyBomb;
import net.grandtheftmc.gtm.weapon.explosive.TearGas;
import net.grandtheftmc.gtm.weapon.melee.BaseballBat;
import net.grandtheftmc.gtm.weapon.melee.Chainsaw;
import net.grandtheftmc.gtm.weapon.melee.Dildo;
import net.grandtheftmc.gtm.weapon.melee.Katana;
import net.grandtheftmc.gtm.weapon.melee.Knife;
import net.grandtheftmc.gtm.weapon.melee.NightStick;
import net.grandtheftmc.gtm.weapon.melee.Rake;
import net.grandtheftmc.gtm.weapon.ranged.assault.AdvancedRifle;
import net.grandtheftmc.gtm.weapon.ranged.assault.AssaultRifle;
import net.grandtheftmc.gtm.weapon.ranged.assault.BullpupRifle;
import net.grandtheftmc.gtm.weapon.ranged.assault.CarbineRifle;
import net.grandtheftmc.gtm.weapon.ranged.assault.SpecialCarbine;
import net.grandtheftmc.gtm.weapon.ranged.launcher.GrenadeLauncher;
import net.grandtheftmc.gtm.weapon.ranged.launcher.HomingLauncher;
import net.grandtheftmc.gtm.weapon.ranged.launcher.NetLauncher;
import net.grandtheftmc.gtm.weapon.ranged.launcher.RPG;
import net.grandtheftmc.gtm.weapon.ranged.lmg.CombatMG;
import net.grandtheftmc.gtm.weapon.ranged.lmg.MG;
import net.grandtheftmc.gtm.weapon.ranged.pistol.CombatPistol;
import net.grandtheftmc.gtm.weapon.ranged.pistol.HeavyPistol;
import net.grandtheftmc.gtm.weapon.ranged.pistol.MarksmanPistol;
import net.grandtheftmc.gtm.weapon.ranged.pistol.Pistol;
import net.grandtheftmc.gtm.weapon.ranged.pistol.StunGun;
import net.grandtheftmc.gtm.weapon.ranged.shotgun.AssaultShotgun;
import net.grandtheftmc.gtm.weapon.ranged.shotgun.HeavyShotgun;
import net.grandtheftmc.gtm.weapon.ranged.shotgun.Musket;
import net.grandtheftmc.gtm.weapon.ranged.shotgun.PumpShotgun;
import net.grandtheftmc.gtm.weapon.ranged.shotgun.SawedoffShotgun;
import net.grandtheftmc.gtm.weapon.ranged.smg.AssaultSMG;
import net.grandtheftmc.gtm.weapon.ranged.smg.CombatPDW;
import net.grandtheftmc.gtm.weapon.ranged.smg.GusenbergSweeper;
import net.grandtheftmc.gtm.weapon.ranged.smg.MicroSMG;
import net.grandtheftmc.gtm.weapon.ranged.smg.SMG;
import net.grandtheftmc.gtm.weapon.ranged.sniper.HeavySniper;
import net.grandtheftmc.gtm.weapon.ranged.sniper.SniperRifle;
import net.grandtheftmc.gtm.weapon.ranged.special.Clausinator;
import net.grandtheftmc.gtm.weapon.ranged.special.Flamethrower;
import net.grandtheftmc.gtm.weapon.ranged.special.GoldMinigun;
import net.grandtheftmc.gtm.weapon.ranged.special.Minigun;
import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;

/**
 * Created by Luke Bingham on 25/07/2017.
 */
public class WeaponRegistry implements Component<WeaponRegistry, GTM> {

    private static final Set<Material> EMPTY_SET = Sets.newHashSet();

    private final ConcurrentHashMap<UUID, PlayerStateCache> playerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Hologram> weaponHolograms = new ConcurrentHashMap<>();
    private final Location minLoc, maxLoc;

    public WeaponRegistry(JavaPlugin plugin, WeaponManager weaponManager) {
        List<Weapon<?>> list = new ArrayList<Weapon<?>>();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        if (!Core.getSettings().isSister()) {
            list.add(new Dildo());
        }

        list.addAll(Arrays.asList(

                //PISTOL
                new Pistol(),
                new StunGun(),
                new CombatPistol(),
                new MarksmanPistol(),
                new HeavyPistol(),

                //SMG
                new SMG(),
                new MicroSMG(),
                new CombatPDW(),
                new GusenbergSweeper(),
                new AssaultSMG(),

                //SHOTGUN
                new SawedoffShotgun(),
                new PumpShotgun(),
                new Musket(),
                new AssaultShotgun(),
                new HeavyShotgun(),

                //ASSAULT RIFLE
                new AssaultRifle(),
                new CarbineRifle(),
                new BullpupRifle(),
                new AdvancedRifle(),
                new SpecialCarbine(),

                //LMG
                new MG(),
                new CombatMG(),

                //SNIPER
                new SniperRifle(),
                new HeavySniper(),

                //SPECIAL
                new Minigun(),
                new GoldMinigun(),
                new NetLauncher(),
                new Flamethrower(),
                new Clausinator(),

                //LAUNCHER
                new RPG(),
                new HomingLauncher(),
                new GrenadeLauncher(),

                //MELEE
                new Knife(),
                new BaseballBat(),
                new Rake(),
                new NightStick(),
                new Chainsaw(),
                new Katana(),
//                new Dildo(),

                //THROWABLE
                new Grenade(),
                new MolotovCocktail(),
                new ProximityMine(),
                new StickyBomb(),
                new TearGas()
        ));


        weaponManager.registerWeapons(list);

        final World spawn = Bukkit.getWorld("spawn");
        this.minLoc = new Location(spawn, -358.0, 0, 226.0);
        this.maxLoc = new Location(spawn, -378.0, 0, 247.0);

        for (Weapon<?> weapon : list) {
            if (!(weapon instanceof WeaponVisualStatue)) continue;
            WeaponVisualStatue statue = (WeaponVisualStatue) weapon;

            Location origin = statue.getOrigin(spawn);
            if (!origin.getChunk().isLoaded()) origin.getChunk().load();
            for (Entity entity : origin.getWorld().getNearbyEntities(origin, 2, 2, 2)) {
                if (entity.getType() != EntityType.ARMOR_STAND) continue;
                entity.remove();
            }
        }

        ServerUtil.runTaskLater(() -> {
            for (Weapon<?> weapon : list) {
                if (!(weapon instanceof WeaponVisualStatue)) continue;
                ((WeaponVisualStatue) weapon).spawnVisual(spawn);
            }
        }, 20 * 15);
    }

    @EventHandler
    protected final void onArmorstandInteract(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked() == null) return;
        if (!event.getRightClicked().hasMetadata("statue")) return;
        event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(this.maxLoc.getWorld())) return;

        ServerUtil.runTaskAsync(() -> {
            if (!this.isInRegion(player)) return;

            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 6, 6, 6)) {
                if (entity == null) return;
                if (entity.getType() != EntityType.ARMOR_STAND) continue;
                if (!this.isLookingAt(player, (LivingEntity) entity)) continue;

                ArmorStand statue = (ArmorStand) entity;
                if (!statue.hasMetadata("statueview")) continue;

                this.createAndShow(player, statue);
                break;
            }
        });
    }

    @EventHandler
    protected final void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() == null) return;
        if (!event.getEntity().hasMetadata("statue")) return;
        event.setCancelled(true);
    }

    @EventHandler
    protected final void onArmorstandInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() == null) return;
        if (!event.getRightClicked().hasMetadata("statue")) return;
        event.setCancelled(true);

        Weapon<?> weapon = (Weapon<?>) event.getRightClicked().getMetadata("statueview").get(0).value();
        if (weapon == null) return;

        Player player = event.getPlayer();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        
        if (!GTM.getSettings().canBuy()){
        	player.sendMessage(ChatColor.RED + "Buying things is currently disabled.");
        	return;
        }

        GameItem gi = GTM.getItemManager().getItem(weapon.getCompactName());
        double price = gi.getBuyPrice();
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

        ItemStack stack = gi.getItem();
        WeaponSkin weaponSkin = user.getEquippedWeaponSkin(weapon);

        if (weaponSkin != null){
            stack.setDurability(weaponSkin.getIdentifier());
        }

        Utils.giveItems(player, stack);
    }

    private Location getLocationFromSize(Location original, double x, double y, double z, int size) {
        Location loc = original.clone();
        loc.setY((loc.getY() + y) + (0.25 * size));
        loc.setX(loc.getX() + x);
        loc.setZ(loc.getZ() + z);
        return loc;
    }

    @EventHandler
    protected final void onWorldChange(PlayerChangedWorldEvent event) {
        for (Hologram otherHologram : weaponHolograms.values()) {
            otherHologram.getVisibilityManager().hideTo(event.getPlayer());
        }
    }

    @EventHandler
    protected final void onLeave(PlayerQuitEvent event) {
        for (Hologram otherHologram : weaponHolograms.values()) {
            otherHologram.getVisibilityManager().hideTo(event.getPlayer());
        }
    }

    private synchronized void createAndShow(Player player, ArmorStand statue) {
        Weapon<?> weapon = (Weapon<?>) statue.getMetadata("statue").get(0).value();
        if (weapon == null) {
            return;
        }

        //IN DEV (works, movement event doesn't)
        if (this.playerCache.containsKey(player.getUniqueId())) {
            PlayerStateCache cache = this.playerCache.get(player.getUniqueId());
            if (cache.expired()) {
                cache.weapon = weapon;
                cache.time = System.currentTimeMillis() + 6000;
            }
            else {
                if (cache.weapon.getCompactName().equals(weapon.getCompactName()))
                    return;

                cache.weapon = weapon;
                cache.time = System.currentTimeMillis() + 6000;
            }
        }
        else {
            this.playerCache.put(player.getUniqueId(), new PlayerStateCache(weapon, System.currentTimeMillis() + 6000));
        }

        if (weaponHolograms.containsKey(weapon.getCompactName())) {
            Hologram hologram = weaponHolograms.get(weapon.getCompactName());

            for (Hologram otherHologram : weaponHolograms.values()) {
                if (otherHologram.equals(hologram)) continue;
                otherHologram.getVisibilityManager().hideTo(player);
            }

            if (hologram.getVisibilityManager().isVisibleTo(player))
                hologram.getVisibilityManager().hideTo(player);
            else hologram.getVisibilityManager().showTo(player);
            return;
        }

        ItemStack weaponClone = weapon.createItemStack().clone();
        LinkedList<String> displayList = Lists.newLinkedList();

        double price = GTM.getItemManager().getItem(weapon.getCompactName()).getBuyPrice();

        //Name & Spacer.
        displayList.add(C.YELLOW + C.BOLD + weapon.getName() + C.RESET + C.GREEN + "  $" + C.BOLD + price + C.RESET);
        displayList.add(" ");

        //Load the weapon description into the List.
        for (String str : weapon.getDescription()) {
            displayList.add(C.GRAY + C.ITALIC + str + C.RESET);
        }

        //Spacer
        displayList.add(" ");

        //Load the weapon stats into the List.
        for (String str : weaponClone.getItemMeta().getLore()) {
            if (!ChatColor.stripColor(str).contains("::::::::")) continue;
            displayList.add(str + C.RESET);
        }

        ServerUtil.runTask(() -> {
            double x = 0, y = 0, z = 0;
            if (statue.hasMetadata("statue_X")) x = (double) statue.getMetadata("statue_X").get(0).value();
            if (statue.hasMetadata("statue_Y")) y = (double) statue.getMetadata("statue_Y").get(0).value();
            if (statue.hasMetadata("statue_Z")) z = (double) statue.getMetadata("statue_Z").get(0).value();

            Hologram hologram = HologramsAPI.createHologram(GTM.getInstance(), this.getLocationFromSize(((WeaponVisualStatue) weapon).getOrigin(statue.getWorld()), x, y, z, displayList.size()));
            for (String str : displayList) hologram.appendTextLine(str);
            hologram.getVisibilityManager().setVisibleByDefault(false);

            weaponHolograms.put(weapon.getCompactName(), hologram);

            for (Hologram otherHologram : weaponHolograms.values()) {
                if (otherHologram.equals(hologram)) continue;
                otherHologram.getVisibilityManager().hideTo(player);
            }

            hologram.getVisibilityManager().showTo(player);
        });
    }

    private boolean isInRegion(Player player) {
        Location loc = player.getLocation();
        return (loc.getX() > this.maxLoc.getX() && loc.getX() < this.minLoc.getX()) &&
                (loc.getZ() < this.maxLoc.getZ() && loc.getZ() > this.minLoc.getZ());
    }

    private boolean isLookingAt(Player player, LivingEntity entity) {
        Location eye = player.getEyeLocation();
        org.bukkit.util.Vector toEntity = entity.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.99D;
    }

    private class PlayerStateCache {
        public Weapon<?> weapon = null;
        public long time;

        public PlayerStateCache(Weapon<?> weapon, long time) {
            this.weapon = weapon;
            this.time = time;
        }

        public boolean expired() {
            return System.currentTimeMillis() >= time;
        }
    }
}
