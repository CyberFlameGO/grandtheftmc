package net.grandtheftmc.gtm.gang.member;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.users.GTMUser;

import java.util.Optional;
import java.util.UUID;

public interface GangMember {

    UUID getUniqueId();

    String getName();
    void setName(String name);

    GangRole getRole();
    void setRole(int id, GangRole role);

    boolean isCoLeader();
    boolean isOnline();

    boolean getChatToggle();
    void toggleChat();
    void setChat(boolean on);

    Optional<Gang> getViewingGang();
    void setViewingGang(Gang gang);
    boolean isViewingGang(Gang gang);

    Optional<GangMember> getViewingGangMember();
    void setViewingGangMember(GangMember member);
    boolean isViewingGangMember(UUID uuid);
}
