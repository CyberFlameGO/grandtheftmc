package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.tasks.LotteryPlayer;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chat implements Listener {
    private final Map<String, Map<String, Integer>> recentChats = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = e.getMessage();
        ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
        User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.getBooleanFromStorage(BooleanStorageType.BRIBING)) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        return;
                    ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                    if ("quit".equalsIgnoreCase(msg)) {
                        user.setBooleanToStorage(BooleanStorageType.BRIBING, false);
                        player.sendMessage(Lang.BRIBE.f("&7You canceled bribing the cop who arrested you."));
                        return;
                    }
                    if (!user.isArrested()) {
                        user.setBooleanToStorage(BooleanStorageType.BRIBING, false);
                        player.sendMessage(Lang.BRIBE.f("&7You are not in jail!"));
                        return;
                    }
                    if (user.getJailTimer() < 5) {
                        user.setBooleanToStorage(BooleanStorageType.BRIBING, false);
                        player.sendMessage(Lang.BRIBE.f("&7You are already being released!"));
                        return;
                    }
                    Player cop = Bukkit.getPlayer(user.getJailCop());
                    ViceUser copUser = cop == null ? null : Vice.getUserManager().getLoadedUser(cop.getUniqueId());
                    if (cop == null || !copUser.isCop()) {
                        player.sendMessage(Lang.BRIBE.f("&7The cop who arrested you (&3&l" + user.getJailCopName() + "&7) is off duty!"));
                        return;
                    }
                    double amnt;
                    try {
                        amnt = Utils.round(Double.parseDouble(msg));
                    } catch (NumberFormatException e1) {
                        player.sendMessage(
                                Utils.f(Lang.BRIBE + "&7Please enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    if (amnt < 5000) {
                        player.sendMessage(Lang.BRIBE.f("&7Bribes must be at least &a$&l5,000!"));
                        return;
                    }
                    if (user.getBribe() * 1.05 > amnt) {
                        player.sendMessage(Lang.BRIBE.f("&7You must raise the bribe by at least &a&l5%&7 of &a$&l" + user.getBribe() + "&7 (&a$&l" + (user.getBribe() * 1.05) + "&7)! Please enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    if (!user.hasMoney(amnt)) {
                        player.sendMessage(Lang.BRIBE.f("&7You don't have &c$&l" + amnt + "&7! Please enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    user.setBribe(amnt);
                    player.sendMessage(Lang.BRIBE.f("&7You sent a bribe offer of &a$&l" + amnt + "&7 to &3&l" + cop.getName() + "&7. You can negotiate with them using &a\"/msg " + cop.getName() + "\"&7!"));
                    cop.spigot().sendMessage(new ComponentBuilder(Lang.BRIBE.f("&7A bribe offer of &a$&l" + amnt + "&7 was sent to you by &3&l" + player.getName() + "&7!")).append(" [ACCEPT] ").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe accept " + player.getName())).append("[DENY]").color(ChatColor.DARK_RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe deny " + player.getName())).create());
                }
            }.runTask(Vice.getInstance());
        } else if (user.getBooleanFromStorage(BooleanStorageType.BUYING_LOTTERY_TICKETS)) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        return;
                    ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                    if ("quit".equalsIgnoreCase(msg)) {
                        user.setBooleanToStorage(BooleanStorageType.BUYING_LOTTERY_TICKETS, false);
                        player.sendMessage(Utils.f(Lang.LOTTERY + "&7You cancelled buying lottery tickets!"));
                        MenuManager.openMenu(player, "lottery");
                        return;
                    }
                    int amnt;
                    try {
                        amnt = Integer.parseInt(msg);
                    } catch (NumberFormatException e1) {
                        player.sendMessage(Utils.f(Lang.LOTTERY + "&7Please enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    if (amnt < 1) {
                        player.sendMessage(Utils.f(Lang.LOTTERY
                                + "&7The minimum amount is &e&l1&7! Enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    if (amnt > 100000) {
                        player.sendMessage(Utils.f(Lang.LOTTERY
                                + "&7The maximum amount is &e&l100000&7! Enter a valid number or type &a\"quit\"&7!"));
                        return;
                    }
                    if (!user.hasMoney(amnt * 500)) {
                        player.sendMessage(Lang.LOTTERY.f("&7You don't have &a$&l" + (amnt * 500)
                                + "&7 to buy &e&l" + amnt + " Tickets&7! Enter a valid amount or type &a\"quit\"&7!"));
                        return;
                    }
                    user.setBooleanToStorage(BooleanStorageType.BUYING_LOTTERY_TICKETS, false);
                    user.takeMoney(amnt * 500);
                    LotteryPlayer p = Vice.getLottery().getLotteryPlayer(uuid);
                    if (p == null){ p = new LotteryPlayer(uuid, player.getName());
                    Vice.getLottery().addLotteryPlayer(p);}
                    p.addTickets(amnt);
                    ViceUtils.updateBoard(player, user);
                    player.sendMessage(
                            Utils.f(Lang.LOTTERY + "&7You bought &e&l" + amnt + " Tickets&7 for &a$&l" + (amnt * 500) + "&7!"));
                }
            }.runTask(Vice.getInstance());
        }
        /*if (!coreUser.isRank(UserRank.MOD)) {
            if (this.recentChats.containsKey(player.getName())) {
                if (this.recentChats.get(player.getName()).containsKey(msg)) {
                    if (this.recentChats.get(player.getName()).get(msg) == 4) {
                        e.getRecipients().removeAll(e.getRecipients());
                        e.getRecipients().add(player);
                    } else if (this.recentChats.get(player.getName()).get(msg) >= 5) {
                        e.setCancelled(true);
                    }
                    player.sendMessage(Lang.HEY.f("&7Slow down! Spamming can get you in trouble."));
                    this.recentChats.get(player.getName()).put(msg, this.recentChats.get(player.getName()).get(msg) + 1);
                } else {
                    this.recentChats.get(player.getName()).put(msg, 1);
                }
            } else {
                this.recentChats.put(player.getName(), new HashMap<>());
                this.recentChats.get(player.getName()).put(msg, 1);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Chat.this.recentChats.get(player.getName()).remove(msg);
                }
            }.runTaskLater(Vice.getInstance(), 800);
        }*/
    }
}


