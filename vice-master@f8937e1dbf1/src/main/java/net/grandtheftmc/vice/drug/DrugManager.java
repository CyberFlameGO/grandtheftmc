package net.grandtheftmc.vice.drug;

import com.google.common.collect.Lists;
import net.grandtheftmc.vice.drug.attribute.DrugAttribute;
import net.grandtheftmc.vice.drug.item.*;
import net.grandtheftmc.vice.drug.item.beer.BeerBottle;
import net.grandtheftmc.vice.drug.item.beer.CraftBeer;
import net.grandtheftmc.vice.drug.item.beer.HumulusLupulusFruit;
import net.grandtheftmc.vice.drug.item.beer.HumulusLupulusSeed;
import net.grandtheftmc.vice.drug.item.meth.puremeth.Methylamine;
import net.grandtheftmc.vice.drug.item.meth.puremeth.PureMeth;
import net.grandtheftmc.vice.drug.item.meth.whitemeth.EphredraSinica;
import net.grandtheftmc.vice.drug.item.meth.whitemeth.EphredraSinicaSeeds;
import net.grandtheftmc.vice.drug.item.meth.whitemeth.WhiteMeth;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;

public final class DrugManager {

    private final List<BaseDrugItem<? extends DrugAttribute>> drugList;

    public DrugManager() {
        this.drugList = Lists.newArrayList();
//        this.drugList.addAll(Arrays.asList(
//                new BeerBottle(),
//                new CraftBeer(),
//                new HumulusLupulusFruit(),
//                new HumulusLupulusSeed(),
//                new Methylamine(),
//                new PureMeth(),
//                new EphredraSinica(),
//                new EphredraSinicaSeeds(),
//                new WhiteMeth(),
//                new Acid(),
//                new Cocaine(),
//                new ConcentratedMagicMushroom(),
//                new Crack(),
//                new DistilledVodka(),
//                new DriedMagicMushroom(),
//                new Hop(),
//                new Joint(),
//                new LSD(),
//                new MagicMushroom(),
//                new MarijuanaLeaf(),
//                new MarijuanaSeed(),
//                new PotBrownie(),
//                new Vodka(),
//                new WeedBuds()
//        ));
    }
}
