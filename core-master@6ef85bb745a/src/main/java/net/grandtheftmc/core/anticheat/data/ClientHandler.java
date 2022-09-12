package net.grandtheftmc.core.anticheat.data;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.anticheat.Anticheat;
import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.report.ReportManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.ServerUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClientHandler {

    private final HashMap<UUID, ClientData> players = Maps.newHashMap();

    public HashMap<UUID, ClientData> getPlayers() {
        return this.players;
    }
    private final Anticheat anticheat;
    private final ReportManager reportManager;

    public ClientHandler(Anticheat anticheat, ReportManager reportManager) {
        this.anticheat = anticheat;
        this.reportManager = reportManager;
    }

    public ClientData addClientData(Player player) {
        players.putIfAbsent(player.getUniqueId(), new ClientData(player, this.anticheat));
        return players.get(player.getUniqueId());
    }

    public ClientData getClientData(Player player) {
        return players.getOrDefault(player.getUniqueId(), null);
    }

    public void removeClientData(UUID uuid) {
        this.players.remove(uuid);
    }

    /**
     * Notify staff when a client is hacking.
     *
     * @param client
     */
    public void notifyCheck(ClientData client) {
        CheatType cheatType = client.getDetectedHack();
        int count = client.getCount(cheatType);
        double cps = client.getCPS();
        int hits = client.getHits();
        int misses = client.getMisses();
        double lrd = client.getLastReachDistance();
        int lag = client.getPing();
        int reports = this.reportManager.getReports(client.getPlayer());
        Player player = client.getPlayer();

//        DecimalFormat f = new DecimalFormat("##.00");
//        lrd = Double.parseDouble(f.format(lrd));

        BaseComponent[] components = TextComponent.fromLegacyText(Lang.ANTICHEAT.s() + Utils.f("&c" + player.getName() + "&7 has triggered &c" + cheatType.getName().toUpperCase() + "&7 event! &fx&l" + count + "&7"));
        for (BaseComponent c : components) {
            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Utils.f("&7Reports&f: &c&l" + reports + "\n&7Ping&f: &a&l" + lag + "\n\n" + "&7&oClick to inspect"))));
            c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect " + player.getName()));
        }

        ServerUtil.runTask(() -> {
            if(count % 10 == 0) {
                for (User user : UserManager.getInstance().getUsers()) {
                    if (!user.isStaff()){
                    	continue;
                    }
                    
                    Bukkit.getPlayer(user.getUUID()).spigot().sendMessage(components);
                }
            }

            client.resetDetection();
        });
    }
}
