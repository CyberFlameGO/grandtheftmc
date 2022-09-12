package net.grandtheftmc.gtm.drugs.categories;

/**
 * Created by Remco on 25-3-2017.
 */
public interface IDrugCategory {

    /**
     * The name of the current Category, so we can define it somewhere.
     * @return name
     */
    String name();

    /**
     * The general description of the category of Drug.
     * @return description
     */
    String description();

}
