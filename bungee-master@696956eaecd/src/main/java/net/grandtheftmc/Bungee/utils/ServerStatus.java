package net.grandtheftmc.Bungee.utils;

import io.netty.util.internal.ConcurrentSet;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashSet;
import java.util.Set;

public class ServerStatus implements Callback<ServerPing> {
    private static final ConcurrentSet<ServerStatus> serverStatuses = new ConcurrentSet<>();
    private final ServerInfo serverInfo;
    private boolean online;


    public ServerStatus(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        serverStatuses.add(this);
    }

    public static ServerStatus getServerStatus(ServerInfo serverInfo) {
        Set<ServerStatus> tempStatuses = new HashSet<>(serverStatuses);
        return tempStatuses.stream()
                .filter(serverStatus -> serverStatus.getServerInfo() == serverInfo)
                .findFirst().orElse(new ServerStatus(serverInfo));
    }

    @Override
    public void done(ServerPing serverPing, Throwable throwable) {
        this.online = throwable == null;
    }

    public void updateStatus() {
        this.serverInfo.ping(this);
    }

    public boolean isOnline() {
        return this.online;
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }
}
