package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.trashcan.TrashCanManager;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.CheatCodeState;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.Player;

public class SellCommand extends CoreCommand<Player> {

	public SellCommand() {
		super("sell", "Quick sell, open a virtual trashcan", "qsell", "quicksell");
	}

	@Override
	public void execute(Player sender, String[] strings) {
		sender.closeInventory();
		User user = Core.getUserManager().getLoadedUser(sender.getUniqueId());
		GTMUser gtmUser = GTM.getUserManager().getLoadedUser(sender.getUniqueId());
		if (gtmUser.getCheatCodeState(CheatCode.QUICKSELL).getState() == State.LOCKED && !user.getUserRank().hasRank(UserRank.SUPREME)) {
			sender.sendMessage(Utils.f("&cThis command requires the &7QuickSell Cheatcode&c or &lSUPREME&c!"));
			return;
		}

		if (sender.getWorld().getName().equals("spawn")) {
			TrashCanManager.openTrashCan(sender);
			return;
		}

		HouseUser houseUser = Houses.getUserManager().getLoadedUser(sender.getUniqueId());
		if (houseUser != null && (houseUser.isInsideHouse() || houseUser.isInsidePremiumHouse())) {
			TrashCanManager.openTrashCan(sender);
			return;
		}

		sender.sendMessage(C.RED + "You can only use this cheatcode in your House or at spawn.");
	}
}
