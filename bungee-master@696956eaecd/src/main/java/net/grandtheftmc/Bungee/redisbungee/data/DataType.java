package net.grandtheftmc.Bungee.redisbungee.data;

public enum DataType {

    GMSG("GMSG"), STAFFCHAT("STAFFCHAT"), HELP("HELP"), HELP_CLOSE("HELP_C"), SOCIALSPY("SOCIALSPY"), MOTD("MOTD"),
    PERMS("PERMS"), STAFF_JOIN("STAFFJOIN"), LOG("LOG");

    private final String identifier;

    DataType(String identifier) {
        this.identifier = identifier;
    }
}
