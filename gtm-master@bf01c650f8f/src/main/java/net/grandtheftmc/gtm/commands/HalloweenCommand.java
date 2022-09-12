package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.gtm.holidays.halloween.PlayerScare;
import net.grandtheftmc.gtm.holidays.halloween.dao.ServerCoupon;
import net.grandtheftmc.gtm.holidays.halloween.dao.ServerCouponDAO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Timothy Lampen on 2017-10-23.
 */
public class HalloweenCommand extends CoreCommand<Player> {
    public HalloweenCommand() {
        super("halloween", "commands surrounding halloween!");
    }

    @Override
    public void execute(Player sender, String[] args) {
//        if(!sender.isOp()) {
//            sender.sendMessage(Lang.NOPERM.f(""));
//            return;
//        }
//        if(args.length==0) {
//            sender.sendMessage(Utils.f("&7/halloween reset"));
//            sender.sendMessage(Utils.f("&7/halloween trigger"));
//            return;
//        }
//        switch (args[0].toLowerCase()) {
//            case "reset": {
//                try(Connection conn = BaseDatabase.getInstance().getConnection()){
//                    ServerCouponDAO.deleteServerCoupon(conn, sender.getUniqueId(), true);
//                    sender.sendMessage(Utils.f("&7deleted your coupon from DB"));
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//            case"trigger": {
//                PlayerScare.initScare(sender);
//            }
//        }
    }
}
