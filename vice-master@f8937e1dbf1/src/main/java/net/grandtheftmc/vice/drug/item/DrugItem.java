package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugAttribute;
import net.grandtheftmc.vice.items.GameItem;

public interface DrugItem<T extends DrugAttribute> {

    /**
     * Get the name of the DrugItem
     *
     * @return name
     */
    String getName();

    /**
     * Get the short name of the DrugItem
     *
     * @return name
     */
    String getShortName();

    /**
     * Get the GameItem version of this DrugItem.
     *
     * @return Game Item
     */
    GameItem getGameItem();

    /**
     * Set the Game Item.
     *
     * @param gameItem Item
     */
    void setGameItem(GameItem gameItem);

    /**
     * Get the Type of Drug this Item is affiliated with.
     *
     * @return Drug Type
     */
    DrugType getDrugType();

    T getAttribute();
    void setAttribute(T attribute);
}
