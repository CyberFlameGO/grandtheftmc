package net.grandtheftmc.core.nametags;

import net.grandtheftmc.core.util.Utils;

/**
 * Created by Liam on 17/11/2016.
 */
public class Nametag {

    private final String name;
    private final String displayName;
    private final int price;

    Nametag(String name, String displayName, int price) {
        this.name = name;
        this.displayName = displayName;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return Utils.f(this.displayName);
    }

    public int getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
