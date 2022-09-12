package net.grandtheftmc.vice.listeners;


import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.events.EquipArmorType;
import net.grandtheftmc.vice.items.ArmorUpgrade;
import net.grandtheftmc.vice.events.ArmorEquipEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Timothy Lampen on 7/6/2017.
 */
public class ArmorEquip implements Listener {

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ArmorUpgrade.reloadArmorUpgrades(player);
    }
}
