package net.grandtheftmc.core.servers;

import com.j0ach1mmall3.jlib.integration.pinger.Pinger;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private String name;
    private ServerType type;
    private int number;
    private int onlinePlayers;
    private int maxPlayers;
    private String map;
    private String gameState;
    private List<Location> joinSigns = new ArrayList<>();
    private int round;
    private UserRank rankToJoin;
    private boolean offline;
    private String ip;
    private int port;
    private long lastUpdate;

    public Server(String name, ServerType type, int number, String ip, int port, UserRank rankToJoin) {
        this.name = name;
        this.type = type;
        this.number = number;
        this.ip = ip;
        this.port = port;
        this.rankToJoin = rankToJoin;
    }

    public Server(String name, ServerType type, int number, String ip, int port, boolean offline, int onlinePlayers,
                  int maxPlayers, String map, String gameState, int round, UserRank rankToJoin, List<Location> joinSigns) {
        this.name = name;
        this.type = type;
        this.number = number;
        this.ip = ip;
        this.port = port;
        this.offline = offline;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.map = map;
        this.gameState = gameState;
        this.round = round;
        this.rankToJoin = rankToJoin;
        this.joinSigns = joinSigns;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void ping() {
        if (this.ip == null || this.port <= 0) {
            Core.log("Server '" + this.name + "' does not have an ip/port configured in the database!");
            return;
        }
        Pinger pinger = new Pinger(this.ip, this.port);
        pinger.ping(Core.getInstance(), new PingHandler(this));
    }

    public void updateJoinSigns() {
        if (this.joinSigns.isEmpty())
            return;
        String line0 = null;
        String line1 = null;
        String line2 = null;
        String line3 = null;
        if (this.offline) {
            line0 = "&4&l█████████";
            line1 = "&cRestarting...";
            line2 = "&c&l" + this.name.toUpperCase();
            line3 = "&4&l█████████";
        } else if (this.gameState == null) {
            line0 = (this.isFull() ? "&c&l" : "&a&l") + '[' + this.name.toUpperCase() + ']';
            line1 = this.map == null ? "" : "&7" + this.map;
            line2 = (this.isFull() ? "&c" : "&7") + this.onlinePlayers + " / " + this.maxPlayers;
            line3 = "&a&lLobby";
        } else
            switch (this.gameState.toLowerCase()) {
                case "none":
                case "lobby":
                    line0 = (this.isFull() ? "&c&l" : "&a&l") + '[' + this.name.toUpperCase() + ']';
                    line1 = this.map == null ? "" : "&7" + this.map;
                    line2 = (this.isFull() ? "&c" : "&7") + this.onlinePlayers + " / " + this.maxPlayers;
                    line3 = "&a&lLobby";
                    break;
                case "ingame":
                    line0 = (this.isFull() ? "&c&l" : "&8&l") + '[' + this.name.toUpperCase() + ']';
                    line1 = this.map == null ? "" : "&7" + this.map;
                    line2 = (this.isFull() ? "&c" : "&7") + this.onlinePlayers + " / " + this.maxPlayers;
                    line3 = "&8&lRound " + this.round;
                    break;

                case "end":
                    line0 = "&c&l[" + this.name.toUpperCase() + ']';
                    line1 = this.map == null ? "" : "&7" + this.map;
                    line2 = (this.isFull() ? "&c" : "&7") + this.onlinePlayers + " / " + this.maxPlayers;
                    line3 = "&c&lEnding";
                    break;
            }
        for (Location loc : new ArrayList<>(this.joinSigns)) {
            BlockState state = loc.getBlock().getState();
            if (state.getType() == Material.SIGN_POST || state.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) state;
                sign.setLine(0, line0);
                sign.setLine(1, line1);
                sign.setLine(2, line2);
                sign.setLine(3, line3);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;

    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
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

    public List<Location> getJoinSigns() {
        return this.joinSigns;
    }

    public void setJoinSigns(List<Location> joinSigns) {
        this.joinSigns = joinSigns;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;

    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;

    }

    public boolean isFull() {
        return this.onlinePlayers >= this.maxPlayers;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerType getType() {
        return this.type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;

    }

    public UserRank getRankToJoin() {
        return this.rankToJoin;
    }

    public void setRankToJoin(UserRank rankToJoin) {
        this.rankToJoin = rankToJoin;
    }

    public boolean needsRankToJoin() {
        return this.rankToJoin != null;
    }

    public void setLastUpdate(long l) {
        this.lastUpdate = l;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

}
