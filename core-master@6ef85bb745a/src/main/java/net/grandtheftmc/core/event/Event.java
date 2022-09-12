package net.grandtheftmc.core.event;

import net.grandtheftmc.core.util.Identifiable;
import net.grandtheftmc.core.util.PluginAssociated;

/**
 * Interface representation of an event, something like Halloween, or Easter.
 * 
 * @author sbahr
 */
public interface Event extends Identifiable<String>, PluginAssociated {

	/**
	 * Initialize the event.
	 */
	void init();

	/**
	 * Optional call-back for when the event is initialized.
	 */
	void onInit();

	/**
	 * Start the event.
	 */
	void start();

	/**
	 * Optional call-back for when the event starts.
	 */
	void onStart();

	/**
	 * End the event.
	 */
	void end();

	/**
	 * Optional call-back for when the event ends.
	 */
	void onEnd();

	/**
	 * Get the state of this event.
	 * 
	 * @return The state of this event.
	 */
	EventState getState();

	/**
	 * Set the state of this event.
	 * 
	 * @param eventState - the new state
	 */
	void setState(EventState eventState);

}
