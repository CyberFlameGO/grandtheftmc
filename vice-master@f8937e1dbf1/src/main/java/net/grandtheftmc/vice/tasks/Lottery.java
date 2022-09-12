package net.grandtheftmc.vice.tasks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.WeightedRandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Liam on 11/01/2017.
 */
public class Lottery {

    private LocalDateTime end;
    private Location hologramLocation;
    private Hologram hologram;
    private final List<TextLine> textLines = new ArrayList<>();
    private final List<LotteryPlayer> lotteryPlayers = new ArrayList<>();
    private List<LotteryPlayer> winners = new ArrayList<>(3);

    public Lottery() {
        //this.loadConfig();
       // this.startScheduler();
    }

    public void loadConfig() {
        YamlConfiguration c = Vice.getSettings().getLotteryConfig();
        this.hologramLocation = Utils.teleportLocationFromString(c.getString("hologramLocation"));
        this.end = c.get("end") == null ? LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)
                : LocalDateTime.of(c.getInt("end.year"), c.getInt("end.month"), c.getInt("end.day"), c.getInt("end.hour"), c.getInt("end.minute"));
        this.winners.clear();
        if (c.get("winners") != null)
            for (String s : c.getConfigurationSection("winners").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(c.getString("winners." + s + ".uuid"));
                    String name = c.getString("winners." + s + ".name");
                    double amnt = c.getDouble("winners." + s + ".amount");
                    boolean paid = c.getBoolean("winners." + s + ".paid");
                    LotteryPlayer player = new LotteryPlayer(uuid, name);
                    player.setAmount(amnt);
                    player.setPaid(paid);
                    this.winners.add(player);
                } catch (Exception e) {
                    Core.log("Error while loading lottery winner " + s);
                    e.printStackTrace();
                }
            }
        this.lotteryPlayers.clear();
        if (c.get("players") != null)
            for (String s : c.getConfigurationSection("players").getKeys(false))
                try {
                    UUID uuid = UUID.fromString(s);
                    LotteryPlayer player = new LotteryPlayer(uuid, c.getString("players." + uuid + ".name"));
                    player.setTickets(c.getInt("players." + uuid + ".tickets"));
                    this.lotteryPlayers.add(player);
                } catch (Exception e) {
                    Core.log("Error while loading lottery player " + s);
                    e.printStackTrace();
                }
    }

    public void saveConfig() {
        YamlConfiguration c = Vice.getSettings().getLotteryConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        c.set("hologramLocation", Utils.teleportLocationToString(this.hologramLocation));
        c.set("end.year", this.end.getYear());
        c.set("end.month", this.end.getMonthValue());
        c.set("end.day", this.end.getDayOfMonth());
        c.set("end.hour", this.end.getHour());
        c.set("end.minute", this.end.getMinute());
        for (int i = 0; i < 3; i++) {
            LotteryPlayer player = this.getWinner(i);
            if (player == null) continue;
            c.set("winners." + i + ".uuid", player.getUUID().toString());
            c.set("winners." + i + ".name", player.getName());
            c.set("winners." + i + ".amount", player.getAmount());
            c.set("winners." + i + ".paid", player.isPaid());
        }
        for (LotteryPlayer player : this.lotteryPlayers) {
            c.set("players." + player.getUUID() + ".name", player.getName());
            c.set("players." + player.getUUID() + ".tickets", player.getTickets());
        }
        Utils.saveConfig(c, "lottery");
    }

    private void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Lottery.this.end != null && Lottery.this.end.isBefore(LocalDateTime.now(ZoneId.of("UTC"))))
                    Lottery.this.end();
                Lottery.this.updateHologram();
            }
        }.runTaskTimer(Vice.getInstance(), 20, 20);

    }

    public static void test() {
        WeightedRandomCollection<String> collection = new WeightedRandomCollection<>();
        collection.add(50, "Top Guy");
        collection.add(5, "Medium Guy");
        collection.add(3, "Mediocre Guy");
        collection.add(2, "Peasant1");
        collection.add(2, "Peasant2");
        collection.add(2, "Peasant3");
        collection.add(2, "Peasant4");
        collection.add(2, "Peasant5");
        collection.add(1, "Slave1");
        collection.add(1, "Slave2");
        collection.add(1, "Slave3");
        collection.add(1, "Slave4");
        collection.add(1, "Slave5");
        List<String> names = new ArrayList<>();
        Bukkit.broadcastMessage("--- " + collection.values().size());
        for (int i = 0; i < 13; i++)
            if (!names.contains(collection.next()))
                names.add(collection.last());

        names.forEach(Bukkit::broadcastMessage);
    }


    public void end() {
        this.end = LocalDateTime.now(ZoneId.of("UTC")).plusDays(7);
        WeightedRandomCollection<LotteryPlayer> players = new WeightedRandomCollection<>();
        for (LotteryPlayer player : this.lotteryPlayers) {
            players.add(player.getTickets(), player);
        }
        this.winners = players.getUniqueElements(3);
        double value = this.getPotValue();
        LotteryPlayer winner1 = this.getWinner(0);
        LotteryPlayer winner2 = this.getWinner(1);
        LotteryPlayer winner3 = this.getWinner(2);
        if (winner1 != null) winner1.addAmount(0.7 * value);
        if (winner2 != null) winner2.addAmount(0.2 * value);
        if (winner3 != null) winner3.addAmount(0.1 * value);
        ViceUtils.log("lottery", winner1.getName() + " has won the lottery prize of " + 0.7 * value + "(70% of the pot)");
        ViceUtils.log("lottery", winner2.getName() + " has won the lottery prize of " + 0.2 * value + "(20% of the pot)");
        ViceUtils.log("lottery", winner3.getName() + " has won the lottery prize of " + 0.1 * value + "(10% of the pot)");
        this.lotteryPlayers.clear();
        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(new String[]{"", Utils.f(ViceUtils.HEADER), "",
                    Utils.fc("&e&lLottery Results"), "",
                    Utils.fc("&7For a total pot of &a&l" + Utils.formatMoney(value)),
                    Utils.fc("&a#&l1&7: &r" + winner1 + " &a&l" + Utils.formatMoney(0.7 * value) + "&7 (&a70%&7 of the pot)"),
                    Utils.fc("&a#&l2&7: &r" + winner2 + " &a&l" + Utils.formatMoney(0.2 * value) + "&7 (&a20%&7 of the pot)"),
                    Utils.fc("&a#&l3&7: &r" + winner3 + " &a&l" + Utils.formatMoney(0.1 * value) + "&7 (&a10%&7 of the pot)"),
                    "", Utils.fc("&e&lCongratulations to the winners!"),
                    "", Utils.f(ViceUtils.FOOTER), ""});

        for (int i = 0; i < 3 && i < this.winners.size(); i++) {
            LotteryPlayer winner = this.winners.get(i);
            if (winner != null && !winner.isPaid()) {
                Player player = Bukkit.getPlayer(winner.getUUID());
                if (player == null) continue;
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.addMoney(winner.getAmount());
                winner.setPaid(true);
                player.sendMessage(Lang.LOTTERY.f("&7You've won the lottery, coming in " + (i == 0 ? "1st" : i == 1 ? "2nd" : i == 2 ? "3rd" : "error") + "! &a" + Utils.formatMoney(winner.getAmount()) + "&7 was added to your balance."));
                ViceUtils.updateBoard(player, user);
            }
        }

    }


    public void updateHologram() {
        if (this.hologramLocation == null) return;
        LotteryPlayer winner1 = this.winners.isEmpty() ? null : this.winners.get(0);
        LotteryPlayer winner2 = this.winners.size() > 1 ? this.winners.get(1) : null;
        LotteryPlayer winner3 = this.winners.size() > 2 ? this.winners.get(2) : null;
        if (this.hologram == null) {
            this.hologram = HologramsAPI.createHologram(Vice.getInstance(), this.hologramLocation.clone().add(0.5, 4, 0.5));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&e&lLottery")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&7&oGo big or go home!")));
            this.textLines.add(this.hologram.appendTextLine(""));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&7Pot value:")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&a&l" + Utils.formatMoney(this.getPotValue()))));
            this.textLines.add(this.hologram.appendTextLine(""));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&7Time until jackpot:")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&a&l" + this.timeToEnd())));
            this.textLines.add(this.hologram.appendTextLine(""));
            this.textLines.add(this.hologram.appendTextLine(Utils.f("&7Last week's winners:")));
            this.textLines.add(this.hologram.appendTextLine(Utils.f(winner1 == null ? "" : ("&a#&l1&7: &r" + winner1 + " &a" + Utils.formatMoney(winner1.getAmount()) + "&7 (&a70%&7 of the pot)"))));
            this.textLines.add(this.hologram.appendTextLine(Utils.f(winner2 == null ? "" : ("&a#&l2&7: &r" + winner2 + " &a" + Utils.formatMoney(winner2.getAmount()) + "&7 (&a20%&7 of the pot)"))));
            this.textLines.add(this.hologram.appendTextLine(Utils.f(winner3 == null ? "" : ("&a#&l3&7: &r" + winner3 + " &a" + Utils.formatMoney(winner3.getAmount()) + "&7 (&a10%&7 of the pot)"))));
        } else {
            this.textLines.get(4).setText(Utils.f("&a&l" + Utils.formatMoney(this.getPotValue())));
            this.textLines.get(7).setText(Utils.f("&a&l" + this.timeToEnd()));
            this.textLines.get(10).setText(Utils.f("&a#&l1&7: &r" + (winner1 == null ? "" : (winner1 + " &a" + Utils.formatMoney(winner1.getAmount()) + "&7 (&a70%&7 of the pot)"))));
            this.textLines.get(11).setText(Utils.f("&a#&l2&7: &r" + (winner2 == null ? "" : (winner2 + " &a" + Utils.formatMoney(winner2.getAmount()) + "&7 (&a20%&7 of the pot)"))));
            this.textLines.get(12).setText(Utils.f("&a#&l2&7: &r" + (winner3 == null ? "" : (winner3 + " &a" + Utils.formatMoney(winner3.getAmount()) + "&7 (&a10%&7 of the pot)"))));

        }
    }

    public String timeToEnd() {
        if (this.end == null) this.end = LocalDateTime.now(ZoneId.of("UTC")).plusDays(1);
        return Utils.timeInSecondsToText(ChronoUnit.SECONDS.between(LocalDateTime.now(ZoneId.of("UTC")), this.end));
    }

    public List<LotteryPlayer> getTickets() {
        return this.lotteryPlayers;
    }

    public LotteryPlayer getLotteryPlayer(UUID uuid) {
        return this.lotteryPlayers.stream().filter(player -> Objects.equals(player.getUUID(), uuid)).findFirst().orElse(null);
    }

    public LotteryPlayer getWinner(UUID uuid) {
        return this.winners.stream().filter(player -> Objects.equals(player.getUUID(), uuid)).findFirst().orElse(null);
    }


    public LotteryPlayer getWinner(int i) {
        return this.winners.size() > i ? this.winners.get(i) : null;
    }

    public double getPotValue() {
        return 500 * this.lotteryPlayers.stream().mapToInt(LotteryPlayer::getTickets).sum();
    }

    public void joinCheck(Player player, User user, ViceUser viceUser) {
        for (int i = 0; i < 3 && i < this.winners.size(); i++) {
            LotteryPlayer winner = this.winners.get(i);
            if (winner != null && !winner.isPaid() && Objects.equals(winner.getUUID(), player.getUniqueId())) {
                viceUser.addMoney(winner.getAmount());
                winner.setPaid(true);
                player.sendMessage(Lang.LOTTERY.f("&7You've won the lottery, coming in " + (i == 0 ? "1st" : i == 1 ? "2nd" : i == 2 ? "3rd" : "error") + "! &a$&l" + Utils.formatMoney(winner.getAmount()) + "&7 was added to your balance."));
            }
        }
        if (user.isSpecial()) {
            LotteryPlayer p = this.getLotteryPlayer(player.getUniqueId());
            if (p != null) return;
            p = new LotteryPlayer(player.getUniqueId(), player.getName());
            this.lotteryPlayers.add(p);
            p.addTickets(ViceUtils.getFreeLotteryTickets(user.getUserRank()));
            player.sendMessage(Lang.LOTTERY.f("&7Thank you for financially supporting GTM! You have been given &a&l" + p.getTickets() + "&7 free lottery tickets for this week's draw."));
        }
    }

    public List<LotteryPlayer> getLastWinners() {
        return this.winners;
    }

    public void addLotteryPlayer(LotteryPlayer p) {
        this.lotteryPlayers.add(p);
    }

    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    public Location getHologramLocation() {
        return this.hologramLocation;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }
}
