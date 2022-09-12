package net.grandtheftmc.core.whitelist;

import java.util.UUID;

public class WhitelistedUser {

    private UUID uuid;
    private String name;

    public WhitelistedUser(UUID uuid, String name) {
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
