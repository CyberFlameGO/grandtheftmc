package net.grandtheftmc.gtm.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.TopValue;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMUser;

public class MoneyCommand implements CommandExecutor {

	/** Only display this many highscores, paginated */
	public static final Integer MAX_HIGHSCORES = 80;
	/** Number of results to display per page */
	public static final Integer RESULTS_PER_PAGE = 8;
	/** Sorted list of top bank values */
	private List<TopValue> topBank;
	/** Sorted list of top money values */
	private List<TopValue> topMoney;

    public MoneyCommand(){
    	this.topBank = new ArrayList<>();
    	this.topMoney = new ArrayList<>();
    	
    	// run update every 60 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
               fetchTopBank();
               fetchTopMoney();
            }
        }.runTaskTimerAsynchronously(GTM.getInstance(), 0,20L * (60*60));
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.money")) {
        	
        	if (args.length > 0 && args[0].equalsIgnoreCase("top")){
        		// ALLOW
        	}
        	else{
                s.sendMessage(Lang.NOPERM.toString());
                return true;
        	}
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/money balance <player>"));
            s.sendMessage(Utils.f("&c/money give <player> <amount>"));
            s.sendMessage(Utils.f("&c/money take <player> <amount>"));
            s.sendMessage(Utils.f("&c/money bank <player>"));
            s.sendMessage(Utils.f("&c/money givebank <player> <amount>"));
            s.sendMessage(Utils.f("&c/money takebank <player> <amount>"));
            s.sendMessage(Utils.f("&c/money top <money/bank> [page] - Shows the baltop for cash, bank or combined"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "balance": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/money balance <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    UUID senderUUID = s instanceof Player ? ((Player) s).getUniqueId() : null;
                    s.sendMessage(Utils.f("&cThat player isn't online, so please wait while the money is pulled from the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }
                        
                        int money = 0;
                        
                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                            
                        	money = CurrencyDAO.getCurrency(connection, Currency.MONEY.getServerKey(), uuid, Currency.MONEY);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        int finalMoney = money;
                        ServerUtil.runTask(() -> (senderUUID == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(senderUUID)).sendMessage(Lang.MONEY.f("&a " + args[1] + " has $" + finalMoney)));
                    });

                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Utils.f(Lang.MONEY + "&a" + player.getName() + "&7 has &a$&l" + user.getMoney() + "&7!"));
                return true;
            }
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money give <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the money is forcibly updated in the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }
                        
                        try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        	CurrencyDAO.addCurrency(conn, Currency.MONEY.getServerKey(), uuid, Currency.MONEY, (int) amnt);
                        }
                        catch(Exception e){
                        	e.printStackTrace();
                        }

                        if (uuid == null) Core.log("Error while logging giveMoneyCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                        else Utils.insertLog(uuid, args[1], "giveMoneyCommand", "MONEY", "$" + amnt + " Money", amnt, 0);
                    });
                    
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.addMoney(amnt);
                Core.getUserManager().getLoadedUser(player.getUniqueId()).insertLog(player, "giveMoneyCommand", "MONEY", "$" + amnt + " Money", amnt, 0);
                GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils.f(Lang.MONEY + "&7You gave &a$&l" + amnt + "&7 to &a" + player.getName() + "&7!"));
                player.sendMessage(
                        Utils.f(Lang.MONEY + "&7You were given &a$&l" + amnt + "&7 by &a" + s.getName() + "&7!"));
                return true;
            }
            case "take": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money take <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the money is forcibly updated in the database."));

                    double finalAmnt1 = amnt;
                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }
                        
                        try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        	CurrencyDAO.addCurrency(conn, Currency.MONEY.getServerKey(), uuid, Currency.MONEY, (int) -finalAmnt1);
                        }
                        catch(Exception e){
                        	e.printStackTrace();
                        }
                        
                        if (uuid == null) Core.log("Error while logging takeMoneyCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -finalAmnt1);
                        else Utils.insertLog(uuid, args[1], "takeMoneyCommand", "MONEY", "-$" + finalAmnt1 + " Money", -finalAmnt1, 0);
                    });
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.hasMoney(amnt))
                    amnt = user.getMoney();
                user.takeMoney(amnt);
                Core.getUserManager().getLoadedUser(player.getUniqueId()).insertLog(player, "takeMoneyCommand", "MONEY", "-$" + amnt + " Money", -amnt, 0);
                GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils.f(Lang.MONEY + "&7You took &c$&l" + amnt + "&7 from &a" + player.getName() + "&7!"));
                player.sendMessage(
                        Utils.f(Lang.MONEY + "&c$&l" + amnt + "&7 was taken from you by &a" + s.getName() + "&7!"));
                return true;
            }
            case "bank": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/money balance <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    UUID senderUUID = s instanceof Player ? ((Player) s).getUniqueId() : null;
                    s.sendMessage(Utils.f("&cThat player isn't online, so please wait while the money is pulled from the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        String name = null;
                        int money = 0;
                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                            try (PreparedStatement statement = connection.prepareStatement("select name,bank from " + Core.name() + " where uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "');")) {
                                try (ResultSet result = statement.executeQuery()) {
                                    if (result.next()) {
                                        name = result.getString("name");
                                        money = result.getInt("bank");
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        String finalName = name;
                        int finalMoney = money;
                        ServerUtil.runTask(() -> (senderUUID == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(senderUUID)).sendMessage(Lang.MONEY.f("&a " + finalName + " has $" + finalMoney + " in the bank")));
                    });
                    
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Utils.f(
                        Lang.BANK + "&a" + player.getName() + "&7 has &a$&l" + user.getBank() + "&7 in his bank account!"));
                return true;
            }
            case "givebank": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money givebank <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the bank money is forcibly updated in the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        BaseDatabase.runCustomQuery("update " + Core.name() + " set bank=bank+" + amnt + " where uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "');");

                        if(uuid == null) Core.log("Error while logging giveBankCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                        else Utils.insertLog(uuid, args[1], "giveBankCommand", "MONEY", "$" + amnt + " Money", amnt, 0);
                    });

                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.addBank(amnt);
                Core.getUserManager().getLoadedUser(player.getUniqueId()).insertLog(player, "giveBankCommand", "MONEY", "$" + amnt + " Money", amnt, 0);
                GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils
                        .f(Lang.BANK + "&7You put &a$&l" + amnt + "&7 into &a" + player.getName() + "&7's bank account!"));
                player.sendMessage(
                        Utils.f(Lang.BANK + "&a$&l" + amnt + "&7 was put into your account by &a" + s.getName() + "&7!"));
                return true;
            }
            case "takebank":
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money takebank <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the bank money is forcibly updated in the database."));

                    double finalAmnt = amnt;
                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        BaseDatabase.runCustomQuery("update " + Core.name() + " set bank=bank-" + finalAmnt + "' where uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "');");

                        if(uuid == null) Core.log("Error while logging takeBankCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -finalAmnt);
                        else Utils.insertLog(uuid, args[1], "takeBankCommand", "MONEY", "-$" + finalAmnt + " Money", -finalAmnt, 0);
                    });
                    
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.hasBank(amnt))
                    amnt = user.getBank();
                user.takeBank(amnt);
                Core.getUserManager().getLoadedUser(player.getUniqueId()).insertLog(player, "takeBankCommand", "MONEY", "-$" + amnt + " Money", -amnt, 0);
                GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils
                        .f(Lang.BANK + "&7You took &c$&l" + amnt + "&7 from &a" + player.getName() + "&7's bank account!"));
                player.sendMessage(
                        Utils.f(Lang.BANK + "&c$&l" + amnt + "&7 was taken from your account by &a" + s.getName() + "&7!"));
                return true;
            case "top": {
            	
                boolean bank = false;
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }
                if (args.length < 2 || args.length > 3) {
                    s.sendMessage(Utils.f("&c/money top <money/bank> [page]"));
                    return true;
                }
                
                if ("money".equalsIgnoreCase(args[1])) {
                    s.sendMessage(Lang.MONEY.f("&7Looking up Money Top in database..."));
                } else if ("bank".equalsIgnoreCase(args[1])) {
                    bank = true;
                    s.sendMessage(Lang.MONEY.f("&7Looking up Bank Top in database..."));
                } else {
                    s.sendMessage(Utils.f("&c/money top <money/bank>"));
                    return true;
                }
                
                int page = 1;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } 
                    catch (NumberFormatException ignored) {
                    	
                    }
                }
                
                int maxPages = MAX_HIGHSCORES / RESULTS_PER_PAGE;
                if (page > maxPages){
                	page = maxPages;
                }
                
                s.sendMessage(bank ? Lang.BANK.f("&7Bank Top Page &a&l" + page + "&7:") : Lang.MONEY.f("&7Money Top Page &a&l" + page + "&7:"));
                
                // assign a copy
                List<TopValue> leaderboard = topMoney;
                if (bank){
                	leaderboard = topBank;
                }
                
                // iterate from 0 to results per page
                for (int i = 0; i < RESULTS_PER_PAGE; i++){
                	
                	// compute the index that we want to retrieve from the top scores
                	int topIndex = ((page - 1) * RESULTS_PER_PAGE) + i;
                	
                	// examples
                	// page = 1
                	// topIndex = (0 * 5) + 0 = 0
                	// topIndex = (0 * 5) + 1 = 1
                	// page = 2
                	// topIndex = (1 * 5) + 0 = 5
                	// topIndex = (1 * 5) + 1 = 6
                	
                	if (topIndex < leaderboard.size()){
                		TopValue topValue = leaderboard.get(topIndex);
                		if (topValue != null){
                			s.sendMessage(Utils.f("&a#&l" + (topIndex + 1) + "&7: &r" + topValue.getId() + "&7 &a" + Utils.formatMoney(topValue.getAmount())));
                		}
                	}
                }
                
                if (page != maxPages){
                	s.sendMessage(Utils.f("&7Type &a/money top " + (bank ? "bank" : "money") + ' ' + (page + 1) + "&7 for more..."));
                }
                
                return true;
            }
            
            default:
                s.sendMessage(Utils.f("&c/money balance <player>"));
                s.sendMessage(Utils.f("&c/money give <player> <amount>"));
                s.sendMessage(Utils.f("&c/money take <player> <amount>"));
                s.sendMessage(Utils.f("&c/money bank <player>"));
                s.sendMessage(Utils.f("&c/money givebank <player> <amount>"));
                s.sendMessage(Utils.f("&c/money takebank <player> <amount>"));
                s.sendMessage(Utils.f("&c/money top <money/bank> [page] - Shows the baltop for cash, bank or combined"));
                return true;
        }

    }
    
    /**
     * Fetch the top banks in the database, and update fields.
     */
    protected void fetchTopBank(){
    	
    	try (Connection connection = BaseDatabase.getInstance().getConnection()){
	
    		try(PreparedStatement ps = connection.prepareStatement("SELECT name, bank FROM "+Core.name()+" WHERE name != 'ERROR' ORDER BY bank DESC LIMIT ?;")) {
				ps.setInt(1, MAX_HIGHSCORES);
                
				try(ResultSet result = ps.executeQuery()) {
				    
					Map<String, Integer> top = new HashMap<>();
					while(result.next()){
						String name = result.getString("name");
						int balance = (int) result.getDouble("bank");
						
						top.put(name, balance);
					}
					ServerUtil.runTask(() -> {
				        topBank.clear();
				        top.forEach((k, v) -> {
				        	topBank.add(new TopValue(k, v));
				        });
				        Collections.sort(topBank, Collections.reverseOrder());
				    });
				}
            }
        } 
    	catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fetch the top money in the database, and update fields.
     */
    protected void fetchTopMoney(){
    	
    	try (Connection connection = BaseDatabase.getInstance().getConnection()){
    		
    		String query = "SELECT U.name as name, UC.amount AS money FROM user U, user_currency UC WHERE U.uuid=UC.uuid AND server_key=? AND currency=? ORDER BY amount DESC LIMIT ?;";
    		
    		try(PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, Core.name().toUpperCase());
				ps.setString(2, "MONEY");
				ps.setInt(3, MAX_HIGHSCORES);
                
				try(ResultSet result = ps.executeQuery()) {
				    
					Map<String, Integer> top = new HashMap<>();
					while(result.next()){
						String name = result.getString("name");
						int balance = result.getInt("money");
						
						top.put(name, balance);
					}
					ServerUtil.runTask(() -> {
						topMoney.clear();
				        top.forEach((k, v) -> {
				        	topMoney.add(new TopValue(k, v));
				        });
				        
				        Collections.sort(topMoney, Collections.reverseOrder());
				    });
				}
            }
        } 
    	catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
