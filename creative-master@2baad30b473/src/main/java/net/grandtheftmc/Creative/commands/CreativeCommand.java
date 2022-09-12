package net.grandtheftmc.Creative.commands;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreativeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (!s.isOp()) {
			s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
			return true;
		}
		if (args.length == 0) {
			s.sendMessage(Utils.f("&c/creative reload"));
			s.sendMessage(Utils.f("&c/creative save"));
			return true;
		}
		switch (args[0].toLowerCase()) {
			case "reload": {
				Creative.getInstance().load();
				s.sendMessage(Utils.f("&7Creative config reloaded."));
				return true;
			}
			case "save": {
				Creative.getInstance().save();
				s.sendMessage(Utils.f("&7Creative config saved."));
				return true;
			}
		}
		s.sendMessage(Utils.f("&c/creative <reload>"));
		return true;
	}

}
