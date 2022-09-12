package net.grandtheftmc.core.anticheat.report;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.base.Preconditions;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.anticheat.Anticheat;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;

public class ReportManager implements Component<ReportManager, Core> {

//    public final HashMap<UUID, PlayerReportData> cache = Maps.newHashMap();
//
//    public PlayerReportData getPlayerReportData(UUID uniqueId) {
//        if(!cache.containsKey(uniqueId)) {
//            PlayerReportData playerReportData = new PlayerReportData(uniqueId);
//            cache.put(uniqueId, playerReportData);
//        }
//        return null;
//    }

    private static final long counter;
    private static final long reportCooldown, watchTime;
    private static final ConcurrentHashMap<UUID, Data> storedData;

    static {
        counter = 20*60;
        reportCooldown = 1000 * 60 * 5;
        watchTime = 1000 * 60 * 5;
        storedData = new ConcurrentHashMap<>();
    }

    private Anticheat anticheat;

    public ReportManager(Core core, Anticheat anticheat) {
        this.anticheat = anticheat;
        Bukkit.getPluginManager().registerEvents(this, core);
        new ReportCommand(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            for(UUID uuid : storedData.keySet()) {
                Data data = storedData.get(uuid);
                if(data.timeReported != -1 && System.currentTimeMillis() - data.timeReported >= watchTime) {
                    data.timeReported = -1;
                    anticheat.getClientHandler().removeClientData(uuid);
                    System.out.println("Removed " + uuid.toString());
                }
            }
        }, counter, counter);
    }

    public int getReports(Player player) {
        Preconditions.checkNotNull(player);
        if(!storedData.containsKey(player.getUniqueId())) return 0;
        return storedData.get(player.getUniqueId()).reports;
    }

    public void report(Player player, Player target, String reason) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(target);

        storedData.computeIfAbsent(player.getUniqueId(), k -> new Data());
        storedData.computeIfAbsent(target.getUniqueId(), k -> new Data());

        Data pd = storedData.getOrDefault(player.getUniqueId(), null);
        Preconditions.checkNotNull(pd);
        if (pd.lastReport > -1 && System.currentTimeMillis() - pd.lastReport < reportCooldown) {
            long difference = 300 - ((System.currentTimeMillis() - pd.lastReport) / 1000);
            player.sendMessage(Lang.ANTICHEAT.f("&cPlease wait " + Utils.timeInSecondsToText(difference, C.DARK_RED, C.RED, C.WHITE) + " &cto report again."));
            return;
        }
        pd.lastReport = System.currentTimeMillis();
        player.sendMessage(Lang.ANTICHEAT.f("Reported " + target.getName() + " for '" + reason + "'"));
        player.sendMessage(Lang.ANTICHEAT.f("Note that false reporting is punishable."));

        Data td = storedData.getOrDefault(target.getUniqueId(), null);
        Preconditions.checkNotNull(td);
        td.timeReported = System.currentTimeMillis();
        td.reports += 1;

        anticheat.getClientHandler().addClientData(target);

        for (User user : UserManager.getInstance().getUsers()) {
            if (!user.isStaff()){
            	continue;
            }
            Bukkit.getPlayer(user.getUUID()).sendMessage(Lang.ANTICHEAT.f("&c" + target.getName() + "&7 has been reported by &c" + player.getName() + "&7 for '&f" + reason + "&7'"));
        }
    }

    public class Data {
        private long lastReport = -1, timeReported = -1;
        private int reports = 0;

        public long getLastReport() {
            return lastReport;
        }

        public long getTimeReported() {
            return timeReported;
        }

        public int getReports() {
            return reports;
        }

        public void setLastReport(long lastReport) {
            this.lastReport = lastReport;
        }

        public void setTimeReported(long timeReported) {
            this.timeReported = timeReported;
        }

        public void addReports(int value) {
            this.reports += value;
        }

        @Override
		public String toString() {
            return "ReportManager.Data [ {lastReport:" + lastReport + "}, {timeReported:" + timeReported + "}, {reports:" + reports + "} ];";
        }
    }

    @EventHandler
    protected final void onPlayerQuit(PlayerQuitEvent event) {
        storedData.remove(event.getPlayer().getUniqueId());
    }
}
