package net.grandtheftmc.gtm.commands;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;

public class SettingsCommand extends CoreCommand<CommandSender> implements RankedCommand {

	// declarations of all handled flags
	enum SupportedSetting {

		// TODO add more flags here
		PVP, TRANSFER, CHEATCODES, PAY, BANK_TO_BANK, BUY, TRADE, BOUNTY, BOUNTY_TAX, BOUNTY_TAX_PERCENT, KIT,
		DEATH_TAX, DEATH_TAX_SCALED, DEATH_TAX_PERCENT, DEATH_TAX_MIN, DEATH_TAX_MAX;

		public static Optional<SupportedSetting> fromID(String id) {
			for (SupportedSetting ss : values()) {
				if (ss.name().equalsIgnoreCase(id)) {
					return Optional.of(ss);
				}
			}

			return Optional.empty();
		}
	}

	/**
	 * Create a new SettingsCommand.
	 * <p>
	 * This handles any types of variable flags that need to be set/read.
	 */
	public SettingsCommand() {
		super("settings", "Change some settings", "conf");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN))
				return;
		}

		// must supply 2 args
		if (args.length < 2) {
			sendHelp(sender);
			return;
		}

		// parse first two variables
		String command = args[0];
		SupportedSetting ss = SupportedSetting.fromID(args[1]).orElse(null);

		if (command == null || ss == null) {
			sendHelp(sender);
			return;
		}

		switch (command.toLowerCase()) {
			case "get":
				handleGet(sender, command, ss);
				break;
			case "set":
				if (args.length >= 3) {
					handleSet(sender, command, ss, args);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Need to specify third argument for command 'set'.");
				}
				break;
		}
	}

	/**
	 * Handles the parsing of the get command.
	 * 
	 * @param sender - the sender sending the command
	 * @param command - the command they want
	 * @param ss - the setting they want
	 */
	private void handleGet(CommandSender sender, String command, SupportedSetting ss) {
		switch (ss) {
			case PVP:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isPvp());
				break;
			case TRANSFER:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isServerTransfer());
				break;
			case CHEATCODES:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isGlobalCheatcodes());
				break;
			case PAY:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isPayCommand());
				break;
			case BANK_TO_BANK:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isBankToBankTransfer());
				break;
			case BUY:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().canBuy());
				break;
			case TRADE:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().canTrade());
				break;
			case BOUNTY:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isBountySystem());
				break;
			case BOUNTY_TAX:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isBountyTax());
				break;
			case BOUNTY_TAX_PERCENT:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().getBountyTaxPercent());
				break;
			case KIT:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isKitSystem());
				break;
			case DEATH_TAX:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isServerDeathTax());
				break;
			case DEATH_TAX_SCALED:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().isServerDeathTaxScaled());
				break;
			case DEATH_TAX_PERCENT:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().getServerDeathBasePercent());
				break;
			case DEATH_TAX_MIN:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().getServerDeathTaxMin());
				break;
			case DEATH_TAX_MAX:
				sender.sendMessage(ChatColor.RED + ss.name() + ChatColor.WHITE + "=" + ChatColor.GRAY + GTM.getSettings().getServerDeathTaxMax());
				break;

			// TODO add more flags here
			default:
				break;
		}
	}

	/**
	 * Handles the parsing of the set command.
	 * 
	 * @param sender - the sender sending the command
	 * @param command - the command they want
	 * @param ss - the setting they want to set
	 * @param args - command arguments
	 */
	private void handleSet(CommandSender sender, String command, SupportedSetting ss, String[] args) {

		// Note: Disregard args[0] and args[1]

		switch (ss) {
			case PVP:

				boolean value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setPvp(value);
				GTM.getSettings().getGtmConfig().set("pvp", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case TRANSFER:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setServerTransfer(value);
				GTM.getSettings().getGtmConfig().set("server-transfer", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case CHEATCODES:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setGlobalCheatcodes(value);
				GTM.getSettings().getGtmConfig().set("cheatcodes", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case PAY:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setPayCommand(value);
				GTM.getSettings().getGtmConfig().set("pay", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case BANK_TO_BANK:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setBankToBankTransfer(value);
				GTM.getSettings().getGtmConfig().set("bank-to-bank-transfer", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case BUY:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setBuy(value);
				GTM.getSettings().getGtmConfig().set("buy", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case TRADE:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setTrade(value);
				GTM.getSettings().getGtmConfig().set("player-trade", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case BOUNTY:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setBountySystem(value);
				GTM.getSettings().getGtmConfig().set("bounty-system", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case BOUNTY_TAX:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setBountyTax(value);
				GTM.getSettings().getGtmConfig().set("bounty-system-tax", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case BOUNTY_TAX_PERCENT:

				double percent = 0;
				try {
					percent = Double.parseDouble(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a double!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + percent);

				GTM.getSettings().setBountyTaxPercent(percent);
				GTM.getSettings().getGtmConfig().set("bounty-tax-percent", percent);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case KIT:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setKitSystem(value);
				GTM.getSettings().getGtmConfig().set("kit-system", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case DEATH_TAX:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setServerDeathTax(value);
				GTM.getSettings().getGtmConfig().set("server-death-tax", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case DEATH_TAX_SCALED:

				value = false;
				try {
					value = Boolean.parseBoolean(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a boolean!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + value);

				GTM.getSettings().setServerDeathTaxScaled(value);
				GTM.getSettings().getGtmConfig().set("server-death-tax-scaled", value);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case DEATH_TAX_PERCENT:

				percent = 0;
				try {
					percent = Double.parseDouble(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to a double!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + percent);

				GTM.getSettings().setServerDeathBasePercent(percent);
				GTM.getSettings().getGtmConfig().set("server-death-tax-percent", percent);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case DEATH_TAX_MIN:

				int amount = 0;
				try {
					amount = Integer.parseInt(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to an integer!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + amount);

				GTM.getSettings().setServerDeathTaxMin(amount);
				GTM.getSettings().getGtmConfig().set("server-death-tax-min", amount);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;
			case DEATH_TAX_MAX:

				amount = 0;
				try {
					amount = Integer.parseInt(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Unable to parse '" + args[2] + "' to an integer!");
					e.printStackTrace();
					return;
				}

				sender.sendMessage(ChatColor.GRAY + "Attempting to set " + ChatColor.RED + ss + ChatColor.GRAY + " to " + ChatColor.WHITE + amount);

				GTM.getSettings().setServerDeathTaxMax(amount);
				GTM.getSettings().getGtmConfig().set("server-death-tax-max", amount);
				Utils.saveConfig(GTM.getSettings().getGtmConfig(), "gtm");

				sender.sendMessage(ChatColor.GREEN + "SUCCESS!");

				break;

			// TODO add more flags here
			default:
				break;
		}
	}

	/**
	 * Send help message to the command sender.
	 * 
	 * @param sender - the sender of the command
	 */
	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "/settings set <conf> <value>" + ChatColor.WHITE + " - " + ChatColor.GRAY + "Set the new value of the setting");
		sender.sendMessage(ChatColor.RED + "/settings get <conf>" + ChatColor.WHITE + " - " + ChatColor.GRAY + "Get the value of the setting.");

		StringBuilder builder = new StringBuilder();
		for (SupportedSetting ss : SupportedSetting.values()) {
			builder.append(ss.name());
			builder.append(",");
		}

		sender.sendMessage("");
		sender.sendMessage(ChatColor.RED + "Known conf keys: " + ChatColor.WHITE + builder.substring(0, builder.length() - 1).toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRank requiredRank() {
		return UserRank.ADMIN;
	}
}
