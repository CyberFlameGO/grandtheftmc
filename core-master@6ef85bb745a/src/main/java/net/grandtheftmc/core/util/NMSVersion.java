package net.grandtheftmc.core.util;

import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import us.myles.ViaVersion.api.Via;

/**
 * Created by Luke Bingham on 02/08/2017.
 */
public enum NMSVersion {
    MC_1_8(47),

    MC_1_9(107),
    MC_1_9_1(108),
    MC_1_9_2(109),
    MC_1_9_4(110),

    MC_1_10(210),

    MC_1_11(315),
    MC_1_11_2(316),

    MC_1_12(335),
    MC_1_12_1(338),
    MC_1_12_2(340),
    
    MC_1_13(393),
    MC_1_13_1(401),

    UNKNOWN(0);
    ;

    private int protocol;

    private NMSVersion(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocol() {
        return protocol;
    }

    public static NMSVersion getVersion(int id) {
        for(NMSVersion version : values())
            if(version.protocol == id) return version;
        return UNKNOWN;
    }

    public static NMSVersion getVersion(Player player) {
        int id = Via.getAPI().getPlayerVersion(player.getUniqueId());
        
        Core.log("[NMSVersion][Resolve] Player " + player.getName() + " has version id=" + id);
        
        for(NMSVersion version : values())
            if(version.protocol == id) return version;
        return UNKNOWN;
    }
}
