package net.grandtheftmc.vice.items.recipetypes;

/**
 * Created by Timothy Lampen on 7/15/2017.
 */
public abstract class RecipeItem {

    private RecipeType type;

    public RecipeItem(RecipeType type){
        this.type = type;
    }

    public RecipeType getType() {
        return type;
    }
}
