package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import org.bukkit.entity.Player;

public class TwitterCommand extends CoreCommand<Player> {

	public TwitterCommand() {
		super("twitter", "Get the link to our official Twitter page.");
	}

	@Override
	public void execute(Player sender, String[] args) {
		if (Core.getSettings().isSister()) return;
		sender.sendMessage(Lang.TWITTER.f("&7Here is the link to our official Twitter page! &9https://twitter.com/grandtheft_mc/"));
	}
}
