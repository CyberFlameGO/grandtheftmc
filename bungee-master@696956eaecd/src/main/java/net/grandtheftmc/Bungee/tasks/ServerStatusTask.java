package net.grandtheftmc.Bungee.tasks;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.utils.ServerStatus;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class ServerStatusTask {

    public ServerStatusTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> Bungee.getInstance().getProxy().getServers().values().forEach(serverInfo -> {
                    try {
                        ServerStatus serverStatus = ServerStatus.getServerStatus(serverInfo);
                        serverStatus.updateStatus();
                    }
                    catch (RejectedExecutionException ignored) {}
        }), 1, 15, TimeUnit.SECONDS);
    }
}
