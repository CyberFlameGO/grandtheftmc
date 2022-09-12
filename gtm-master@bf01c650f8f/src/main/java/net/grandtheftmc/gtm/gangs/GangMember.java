package net.grandtheftmc.gtm.gangs;

import net.grandtheftmc.gtm.gang.member.GangRole;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GangMember {

    private final UUID uuid;
    private final String name;
    private GangRole rank;

    public GangMember(UUID uuid, String name, GangRole rank) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public GangRole getRank() {
        return this.rank;
    }

    public void setRank(GangRole rank) {
        this.rank = rank;
    }

    public boolean isColeader() {
//        return Objects.equals("coleader", this.rank);
        return this.rank == GangRole.CO_LEADER;
    }

    public boolean isMember() {
//        return Objects.equals("member", this.rank);
        return this.rank == GangRole.MEMBER;
    }

    public String getFormattedRank() {
        return rank.getFormattedTag();
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(this.uuid) != null;
    }
}
