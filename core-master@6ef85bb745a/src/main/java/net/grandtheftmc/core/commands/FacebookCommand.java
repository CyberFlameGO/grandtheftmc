package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import org.bukkit.entity.Player;

public class FacebookCommand extends CoreCommand<Player> {

	public FacebookCommand() {
		super("facebook", "Get the link to our official Facebook page.");
	}

	@Override
	public void execute(Player sender, String[] args) {
		if (Core.getSettings().isSister()) return;
		sender.sendMessage(Lang.FACEBOOK.f("&7Here is the link to our official Facebook page! &9https://www.facebook.com/Grandtheftminecart/"));
	}
}
