package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.entity.Player;

public class SellCommand extends CoreCommand<Player> {

	public SellCommand() {
		super("sell", "Quick sell, open a virtual trashcan", "qsell", "quicksell");
	}

	@Override
	public void execute(Player sender, String[] strings) {
		sender.closeInventory();
		User user = Core.getUserManager().getLoadedUser(sender.getUniqueId());
		ViceUser viceUser = Vice.getUserManager().getLoadedUser(sender.getUniqueId());
		if (viceUser.getCheatCodeState(CheatCode.QUICKSELL).getState() == State.LOCKED && !user.getUserRank().hasRank(UserRank.ELITE)) {
			sender.sendMessage(Utils.f("&cThis command requires the &7QuickSell Cheatcode&c or &e&lELITE&c!"));
			return;
		}

		if (viceUser.isInCombat()) {
			sender.sendMessage(C.RED + "You cannot sell items while in combat!");
			return;
		}

		sender.sendMessage(C.RED + "You can only use this cheatcode in your House or at spawn.");
	}
}
