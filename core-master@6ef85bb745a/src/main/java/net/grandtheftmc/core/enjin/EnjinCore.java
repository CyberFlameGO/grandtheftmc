package net.grandtheftmc.core.enjin;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.enjin.data.EnjinResponse;
import net.grandtheftmc.core.enjin.data.EnjinResult;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Created by Adam on 10/06/2017.
 */
public class EnjinCore {

    public static void init(){
        EnjinCache.cacheAll();
    }

    /**
     * Attempt to a tag a user on Enjin, EnjinResponse class allows for a callback as this function is asynchronous.
     * @param username The username of the user.
     * @param tag The tag we wish to associate with them.
     * @param resp The response callback function.
     */
    public static void tagUser(String username, String tag, final EnjinResponse resp) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            //Attempt to associate username with a userID

            Optional<Integer> userID = EnjinCache.getUserID(username);
            Optional<Integer> tagID = EnjinCache.getTagID(tag);

            if (!userID.isPresent() || !tagID.isPresent()) {
                //try to update the cache
                EnjinCache.cacheAll();
                //Try to obtain the user ID and tagID again
                userID = EnjinCache.getUserID(username);
                tagID = EnjinCache.getTagID(username);

                if (!userID.isPresent()) {
                    Core.getInstance().getLogger().warning("Failed to set tag (" + tag + ") for user" +
                            " (" + username + ") as the USER ID could not be found in the Enjin database.");
                    resp.callback(EnjinResult.FAIL_USERID, username, tag);
                    return;
                }

                if (!tagID.isPresent()) {
                    Core.getInstance().getLogger().warning("Failed to set tag (" + tag + ") for user" +
                            " (" + username + ") as the TAG ID could not be found in the Enjin database.");
                    resp.callback(EnjinResult.FAIL_TAGID, username, tag);
                    return;
                }
            }

            //Build our JSON object
            JSONObject obj = new JSONObject();
            obj.put("jsonrpc", "2.0");
            obj.put("method", "Tags.tagUser");

            JSONObject sub = new JSONObject();
            sub.put("api_key", "4e2846cb945359f54777f27348c2ea9720a78ece7e05e225");
            sub.put("user_id", userID.get());
            sub.put("tag_id", tagID.get());

            obj.put("params", sub);
            JSONObject jsonOut = HTTPInterface.post("http://www.grandtheftmc.net/api/v1/api.php", obj.toString());

            //if the object is null we know something went wrong, otherwise it should be an empty JSON
            resp.callback(jsonOut == null ? EnjinResult.FAIL_OTHER : EnjinResult.SUCCESS, username, tag);

        });
    }
}
