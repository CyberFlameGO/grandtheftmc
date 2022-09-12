package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by ThatAbstractWolf on 2017-08-04.
 */
public class SpankCommand extends CoreCommand<Player> {

	public SpankCommand() {
		super("spank", "Spank them all!");
	}

	@Override
	public void execute(Player player, String[] args) {

		User user = Core.getUserManager().getLoadedUser(player.getUniqueId());

		if (player.getUniqueId().toString().equals("293af3b1-1cc7-47cb-bf50-d5318ea35def")) {
			player.sendMessage(Lang.SPANK.f("Hey there bb, no spanking for you! ;)"));
			return;
		}

		if (user.getLastSpanked() == 0L || System.currentTimeMillis() >= user.getLastSpanked()) {

			if (user.isRank(UserRank.SUPREME)) {

				if (args.length == 1) {

					Player spanking = Bukkit.getPlayer(args[0]);

					if (spanking == null) {
						player.sendMessage(Lang.SPANK.f("You can't spank that player, they're not around this server :("));
						return;
					}

					if (spanking == player) {
						player.sendMessage(Lang.SPANK.f("Ohh you're dirty.. but not dirty enough to spank yourself. :("));
						return;
					}

					for (Player all : Bukkit.getOnlinePlayers()) {

						User allUser = Core.getUserManager().getLoadedUser(all.getUniqueId());

						if (!allUser.getPref(Pref.MESSAGES) && !all.equals(player)) {
							continue;
						}

						if (allUser.getIgnored().contains(player.getName())) {
							continue;
						}

						all.sendMessage(Lang.SPANK.f("Daddy &d&l" + player.getName() + " &7spanked &b&l" + spanking.getName() + " &7:o"));
					}

					user.setLastSpanked(System.currentTimeMillis() + 60000L);
				} else {
					player.sendMessage(Lang.SPANK.f("/spank <name>"));
				}
			} else {
				player.sendMessage(Lang.SPANK.f("&7You must be " + UserRank.SUPREME.getColoredNameBold() + " &7to spank! ;)"));
			}
		} else {
			player.sendMessage(Lang.SPANK.f("You cannot use spank yet! Please wait &d" + TimeUnit.MILLISECONDS.toSeconds(user.getLastSpanked() - System.currentTimeMillis()) + " &7seconds."));
		}
	}
}
