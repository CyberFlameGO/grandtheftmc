package net.grandtheftmc.core.users.eventtag;

import org.bukkit.ChatColor;

/**
 * Created by Timothy Lampen on 2017-12-16.
 */
public enum EventTag {//the names must have same characters in the same arrangement of the enum type.
    FESTIVE("&b&lFestive", false),
    XMAS("&2&lXMAS", false),
    SNOWMAN("&r&lSnowman", false),
    SANTA("&c&lSanta", false),
    HOHOHO("&c&lH&2&lo&c&lH&2&lo&c&lH&2&lo", false),
    NICE("&a&lNice", false),
    NAUGHTY("&c&lNaughty", false),
    HEART("&d&l❤❤❤", false),
    SEXY("&d&lSexy", false),
    STEAMY("&d&lSteamy", false),
    HOT("&d&lHot", false),
    DADDY("&5&lDaddy", false),
    EGG("&c&lE&6&lG&e&lG", false);

    private final String disp;
    private final boolean global;
    EventTag(String disp, boolean global) {
        this.global = global;
        this.disp = ChatColor.translateAlternateColorCodes('&', disp);
    }

    public String getBoldName() {
        return this.disp;
    }

    public boolean isGlobal() {
        return global;
    }

    public static EventTag getEventTagFromName(String name){
        name = ChatColor.stripColor(name);

        if(name.contains("❤❤❤"))//hard coded because it uses UTF-8 characters.
            return EventTag.HEART;

        name = name.toUpperCase();
        return EventTag.valueOf(name);
    }
}
