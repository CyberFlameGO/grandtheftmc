package net.grandtheftmc.core.util;

import org.bukkit.plugin.Plugin;

/**
 * Allows super classes to remind that an interface or object might need a
 * plugin associated with it.
 * 
 * @author sbahr
 */
public interface PluginAssociated<P extends Plugin> {

	/**
	 * Get the plugin that is associated with this object.
	 * 
	 * @return The plugin associated with this object.
	 */
	P getPlugin();
}
