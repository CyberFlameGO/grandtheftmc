package net.grandtheftmc.gtm.drugs;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.grandtheftmc.gtm.drugs.internal.service.Service;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Remco on 25-3-2017.
 */
public class DrugService extends Service {

    //All items, sorted on:
    private final Table<DrugParam[], DrugItem, Drug> items = HashBasedTable.create();

    public DrugService() {
        super("Drug Item Service", new DrugHelper());
    }

    public Drug addDrug(Drug drug, DrugItem item, DrugParam... params) {
        if (this.items.columnKeySet().stream().noneMatch((match) -> false)) {
            items.put(params, item, drug);
            return drug;
        }
        return null;
    }

    public final Collection<Drug> getDrugs() {
        return items.values();
    }

    public final Set<DrugItem> getItems() {
        return items.columnKeySet();
    }

    public Drug getDrug(ItemStack item) {
        return getDrug(DrugItem.getByItemStack(item));
    }

    public Drug getDrug(DrugItem drugItem) {
        return items.columnMap().values().stream().map(Map::values).filter(drugs -> drugs.stream().findFirst().isPresent() && drugs.stream().anyMatch(drugItem::isValid)).map((drug) -> drug.stream().findFirst().get()).findFirst().orElse(null);
    }

    public Optional<Drug> getDrug(String name) {
        Optional<Drug> drug = items.values().stream().filter(targetDrug -> targetDrug.getName().equalsIgnoreCase(name)).findFirst();
        if (!drug.isPresent()) return Optional.empty();
        return drug;
    }

    public Optional<DrugItem> getDrugItem(String name) {
        Optional<DrugItem> drug = items.columnKeySet().stream().filter(drugItem -> drugItem.getItemStack().getItemMeta().getDisplayName().equals(name)).findFirst();
        if (!drug.isPresent()) return Optional.empty();
        return drug;
    }

    public Set<DrugItem> getAllDrugItems(){
        return items.columnKeySet();
    }

    public Table<DrugParam[], DrugItem, Drug> getRawItems(){
        return items;
    }

}
