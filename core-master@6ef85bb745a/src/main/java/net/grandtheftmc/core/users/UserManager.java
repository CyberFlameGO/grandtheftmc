package net.grandtheftmc.core.users;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.grandtheftmc.core.Core;

public class UserManager {

	/** Singleton of this class */
	private static UserManager instance;
	/** Mapping of uuid to user objects */
	private final Map<UUID, User> users = new ConcurrentHashMap<>();

	/**
	 * Private constructor since singletons cannot be initialized
	 */
	private UserManager() {
	}

	/**
	 * Gets the instance of the singleton.
	 * 
	 * @return The singleton of this class.
	 */
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}

		return instance;
	}

	/**
	 * Add a new user to the container.
	 * 
	 * @param user - the user to add
	 * 
	 * @return {@code true} if the user was successfully added, {@code false} if
	 *         the user already exists.
	 */
	public boolean addUser(User user) {
		if (!users.containsKey(user.getUUID())) {
			// TODO debug remove
			Core.log("[UserManager] Adding new user to UserManager, uuid=" + user.getUUID() + ", name=" + user.getName());
			users.put(user.getUUID(), user);
			return true;
		}

		return false;
	}

	/**
	 * Get whether or not the container has a user with the specified uuid.
	 * 
	 * @param uuid - the uuid to lookup
	 * 
	 * @return {@code true} if the user exists already in the container,
	 *         {@code false} otherwise.
	 */
	public boolean hasUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return true;
		}

		return false;
	}

	/**
	 * Get whether or not the container has a user with the specified name.
	 * 
	 * @param name - the name to lookup
	 * 
	 * @return {@code true} if the user exists already in the container,
	 *         {@code false} otherwise.
	 */
	public boolean hasUser(String name) {

		for (User user : users.values()) {
			if (user.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the user with the specified uuid from the container.
	 * 
	 * @param uuid - the uuid to lookup
	 * 
	 * @return The user with the specified uuid, if one exists, otherwise empty.
	 */
	public Optional<User> getUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return Optional.of(users.get(uuid));
		}

		return Optional.empty();
	}

	/**
	 * Get the user with the specified name from the container.
	 * 
	 * @param name - the name to lookup
	 * 
	 * @return The user with the specified name, if one exists, otherwise empty.
	 */
	public Optional<User> getUser(String name) {
		for (User user : users.values()) {
			if (user.getName().equalsIgnoreCase(name)) {
				return Optional.of(user);
			}
		}

		return Optional.empty();
	}

	/**
	 * Removes the user with the specified uuid.
	 * 
	 * @param uuid - the uuid of the user to remove
	 * 
	 * @return The user that was removed, if one was found, otherwise empty.
	 */
	public Optional<User> removeUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			// TODO debug remove
			Core.log("[UserManager] Removing user from UserManager, uuid=" + uuid.toString());
			return Optional.of(users.remove(uuid));
		}

		return Optional.empty();
	}

	/**
	 * Removes the user with the specified name.
	 * 
	 * @param name - the name of the user to remove
	 * 
	 * @return The user that was removed, if one was found, otherwise empty.
	 */
	public Optional<User> removeUser(String name) {

		// lookup user
		User user = getUser(name).orElse(null);

		if (user != null) {
			if (users.containsKey(user.getUUID())) {
				return Optional.of(users.remove(user.getUUID()));
			}
		}

		return Optional.empty();
	}

	/**
	 * Get all users in this container.
	 * 
	 * @return {@link Collection} of {@link User}s inside this container.
	 */
	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	/**
	 * A wrapper around {@link Map#size()}.
	 * 
	 * @return The size of the collection that contains the users.
	 */
	public int size() {
		return users.size();
	}

	/**
	 * Get the user from this manager.
	 * 
	 * @param uuid - the uuid of the user to lookup
	 * 
	 * @return The user from this manager, if one is found.
	 * 
	 * @deprecated - Please use {@link #getUser(UUID)}} instead as the result
	 *             can be {@code null}
	 */
	@Deprecated
	public User getLoadedUser(UUID uuid) {
		return getUser(uuid).orElse(null);
	}
}