package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand extends CoreCommand<Player> {

	public DiscordCommand() {
		super("discord", "Get the invite link to Discord.");
	}

	@Override
	public void execute(Player sender, String[] args) {
		sender.sendMessage(Lang.DISCORD.f("&7Discord Invite link: &9" + (Core.getSettings().isSister() ? "https://discord.gg/ZtpMZ6g" : "https://discord.gg/4P6DVKZ")));
	}
}
