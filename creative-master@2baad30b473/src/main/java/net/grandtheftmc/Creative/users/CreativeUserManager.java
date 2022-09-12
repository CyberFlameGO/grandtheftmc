package net.grandtheftmc.Creative.users;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreativeUserManager {

    private final Map<UUID, CreativeUser> loadedUsers = new HashMap<>();

    public Collection<CreativeUser> getLoadedUsers() {
        return this.loadedUsers.values();
    }

    public boolean unloadUser(UUID uuid) {
        return this.loadedUsers.remove(uuid) != null;
    }

    public CreativeUser getLoadedUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return this.loadedUsers.computeIfAbsent(uuid, CreativeUser::new);
    }

}