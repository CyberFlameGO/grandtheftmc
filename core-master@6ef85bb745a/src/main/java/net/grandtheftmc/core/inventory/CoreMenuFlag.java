package net.grandtheftmc.core.inventory;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public enum CoreMenuFlag {

    /**
     * When a user clicks outside of the inventory ui,
     * the menu will close.
     *
     * ! This does not account for empty slots. !
     */
    CLOSE_ON_NULL_CLICK,

    /**
     * When a user opens the specific inventory,
     * the cursor will be reset back to its
     * default position. (X:Center - Y:Bottom)
     *
     * ! This only accounts for opening !
     */
    RESET_CURSOR_ON_OPEN,

    /**
     * If you would like the inventory to have
     * the same layout as the phone.
     *
     * This overrides the size parameter (the size can be set to anything)
     */
    PHONE_LAYOUT
}
