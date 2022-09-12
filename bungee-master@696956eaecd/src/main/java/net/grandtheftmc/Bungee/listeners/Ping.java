package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Ping implements Listener {

//    private static final int[] prez_is_12 = new int[]{420, 69, 6969, 666};

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();

        ServerPing.Players players = ping.getPlayers();
        //Set online players to equal the number on all servers distributed across redis.
        int online = Bungee.getRedisManager().getRedisAPI().getPlayersOnline().size();
        players.setOnline(online);
        //Was here before, but not exactly sure why, guessing you have no max cap?
//        players.setMax(prez_is_12[ThreadLocalRandom.current().nextInt(0, prez_is_12.length-1)]);
        players.setMax(1500);

        if (Bungee.getSettings().getMotd() != null) {
            //Set our MOTD if one exists
            ping.setDescriptionComponent(Utils.ft(Bungee.getSettings().getMotd()));
        }
    }
}
