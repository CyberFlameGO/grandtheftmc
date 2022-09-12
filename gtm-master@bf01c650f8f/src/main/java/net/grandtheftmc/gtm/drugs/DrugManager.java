package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.drugs.example.*;
import net.grandtheftmc.gtm.drugs.example.lsd.LSD;
import net.grandtheftmc.gtm.drugs.internal.manager.Manager;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DrugManager extends Manager {
    private EffectManager effectManager;
    private DrugDealer drugDealer;
    private LockedBlocks lockedBlocks;
    private final Set<UUID> ods = new HashSet<>();
    private Set<UUID> unmoveable = new HashSet<>();

    public DrugManager() {
        super("Drug Manager", new AtomicInteger(1), new DrugService());
    }

    @Override
    public void start() {
        DrugService service = (DrugService) getService();

        ItemStack roofiedChocolate = DrugUtil.setDisplayName(new ItemStack(Material.RAW_FISH, 1), Utils.f("&b&lRoofied Chocolate"));
        roofiedChocolate = DrugUtil.addLore(roofiedChocolate, "&7Just an inconspicuous piece of chocolate");
        service.addDrug(new RoofiedChocolate(), new DrugItem(roofiedChocolate, new RoofiedChocolate()));

        ItemStack mdma = DrugUtil.setDisplayName(new ItemStack(Material.RECORD_11, 1), Utils.f("&e&lMDMA"));
        mdma = DrugUtil.hideDurability(mdma);
        mdma = DrugUtil.addLore(mdma, "&7Take me to a higher place!");
        service.addDrug(new MDMA(), new DrugItem(mdma, new MDMA()));

        ItemStack lsd = DrugUtil.setDisplayName(new ItemStack(Material.GOLD_RECORD, 1), Utils.f("&d&lLSD"));
        lsd = DrugUtil.hideDurability(lsd);
        service.addDrug(new LSD(), new DrugItem(lsd, new LSD()));

        ItemStack weed = DrugUtil.setDisplayName(new ItemStack(Material.RECORD_5, 1), Utils.f("&2&lWeed Buds"));
        weed = DrugUtil.hideDurability(weed);
        service.addDrug(new Weed(), new DrugItem(weed, new Weed()));

        ItemStack joint = DrugUtil.setDisplayName(new ItemStack(Material.RECORD_3, 1), Utils.f("&2&lJoint"));
        joint = DrugUtil.hideDurability(joint);
        service.addDrug(new Joint(), new DrugItem(joint, new Joint()));

        ItemStack steroids = DrugUtil.setDisplayName(new ItemStack(Material.FLINT_AND_STEEL, 1), Utils.f("&c&lBull Shark Testosterone"));
        steroids = DrugUtil.hideDurability(steroids);
        steroids = DrugUtil.addLore(steroids, "&7Don’t mess with me!");
        steroids.setDurability((short) 3);
        service.addDrug(new Steroids(), new DrugItem(steroids, new Steroids()));

        ItemStack alcohol = DrugUtil.setDisplayName(new ItemStack(Material.FLINT_AND_STEEL, 1), Utils.f("&e&lBeer"));
        alcohol = DrugUtil.hideDurability(alcohol);
        alcohol.setDurability((short) 1);
        service.addDrug(new Alcohol(), new DrugItem(alcohol, new Alcohol()));

        ItemStack meth = DrugUtil.setDisplayName(new ItemStack(Material.FLINT_AND_STEEL, 1), Utils.f("&c&lMeth Pipe"));
        meth = DrugUtil.hideDurability(meth);
        meth.setDurability((short) 4);
        service.addDrug(new Meth(), new DrugItem(meth, new Meth()));

        ItemStack cocaine = DrugUtil.setDisplayName(new ItemStack(Material.RECORD_4, 1), Utils.f("&f&lCocaine"));
        cocaine = DrugUtil.hideDurability(cocaine);
        service.addDrug(new Cocaine(), new DrugItem(cocaine, new Cocaine()));

        ItemStack heroin = DrugUtil.setDisplayName(new ItemStack(Material.FLINT_AND_STEEL, 1), Utils.f("&b&lHeroin Syringe"));
        heroin = DrugUtil.hideDurability(heroin);
        heroin = DrugUtil.addLore(heroin, "&7Stick this thing in my arm!");
        heroin.setDurability((short) 5);
        service.addDrug(new Heroin(), new DrugItem(heroin, new Heroin()));

        this.drugDealer = new DrugDealer();
        this.effectManager = new EffectManager();
        this.lockedBlocks = new LockedBlocks();
    }

    @Override
    public void stop() {
        this.drugDealer.stop();
        this.lockedBlocks.save();
    }

    @Override
    public boolean destroy() {
        return false;
    }

    public DrugDealer getDrugDealer() {//added 'get' because I dont think it makes sense to do .drugDealer().drugDealer() to get drug dealer entity
        return this.drugDealer;
    }

    public EffectManager getEffectManager(){
        return this.effectManager;
    }

    public LockedBlocks getLockedBlocks() {
        return this.lockedBlocks;
    }

    /**
    * @param uuid the uuid of the player
     *
     * @return if the player CAN OD again
    */
    public boolean inOD(UUID uuid){
        return this.ods.contains(uuid);
    }


    /***
     * @param uuid uuid of the player
     *
     * Use when a player has OD'd (at the start of the OD sequence)
     */
    public void addOD(UUID uuid){
        if(!this.ods.contains(uuid)){
            this.ods.add(uuid);
        }
    }

    /**
     * @param uuid the player
     *
     * Use after the OD has completed (at the end of the same OD sequence)
     */
    public void removeOD(UUID uuid){
        if(this.ods.contains(uuid)){
            this.ods.remove(uuid);
        }
    }

}
