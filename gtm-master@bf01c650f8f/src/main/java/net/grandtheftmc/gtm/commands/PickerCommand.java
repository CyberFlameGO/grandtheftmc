package net.grandtheftmc.gtm.commands;

import com.j0ach1mmall3.jlib.inventory.JLibItem;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.Kit;
import net.grandtheftmc.gtm.users.JobMode;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PickerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.hasPermission("picker.use")) {
            s.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f(Lang.GTM + "&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/picker mode <jobMode>"));
            s.sendMessage(Utils.f("&c/picker mechanic"));
            s.sendMessage(Utils.f("&c/picker cabdriver"));
            s.sendMessage(Utils.f("&c/picker warden"));
            s.sendMessage(Utils.f("&c/picker heads"));
            s.sendMessage(Utils.f("&c/picker dealer"));
            s.sendMessage(Utils.f("&c/picker weapondealer"));
            s.sendMessage(Utils.f("&c/picker coinvendor"));


            return true;
        }
        switch (args[0].toLowerCase()) {
            case "coinvendor": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.YELLOW).build().getItemStack());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setCustomName(Utils.f("&e&lCoin Vendor"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.CASINO.f("&7You created a coin vendor!"));
                return true;
            }
            case "weapondealer": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.BLACK).build().getItemStack());
                armorStand.setChestplate(GTM.getItemManager().getItem("ceramicvest").getItem());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(GTM.getItemManager().getItem("chainsaw").getItem());
                armorStand.setCustomName(Utils.f("&e&lArms Dealer"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.HEAD_AUCTION.f("&7You created an Arms Dealer!"));
                return true;
            }
            case "heads": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.GREEN).build().getItemStack());
                armorStand.setChestplate(GTM.getItemManager().getItem("kevlarvest").getItem());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(new JLibItem.Builder().withType(Material.SKULL_ITEM).withDurability((short) 3).withOwner("Samuri629").build().getItemStack());
                armorStand.setCustomName(Utils.f("&e&lHead Salesman"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.HEAD_AUCTION.f("&7You created a Head Salesman!"));
                return true;
            }
            case "mode": {
                JobMode mode = JobMode.getModeOrNull(args[1]);
                if (mode == null) {
                    s.sendMessage(Lang.JOBS.f("&7That Job Mode does not exist!"));
                    return true;
                }
                Kit kit = GTM.getItemManager().getKit(mode.getName());
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That Kit does not exist!"));
                    return true;
                }
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                if (kit.getHelmet() != null)
                    armorStand.setHelmet(kit.getHelmet().getItem().getItem());
                if (kit.getChestPlate() != null)
                    armorStand.setChestplate(kit.getChestPlate().getItem().getItem());
                if (kit.getLeggings() != null)
                    armorStand.setLeggings(kit.getLeggings().getItem().getItem());
                if (kit.getBoots() != null)
                    armorStand.setBoots(kit.getBoots().getItem().getItem());
                if (kit.getOffHand() != null)
                    armorStand.setItemInHand(kit.getOffHand().getItem().getItem());
                armorStand.setCustomName(mode.getColoredNameBold());
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.KITS.f("&7You created a Mode Picker for Job Mode " + mode.getColoredNameBold()
                        + "&7!"));
                return true;
            }
            case "mechanic": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.fromRGB(165, 42, 42)).build().getItemStack());
                armorStand.setChestplate(GTM.getItemManager().getItem("shirt").getItem());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(new ItemStack(Material.WORKBENCH));
                armorStand.setCustomName(Utils.f("&4&lMechanic"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.VEHICLES.f("&7You created a Mechanic!"));
                return true;
            }
            case "cabdriver": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.YELLOW).build().getItemStack());
                armorStand.setChestplate(GTM.getItemManager().getItem("shirt").getItem());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(new ItemStack(Material.WATCH));
                armorStand.setCustomName(Utils.f("&e&lCab Driver"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.VEHICLES.f("&7You created a Cab Driver!"));
                return true;
            }
            case "warden":
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.BLACK).build().getItemStack());
                armorStand.setChestplate(GTM.getItemManager().getItem("kevlarvest").getItem());
                armorStand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(GTM.getItemManager().getItem("nightstick").getItem());
                armorStand.setCustomName(Utils.f("&c&lWarden"));
                armorStand.setCustomNameVisible(true);
                armorStand.setAI(false);
                armorStand.setCollidable(false);
                armorStand.setCanPickupItems(true);
                armorStand.setGravity(false);
                armorStand.setRemoveWhenFarAway(false);
                armorStand.setBasePlate(false);
                armorStand.setArms(true);
                s.sendMessage(Lang.VEHICLES.f("&7You created a Warden!"));
                return true;
            default:
                s.sendMessage(Utils.f("&c/picker mode <jobMode>"));
                s.sendMessage(Utils.f("&c/picker mechanic"));
                s.sendMessage(Utils.f("&c/picker cabdriver"));
                s.sendMessage(Utils.f("&c/picker warden"));
                s.sendMessage(Utils.f("&c/picker dealer"));
                return true;
        }
    }
}