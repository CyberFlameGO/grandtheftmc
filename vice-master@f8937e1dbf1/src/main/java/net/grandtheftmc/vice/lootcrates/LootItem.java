package net.grandtheftmc.vice.lootcrates;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import net.grandtheftmc.vice.items.GameItem;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class LootItem {

    private String item;
    private double chance;
    private int min;
    private int max;
    private boolean isDrug;
    private Area.DropType type;

    public LootItem(String item, double chance, int min, int max, boolean isDrug, Area.DropType type) {
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
        this.isDrug = isDrug;
        this.type = type;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public GameItem getGameItem() {
        if(!isDrug){
            return Vice.getItemManager().getItem(this.item);
        } else {
            Optional<Drug> drug = ((DrugService) Vice.getDrugManager().getService()).getDrug(item);
            if (!drug.isPresent()) {
                return null;
            }
            DrugItem itema = DrugItem.getByDrug(drug.get());
            ItemStack is = itema.getItemStack();
            return new GameItem(item, is, is.getItemMeta().getDisplayName());
        }
    }

    public boolean isDrug(){
        return isDrug;
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Area.DropType getDropType() {
        return type;
    }
}
