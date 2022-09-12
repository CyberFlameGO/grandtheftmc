package net.grandtheftmc.gtm.items;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.users.CheatCode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private String name;
    private double cost;
    private int delay;
    private List<KitItem> contents;
    private KitItem helmet;
    private KitItem chestPlate;
    private KitItem leggings;
    private KitItem boots;
    private KitItem offHand;
    private CheatCode code;

    private String permission;

    public Kit(String name, double cost, int delay, List<KitItem> contents, KitItem helmet, KitItem chestPlate,
            KitItem leggings, KitItem boots, KitItem offHand, String permission, CheatCode code) {
        this.name = name;
        this.cost = cost;
        this.delay = delay;
        this.contents = contents;
        this.helmet = helmet;
        this.chestPlate = chestPlate;
        this.leggings = leggings;
        this.boots = boots;
        this.offHand = offHand;
        this.permission = permission;
        this.code = code;
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (KitItem item : this.contents) {
            GameItem g = item.getItem();
            if (g != null) {
                ItemStack i = g.getItem();
                i.setAmount(item.getAmount());
                items.add(i);
            }
        }
        return items;

    }

    public CheatCode getCode() {
        return code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<KitItem> getContents() {
        return this.contents;
    }

    public void setContents(List<KitItem> contents) {
        this.contents = contents;
    }

    public KitItem getHelmet() {
        return this.helmet;
    }

    public void setHelmet(KitItem helmet) {
        this.helmet = helmet;
    }

    public KitItem getChestPlate() {
        return this.chestPlate;
    }

    public void setChestPlate(KitItem chestPlate) {
        this.chestPlate = chestPlate;
    }

    public KitItem getLeggings() {
        return this.leggings;
    }

    public void setLeggings(KitItem leggings) {
        this.leggings = leggings;
    }

    public KitItem getBoots() {
        return this.boots;
    }

    public void setBoots(KitItem boots) {
        this.boots = boots;
    }

    public KitItem getOffHand() {
        return this.offHand;
    }

    public void setOffHand(KitItem offHand) {
        this.offHand = offHand;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Material getMaterial() {
        KitItem i = this.contents.get(0);
        if (i == null)
            return Material.STONE;
        GameItem g = i.getItem();
        if (g == null)
            return Material.STONE;
        ItemStack it = g.getItem();
        if (it == null)
            return Material.STONE;
        return it.getType();
    }

    public String getDisplayName() {
        UserRank rank = UserRank.getUserRankOrNull(this.name);
        return Utils.f(rank == null ? "&e&l" + String.valueOf(this.name.charAt(0)).toUpperCase() + this.name.substring(1)
                : rank.getColoredNameBold());
    }

    public boolean hasArmor() {
        return this.helmet != null || this.chestPlate != null || this.leggings != null || this.boots != null;
    }

}
