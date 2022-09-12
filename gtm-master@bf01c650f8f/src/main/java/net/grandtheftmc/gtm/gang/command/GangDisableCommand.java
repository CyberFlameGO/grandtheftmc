package net.grandtheftmc.gtm.gang.command;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.gang.GangManager;
import org.bukkit.entity.Player;

public class GangDisableCommand extends CoreCommand<Player> implements RankedCommand {

    public GangDisableCommand() {
        super("gangdisable", "Temp disable features of gangs.");
    }

    @Override
    public void execute(Player sender, String[] strings) {
        GangManager.ENABLED = !GangManager.ENABLED;
        sender.sendMessage(Lang.GANGS.f("&cCreating & Info features have been " + (GangManager.ENABLED ? "Enabled." : "Disabled!")));
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
