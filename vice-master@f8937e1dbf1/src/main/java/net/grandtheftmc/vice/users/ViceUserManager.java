package net.grandtheftmc.vice.users;

import java.util.*;

public class ViceUserManager {

    private final Map<UUID, ViceUser> loadedUsers = new HashMap<>();

    public Collection<ViceUser> getLoadedUsers() {
        return this.loadedUsers.values();
    }

    public boolean unloadUser(UUID uuid) {
        if(this.loadedUsers.containsKey(uuid)){
            this.loadedUsers.get(uuid).save();
            this.loadedUsers.remove(uuid);
            return true;
        }
        return false;
    }

    public ViceUser getLoadedUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return this.loadedUsers.computeIfAbsent(uuid, k -> new ViceUser(uuid));
    }

}