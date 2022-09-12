package net.grandtheftmc.gtm.users;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GTMUserManager {

	/** Singleton of this class */
	private static GTMUserManager instance;
	/** Mapping of uuid to gtm user objects */
	private Map<UUID, GTMUser> users = new ConcurrentHashMap<>();

	/**
	 * Private constructor since singletons cannot be initialized
	 */
	private GTMUserManager() {
	}

	/**
	 * Gets the instance of the singleton.
	 * 
	 * @return The singleton of this class.
	 */
	public static GTMUserManager getInstance() {
		if (instance == null) {
			instance = new GTMUserManager();
		}

		return instance;
	}

	/**
	 * Add a new gtm user to the container.
	 * 
	 * @param user - the gtm user to add
	 * 
	 * @return {@code true} if the gtm user was successfully added,
	 *         {@code false} if the user already exists.
	 */
	public boolean addUser(GTMUser user) {
		if (!users.containsKey(user.getUUID())) {
			users.put(user.getUUID(), user);
			return true;
		}

		return false;
	}

	/**
	 * Get whether or not the container has a gtm user with the specified uuid.
	 * 
	 * @param uuid - the uuid to lookup
	 * 
	 * @return {@code true} if the gtm user exists already in the container,
	 *         {@code false} otherwise.
	 */
	public boolean hasUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return true;
		}

		return false;
	}

	/**
	 * Get whether or not the container has a gtm user with the specified name.
	 * 
	 * @param name - the name to lookup
	 * 
	 * @return {@code true} if the gtm user exists already in the container,
	 *         {@code false} otherwise.
	 */
	public boolean hasUser(String name) {

		for (GTMUser user : users.values()) {
			if (user.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the gtm user with the specified uuid from the container.
	 * 
	 * @param uuid - the uuid to lookup
	 * 
	 * @return The gtm user with the specified uuid, if one exists, otherwise
	 *         empty.
	 */
	public Optional<GTMUser> getUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return Optional.of(users.get(uuid));
		}

		return Optional.empty();
	}

	/**
	 * Get the gtm user with the specified name from the container.
	 * 
	 * @param name - the name to lookup
	 * 
	 * @return The gtm user with the specified name, if one exists, otherwise
	 *         empty.
	 */
	public Optional<GTMUser> getUser(String name) {
		for (GTMUser user : users.values()) {
			if (user.getName().equalsIgnoreCase(name)) {
				return Optional.of(user);
			}
		}

		return Optional.empty();
	}

	/**
	 * Removes the gtm user with the specified uuid.
	 * 
	 * @param uuid - the uuid of the gtm user to remove
	 * 
	 * @return The gtm user that was removed, if one was found, otherwise empty.
	 */
	public Optional<GTMUser> removeUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return Optional.of(users.remove(uuid));
		}

		return Optional.empty();
	}

	/**
	 * Removes the gtm user with the specified name.
	 * 
	 * @param name - the name of the gtm user to remove
	 * 
	 * @return The gtm user that was removed, if one was found, otherwise empty.
	 */
	public Optional<GTMUser> removeUser(String name) {

		// lookup user
		GTMUser user = getUser(name).orElse(null);

		if (user != null) {
			if (users.containsKey(user.getUUID())) {
				return Optional.of(users.remove(user.getUUID()));
			}
		}

		return Optional.empty();
	}

	/**
	 * Get all gtm users in this container.
	 * 
	 * @return {@link Collection} of {@link GTMUser}s inside this container.
	 */
	public Collection<GTMUser> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	/**
	 * A wrapper around {@link Map#size()}.
	 * 
	 * @return The size of the collection that contains the gtm users.
	 */
	public int size() {
		return users.size();
	}

	/**
	 * Get the gtm user from this manager.
	 * 
	 * @param uuid - the uuid of the user to lookup
	 * 
	 * @return The gtm user from this manager, if one is found.
	 * 
	 * @deprecated - Please use {@link #getUser(UUID)}} instead as the result
	 *             can be {@code null}
	 */
	@Deprecated
	public GTMUser getLoadedUser(UUID uuid) {
		return getUser(uuid).orElse(null);
	}
}