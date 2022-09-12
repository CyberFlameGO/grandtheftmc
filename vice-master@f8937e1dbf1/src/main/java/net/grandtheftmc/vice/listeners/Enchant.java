package net.grandtheftmc.vice.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Timothy Lampen on 7/29/2017.
 */
public class Enchant implements Listener {
    private List<Material> list = Arrays.asList(Material.DIAMOND_BOOTS
            , Material.DIAMOND_CHESTPLATE
            , Material.DIAMOND_HELMET
            , Material.DIAMOND_LEGGINGS
            , Material.DIAMOND_SWORD
            , Material.CHAINMAIL_BOOTS
            , Material.CHAINMAIL_CHESTPLATE
            , Material.CHAINMAIL_HELMET
            , Material.CHAINMAIL_LEGGINGS
            , Material.GOLD_BOOTS
            , Material.GOLD_CHESTPLATE
            , Material.GOLD_HELMET
            , Material.GOLD_LEGGINGS
            , Material.GOLD_SWORD
            , Material.IRON_BOOTS
            , Material.IRON_CHESTPLATE
            , Material.IRON_HELMET
            , Material.IRON_LEGGINGS
            , Material.IRON_SWORD
            , Material.LEATHER_BOOTS
            , Material.LEATHER_CHESTPLATE
            , Material.LEATHER_HELMET
            , Material.LEATHER_LEGGINGS
            , Material.STONE_SWORD
            , Material.WOOD_SWORD);

    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent event) {
        if (this.list.contains(event.getItem().getType()))
            event.setCancelled(true);
    }

}
