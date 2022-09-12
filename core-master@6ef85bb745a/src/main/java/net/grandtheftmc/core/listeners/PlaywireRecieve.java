package net.grandtheftmc.core.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.playwire.events.AsyncPlaywireRecieveEvent;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-12-06.
 */
public class PlaywireRecieve implements Listener {

    /**
     * Disabled, done through command instead.
     */
    //@EventHandler
    public void onRecieve(AsyncPlaywireRecieveEvent event){
        UUID uuid = event.getUUID();
        if(Bukkit.getPlayer(uuid)==null)
            return;//since this event would trigger ALL cores on ALL servers, a listener should only give rewards to people on the one server.
        Player player = Bukkit.getPlayer(uuid);
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());

        switch (event.getType()) {
            case COUNTED: {//when the player has watched the video
                user.setCouponCredits(user.getCouponCredits()+2);
                player.sendMessage(Lang.REWARDS.f("&7Thanks for watching the ad! Here are &a2 &7coupon credits!"));
                break;
            }
            case UNCOUNTED: {//when the player has watched too many videos in a certain time limit (not set by us, set by playwire)
                break;
            }
            case BLOCKED: {//when the player blocks an ad with ad block
                break;
            }
            case UNFILLED: {//when there is no ad to show the player
                break;
            }
            case PING: {//when the playwire server is pinged
                break;
            }
            case CONFIRM_SUBSCRIPTION: {//when the playwire channel is subscribed to (onEnable)
                break;
            }
            case WELCOME: {//when the player opens the webpage (a guess)
                break;
            }
        }
    }
}
