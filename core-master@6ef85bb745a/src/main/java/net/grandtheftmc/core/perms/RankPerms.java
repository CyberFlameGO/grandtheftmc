package net.grandtheftmc.core.perms;

import java.util.ArrayList;
import java.util.List;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Component;

public class RankPerms implements Component<RankPerms, Core> {

    private UserRank rank;
    private List<String> perms = new ArrayList<>();

    public RankPerms(UserRank rank, List<String> perms) {
        this.rank = rank;
        if (perms != null)
            this.perms = perms;
    }

    @Override
    public RankPerms onDisable(Core plugin) {
        this.perms.clear();
        return this;
    }

    public RankPerms(UserRank rank) {
        this.rank = rank;
    }

    public UserRank getRank() {
        return this.rank;
    }

    public void setRank(UserRank rank) {
        this.rank = rank;
    }

    public List<String> getPerms() {
        return this.perms;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
        Core.getPermsManager().updatePerms(this.rank);
    }

    public void addPerm(String perm) {
        if (!this.perms.contains(perm))
            this.perms.add(perm.toLowerCase());
        Core.getPermsManager().updatePerms(this.rank);
    }

    public void removePerm(String perm) {
        this.perms.remove(perm);
        Core.getPermsManager().updatePerms(this.rank);
    }

}
