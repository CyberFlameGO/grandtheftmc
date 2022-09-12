package net.grandtheftmc.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.trading.TradeManager;

/**
 * Created by Timothy Lampen on 11/1/2017.
 */
public class TradeCommand extends CoreCommand<Player> {
    public TradeCommand() {
        super("trade", "trade with other players", "tradeplayer");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length==0){
            player.sendMessage(Lang.TRADE.f("&7/trade <player> &6- sends a trade request to the target player"));
            return;
        }
        TradeManager tm = Core.getInstance().getTradeManager();
        
        if (!tm.isEnabled()){
        	player.sendMessage(ChatColor.RED + "Trading is currently disabled!");
        	return;
        }
        
        switch (args[0].toLowerCase()) {
            case "accept": {
                if(!tm.getPendingTrade(player.getUniqueId()).isPresent()) {
                    player.sendMessage(Lang.TRADE.f("&7You do not have any pending trade requests."));
                    return;
                }
                Player origin = Bukkit.getPlayer(tm.getPendingTrade(player.getUniqueId()).get().getValue());
                if(origin == null) {
                    player.sendMessage(Lang.TRADE.f("&7The player who sent you the request is no longer online."));
                    tm.removePendingTrade(player.getUniqueId());
                    return;
                }
                if(Core.getTradeManager().getTrade(player).isPresent() || Core.getTradeManager().getTrade(origin).isPresent()) {
                    player.sendMessage(Lang.TRADE.f("&7You cannot trade with this player since they are already in a trade!"));
                    tm.removePendingTrade(player.getUniqueId());
                    return;
                }
                if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                    player.sendMessage(Lang.TRADE.f("&7You cannot accept trades while spectating."));
                    return;
                }
                if(origin.getGameMode() != GameMode.SURVIVAL && origin.getGameMode() != GameMode.ADVENTURE) {
                    player.sendMessage(Lang.TRADE.f("&7This player cannot accept trades just yet."));
                    return;
                }
                if(!player.getWorld().getName().equals(origin.getWorld().getName()) || player.getLocation().distance(origin.getLocation())>10) {
                    player.sendMessage(Lang.TRADE.f("&7You cannot trade with this player because they are too far away!"));
                    tm.removePendingTrade(player.getUniqueId());
                    return;
                }
                tm.removePendingTrade(player.getUniqueId());
                Core.getTradeManager().startTrade(origin, player);
                return;
            }
            case "deny": {
                if(!tm.getPendingTrade(player.getUniqueId()).isPresent()) {
                    player.sendMessage(Lang.TRADE.f("&7You do not have any pending trade requests."));
                    return;
                }
                Player origin = Bukkit.getPlayer(tm.getPendingTrade(player.getUniqueId()).get().getValue());
                if(origin == null) {
                    player.sendMessage(Lang.TRADE.f("&7The player who sent you the request is no longer online."));
                    return;
                }
                tm.removePendingTrade(player.getUniqueId());
                origin.sendMessage(Lang.TRADE.f("&7The player whom you sent the request to has declined it."));
                player.sendMessage(Lang.TRADE.f("&7You have declined the trade request."));
                return;
            }
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target==null) {
            player.sendMessage(Lang.TRADE.f("&7That player is currently not online."));
            return;
        }

        if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
            player.sendMessage(Lang.TRADE.f("&7You cannot request to trade while spectating."));
            return;
        }

        if(target.getGameMode() != GameMode.SURVIVAL && target.getGameMode() != GameMode.ADVENTURE) {
            player.sendMessage(Lang.TRADE.f("&7This player cannot accept trades just yet."));
            return;
        }

        if(!target.getWorld().getName().equals(player.getWorld().getName())) {
            player.sendMessage(Lang.TRADE.f("&7This player out of range.."));
            return;
        }

        if(target.getLocation().distance(player.getLocation()) > 10) {
            player.sendMessage(Lang.TRADE.f("&7This player out of range.."));
            return;
        }

        if(target.equals(player)) {
            player.sendMessage(Lang.TRADE.f("&7You cannot trade yourself."));
            return;
        }
        tm.addPendingTrade(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(Lang.TRADE.f("&7You have sent a trade request to &6" + target.getName() + "&7."));
        target.sendMessage(Lang.TRADE.f("&7You have recieved a trade request from &6" + player.getName() + "&7. Please use '&a/trade accept&7' or '&c/trade deny&7' to respond."));
    }
}
