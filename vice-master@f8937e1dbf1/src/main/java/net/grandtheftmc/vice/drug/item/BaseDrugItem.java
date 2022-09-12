package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugAttribute;
import net.grandtheftmc.vice.items.GameItem;

public abstract class BaseDrugItem<T extends DrugAttribute> implements DrugItem<T> {

    private final String name, shortName;
    private GameItem gameItem;
    private final DrugType drugType;

    private T attribute = null;

    /**
     * Construct a new Drug Item
     */
    public BaseDrugItem(String name, DrugType drugType) {
        this.name = name;
        this.shortName = name.toLowerCase().replace(" ", "");
        this.drugType = drugType;
    }

    /**
     * Get the name of the DrugItem
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the short name of the DrugItem
     *
     * @return name
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     * Get the GameItem version of this DrugItem.
     *
     * @return Game Item
     */
    @Override
    public GameItem getGameItem() {
        return gameItem;
    }

    /**
     * Set the Game Item.
     *
     * @param gameItem Item
     */
    @Override
    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }

    /**
     * Get the Type of Drug this Item is affiliated with.
     *
     * @return Drug Type
     */
    @Override
    public DrugType getDrugType() {
        return drugType;
    }

    @Override
    public T getAttribute() {
        return attribute;
    }

    @Override
    public void setAttribute(T attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "" + this.name + ":{" + "DrugType:" + drugType.name() + ", Attribute:" + attribute.getClass().getSimpleName() + "}";
    }
}
