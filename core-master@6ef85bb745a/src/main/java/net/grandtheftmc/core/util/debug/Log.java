package net.grandtheftmc.core.util.debug;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Log {

	/**
	 * Log an error message. This will show up in the console as
	 * [SEVERE][prefix] [message]
	 * 
	 * @param args - Arguments to log in an error message.
	 * @param prefix - Prefix of the plugin. ex: "Hyphenical"
	 */
	public static void error(String prefix, String... args) {
		log(Level.SEVERE, prefix, args);
	}

	/**
	 * Log an info message. This will show up in the console as [INFO][prefix]
	 * [message]
	 * 
	 * @param args - Arguments to log with the prefix of "[INFO]".
	 * @param prefix - Prefix of the plugin. ex: "Hyphenical"
	 */
	public static void info(String prefix, String... args) {
		log(Level.INFO, prefix, args);
	}

	/**
	 * Log a warning message. This will show up in the console as
	 * [WARNING][prefix] [message]
	 * 
	 * @param args - Arguments to log with the prefix of "[WARNING]"
	 * @param prefix - Prefix of the plugin. ex: "Hyphenical"
	 */
	public static void warning(String prefix, String... args) {
		log(Level.WARNING, prefix, args);
	}

	/**
	 * Logs the error message to Bukkit's logger. This will show up in the
	 * console as [LEVEL][PLUGIN] [message]
	 * 
	 * @param level - level in which to log the arguments.
	 * @param prefix - prefix of the plugin name.
	 * @param args - arguments to log.
	 */
	private static void log(Level level, String prefix, String... args) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
		}

		Bukkit.getLogger().log(level, String.format("[" + prefix + "] %s", buffer.toString()));
	}
}
