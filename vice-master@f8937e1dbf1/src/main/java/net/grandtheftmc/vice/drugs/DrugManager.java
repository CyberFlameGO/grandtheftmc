package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.example.*;
import net.grandtheftmc.vice.drugs.internal.manager.Manager;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DrugManager extends Manager {
    private EffectManager effectManager;
    private LockedBlocks lockedBlocks;
    private final Set<UUID> ods = new HashSet<>();
    private final Set<UUID> unmoveable = new HashSet<>();

    public DrugManager() {
        super("Drug Manager", new AtomicInteger(1), new DrugService());
    }

    @Override
    public void start() {
        DrugService service = (DrugService) this.getService();
        ItemStack mdma = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 755), Utils.f("&e&lMDMA"));
        mdma = DrugUtil.hideDurability(mdma);
        mdma = DrugUtil.addLore(mdma, "&7Take me to a higher place!");
        service.addDrug(new MDMA(), new DrugItem(mdma, new MDMA()));

        ItemStack lsd = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SPADE, 1, (short) 16), Utils.f("&d&lLSD"));
        lsd = DrugUtil.hideDurability(lsd);
        DrugItem drugItem = new DrugItem(lsd, new LSD());
        service.addDrug(new LSD(), drugItem);


        ItemStack weed = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SPADE, 1, (short) 7), Utils.f("&2&lWeed Buds"));
        weed = DrugUtil.hideDurability(weed);
        service.addDrug(new Weed(), new DrugItem(weed, new Weed()));

        ItemStack joint = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 752), Utils.f("&2&lJoint"));
        joint = DrugUtil.hideDurability(joint);
        service.addDrug(new Joint(), new DrugItem(joint, new Joint()));

        ItemStack steroids = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 758), Utils.f("&c&lBull Shark Testosterone"));
        steroids = DrugUtil.hideDurability(steroids);
        steroids = DrugUtil.addLore(steroids, "&7Don’t mess with me!");
        service.addDrug(new Steroids(), new DrugItem(steroids, new Steroids()));

        ItemStack alcohol = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SPADE, 1, (short) 2), Utils.f("&e&lBeer"));
        alcohol = DrugUtil.hideDurability(alcohol);
        service.addDrug(new Alcohol(), new DrugItem(alcohol, new Alcohol()));

        ItemStack meth = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 759), Utils.f("&c&lMeth Pipe"));
        meth = DrugUtil.hideDurability(meth);
        service.addDrug(new Meth(), new DrugItem(meth, new Meth()));

        ItemStack cocaine = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SPADE, 1, (short) 14), Utils.f("&f&lCocaine"));
        cocaine = DrugUtil.hideDurability(cocaine);
        service.addDrug(new Cocaine(), new DrugItem(cocaine, new Cocaine()));

        ItemStack heroin = DrugUtil.setDisplayName(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 760), Utils.f("&b&lHeroin Syringe"));
        heroin = DrugUtil.hideDurability(heroin);
        heroin = DrugUtil.addLore(heroin, "&7Stick this thing in my arm!");
        service.addDrug(new Heroin(), new DrugItem(heroin, new Heroin()));

        this.effectManager = new EffectManager();
        this.lockedBlocks = new LockedBlocks();
    }

    @Override
    public void stop() {
        this.lockedBlocks.save();
    }

    @Override
    public boolean destroy() {
        return false;
    }

    /**
     * @apiNote see note in ItemManager#loadRecipes()
     */
    public void loadDrugRecipes() {
//        Vice.getItemManager().registerCustomRecipe(new HeroinCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new OpiumCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new JointCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new PotBrownieCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new WeedBudCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new CocaineCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new LSDCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new MethPipeCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new MethBaggyBrewingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new BeerBrewingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new MDMABrewingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new HopsCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new VodkaBrewingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new HeroinSyringeCraftingRecipe());
//        Vice.getItemManager().registerCustomRecipe(new SyringeCraftingRecipe());
    }


    public EffectManager getEffectManager() {
        return this.effectManager;
    }

    public LockedBlocks getLockedBlocks() {
        return this.lockedBlocks;
    }

    /**
     * @param uuid the uuid of the player
     * @return if the player CAN OD again
     */
    public boolean inOD(UUID uuid) {
        return this.ods.contains(uuid);
    }


    /***
     * @param uuid uuid of the player
     *
     * Use when a player has OD'd (at the start of the OD sequence)
     */
    public void addOD(UUID uuid) {
        if (!this.ods.contains(uuid)) {
            this.ods.add(uuid);
        }
    }

    /**
     * @param uuid the player
     *             <p>
     *             Use after the OD has completed (at the end of the same OD sequence)
     */
    public void removeOD(UUID uuid) {
        if (this.ods.contains(uuid)) {
            this.ods.remove(uuid);
        }
    }

    public static boolean isDrug(ItemStack itemStack) {
        return ((DrugService) Vice.getDrugManager().getService()).getAllDrugItems().stream().anyMatch(drugItem -> drugItem.getItemStack().isSimilar(itemStack));
    }

}
