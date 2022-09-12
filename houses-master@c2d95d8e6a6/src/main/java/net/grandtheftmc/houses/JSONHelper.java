package net.grandtheftmc.houses;

import org.json.JSONObject;

public class JSONHelper {

    private final JSONObject object;

    public JSONHelper() {
        this.object = new JSONObject();
    }

    public JSONHelper put(String k, int v) {
        object.put(k, v);
        return this;
    }

    public JSONHelper put(String k, String v) {
        object.put(k, v);
        return this;
    }

    public JSONHelper put(String k, boolean v) {
        object.put(k, v);
        return this;
    }

    public String string() {
        return object.toString();
    }
}
