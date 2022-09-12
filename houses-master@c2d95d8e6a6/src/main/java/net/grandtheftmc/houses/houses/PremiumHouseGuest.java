package net.grandtheftmc.houses.houses;

import java.util.UUID;

public class PremiumHouseGuest {

    private UUID uuid;
    private String name;

    public PremiumHouseGuest(UUID uuid) {
        this.uuid = uuid;
    }

    public PremiumHouseGuest(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
