package net.grandtheftmc.core.voting;

import java.util.Optional;

public enum VoteSite {
    ONE(1, "http://minecraft-mp.com/server/14659/vote/", "minecraft-mp.com"),
    TWO(2, "http://minecraftservers.org/vote/102886", "minecraftservers.org"),
    THREE(3, "http://minecraft-server-list.com/server/210232/vote/", "mcsl"),
    FOUR(4, "http://topg.org/Minecraft/in-365133", "topg.org"),
    FIVE(5, "https://topminecraftservers.org/vote/1822", "TopMinecraftServers");

	/** The id of the vote site */
	private final int id;
	/** The url for the vote site */
    private final String url;
    /** The name for the vote site */
    private final String name;

    VoteSite(int id, String url, String name) {
    	this.id = id;
        this.url = url;
        this.name = name;
    }

    public static Optional<VoteSite> find(String search) {
        for (VoteSite voteSite : VoteSite.values()) {
            if (voteSite.name.equalsIgnoreCase(search)
                    || voteSite.url.equalsIgnoreCase(search)) {
                return Optional.of(voteSite);
            }
        }
        return Optional.empty();
    }
    
    public int getId(){
    	return this.id;
    }

    public String getURL() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public int getImportance() {
        return this.ordinal();
    }
}