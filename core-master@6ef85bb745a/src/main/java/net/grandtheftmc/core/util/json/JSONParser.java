package net.grandtheftmc.core.util.json;

import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONParser {

	/** The json object to parse */
	private final JSONObject jsonObject;

	/**
	 * Construct a new JSONParser, which is a utility tool to edit/grab keys.
	 * 
	 * @param jsonObject - the json object to parse.
	 */
	public JSONParser(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	/**
	 * Returns a new JSONParser, given the key specified can be converted to a
	 * JSONObject.
	 * 
	 * @param key - the key attempting to parse
	 * 
	 * @return A new JSONParser with the specified key as the new JSONObject in
	 *         the parser.
	 */
	public JSONParser parseObject(String key) {
		Object obj = jsonObject.get(key);
		return new JSONParser(obj instanceof JSONObject ? (JSONObject) obj : new JSONObject());
	}

	/**
	 * Get whether or not this parser has the specified key.
	 * 
	 * @param key - the key specified
	 * 
	 * @return {@code true} if the parser has the specified key, {@code false}
	 *         otherwise.
	 */
	public boolean hasKey(String key) {
		return jsonObject.containsKey(key);
	}

	/**
	 * Grab the key and convert it to a JSONObject.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The JSONObject that was retrieved.
	 */
	public JSONObject getObject(String key) {
		return (JSONObject) jsonObject.get(key);
	}

	/**
	 * Grab the key and convert it to a JSONArray.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The JSONArray that was retrieved.
	 */
	public JSONArray getJSONArray(String key) {
		return (JSONArray) jsonObject.get(key);
	}

	/**
	 * Grab the key and convert it to a String.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The String that was retrieved.
	 */
	public String getString(String key) {
		return (String) jsonObject.get(key);
	}

	/**
	 * Grab the key and convert it to an Int.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Int that was retrieved.
	 */
	public int getInt(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Long) {
			return ((Long) obj).intValue();
		}
		else if (obj instanceof Double) {
			return ((Double) obj).intValue();
		}
		else if (obj instanceof Float) {
			return ((Float) obj).intValue();
		}
		else if (obj instanceof Short) {
			return ((Short) obj).intValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).intValue();
		}

		return (Integer) obj;
	}

	/**
	 * Grab the key and convert it to a Long.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Long that was retrieved.
	 */
	public long getLong(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Integer) {
			return ((Integer) obj).longValue();
		}
		else if (obj instanceof Double) {
			return ((Double) obj).longValue();
		}
		else if (obj instanceof Float) {
			return ((Float) obj).longValue();
		}
		else if (obj instanceof Short) {
			return ((Short) obj).longValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).longValue();
		}

		return (Long) obj;
	}

	/**
	 * Grab the key and convert it to a Double.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Double that was retrieved.
	 */
	public double getDouble(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Long) {
			return ((Long) obj).doubleValue();
		}
		else if (obj instanceof Integer) {
			return ((Integer) obj).doubleValue();
		}
		else if (obj instanceof Float) {
			return ((Float) obj).doubleValue();
		}
		else if (obj instanceof Short) {
			return ((Short) obj).doubleValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).doubleValue();
		}

		return (Double) obj;
	}

	/**
	 * Grab the key and convert it to a Float.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Float that was retrieved.
	 */
	public float getFloat(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Long) {
			return ((Long) obj).floatValue();
		}
		else if (obj instanceof Double) {
			return ((Double) obj).floatValue();
		}
		else if (obj instanceof Integer) {
			return ((Integer) obj).floatValue();
		}
		else if (obj instanceof Short) {
			return ((Short) obj).floatValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).floatValue();
		}

		return (Float) obj;
	}

	/**
	 * Grab the key and convert it to a Short.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Short that was retrieved.
	 */
	public short getShort(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Long) {
			return ((Long) obj).shortValue();
		}
		else if (obj instanceof Double) {
			return ((Double) obj).shortValue();
		}
		else if (obj instanceof Float) {
			return ((Float) obj).shortValue();
		}
		else if (obj instanceof Integer) {
			return ((Integer) obj).shortValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).shortValue();
		}

		return (Short) obj;
	}

	/**
	 * Grab the key and convert it to a Byte.
	 * <p>
	 * Note: If the value in the JSON is of a different type, values can be lost
	 * when casting.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Byte that was retrieved.
	 */
	public byte getByte(String key) {
		Object obj = jsonObject.get(key);

		if (obj instanceof Long) {
			return ((Long) obj).byteValue();
		}
		else if (obj instanceof Double) {
			return ((Double) obj).byteValue();
		}
		else if (obj instanceof Float) {
			return ((Float) obj).byteValue();
		}
		else if (obj instanceof Integer) {
			return ((Integer) obj).byteValue();
		}
		else if (obj instanceof Byte) {
			return ((Byte) obj).byteValue();
		}

		return (Byte) obj;
	}

	/**
	 * Grab the key and convert it to a boolean.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The boolean that was retrieved.
	 */
	public boolean getBoolean(String key) {
		return (Boolean) jsonObject.get(key);
	}

	/**
	 * Grab the key and convert it to a UUID.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The UUID that was retrieved, if one exists. {@code null} if the
	 *         value cannot be formatted to a UUID.
	 */
	public UUID getUUID(String key) {
		try {
			return UUID.fromString((String) jsonObject.get(key));
		}
		catch (IllegalArgumentException ignored) {
		}

		return null;
	}

	/**
	 * Grab the key and convert it to an Object.
	 * 
	 * @param key - the key to grab
	 * 
	 * @return The Object that was retrieved.
	 */
	public Object get(String key) {
		return jsonObject.get(key);
	}

}
