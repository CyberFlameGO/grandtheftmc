package net.grandtheftmc.gtm.lootcrates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.j0ach1mmall3.jlib.methods.Random;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;

public class LootCrate {

    private Location location;
    private long timer;
    private Hologram hologram;
    private final List<TextLine> textLines = new ArrayList<>();

    public LootCrate(Location location) {
        this.location = location;
        this.timer = 60;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTimer() {
        return this.timer;
    }

    public void resetTimer() {
        this.timer = GTM.getCrateManager().getCooldown() * 60L;
    }

    public void tick() {
        if (this.timer > 0) {
            if(!this.location.getChunk().isLoaded()) {
                this.timer--;
                return;
            }

//            if(Stream.of(this.location.getChunk().getEntities())
//                    .filter(entity -> entity instanceof Player && entity.getLocation().distanceSquared(this.location) < 225)
//                    .count() <= 0) {
//                this.timer--;
//                return;
//            }

          /*  if(Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getLocation().getWorld().getName().equals(location.getWorld().getName())
                        && player.getLocation().distanceSquared(location) <= 200)
                    .count() <= 0) {
                this.timer--;
                return;
            }*/

            this.updateHologram("&7Restocks in &a" + Utils.timeInSecondsToText(this.timer) + "&7!");
            this.timer--;
            this.updateVisibility();
        } else if (this.timer == 0) {
            this.closeOutViewers(); //Close out inventory viewers before restocking.
            this.restock();
            this.updateVisibility();
            timer = -1;
        }
        else{
            this.updateVisibility();
        }
    }

    public void restock() {
        BlockState state = this.location.getBlock().getState();
        if (!(state instanceof Chest)) {
            GTM.log("Loot Chest at location " + Utils.blockLocationToString(this.location) + " is not a Chest!");
            this.updateHologram("&c&lERROR: Please contact an admin!");
            this.timer = -1;
            return;
        }
        Chest chest = (Chest) state;
        chest.setCustomName(Utils.f("&e&lLoot Crate"));

//        CraftChest craftChest = (CraftChest) chest;
//        TileEntityChest tileEntityChest = craftChest.getTileEntity();
//        tileEntityChest.setCustomName(Utils.f("&e&lLoot Crate"));

        Inventory inv = chest.getBlockInventory();
        inv.clear();
        GTM.getCrateManager().getItems().stream().filter(item -> Utils.calculateChance(item.getChance())).forEach(item -> {

            // attempt to get weapon, if it is one
            Optional<Weapon<?>> weaponOpt = GTMGuns.getInstance().getWeaponManager().getWeapon(item.getGameItem().getName());
            
            if (weaponOpt.isPresent()){
            	ItemStack stack = weaponOpt.get().createItemStack(item.getStars(), null);
            	stack.setAmount(Utils.randomNumber(item.getMin(), item.getMax()));
            	Utils.putItemInInventoryRandomly(inv, stack);
            }
            else {
                /**
                 * TODO: Different weapon skin rarities
                 */
                if (item.getItemName().startsWith("weapon_skin_")) {
                    this.restockWeaponSkin(inv, item);
                } else {
                    ItemStack stack = item.getGameItem().getItem();
                    stack.setAmount(Utils.randomNumber(item.getMin(), item.getMax()));
                    Utils.putItemInInventoryRandomly(inv, stack);
                }
            }
        });
        this.updateHologram("&7Restocked!");
        this.timer = -1;
    }
    
    private void restockWeaponSkin(Inventory inv, LootItem item) {
        List<Weapon<?>> weapons = new ArrayList<Weapon<?>>(GTMGuns.getInstance().getWeaponManager().getRegisteredWeapons());
        Weapon<?> randomWeapon = weapons.get(Random.getInt(weapons.size()));
        WeaponSkin randomSkin = null;

        if (randomWeapon == null || randomWeapon.getWeaponSkins() == null || randomWeapon.getWeaponSkins().length <= 1) {
            this.restockWeaponSkin(inv, item);
        }
        
        short[] commonSkins = {
                5, 7
        };

        short[] rareSkins = {
                2, 6
        };

        if (item.getItemName().endsWith("common")) {
            randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, commonSkins[Random.getInt(0, commonSkins.length)]);
        } else if (item.getItemName().endsWith("rare")) {
            randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, rareSkins[Random.getInt(0, rareSkins.length)]);
        }

        Utils.putItemInInventoryRandomly(inv, GTM.getWeaponSkinManager().createSkinItem(randomWeapon, randomSkin));
    }

    private void updateHologram(String text) {
        if (this.hologram == null) {
            this.hologram = HologramsAPI.createHologram(GTM.getInstance(), this.location.clone().add(0.5, 2, 0.5));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&e&lLoot Crate")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f(text)));
            this.hologram.getVisibilityManager().setVisibleByDefault(false);
        } else
            this.textLines.get(1).setText(Utils.f(text));

    }

    private void updateVisibility() {
        VisibilityManager v = this.hologram.getVisibilityManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(player.getWorld(), this.location.getWorld()) && player.getLocation().distanceSquared(this.location) < 200) {
                if (!v.isVisibleTo(player))
                    v.showTo(player);
            } else if (v.isVisibleTo(player))
                v.hideTo(player);
        }
    }

    public void removeHologram() {
        this.hologram.delete();
        this.hologram = null;
        this.textLines.clear();

    }

    private void closeOutViewers() {
        if (this.location == null) return;
        Block block = this.location.getBlock();
        if (block.getType() != Material.CHEST) return;
        Chest chest = (Chest) block.getState();
        chest.getInventory().getViewers().forEach(HumanEntity::closeInventory);
    }
}
