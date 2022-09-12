package net.grandtheftmc.core.servers;

import com.j0ach1mmall3.jlib.integration.pinger.PingResponse;
import com.j0ach1mmall3.jlib.integration.pinger.PingResponse.Players;
import com.j0ach1mmall3.jlib.storage.database.CallbackHandler;

public class PingHandler implements CallbackHandler<PingResponse> {

    private final Server server;

    public PingHandler(Server server) {
        this.server = server;
    }

    @Override
    public void callback(PingResponse resp) {
        this.server.setLastUpdate(System.currentTimeMillis());
        if (resp == null) {
           // Utils.b("Updating OFFLINE server " + this.server.getName() + " with ip " + this.server.getIp() + ':' + this.server.getPort());
            this.server.setOffline(true);
            return;
        }
        this.server.setOffline(false);
        Players players = resp.getPlayers();
        this.server.setOnlinePlayers(players.getOnline());
        this.server.setMaxPlayers(players.getMax());
        String desc = resp.getDescription().getText();
        String[] a = desc.split(",");
        if (a.length > 0)
            for (String s : a) {
                String[] array = s.split(":");
                if (array.length != 2)
                    continue;
                switch (array[0]) {
                    case "map":
                        this.server.setMap(array[1]);
                        break;
                    case "gameState":
                        this.server.setGameState(array[1]);
                        break;
                    case "round":
                        this.server.setRound(Integer.parseInt(array[1]));
                        break;
                    default:
                        break;
                }
            }
        this.server.updateJoinSigns();
    }

}
