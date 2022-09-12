package net.grandtheftmc.core.util.title;

import org.bukkit.entity.Player;

/**
 * Created by Luke Bingham on 07/08/2017.
 */
public class NMSTitle {

    @Deprecated //NMS code removed.
    public static void sendTitle (Player player, String title, String subtitle, int fadein, int duration, int fadeout) {
        player.sendTitle(title, subtitle, fadein, duration, fadeout);
    }
}
