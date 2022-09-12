package net.grandtheftmc.gtm.listeners;

import java.util.HashSet;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;

/**
 * Created by Timothy Lampen on 7/6/2017.
 */
public class ArmorEquip implements Listener {

    @EventHandler
    public void onPickupCustomLeatherArmor(PlayerPickupItemEvent event) {
        ItemStack is = event.getItem().getItemStack();
//        new Location("", x,y,z),
        if(is.getType().toString().contains("LEATHER") && is.getType() != Material.LEATHER) {
            if(!isCustomColor(((LeatherArmorMeta)is.getItemMeta()).getColor()))
                return;
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEquipCustom(ArmorEquipEvent event) {
    	
        Player player = event.getPlayer();
        
        // only handle custom equips
        if(event.getType()!=EquipArmorType.CUSTOM)
            return;
        
        // only handle drag events 
        if(event.getMethod()!= ArmorEquipEvent.EquipMethod.DRAG && event.getMethod() != ArmorEquipEvent.EquipMethod.DEATH)
            return;
        
        ItemStack newArmor = event.getNewArmorPiece();
        ItemStack oldArmor = event.getOldArmorPiece();

        ItemStack toSlot = newArmor;
        ItemStack toHand = oldArmor;
        if(oldArmor.getType().toString().contains("LEATHER")) {
            LeatherArmorMeta meta = (LeatherArmorMeta)oldArmor.getItemMeta();
            if(isCustomColor(meta.getColor()))
                toHand = getGameItemFromLeather(meta, event.getArmorSlot());
        }
        if(newArmor.getType() == Material.DIAMOND_SWORD) {
            ItemStack is = null;
            switch (event.getArmorSlot()) {
                case BOOTS:
                    is = new ItemStack(Material.LEATHER_BOOTS);
                    break;
                case LEGGINGS:
                    is = new ItemStack(Material.LEATHER_LEGGINGS);
                    break;
                case CHESTPLATE:
                    is = new ItemStack(Material.LEATHER_CHESTPLATE);
                    break;
                case HELMET:
                    is = new ItemStack(Material.LEATHER_HELMET);
                    break;
            }
            LeatherArmorMeta meta = (LeatherArmorMeta)is.getItemMeta();
            meta.setColor(getColorFromSwordDurability(newArmor.getDurability()));
            is.setItemMeta(meta);
            toSlot = is;
        }

        if(newArmor.getType() != Material.AIR && event.getArmorSlot()!=getArmorSlotFromSwordDura(newArmor.getDurability()) && event.getMethod() == ArmorEquipEvent.EquipMethod.DRAG) {
            event.setCancelled(true);
            return;
        }

        ItemStack finalToHand = toHand;
        ItemStack finalToSlot = toSlot;
        
        if (event.getMethod() != ArmorEquipEvent.EquipMethod.DEATH){
        	event.setCancelled(true);
        	player.getInventory().setItem(event.getArmorSlot().getSlot(), finalToSlot);
            player.setItemOnCursor(finalToHand);
        }
        
        ServerUtil.runTaskLater(() -> {
            if(event.getMethod()== ArmorEquipEvent.EquipMethod.DEATH) {
                player.getWorld().dropItem(player.getLocation(), finalToHand);
                // TODO note: cancelling the event here does nothing, as its ran 2 ticks later...?
                event.setCancelled(true);
            }

            player.updateInventory();
        }, 2);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event){
        if(event.getType()== EquipArmorType.CUSTOM)
            return;
        Player player = event.getPlayer();
        player.setMaxHealth(20);
        ItemStack is = event.getNewArmorPiece();
        if(is!=null && is.getAmount()>1) {
            player.sendMessage(Lang.GTM.f("&7You cannot equip stacked armor!"));
            event.setCancelled(true);
            return;
        }
        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for(ItemStack is : player.getInventory().getArmorContents()){
                    for(ArmorUpgrade upgrade : ArmorUpgrade.getArmorUpgrades(is)){
                        if(upgrade==ArmorUpgrade.ENHANCED){
                            player.setMaxHealth(player.getMaxHealth()+10);
                        }
                        HashSet<PotionEffect> effects = upgrade.getPotionEffects();
                        for (PotionEffect effect : effects) {
                            if(player.hasPotionEffect(effect.getType())){
                                int base = player.getPotionEffect(effect.getType()).getAmplifier()==0 ? 1 : player.getPotionEffect(effect.getType()).getAmplifier();
                                int effectAmp = effect.getAmplifier()==0 ? 1 : effect.getAmplifier();
                                player.removePotionEffect(effect.getType());
                                player.addPotionEffect(new PotionEffect(effect.getType(), Integer.MAX_VALUE, base+effectAmp));
                            }
                            else{
                                player.addPotionEffect(effect);
                            }
                        }
                    }
                }
            }
        }.runTaskLater(GTM.getInstance(), 1);
    }

    /*
    *
    *
    *
    * If anyone can suggest a better way to handle this, I am open to suggestions. ~ Tim.
    *
    *
    * */

    public static boolean isCustomColor(Color c) {
        if(c.getRed()==153 && c.getGreen()==0 && c.getBlue()==0)//is santa
            return true;
        else if(c.getRed()==0 && c.getGreen()==77 && c.getBlue()==26)//is elf
            return true;
        else if(c.getRed()==102 && c.getGreen()==51 && c.getBlue()==0)// is rudolf
            return true;
        return false;
    }

    private EquipArmorType getArmorSlotFromSwordDura(short durability){
        switch (durability) {
            case 1007:
            case 1008:
            case 1009:
                return EquipArmorType.HELMET;
            case 1014:
            case 1017:
            case 1020:
                return EquipArmorType.CHESTPLATE;
            case 1015:
            case 1018:
            case 1021:
                return EquipArmorType.LEGGINGS;
            case 1016:
            case 1019:
            case 1022:
                return EquipArmorType.BOOTS;
        }
        return null;
    }

    /**
     * @param m the item meta of the leather armor
     * @param type the slot of the item
     * @return the diamond sword item that represents the item
     */
    private ItemStack getGameItemFromLeather(LeatherArmorMeta m, EquipArmorType type) {
        Color c = m.getColor();

        if(c.getRed()==153 && c.getGreen()==0 && c.getBlue()==0) {//is santa
            switch (type) {
                case BOOTS:
                    return GTM.getItemManager().getItem("santaboots").getItem();
                case LEGGINGS:
                    return GTM.getItemManager().getItem("santapants").getItem();
                case CHESTPLATE:
                    return GTM.getItemManager().getItem("santatunic").getItem();
                case HELMET:
                    return GTM.getItemManager().getItem("santahat").getItem();
            }
        }
        else if(c.getRed()==0 && c.getGreen()==77 && c.getBlue()==26) {//is elf
            switch (type) {
                case BOOTS:
                    return GTM.getItemManager().getItem("elfboots").getItem();
                case LEGGINGS:
                    return GTM.getItemManager().getItem("elfpants").getItem();
                case CHESTPLATE:
                    return GTM.getItemManager().getItem("elftunic").getItem();
                case HELMET:
                    return GTM.getItemManager().getItem("elfhat").getItem();
            }
        }
        else if(c.getRed()==102 && c.getGreen()==51 && c.getBlue()==0) {// is rudolf
            switch (type) {
                case BOOTS:
                    return GTM.getItemManager().getItem("rudolfboots").getItem();
                case LEGGINGS:
                    return GTM.getItemManager().getItem("rudolfpants").getItem();
                case CHESTPLATE:
                    return GTM.getItemManager().getItem("rudolftunic").getItem();
                case HELMET:
                    return GTM.getItemManager().getItem("rudolfhat").getItem();
            }
        }
        return null;
    }

    /**
     * @param durability the durability of the sword
     * @return the color that is associated with the diamond sword 'armor' to set the leather
     */
    private Color getColorFromSwordDurability(short durability){
        switch (durability) {
            case 1009:
            case 1014:
            case 1015:
            case 1016:
                return Color.fromRGB(153, 0, 0);//santa
            case 1008:
            case 1017:
            case 1018:
            case 1019:
                return Color.fromRGB(0, 77, 26);//elf
            case 1007:
            case 1020:
            case 1021:
            case 1022:
                return Color.fromRGB(102, 51, 0);//rudolf
        }
        return Color.BLACK;
    }
}
