package net.grandtheftmc.vice.areas.builder;

import org.bukkit.Location;

import java.util.UUID;

public class AreaBuilder {

    private String name;
    private UUID creator;
    private Location c1, c2;

    public AreaBuilder(String name, UUID creator) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return this.name;
    }

    public UUID getCreator() {
        return this.creator;
    }

    public Location getCorner1() {
        return this.c1;
    }

    public Location getCorner2() {
        return this.c2;
    }

    public void setCorner1(Location location) {
        this.c1 = location;
    }

    public void setCorner2(Location location) {
        this.c2 = location;
    }

}
