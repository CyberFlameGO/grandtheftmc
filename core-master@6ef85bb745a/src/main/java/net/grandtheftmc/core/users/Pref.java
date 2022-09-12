package net.grandtheftmc.core.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.servers.ServerType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum Pref {

    PLAYERS_SHOWN("playersShown", "Show Players", Material.EYE_OF_ENDER),
    TP_REQUESTS("tpRequests", "Teleport Requests", Material.ENDER_PEARL),
    ANNOUNCEMENTS("announcements", "Announcements", Material.EMPTY_MAP),
    MESSAGES("messages", "Private Messages", Material.BOOK),
    DEATH_MESSAGES("deathMessages", "Death Messages", Material.SKULL_ITEM),
    USE_SCOREBOARD("useScoreboard", "Show Scoreboard", Material.DIAMOND),
    SOCIALSPY("socialSpy", "SocialSpy", Material.NETHER_STAR),
    TINT_HEALTH("tintHealth", "Tint Health", Material.INK_SACK, (byte) 1),
    KEEP_COSMETICS("keepCosmetics", "Keep Cosmetics", Material.ENDER_CHEST),
    SHOW_PARTICLES("showParticles", "Show Bullet Particles", Material.IRON_HOE),
    AUTO_CLAIM_VOTE_REWARD("autoVoteClaim", "Auto Claim Vote Rewards", Material.FLINT_AND_STEEL, (byte) 45);
    private final String dbName;
    private final String displayName;
    private final Material material;
    private byte materialData = 0;

    Pref(String dbName, String displayName, Material material) {
        this.dbName = dbName;
        this.displayName = displayName;
        this.material = material;
    }

    Pref(String dbName, String displayName, Material material, Byte materialData) {
        this.dbName = dbName;
        this.displayName = displayName;
        this.material = material;
        this.materialData = materialData;
    }

    public static Pref getPref(String dbName) {
        for (Pref pref : Pref.values())
            if (pref.dbName.equalsIgnoreCase(dbName))
                return pref;
        return null;
    }

    public static Pref getPrefByDisplayName(String displayName) {
        for (Pref pref : Pref.values())
            if (pref.displayName.equalsIgnoreCase(displayName))
                return pref;
        return null;
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Byte getMaterialData() {
        return this.materialData;
    }

    public boolean isEnabled(Player player, User u, ServerType type) {
        switch (this) {
            case PLAYERS_SHOWN:
                return u.canTogglePlayers() && type == ServerType.HUB;
            case TP_REQUESTS:
                return type != ServerType.GLIDERS && type != ServerType.HUB;
            case ANNOUNCEMENTS:
                return !Core.getAnnouncer().getAnnouncements().isEmpty();
            case MESSAGES:
                return true;
            case DEATH_MESSAGES:
                return type == ServerType.GLIDERS || type == ServerType.GTM;
            case USE_SCOREBOARD:
                return type != ServerType.CREATIVE;
            case SOCIALSPY:
                return player.hasPermission("socialspy") || u.isRank(UserRank.SRMOD);
            case TINT_HEALTH:
                return type == ServerType.GTM;
            case KEEP_COSMETICS:
                return type != ServerType.GLIDERS;
            case SHOW_PARTICLES:
                return type == ServerType.GTM;
            case AUTO_CLAIM_VOTE_REWARD:
                return type == ServerType.GTM;
            default:
                return false;
        }
    }
}
