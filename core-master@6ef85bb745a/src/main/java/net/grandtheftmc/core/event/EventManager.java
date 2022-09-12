package net.grandtheftmc.core.event;

import java.sql.Connection;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;

/**
 * An event manager, which should be subclassed and init() should be overloaded to create the new init class for this manager.
 * 
 * @author sbahr
 */
public class EventManager {

	/** How often we tick with the sync task (20 ticks) */
	protected static long TICK_INTERVAL = 20;

	/** The instance of this class */
	protected static EventManager instance;
	/** Whether or not this manager was initialized */
	protected static boolean initialized;

	/** The owning plugin */
	protected Plugin plugin;
	/** The server key for this manager */
	protected String serverKey;
	/** The active event */
	protected Event event;
	// deprecated due to not needing multiple events
	// /** Maps the event id to the event */
	//private Map<String, Event> idToEvent;

	/**
	 * Construct a new EventManager.
	 * <p>
	 * This handles and stores all events.
	 * 
	 * @param plugin - the owning plugin
	 * @param serverKey - the server key for this manager
	 */
	protected EventManager(Plugin plugin, String serverKey) {
		this.plugin = plugin;
		this.serverKey = serverKey;
		//this.idToEvent = new HashMap<>();
		
		Core.log("[EventManager] Constructed EventManager... Listening on serverKey=" + serverKey);
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
	 * Get the instance of this manager.
	 * 
	 * @return The singleton instance for this manager.
	 * 
	 * @throws IllegalStateException if the manager was never initialized with
	 *             {@link #init(Plugin)}.
	 */
	public static EventManager getInstance() throws IllegalStateException {
		if (instance == null) {
			if (!initialized) {
				throw new IllegalStateException("The EventManager was never initialized by the owning plugin! This is a severe error and should be fixed. Please call EventManager.init() first!");
			}
		}

		return instance;
	}
	
//	/**
//	 * Adds the specified event to the manager.
//	 * 
//	 * @param event - the event to add
//	 * 
//	 * @return {@code true} if the event was added, {@code false} otherwise.
//	 * @deprecated - this is for multiple event handling
//	 */
//	@Deprecated
//	public boolean addEvent(Event event) {
//		if (!idToEvent.containsKey(event.getId())) {
//			idToEvent.put(event.getId(), event);
//			return true;
//		}
//
//		return false;
//	}

//	/**
//	 * Get the event from the manager with the specified id.
//	 * 
//	 * @param id - the id of the event to get
//	 * 
//	 * @return The event, with the given id, if one exists.
//	 * @deprecated - this is for multiple event handling
//	 */
//	@Deprecated
//	public Optional<Event> getEvent(String id) {
//		if (idToEvent.containsKey(id)) {
//			return Optional.of(idToEvent.get(id));
//		}
//
//		return Optional.empty();
//	}

//	/**
//	 * Remove the event from the manager with the specified id.
//	 * 
//	 * @param id - the id of the event to remove
//	 * 
//	 * @return The event, with the given id, if one exists, and was successfully
//	 *         removed.
//	 * @deprecated - this is for multiple event handling
//	 */
//	@Deprecated
//	public Optional<Event> removeEvent(String id) {
//		if (idToEvent.containsKey(id)) {
//			return Optional.of(idToEvent.remove(id));
//		}
//
//		return Optional.empty();
//	}
	
	/**
	 * Get the active event from this manager, if one exists.
	 * 
	 * @return The active event, if one exists.
	 */
	public Optional<Event> getEvent() {
		return Optional.ofNullable(event);
	}

	/**
	 * Get the sync task for this event manager.
	 * <p>
	 * This fetches the database to learn about new events.
	 * </p>
	 * 
	 * @return The sync task for this event manager.
	 */
	public BukkitTask getSyncTask() {
		return Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
			
			// if we have an active event
			if (getEvent().isPresent()){
				Event current = getEvent().get();
				if (current != null && current instanceof BaseEvent){
					BaseEvent baseEvent = (BaseEvent) current;
					
					// if the event is over
					if (System.currentTimeMillis() > baseEvent.getEndTime()){
						
						// end the event and nullify
						baseEvent.end();
						this.event = null;
						
						// async clear active event
						Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
							try (Connection conn = BaseDatabase.getInstance().getConnection()){
								EventDAO.clearActiveEvent(conn, serverKey);
							}
							catch(Exception e){
								e.printStackTrace();
							}
						});
					}
				}
			}

			// async fetch
			Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
				
				EventData eventData = null;
				try (Connection conn = BaseDatabase.getInstance().getConnection()){
					eventData = EventDAO.getActiveEvent(conn, serverKey).orElse(null);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				final EventData event = eventData;
				Bukkit.getScheduler().runTask(getPlugin(), () -> {
					
					// if we have an event in the database
					if (event != null){
						
						// do we have a current event
						Event current = getEvent().orElse(null);
						if (current == null){
							
							// construct and start the event
							BaseEvent baseEvent = constructEvent(getPlugin(), event).orElse(null);

							if(baseEvent == null)
								return;
							this.event = baseEvent;
							this.event.start();
						}
					}
					else{
						
						// do we have a current event
						Event current = getEvent().orElse(null);
						
						if (current != null){
							current.end();
							
							// null the event
							this.event = null;
						}
					}
					
				});
			});
			
		}, 0, TICK_INTERVAL);
	}

	/**
	 * {@inheritDoc}
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Construct a BaseEvent object based off the event data.
	 * 
	 * @param plugin - the owning plugin
	 * @param data - the data bound for the event
	 * 
	 * @return The BaseEvent object that was constructed, if one exists.
	 */
	public Optional<BaseEvent> constructEvent(Plugin plugin, EventData data){
		
		BaseEvent event = null;
		switch(data.getEventType()){
			// TODO add more CORE events here
			default:
				break;
		}
		
		return Optional.ofNullable(event);
	}
	
//	/**
//	 * Get all the events this manager knows about.
//	 * 
//	 * @return An unmodifiable collection of all the events the manager knows
//	 *         about.
//	 * @deprecated - this is for multiple event handling
//	 */
//	@Deprecated
//	public Collection<Event> getEvents() {
//		return Collections.unmodifiableCollection(idToEvent.values());
//	}
}

