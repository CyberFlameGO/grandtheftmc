package net.grandtheftmc.core.alert.command;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.alert.Alert;
import net.grandtheftmc.core.alert.AlertEntry;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;
import net.grandtheftmc.core.alert.ui.AlertsMenu;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public class AlertCommand extends CoreCommand<Player> implements RankedCommand {

    private final Core core;
    private final AlertManager alertManager;

    /**
     * Construct a new command.
     */
    public AlertCommand(Core core, AlertManager alertManager) {
        super(
                "alerts",
                "View all Alerts or create a new one."
        );
        this.core = core;
        this.alertManager = alertManager;
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                this.alertManager.reload(obj -> {
                    if (obj) {
                        sender.sendMessage(Lang.ALERTS.f("Successfully reloaded. (" + (System.currentTimeMillis() - start) + "ms)"));
                    } else {
                        sender.sendMessage(Lang.ALERTS.f("An error occurred while reloading!"));
                    }
                });
                return;
            }
        }

        new AlertsMenu(this.alertManager).openInventory(sender);
    }

    /**
     * Get the required rank to use said command.
     *
     * @return UserRank
     */
    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
