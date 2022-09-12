package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponShootEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class WeaponShoot implements Listener {
    private final List<String> carGuns = Arrays.asList("Pistol", "CombatPistol", "HeavyPistol", "MarksmanPistol",
            "TearGas", "StickyBomb", "Grenade", "MolotovCocktail", "ProximityMine", "MicroSMG", "SMG");

    @EventHandler(ignoreCancelled = true)
    public void onRangedWeaponShootEvent(RangedWeaponShootEvent event) {
        if (event.getLivingEntity().getType() != EntityType.PLAYER) return;
        Weapon weapon = event.getWeapon();
        String weaponName = weapon.getName();
        Player shooter = (Player) event.getLivingEntity();
        boolean inCar = shooter.getVehicle() != null && shooter.getVehicle().hasMetadata("WastedVehiclePassenger");
//        if (weapon.getItemStack().getDurability() != 0) {
//            if (weapon.getName().equalsIgnoreCase("GoldMinigun")
//                    || weapon.getName().equalsIgnoreCase("Flamethrower")) return;
//            weapon.getItemStack().setDurability((short) 0);
//        }
        if(inCar && !carGuns.contains(weaponName)) {
            shooter.sendMessage(Lang.HEY.f("&7Weapon cannot be used in Car"));
            event.setCancelled(true);
            return;
        }
        if (weaponName.contains("NetLauncher")) {
            if(shooter.getWorld().getName().equalsIgnoreCase("spawn")) {
                shooter.sendMessage(Lang.HEY.f("&7The Net Launcher cannot be used in spawn"));
                event.setCancelled(true);
            }
        }
    }
}
