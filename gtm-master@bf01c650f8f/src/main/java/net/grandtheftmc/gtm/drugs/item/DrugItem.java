package net.grandtheftmc.gtm.drugs.item;

import com.j0ach1mmall3.jlib.inventory.JLibItem;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class DrugItem {

    private final static DrugService drugService = (DrugService) GTM.getDrugManager().getService();

    private ItemStack itemStack;
    private Drug drug;

    public DrugItem(Material material, int amount, UUID owner, Drug drug) {
        this.itemStack = new ItemStack(material, amount);
        this.drug = drug;
        apply();
    }

    public DrugItem(ItemStack itemStack, UUID owner, Drug drug) {
        this.itemStack = itemStack.clone();
        this.drug = drug;
        apply();
    }


    public DrugItem(ItemStack itemStack, Drug drug) {
        this.itemStack = itemStack.clone();
        this.drug = drug;
        apply();
    }

    public DrugItem(JLibItem item, Drug drug){
        this.itemStack = item.getItemStack();
        this.drug = drug;
        apply();
    }

    protected DrugItem(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public final static DrugItem getByItemStack(ItemStack itemStack) {
        ItemStack clone = itemStack.clone();
        clone.setAmount(1);
        DrugItem base = new DrugItem(clone);
        if (drugService.getDrugs().stream().anyMatch(base::isValid) || drugService.getItems().contains(base)) return base;
        return null;
    }

    public static DrugItem getByDrug(Drug drug){
        if(drugService.getRawItems().rowMap().values().stream().anyMatch(drugItemDrugMap -> drugItemDrugMap.containsValue(drug))){
            Map<DrugItem, Drug> drugs = drugService.getRawItems().rowMap().values().stream().filter(drugItemDrugMap ->  drugItemDrugMap.containsValue(drug)).findFirst().get();
            for(DrugItem item : drugs.keySet()){
                if(drugs.get(item).equals(drug)){
                    return item;
                }
            }
        }
        return null;
    }

    public void apply() {
        if (!isValid(drug)) {
            net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
            NBTTagString drugName = new NBTTagString(drug.getName());
            assert tag != null;
            tag.set("drugName", drugName);
            nmsCopy.setTag(tag);
            this.itemStack = CraftItemStack.asBukkitCopy(nmsCopy);

        }
    }

    public final ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isValid(Drug drug) {
        if(itemStack!=null && drug!=null) {
            net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
            if(nmsCopy!=null) {
                NBTTagCompound tag = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
                if ((tag != null ? tag.get("drugName") : null) != null) {
                    String name = tag.getString("drugName");
                    return drugService.getDrugs().stream().map(Drug::getName).anyMatch(item -> item.equalsIgnoreCase(name) && drug.getName().equalsIgnoreCase(item));
                }
            }
        }
        return false;
    }

    public Drug drug(){
        return drug;
    }
}
