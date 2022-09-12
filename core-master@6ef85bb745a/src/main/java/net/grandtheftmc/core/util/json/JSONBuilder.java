package net.grandtheftmc.core.util.json;

import org.json.simple.JSONObject;

public class JSONBuilder {

	/** The json object being built */
	private final JSONObject jsonObject;

	/**
	 * Construct a new JSONBuilder, used to concatenate key/values into a valid
	 * JSON format.
	 */
	public JSONBuilder() {
		this.jsonObject = new JSONObject();
	}

	/**
	 * Construct a new JSONBuilder, using an existing object.
	 * 
	 * @param existing - the existing json object
	 */
	public JSONBuilder(JSONObject existing) {
		this.jsonObject = existing;
	}

	/**
	 * Set the key in the JSON to the specified value.
	 * 
	 * @param key - the key to set
	 * @param value - the value to set
	 * 
	 * @return This builder, in order to keep chaining.
	 */
	public JSONBuilder set(String key, Object value) {
		jsonObject.put(key, value);

		return this;
	}

	/**
	 * Creates the JSONObject from this builder.
	 * 
	 * @return The JSONObject that was created from this builder.
	 */
	public JSONObject create() {
		return jsonObject;
	}
}

