package net.grandtheftmc.gtm.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.Head;
import net.grandtheftmc.gtm.tasks.LotteryPlayer;
import net.grandtheftmc.gtm.users.ChatAction;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserDAO;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.PersonalVehicle;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Chat implements Listener {
    private final Pattern p = Pattern.compile("-?\\d+");
    
    /** The server ids that players can transfer to */
    public static final Set<Integer> TRANSFER_SERVER_ALLOWED = new HashSet<Integer>(Arrays.asList(1,4));

    /**
     * If what you are adding is something where players type a value in chat please use the methods in the GTMUser class seen here:
     * - GTMUser#clearCurrentChatAction
     * - GTMUser#resetCurrentChatTimer
     * - GTMUser#setCurrentChatAction
     * <p>
     * These will also work if you get two values, but that is the limit currently.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = e.getMessage();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        
        if (user == null){
        	return;
        }
        
        if (user.getCurrentChatAction() != null) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    switch (user.getCurrentChatAction()) {

                        case CONFIRM_TRANSFER_2: {
                            TransferPayload payload = (TransferPayload) user.getCurrentChatValue();

                            if (msg.equalsIgnoreCase("cancel")) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.GTM.f("&cYou have cancelled the transfer process."));
                                return;
                            }

                            int id;
                            try {
                                Matcher matcher = p.matcher(msg);

                                String found = "";
                                while (matcher.find())
                                    found = matcher.group();

                                id = Integer.parseInt(found);
                                id = Math.abs(id);
                            } catch (Exception nfe) {
                                player.sendMessage(Lang.GTM.f("&cCannot parse &6" + msg + " &cas a number. &6Please enter a number type '&ccancel&6'"));
                                user.resetCurrentChatActionTimer(payload.getGeneratedNumber());
                                return;
                            }

                            if (id != payload.getGeneratedNumber()) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.GTM.f("&cYou have cancelled the transfer process."));
                                return;
                            }

                            //the selling of the houses and such must be done BEFORE the player logs off.
                            HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());

                            List<House> houses = houseUser.getHouses().stream().map(userHouse -> Houses.getHousesManager().getHouse(userHouse.getId())).collect(Collectors.toList());
                            houses.forEach(house -> house.sellHouse(player, user, houseUser));

                            List<PremiumHouse> premiumHouses = new ArrayList<>(houseUser.getPremiumHouses());
                            premiumHouses.forEach(premHouse -> premHouse.sell(player, user, houseUser));
                            
                            int vehicleSum = 0;
                            // if user has vehicles
                            if (user.getVehicles() != null && user.getVehicles().size() > 0){
                            	for (PersonalVehicle v : user.getVehicles()){
                            		GameItem gameItem = GTM.getItemManager().getItemFromVehicle(v.getVehicle());
                                	if (gameItem != null){
                                		
                                		// Note: this "price" is the BUY price in MenuListener.
                                		int price = (int) (gameItem.getSellPrice() * 2);
                                		vehicleSum += price;
                                	}
                            	}
                            }
                            
                            // TODO debug remove
                            Core.log("[Chat][Transfer] Vehicle sum for " + player.getName() + " was " + vehicleSum);
                            if (vehicleSum >= 0){
                            	user.addMoney(vehicleSum);
                            }
                            
                            // get unlocked user tags
                            Set<EventTag> userTags = coreUser.getUnlockedTags();
                            
                            ServerUtil.runTaskLater(() -> {
                                GTM.getInstance().getTransferingPlayers().add(player.getUniqueId());
                                
                                String playerName = player.getName();
                                UUID playerUUID = player.getUniqueId();
                                UserRank rank = coreUser.getUserRank();
                                
                                String commandLine = "tempban " + playerName + " 5m Transferring data";
                    			Core.log("[Chat][TRANSFER][DEBUG] Running command: " + commandLine);
                    			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
                    			
                                //player.kickPlayer(Lang.GTM.f("&e&lYour data is being transferred."));
                                ServerUtil.runTaskLaterAsync(() -> {
                                	Core.log("[Chat][TRANSFER][DEBUG] Transferring data for " + playerName);
                                	GTMUserDAO.transferData(player.getUniqueId(), payload.getGtmID());
                                	
                                	if (userTags != null){
                                    	userTags.forEach(ut -> {
                                    		
                                    		// TODO debug remove
                                            Core.log("[Chat][Transfer] UserTag=" + ut + ", serverKey=GTM" + payload.getGtmID());
                                            
                                    		if (!ut.isGlobal()){
                                    			UserDAO.addPlayerTag(uuid, "GTM" + payload.getGtmID(), ut);
                                    		}
                                    	});
                                	}
                                }, 20 * 5);
                                
                                ServerUtil.runTask(() -> {
                            		if (!rank.hasRank(UserRank.HELPOP)){
                            			Bukkit.getBanList(Type.NAME).addBan(playerName, "TRANSFER TO GTM" + payload.getGtmID(), null, "SERVER");
                            		}
                            	});
                            }, 20 * 3);
                            ServerUtil.runTaskLater(() -> GTM.getInstance().getTransferingPlayers().remove(player.getUniqueId()), 20 * 60);

                            return;
                        }

                        case CONFIRM_TRANSFER: {

                            if (msg.equalsIgnoreCase("cancel")) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.GTM.f("&cYou have cancelled the transfer process."));
                                return;
                            }
                            int id;
                            try {
                                id = Integer.parseInt(msg.replace("GTM", ""));
                            } catch (NumberFormatException nfe) {
                                player.sendMessage(Lang.GTM.f("&cCannot parse &6" + msg + " &cas a GTM server. &6Please enter a GTM server (ex. GTM1) or type '&ccancel&6'"));
                                user.resetCurrentChatActionTimer(null);
                                return;
                            }
                            
                            if (!TRANSFER_SERVER_ALLOWED.contains(id)){
                            	player.sendMessage(Lang.GTM.f("&cSorry, but you cannot currently transfer to that server. &6Please enter a different GTM server or type '&ccancel&6'"));
                                user.resetCurrentChatActionTimer(null);
                            }

                            int generatedNumber = ThreadLocalRandom.current().nextInt(10000000, 100000000);
                            user.setCurrentChatAction(ChatAction.CONFIRM_TRANSFER_2, new TransferPayload(generatedNumber, id));
                            player.sendMessage(Lang.GTM.f("&cAre you 100% sure you would like to complete this irreversible transfer? &aIf so please type &6" + generatedNumber + "&a in chat."));
                            player.sendMessage(Lang.GTM.f("&cYou will be banned from the network while we transfer the data."));
                            return;
                        }
                        case BRIBING: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.BRIBE.f("&7You canceled bribing the cop who arrested you."));
                                return;
                            }

                            if (!user.isArrested()) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.BRIBE.f("&7You are not in jail!"));
                                return;
                            }

                            if (user.getJailTimer() < 5) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.BRIBE.f("&7You are already being released!"));
                                return;
                            }

                            Player cop = Bukkit.getPlayer(user.getJailCop());
                            GTMUser copUser = cop == null ? null : GTM.getUserManager().getLoadedUser(cop.getUniqueId());
                            if (cop == null || copUser.getJobMode() != JobMode.COP) {
                                player.sendMessage(Lang.BRIBE.f("&7The cop who arrested you (&3&l" + user.getJailCopName() + "&7) is off duty!"));
                                user.clearCurrentChatAction();
                                return;
                            }

                            double amnt;
                            try {
                                amnt = Utils.round(Double.parseDouble(msg));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.BRIBE + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (amnt < 5000) {
                                player.sendMessage(Lang.BRIBE.f("&7Bribes must be at least &a$&l5,000!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (user.getBribe() * 1.05 > amnt) {
                                player.sendMessage(Lang.BRIBE.f("&7You must raise the bribe by at least &a&l5%&7 of &a$&l" + user.getBribe() + "&7 (&a$&l" + (user.getBribe() * 1.05) + "&7)! Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (!user.hasMoney(amnt)) {
                                player.sendMessage(Lang.BRIBE.f("&7You don't have &c$&l" + amnt + "&7! Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            user.clearCurrentChatAction();
                            user.setBribe(amnt);
                            player.sendMessage(Lang.BRIBE.f("&7You sent a bribe offer of &a$&l" + amnt + "&7 to &3&l" + cop.getName() + "&7. You can negotiate with them using &a\"/msg " + cop.getName() + "\"&7!"));
                            cop.spigot().sendMessage(new ComponentBuilder(Lang.BRIBE.f("&7A bribe offer of &a$&l" + amnt + "&7 was sent to you by &3&l" + player.getName() + "&7!")).append(" [ACCEPT] ").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe accept " + player.getName())).append("[DENY]").color(ChatColor.DARK_RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe deny " + player.getName())).create());
                            break;
                        }

                        case BIDDING_HEAD: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.HEAD_AUCTION.f("&7You canceled bidding."));
                                return;
                            }
                            Head head = user.getBiddingHead();
                            if (head == null) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.HEAD_AUCTION.f("&7You canceled bidding."));
                                return;
                            }
                            double amnt;
                            try {
                                amnt = Utils.round(Double.parseDouble(msg));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(
                                        Utils.f(Lang.HEAD_AUCTION + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            if (head.hasExpired() || head.isDone()) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Lang.HEAD_AUCTION.f("&7The bidding has expired!"));
                                return;
                            }
                            if (!user.hasMoney(amnt)) {
                                player.sendMessage(Lang.HEAD_AUCTION.f("&7You don't have &c$&l" + amnt + "&7! &7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (head.hasBid()) {
                                if (head.getBid() * 1.05 > amnt) {
                                    player.sendMessage(Lang.HEAD_AUCTION.f("&7You must bid at least &a&l5%&7 more than the current bid of &a$&l" + head.getBid() + "&7 (&a$&l" + (head.getBid() * 1.05) + "&7)!"));
                                    user.resetCurrentChatActionTimer(0);
                                    return;
                                }
                                head.returnBidderMoney();
                            } else if (amnt < 10000) {
                                player.sendMessage(Lang.HEAD_AUCTION.f("&7You must bid at least the starting bid of &a$&l10,000&7! &7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            user.clearCurrentChatAction();
                            head.setBid(player, amnt);
                            user.takeMoney(amnt);
                            GTMUtils.updateBoard(player, user);
                            player.sendMessage(Lang.HEAD_AUCTION.f("&7You have bid &a$&l" + head.getBid() + "&7 for &e&l" + head.getHead() + "'s Head&7! Please wait &c&l" + Utils.timeInMillisToText(head.getTimeUntilExpiry()) + "&7 for the auction to end."));
                            Player seller = Bukkit.getPlayer(head.getSellerUUID());
                            if (seller != null && !Objects.equals(seller, player))
                                seller.sendMessage(Lang.HEAD_AUCTION.f("&a&l" + player.getName() + "&7 has bid &a$&l" + head.getBid() + "&7 on &e&l" + head.getHead() + "'s Head&7!"));
                            break;
                        }
                        case PICKING_BOUNTY: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You cancelled picking the amount."));
                                MenuManager.openMenu(player, "bountiesplace");
                                return;
                            }
                            int amnt;
                            try {
                                amnt = Integer.parseInt(msg);
                            } catch (NumberFormatException e1) {
                                player.sendMessage(
                                        Utils.f(Lang.BOUNTIES + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            if (amnt < 2000) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES
                                        + "&7The minimum bid is &a$&l2.000&7! Enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            if (!user.hasMoney(amnt)) {
                                player.sendMessage(Lang.BOUNTIES.f("&7You don't have &a$&l" + amnt
                                        + "&7 to place this bounty! Enter a valid amount or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            user.clearCurrentChatAction();
                            user.setBountyAmount(amnt);
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You have chosen the amount &a$&l" + amnt + "&7!"));
                            MenuManager.openMenu(player, "bountiesplace");
                            break;
                        }
                        case PICKING_BOUNTY_TARGET: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You cancelled choosing the target."));
                                MenuManager.openMenu(player, "bountiesplace");
                                return;
                            }
                            Player target = Bukkit.getPlayer(msg);
                            if (target == null) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES
                                        + "&7That player is not online! Please enter a valid player name or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You cancelled choosing the target."));
                                return;
                            }
                            user.clearCurrentChatAction();
                            user.setBountyName(target.getName());
                            user.setBountyUUID(target.getUniqueId());
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You have chosen the player &a" + target.getName() + "&7!"));
                            MenuManager.openMenu(player, "bountiesplace");
                            break;
                        }

                        case BANK_DEPOSITING: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.setCurrentChatAction(ChatAction.BANK_DEPOSITING, null);
                                player.sendMessage(Utils.f(Lang.BANK + "&7You cancelled depositing money into your bank!"));
                                MenuManager.openMenu(player, "bank");
                                return;
                            }

                            double amnt;
                            try {
                                amnt = Utils.round(Double.parseDouble(msg));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.BANK + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_DEPOSITING, null);
                                return;
                            }

                            if (amnt <= 100) {
                                player.sendMessage(Utils.f(Lang.BANK
                                        + "&7The minimum amount is &a$&l100&7! Enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_DEPOSITING, null);
                                return;
                            }

                            if (!user.hasMoney(amnt)) {
                                player.sendMessage(Lang.BANK.f("&7You don't have &a$&l" + amnt
                                        + "&7 to deposit into your bank account! Enter a valid amount or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_DEPOSITING, null);
                                return;
                            }

                            user.clearCurrentChatAction();
                            user.depositToBank(amnt);
                            GTMUtils.updateBoard(player, user);
                            player.sendMessage(Utils.f(Lang.BANK + "&7You deposited &a$&l" + amnt + "&7 into your bank account!"));
                            break;
                        }

                        case BANK_WITHDRAWING: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.setCurrentChatAction(ChatAction.BANK_WITHDRAWING, null);
                                player.sendMessage(Utils.f(Lang.BANK + "&7You cancelled withdrawing money from your bank!"));
                                MenuManager.openMenu(player, "bank");
                                return;
                            }
                            double amnt;
                            try {
                                amnt = Utils.round(Double.parseDouble(msg));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.BANK + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_WITHDRAWING, null);
                                return;
                            }
                            if (amnt <= 100) {
                                player.sendMessage(Utils.f(Lang.BANK
                                        + "&7The minimum amount is &a$&l100&7! Enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_WITHDRAWING, null);
                                return;
                            }
                            if (!user.hasBank(amnt)) {
                                player.sendMessage(Lang.BANK.f("&7You don't have &a$&l" + amnt
                                        + "&7 in your bank account! Enter a valid amount or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                user.setCurrentChatAction(ChatAction.BANK_WITHDRAWING, null);
                                return;
                            }
                            user.clearCurrentChatAction();
                            user.withdrawFromBank(amnt);
                            GTMUtils.updateBoard(player, user);
                            player.sendMessage(
                                    Utils.f(Lang.BANK + "&7You withdrew &a$&l" + amnt + "&7 from your bank account!"));
                            break;
                        }
                        case BANK_TRANSFERRING: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Utils.f(Lang.BANK + "&7You cancelled transferring money to another player!"));
                                MenuManager.openMenu(player, "bank");
                                return;
                            }
                            if ((double) user.getCurrentChatValue() == 0) {
                                double amnt;
                                try {
                                    amnt = Utils.round(Double.parseDouble(msg));
                                } catch (NumberFormatException e1) {
                                    player.sendMessage(
                                            Utils.f(Lang.BANK + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                    user.resetCurrentChatActionTimer(0);
                                    return;
                                }
                                if (amnt <= 0) {
                                    player.sendMessage(Utils.f(Lang.BANK
                                            + "&7The minimum amount is &a$&l0&7! Enter a valid number or type &a\"quit\"&7!"));
                                    user.resetCurrentChatActionTimer(0);
                                    return;
                                }
                                if (!user.hasBank(amnt)) {
                                    player.sendMessage(Lang.BANK.f("&7You don't have &a$&l" + amnt
                                            + "&7 in your bank account! Enter a valid amount or type &a\"quit\"&7!"));
                                    user.resetCurrentChatActionTimer(0);
                                    return;
                                }
                                user.resetCurrentChatActionTimer(amnt);
                                player.sendMessage(Utils
                                        .f(Lang.BANK + "&7Please type the name of the player you would like to transfer &a$&l"
                                                + amnt + "&7 to, or type &a\"quit\"&7!"));
                                return;
                            }
                            double amnt = (double) user.getCurrentChatValue();
                            if (!user.hasBank(amnt)) {
                                player.sendMessage(Lang.BANK.f("&7You don't have &a$&l" + amnt
                                        + "&7 in your bank account! Enter a valid amount or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }
                            Player target = Bukkit.getPlayer(msg);
                            if (target == null) {
                                player.sendMessage(Lang.BANK
                                        .f("&7That player is not online! Enter a valid player name or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(amnt);
                                return;
                            }
                            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
                            user.takeBank(amnt);
                            targetGtmUser.addBank(amnt);
                            user.clearCurrentChatAction();
                            User playerUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
                            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                            player.sendMessage(Lang.BANK.f("&7You transferred &a$&l" + amnt + "&7 into &a"
                                    + targetUser.getColoredName(target) + "&7's bank account!"));
                            target.sendMessage(Lang.BANK.f("&a" + playerUser.getColoredName(player) + "&7 transferred &a$&l" + amnt
                                    + "&7 into your bank account!"));
                            GTMUtils.updateBoard(player, playerUser, user);
                            GTMUtils.updateBoard(target, targetUser, targetGtmUser);
                            break;
                        }
                        case BUYING_LOTTERY_TICKETS: {
                            if ("quit".equalsIgnoreCase(msg)) {
                                user.clearCurrentChatAction();
                                player.sendMessage(Utils.f(Lang.LOTTERY + "&7You cancelled buying lottery tickets!"));
                                MenuManager.openMenu(player, "lottery");
                                return;
                            }

                            int amnt;
                            try {
                                amnt = Integer.parseInt(msg);
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.LOTTERY + "&7Please enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (amnt < 1) {
                                player.sendMessage(Utils.f(Lang.LOTTERY
                                        + "&7The minimum amount is &e&l1&7! Enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (amnt > 100000) {
                                player.sendMessage(Utils.f(Lang.LOTTERY
                                        + "&7The maximum amount is &e&l100000&7! Enter a valid number or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            if (!user.hasMoney(amnt * 500)) {
                                player.sendMessage(Lang.LOTTERY.f("&7You don't have &a$&l" + (amnt * 500)
                                        + "&7 to buy &e&l" + amnt + " Tickets&7! Enter a valid amount or type &a\"quit\"&7!"));
                                user.resetCurrentChatActionTimer(0);
                                return;
                            }

                            user.clearCurrentChatAction();
                            user.takeMoney(amnt * 500);
                            LotteryPlayer p = GTM.getLottery().getLotteryPlayer(player.getUniqueId());
                            if (p == null) {
                                p = new LotteryPlayer(player.getUniqueId(), player.getName());
                                GTM.getLottery().addLotteryPlayer(p);
                            }

                            p.addTickets(amnt);
                            GTMUtils.updateBoard(player, user);
                            player.sendMessage(Utils.f(Lang.LOTTERY + "&7You bought &e&l" + amnt + " Tickets&7 for &a$&l" + (amnt * 500) + "&7!"));
                            break;
                        }
                        case GANG_CHAT_ACTION: {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if ("quit".equalsIgnoreCase(msg)) {
                                        user.clearCurrentChatAction();
                                        player.sendMessage(Lang.GANGS.f("&7You cancelled this gang action!"));
                                        MenuManager.openMenu(player, "mygang");
                                        return;
                                    }

                                    Gang userGang = GangManager.getInstance().getGangByMember(player.getUniqueId()).orElse(null);

                                    String ac = (String) user.getCurrentChatValue();
                                    switch (ac) {
                                        case "create":
                                            if (userGang != null) return;
                                            GangManager.getInstance().createGang(player, msg);
                                            user.clearCurrentChatAction();
                                            return;
                                        case "leader": {
                                            Player target = Bukkit.getPlayer(msg);
                                            if (target == null) {
                                                player.sendMessage(Lang.GANGS.f("&7That player is not online!"));
                                                return;
                                            }

                                            if (userGang == null) {
                                                player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                                return;
                                            }

                                            user.clearCurrentChatAction();
                                            userGang.setOwner(player, Core.getUserManager().getLoadedUser(uuid), user, target);
                                            return;
                                        }
                                        case "description": {
                                            if (userGang == null) {
                                                player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                                return;
                                            }
                                            user.clearCurrentChatAction();
                                            userGang.description(player, Core.getUserManager().getLoadedUser(uuid), user, msg);
                                            return;
                                        }
                                        case "name": {
                                            if (userGang == null) {
                                                player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                                return;
                                            }
                                            user.clearCurrentChatAction();
                                            userGang.rename(player, Core.getUserManager().getLoadedUser(uuid), user, msg);
                                            return;
                                        }
                                        case "relation": {
                                            if (userGang == null) {
                                                player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                                return;
                                            }

                                            if (!userGang.isViewingGang(uuid)) {
                                                Gang view = GangManager.getInstance().getGang(msg).orElse(null);
                                                if (view == null) {
                                                    Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!");
                                                    return;
                                                }

                                                user.clearCurrentChatAction();
                                                user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "relation");
                                                userGang.setViewingGang(uuid, view);
                                                player.sendMessage(Lang.GANGS.f("&7Please type in the relation (ally, neutral or enemy) you would like to set towards gang &a" + msg + "&7, or type &a\"quit\" to quit!"));
                                                return;
                                            }

                                            Gang viewingGang = userGang.getViewingGang(uuid).orElse(null);
                                            if (viewingGang == null) return;
                                            userGang.setViewingGang(uuid, null);
                                            user.clearCurrentChatAction();
                                            switch (msg) {
                                                case "ally":
                                                    userGang.ally(player, Core.getUserManager().getLoadedUser(uuid), user, viewingGang.getName());
                                                    return;
                                                case "neutral":
                                                    userGang.neutral(player, Core.getUserManager().getLoadedUser(uuid), user, viewingGang.getName());
                                                    return;
                                                case "enemy":
                                                    userGang.enemy(player, Core.getUserManager().getLoadedUser(uuid), user, viewingGang.getName());
                                                    return;
                                                default:
                                                    player.sendMessage(Lang.GANGS.f("&7The relation must be one of ally, neutral or enemy!"));
                                                    return;
                                            }
                                        }
                                        case "invite":
                                            if (userGang == null) {
                                                player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                                return;
                                            }

                                            Player target = Bukkit.getPlayer(msg);
                                            if (target == null) {
                                                player.sendMessage(Lang.GANGS.f("&7That player is not online!"));
                                                return;
                                            }

                                            user.clearCurrentChatAction();
                                            userGang.invite(player, Core.getUserManager().getLoadedUser(uuid), target);
                                            return;
                                        default:
                                            MenuManager.openMenu(player, "mygang");
                                            player.sendMessage(Lang.GANGS.f("&7That is not a gang action!"));
                                            break;
                                    }
                                }
                            }.runTask(GTM.getInstance());
                        }
                    }
                }
            }.runTask(GTM.getInstance());
        }

        Gang userGang = GangManager.getInstance().getGangByMember(uuid).orElse(null);

        if (userGang != null && userGang.isGangChat(uuid)) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) return;

                    GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                    userGang.chat(player, Core.getUserManager().getLoadedUser(uuid), user, msg);
                    Core.log("[GangChat] " + player.getName() + ": " + msg);
                    return;

                }
            }.runTask(GTM.getInstance());
        }

        if (msg.startsWith("@")) {
            if (userGang != null) {
                String gangMsg = msg.replace("@", "");
                userGang.chat(player, coreUser, user, gangMsg);
                e.setCancelled(true);
            }
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
            }.runTaskLater(GTM.getInstance(), 800);
        }*/
    }

    private class TransferPayload {
        private int generatedNumber;
        private int gtmID;

        public TransferPayload(int generatedNumber, int gtmID) {
            this.gtmID = gtmID;
            this.generatedNumber = generatedNumber;
        }

        public int getGeneratedNumber() {
            return this.generatedNumber;
        }

        public int getGtmID() {
            return this.gtmID;
        }
    }
}


