package net.grandtheftmc.core.event;

import java.sql.Connection;
import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;

public class EventCommand implements CommandExecutor {

	/** The owning plugin */
	private Plugin plugin;

	/**
	 * Construct a new EventCommand.
	 * <p>
	 * This command is used to schedule events in the future.
	 * </p>
	 * 
	 * @param plugin - the owning plugin
	 */
	public EventCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {

		if (s instanceof Player) {
			Player player = (Player) s;

			// get the user
			User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
			if (!user.isRank(UserRank.ADMIN)) {
				player.sendMessage(Lang.NOPERM.toString());
				return true;
			}

			if (args.length > 0) {

				switch (args[0].toLowerCase()) {
					case "schedule":
						handleScheduleCommand(player, args);
						return true;
					case "clear":
						handleClearCommand(player, args);
						return true;
					case "help":
						handleHelpCommand(player);
						return true;
				}
			}

			handleHelpCommand(player);
			return true;
		}

		return true;
	}

	/**
	 * Handles the executing of the help subcommand.
	 * 
	 * @param player - the player executing the command
	 */
	protected void handleHelpCommand(Player player) {

		// event schedule server_key halloween 10-29-17
		// event schedule server_key halloween 10-22-17 10-29-17
		// event clear all
		// event clear server_key halloween

		player.sendMessage(ChatColor.GRAY + "--- " + ChatColor.GOLD + "Event Scheduling" + ChatColor.GRAY + " ---");
		player.sendMessage(ChatColor.WHITE + "/event schedule [serverKey] [eventType] [end_date]" + ChatColor.GRAY + " Schedules event for given server, starting now and ending on endDate.");
		player.sendMessage(ChatColor.GRAY + "Example usage: " + ChatColor.WHITE + "/event schedule " + Core.getSettings().getServer_GTM_shortName() + "1 HALLOWEEN 2017-10-31.10:00:00");
		player.sendMessage("");

		player.sendMessage(ChatColor.WHITE + "/event schedule [serverKey] [eventType] [endDate] [startDate]" + ChatColor.GRAY + " Schedules event for given server, with the startDate / endDate.");
		player.sendMessage("");

		player.sendMessage(ChatColor.WHITE + "/event clear all" + ChatColor.GRAY + " Clear all events across all servers.");
		player.sendMessage("");

		player.sendMessage(ChatColor.WHITE + "/event clears [serverKey]" + ChatColor.GRAY + " Clear the event for the given serverKey.");
		player.sendMessage("");
	}

	/**
	 * Handles the executing of the schedule subcommand.
	 * 
	 * @param player - the player executing the command
	 * @param args - the args
	 */
	protected void handleScheduleCommand(Player player, String[] args) {

		// event schedule [serverKey] [eventType] [end_date]
		if (args.length == 4) {
			String serverKey = args[1];
			EventType eventType = EventType.fromID(args[2]).orElse(null);
			Timestamp endDate = getTimestamp(args[3]);
			
			if (eventType == null){
				player.sendMessage(ChatColor.RED + "Invalid event type. Please try one of the following: ");
				for (EventType et : EventType.values()){
					player.sendMessage(ChatColor.RED + "- " + ChatColor.WHITE + et.getId());
				}
				return;
			}
			
			if (endDate == null){
				player.sendMessage(ChatColor.RED + "Invalid end date as it must be in the form of " + ChatColor.WHITE + "2017-10-31.10:00:00");
				return;
			}
			
			player.sendMessage(ChatColor.YELLOW + "Attempting to register event " + ChatColor.WHITE + eventType.getId() + ChatColor.YELLOW + " for " + ChatColor.WHITE + serverKey + ChatColor.YELLOW + " ending on " + ChatColor.WHITE + endDate.toString());
			
			// TODO add extra data support here
			JSONObject data = new JSONObject();
			
			// async update
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try (Connection conn = BaseDatabase.getInstance().getConnection()) {
					EventDAO.scheduleEvent(conn, serverKey, eventType, data, endDate);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		// event schedule [serverKey] [eventType] [endDate] [startDate]
		else if (args.length == 5) {
			String serverKey = args[1];
			EventType eventType = EventType.fromID(args[2]).orElse(null);
			Timestamp endDate = getTimestamp(args[3]);
			Timestamp startDate = getTimestamp(args[4]);
			
			if (eventType == null){
				player.sendMessage(ChatColor.RED + "Invalid event type. Please try one of the following: ");
				for (EventType et : EventType.values()){
					player.sendMessage(ChatColor.RED + "- " + ChatColor.WHITE + et.getId());
				}
				return;
			}
			
			if (endDate == null){
				player.sendMessage(ChatColor.RED + "Invalid end date as it must be in the form of " + ChatColor.WHITE + "2017-10-31.10:00:00");
				return;
			}
			
			if (startDate == null){
				player.sendMessage(ChatColor.RED + "Invalid start date as it must be in the form of " + ChatColor.WHITE + "2017-10-31.10:00:00");
				return;
			}
			
			player.sendMessage(ChatColor.YELLOW + "Attempting to register event " + ChatColor.WHITE + eventType.getId() + ChatColor.YELLOW + " for " + ChatColor.WHITE + serverKey + ChatColor.YELLOW + " ending on " + ChatColor.WHITE + endDate.toString() + ChatColor.YELLOW + " and starting on " + ChatColor.WHITE + startDate.toString());
			
			// TODO add extra data support here
			JSONObject data = new JSONObject();
			
			// async update
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try (Connection conn = BaseDatabase.getInstance().getConnection()) {
					EventDAO.scheduleEvent(conn, serverKey, eventType, data, startDate, endDate);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * Handles the executing of the clear subcommand.
	 * 
	 * @param player - the player executing the command
	 * @param args - the args
	 */
	protected void handleClearCommand(Player player, String[] args) {

		// /event clear [serverKey]
		if (args.length == 2) {

			String serverKey = args[1];

			if (serverKey.equalsIgnoreCase("all")) {
				player.sendMessage(ChatColor.YELLOW + "Attempting to clear ALL events...");

				// async update
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
					try (Connection conn = BaseDatabase.getInstance().getConnection()) {
						EventDAO.clearAllActiveEvents(conn);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			else {
				
				player.sendMessage(ChatColor.YELLOW + "Attempting to clear the event for server=" + ChatColor.WHITE + serverKey);

				// async update
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
					try (Connection conn = BaseDatabase.getInstance().getConnection()) {
						EventDAO.clearActiveEvent(conn, serverKey);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		}
	}
	
	/**
	 * Get the timestamp from the given text.
	 * 
	 * @param text - the input text
	 * @return The timestamp from the given text, if one exists
	 */
	protected Timestamp getTimestamp(String text){
		
		// our input is 2017-10-31.10:00:00
		String[] parts = text.split("\\.");
		
		if (parts.length == 2){
			// replaces . with space
			return Timestamp.valueOf(parts[0] + " " + parts[1]);
		}
		
		return null;
	}
}
