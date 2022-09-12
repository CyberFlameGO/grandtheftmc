package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUserDAO;
import net.grandtheftmc.vice.utils.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Luke Bingham on 26/08/2017.
 */
public class BaltopCommand extends CoreCommand<Player> {

    public BaltopCommand() {
        super(
                "baltop",
                "Shows the baltop for money",
                "moneytop", "balancetop"
        );
    }

    @Override
    public void execute(Player sender, String[] strings) {
        UUID uuid = sender.getUniqueId();
        new BukkitRunnable() {
            @Override public void run() {
                getTopBalance(10, results -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return;
                        player.sendMessage( Lang.MONEY.f("&7Money Top:"));

                        int i = 0;
                        for (String key : results.keySet()) {
                            i++;
                            player.sendMessage(Utils.f("&a#&l" + (i) + "&7: &r" + key + "&7 &a" + Utils.formatMoney(results.get(key))));
                        }
                    }
                }.runTask(Vice.getInstance()));
            }
        }.runTaskAsynchronously(Vice.getInstance());
    }

    private void getTopBalance(int amount, Callback<LinkedHashMap<String, Double>> callback) {
        LinkedHashMap<String, Double> results = new LinkedHashMap<>();

        Optional<Object[][]> optional = ViceUserDAO.getBalanceTop(amount);
        if(!optional.isPresent()) return;

        for(int i = 0; i < optional.get().length; i++) {
            results.put((String)optional.get()[i][0], (double)optional.get()[i][1]);
        }

        callback.call(results);
    }
}
