package net.grandtheftmc.houses.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.houses.Houses;
import org.bukkit.entity.Player;

public class HouseDisableCommand extends CoreCommand<Player> implements RankedCommand {

    public HouseDisableCommand() {
        super("housedisable", "Temp command to disable house features.");
    }

    @Override
    public void execute(Player sender, String[] strings) {
        Houses.ENABLED = !Houses.ENABLED;
        sender.sendMessage(Lang.HOUSES.f("&cBuying/Selling & Info editing is " + (Houses.ENABLED ? "Enabled." : "Disabled!")));
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
