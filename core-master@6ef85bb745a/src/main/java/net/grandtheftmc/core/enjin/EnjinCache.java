package net.grandtheftmc.core.enjin;

import net.grandtheftmc.core.Core;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Adam on 10/06/2017.
 */
public class EnjinCache {

    /*
        Want to avoid calling get-users and get-tags all the time so store a cache.
     */

    //Map a users name to their enjin ID
    private static Map<String, Integer> userIDs = new HashMap<>();

    //Map a tag name to its id
    private static Map<String, Integer> tagIDs = new HashMap<>();

    /**
     * Cache the entire list of users.
     */
    protected static void cacheAll() {
        //CLear any existing
        tagIDs.clear();
        userIDs.clear();

        //Cache users
        JSONObject json = HTTPInterface.post("http://www.grandtheftmc.net/api/get-users", "");
        if(json==null) {
            Core.error("Couldn't cache enjin users as website didnt respond");
            return;
        }
        Iterator<String> it = json.keys();
        while (it.hasNext()) {
            int id = Integer.parseInt(it.next());
            String name = json.getJSONObject(Integer.toString(id)).getString("username");
            userIDs.put(name.toLowerCase(), id);
        }

        //Cache tags
        json = HTTPInterface.post("http://www.grandtheftmc.net/api/get-tags", "");

        JSONObject tags = json.getJSONObject("tags");
        it = tags.keys();
        while (it.hasNext()) {
            int tagID = Integer.parseInt(it.next());
            String tagName = tags.getJSONObject(Integer.toString(tagID)).getString("name");
            tagIDs.put(tagName.toLowerCase(), tagID);
        }
    }

    /**
     * Attempt to get the user ID of a user.
     *
     * @param username The users username
     * @return
     */
    protected static Optional<Integer> getUserID(String username) {
        Optional<Integer> ret = Optional.empty();

        if (userIDs.containsKey(username.toLowerCase())) {
            ret = Optional.of(userIDs.get(username.toLowerCase()));
        }

        return ret;
    }

    /**
     * Attempt to get the tag ID associated with a tag.
     *
     * @param tag
     * @return
     */
    protected static Optional<Integer> getTagID(String tag) {
        Optional<Integer> ret = Optional.empty();

        if (tagIDs.containsKey(tag.toLowerCase())) {
            ret = Optional.of(tagIDs.get(tag.toLowerCase()));
        }

        return ret;
    }

    /**
     * Return a set of valid tags.
     *
     * @return
     */
    public static Set<String> getTagNames() {
        return tagIDs.keySet();
    }
}
