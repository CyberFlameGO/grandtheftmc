package net.grandtheftmc.gtm.users;

import org.bukkit.ChatColor;

/**
 * Created by Timothy Lampen on 2017-12-16.
 * 
 * @deprecated - unused due to net.grandtheftmc.core.users.eventtag.EventTag.
 */
public enum GTMTag {
    FESTIVE("&b&lFestive"),
    XMAS("&2&lXMAS"),
    SNOWMAN("&r&lSnowman"),
    SANTA("&c&lSanta"),
    HOHOHO("&c&lH&2&lo&c&lH&2&lo&c&lH&2&lo");

    private final String disp;
    GTMTag(String disp) {
        this.disp = ChatColor.translateAlternateColorCodes('&', disp);
    }
}
