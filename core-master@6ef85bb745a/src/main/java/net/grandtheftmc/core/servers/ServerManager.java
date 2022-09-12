package net.grandtheftmc.core.servers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Settings;
import net.grandtheftmc.core.database.dao.ServerInfoDAO;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerManager {
    public List<Server> servers = new ArrayList<>();

    private String map;
    private String gameState;
    private int round;

    public ServerManager() {
        this.loadServers();
        this.loadJoinSigns();
        this.updateThisServer();
        this.startSchedule();
    }

    private int taskId = -1;

    public void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());

    }

    public void updateThisServer() {
        Settings settings = Core.getSettings();
//        Core.getSQL().updateAsyncLater("update servers set type='" + settings.getType() + "',number="
//                + settings.getNumber() + ",onlinePlayers=" + Bukkit.getOnlinePlayers().size() + ",maxPlayers="
//                + Bukkit.getMaxPlayers() + ",map='" + this.map + "',gameState='" + this.gameState + "',round=" + this.round
//                + ",rankToJoin='" + (settings.getRankToJoin() == null ? null : settings.getRankToJoin().toString())
//                + "',lastCheck=" + System.currentTimeMillis() + " where name='" + Core.name() + "';");
        ServerUtil.runTaskAsync(() -> ServerInfoDAO.updateServerInfo(Core.name(), settings.getType(), settings.getNumber(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), this.map, this.gameState, this.round, settings.getRankToJoin()));

        Server server = this.getServer(Core.name());
        if (server == null)
            return;
        server.setMap(this.map);
        server.setGameState(this.gameState);
        server.setRound(this.round);
        server.setOnlinePlayers(Bukkit.getOnlinePlayers().size());
        server.setMaxPlayers(Bukkit.getMaxPlayers());
        server.setOffline(false);
        server.updateJoinSigns();
    }

    public void loadServers() {
        ServerUtil.runTaskAsync(() -> {
            Optional<List<Server>> optional = ServerInfoDAO.fetchAllServers();
            if(!optional.isPresent()) return;
            ServerUtil.runTask(() -> ServerManager.this.servers = new ArrayList<>(optional.get()));
        });
    }

    public void startSchedule() {
        if (this.taskId != -1)
            Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = new BukkitRunnable() {
            @Override
            public void run() {
                ServerManager.this.updateThisServer();
                Core.getServerManager().getServers().stream().filter(server -> !server.getName().equals(Core.name())).forEach(Server::ping);
                if (Core.getSettings().serverWarperEnabled())
                    MenuManager.updateMenu("serverwarper");
            }
        }.runTaskTimer(Core.getInstance(), 100, 100).getTaskId();

    }

    public List<Server> getServers() {
        return this.servers;
    }

    public List<Server> getServers(ServerType type) {
        return this.servers.stream().filter(server -> server.getType() == type).collect(Collectors.toList());
    }

    public Server getServer(String name) {
        for (Server server : this.servers)
            if (server.getName().equalsIgnoreCase(name))
                return server;
        return null;
    }

    public Server getServer(String ip, int port) {
        for (Server server : this.servers)
            if (server.getIp().equals(ip) && server.getPort() == port)
                return server;
        return null;
    }

    public void loadJoinSigns() {
        YamlConfiguration c = Core.getSettings().getJoinSignsConfig();
        for (String s : c.getKeys(false)) {
            Server server = this.getServer(s);
            if (server == null)
                break;
            List<Location> locs = new ArrayList<>();
            for (String l : c.getStringList(s)) {
                Location loc = Utils.blockLocationFromString(l);
                if (l != null)
                    locs.add(loc);
            }
            server.setJoinSigns(locs);
        }
    }

    public void saveJoinSigns(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getJoinSignsConfig();
        for (String key : c.getKeys(false))
            c.set(key, null);
        this.servers.stream().filter(server -> !server.getJoinSigns().isEmpty()).forEach(server -> c.set(server.getName(), server.getJoinSigns().stream().map(Utils::blockLocationToString).collect(Collectors.toList())));
        Utils.saveConfig(c, "joinSigns");
    }

    public void removeJoinSign(Location location) {
        this.servers.stream().filter(server -> server.getJoinSigns().contains(location)).forEach(server -> server.getJoinSigns().remove(location));
    }

    public String getMap() {
        return this.map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getGameState() {
        return this.gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }


}
