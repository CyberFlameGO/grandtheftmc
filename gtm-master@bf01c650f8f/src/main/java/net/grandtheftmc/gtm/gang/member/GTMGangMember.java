package net.grandtheftmc.gtm.gang.member;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangDAO;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class GTMGangMember implements GangMember {

    private final UUID uuid;
    private GangRole role = GangRole.MEMBER;
    private String name;

    private boolean chat = false;
    private Gang viewingGang;
    private GangMember viewingGangMember;

    public GTMGangMember(UUID uuid) {
        this.uuid = uuid;
    }

    public GTMGangMember(UUID uuid, String name, GangRole role) {
        this(uuid);
        this.name = name;
        this.role = role;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public GangRole getRole() {
        return role;
    }

    @Override
    public void setRole(int id, GangRole role) {
        this.role = role;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.setMemberRole(connection, uuid, role, id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isCoLeader() {
        return this.role == GangRole.CO_LEADER;
    }

    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(this.uuid) != null;
    }

    @Override
    public boolean getChatToggle() {
        return this.chat;
    }

    @Override
    public void toggleChat() {
        this.chat = !this.chat;
    }

    @Override
    public void setChat(boolean chat) {
        this.chat = chat;
    }

    @Override
    public Optional<Gang> getViewingGang() {
        return this.viewingGang == null ? Optional.empty() : Optional.of(this.viewingGang);
    }

    @Override
    public boolean isViewingGang(Gang gang) {
        return this.viewingGang != null && this.viewingGang.getUniqueId() == gang.getUniqueId();
    }

    @Override
    public void setViewingGang(Gang viewingGang) {
        this.viewingGang = viewingGang;
    }

    @Override
    public Optional<GangMember> getViewingGangMember() {
        return this.viewingGangMember == null ? Optional.empty() : Optional.of(this.viewingGangMember);
    }

    @Override
    public void setViewingGangMember(GangMember member) {
        this.viewingGangMember = member;
    }

    @Override
    public boolean isViewingGangMember(UUID uuid) {
        return this.viewingGangMember != null && this.viewingGangMember.getUniqueId().equals(uuid);
    }
}
