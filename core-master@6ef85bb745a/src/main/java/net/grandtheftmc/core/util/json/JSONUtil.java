package net.grandtheftmc.core.util.json;

import org.json.simple.JSONObject;

public class JSONUtil {

	/**
	 * Merges 2 JSONObjects together recursively, resulting in the target
	 * containing both
	 * <p>
	 * The source will take priority over the target and will overwrite data if
	 * conflict arises
	 *
	 * @param source The source (the object to merge in to target)
	 * @param target The target (this ends up as the resultant object)
	 */
	public static void deepMerge(JSONObject source, JSONObject target) {
		for (Object key : source.keySet()) {

			Object value = source.get(key);

			if (!target.containsKey(key)) {
				// Add source to target
				target.put(key, value);
			}
			else if (value instanceof JSONObject) {
				// Value is JSONObject, let's have a look at target value
				Object targetValue = target.get(key);
				if (targetValue instanceof JSONObject) {
					// Target has a JSONObject, combine recursively
					deepMerge((JSONObject) value, (JSONObject) targetValue);
				}
				else {
					// Target and source incompatible, source takes priority
					target.put(key, value);
				}
			}
			else {
				// Source replaces target
				target.put(key, value);
			}
		}
	}

}
