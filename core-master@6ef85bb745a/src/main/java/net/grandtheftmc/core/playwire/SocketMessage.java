package net.grandtheftmc.core.playwire;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-12-06.
 */
public class SocketMessage extends JSONObject {

    public SocketMessage(String message) {
        super(message);
    }

    public String getState() {
        return getMessage().getJSONObject("data").getJSONObject("attributes").getString("aasm-state");
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(getJSONObjectBy(getMessage().getJSONArray("included"), "type", "players").getJSONObject("attributes").getString("uuid"));
    }

    public boolean hasType() {
        return has("type");
    }

    public String getType() {
        return getString("type");
    }

    private JSONObject getMessage()
    {
        return getJSONObject("message");
    }

    private JSONObject getJSONObjectBy(JSONArray jsonArray, String key, String value) {
        for(int i = 0; i<jsonArray.length(); i++){
            JSONObject jsonObject = (JSONObject)jsonArray.get(i);

            if (jsonObject.getString(key).equals(value)) {
                return jsonObject;
            }
        }
        return null;
    }
}
