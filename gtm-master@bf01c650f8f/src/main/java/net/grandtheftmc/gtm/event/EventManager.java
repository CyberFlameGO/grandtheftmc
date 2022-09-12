package net.grandtheftmc.gtm.event;

import java.util.Optional;

import net.grandtheftmc.gtm.event.christmas.ChristmasEvent;
import net.grandtheftmc.gtm.event.easter.EasterEvent;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.event.BaseEvent;
import net.grandtheftmc.core.event.EventData;
import net.grandtheftmc.gtm.event.halloween.HalloweenEvent;

/**
 * An event manager class that is a subclass of the EventManager from Core.
 * 
 * @author sbahr
 */
public class EventManager extends net.grandtheftmc.core.event.EventManager {


	
	/**
	 * Construct a new EventManager.
	 * 
	 * @param plugin - the owning plugin
	 * @param serverKey - the server key
	 */
	protected EventManager(Plugin plugin, String serverKey) {
		super(plugin, serverKey);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Initialize the manager.
	 * 
	 * @param plugin - the owning plugin
	 * @param serverKey - the key of this server instance
	 */
	public static void init(Plugin plugin, String serverKey) {

		// create singleton instance
		instance = new EventManager(plugin, serverKey);
		initialized = true;
		
		// start sync task
		instance.getSyncTask();
	}

	/**
	 * Construct a BaseEvent object based off the event data.
	 * 
	 * @param plugin - the owning plugin
	 * @param data - the data bound for the event
	 * 
	 * @return The BaseEvent object that was constructed, if one exists.
	 */
	@Override
	public Optional<BaseEvent> constructEvent(Plugin plugin, EventData data){
		
		BaseEvent event = null;
		if(data==null || data.getEventType()==null)
			return Optional.empty();
		switch(data.getEventType()){
			case HALLOWEEN:
				event = new HalloweenEvent(plugin, data.getStartTime().getTime(), data.getEndTime().getTime());
				break;
			case CHRISTMAS:
				event = new ChristmasEvent(plugin, data.getStartTime().getTime(), data.getEndTime().getTime());
				break;
			// TODO add more events here
			case EASTER:
				event = new EasterEvent(plugin, data.getStartTime().getTime(), data.getEndTime().getTime());
				break;
			default:
				break;
		}
		
		return Optional.ofNullable(event);
	}

}
