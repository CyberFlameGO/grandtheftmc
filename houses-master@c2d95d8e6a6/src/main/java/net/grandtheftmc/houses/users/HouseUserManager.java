package net.grandtheftmc.houses.users;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HouseUserManager {

    private final List<HouseUser> loadedUsers = new ArrayList<>();

    public List<HouseUser> getLoadedUsers() {
        return this.loadedUsers;
    }

    public boolean unloadUser(UUID uuid) {
        if (uuid == null)
            return false;
        for (HouseUser user : this.loadedUsers.toArray(new HouseUser[this.loadedUsers.size()]))
            if (uuid.equals(user.getUUID()))
                return this.loadedUsers.remove(user);
        return false;
    }

    public HouseUser getLoadedUser(UUID uuid) {
        if (uuid == null) return null;
        for (HouseUser u : this.loadedUsers.toArray(new HouseUser[this.loadedUsers.size()]))
            if (uuid.equals(u.getUUID()))
                return u;
        HouseUser user = new HouseUser(uuid);
        this.loadedUsers.add(user);
        return user;
    }

}
