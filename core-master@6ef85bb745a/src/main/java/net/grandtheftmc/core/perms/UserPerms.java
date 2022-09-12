package net.grandtheftmc.core.perms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Component;

public class UserPerms implements Component<UserPerms, Core> {

    private UUID uuid;
    private List<String> perms;

    public UserPerms(UUID uuid, List<String> perms) {
        this.uuid = uuid;
        this.perms = perms;
    }

    public UserPerms(UUID uuid) {
        this.uuid = uuid;
        this.perms = new ArrayList<>();
    }

    @Override
    public UserPerms onDisable(Core plugin) {
        this.perms.clear();
        return this;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public List<String> getPerms() {
        return this.perms;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
        Core.getPermsManager().updatePerms(this.uuid);
    }

    public void addPerm(String perm) {
        if (!this.perms.contains(perm))
            this.perms.add(perm.toLowerCase());
        Core.getPermsManager().updatePerms(this.uuid);
    }

    public void removePerm(String perm) {
        this.perms.remove(perm);
        Core.getPermsManager().updatePerms(this.uuid);
    }

}
