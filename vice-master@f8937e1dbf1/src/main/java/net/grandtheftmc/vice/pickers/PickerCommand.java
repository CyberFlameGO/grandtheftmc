package net.grandtheftmc.vice.pickers;

import com.j0ach1mmall3.jlib.inventory.JLibItem;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            s.sendMessage(Utils.f("&c/picker mechanic"));
            s.sendMessage(Utils.f("&c/picker cabdriver"));
            s.sendMessage(Utils.f("&c/picker warden"));
            s.sendMessage(Utils.f("&c/picker copsalary"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "mechanic": {
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(),
                        EntityType.ARMOR_STAND);
                armorStand.setHelmet(new JLibItem.Builder().withType(Material.LEATHER_HELMET).withColor(Color.fromRGB(165, 42, 42)).build().getItemStack());
                armorStand.setChestplate(Vice.getItemManager().getItem("shirt").getItem());
                armorStand.setLeggings(Vice.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(Vice.getItemManager().getItem("nikes").getItem());
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
                armorStand.setChestplate(Vice.getItemManager().getItem("shirt").getItem());
                armorStand.setLeggings(Vice.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(Vice.getItemManager().getItem("nikes").getItem());
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
                armorStand.setChestplate(Vice.getItemManager().getItem("kevlarvest").getItem());
                armorStand.setLeggings(Vice.getItemManager().getItem("pants").getItem());
                armorStand.setBoots(Vice.getItemManager().getItem("nikes").getItem());
                armorStand.setItemInHand(Vice.getItemManager().getItem("nightstick").getItem());
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
            case "copsalary": {
                Vice.getPickerManager().addCopSalary(player.getLocation());
                s.sendMessage(Lang.COPS.f("&7You added the cop salary picker!"));
                return true;
            }
            default:
                s.sendMessage(Utils.f("&c/picker mechanic"));
                s.sendMessage(Utils.f("&c/picker cabdriver"));
                s.sendMessage(Utils.f("&c/picker warden"));
                s.sendMessage(Utils.f("&c/picker copsalary"));
                return true;
        }
    }
}