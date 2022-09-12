package net.grandtheftmc.core.servers;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Material;

import java.util.Arrays;

public enum ServerType {

    NONE("&c&lERROR", "        &c&lERROR         ", Material.REDSTONE_BLOCK, "&7Error"),
    HUB("&6&lHub", "       &7&l" + Core.getSettings().getNetworkShortName() + " &6&lHub        ", Material.REDSTONE, "&7Go back to the lobby!"),
    GTM("&7&l" + Core.getSettings().getServer_GTM_name(), " &7&l" + Core.getSettings().getServer_GTM_name() + " ", Material.MINECART, "&7GTA in Minecraft!"),
    LEGACY("&7&l" + Core.getSettings().getServer_GTM_name() + " &c&lLegacy", "&7&l" + Core.getSettings().getServer_GTM_shortName() + " &c&lLegacy", Material.CHEST, "&7To the good old days!"),
    CREATIVE("&6&lCreative", "        &6&lCreative      ", Material.GOLD_BLOCK, "&7Infinite plots to build anything you want!"),
    GLIDERS("&c&lGlider Assault", "  &c&lGlider Assault  ", Material.ELYTRA, "&7Free for All with weapons and gliders!"),
    VICE("&d&lVice&7&lMC", "&d&lVice&7&lMC", Material.SUGAR, "&7Drug Cartels in Minecraft!");

    private final String displayName;
    private final String scoreboardHeader;
    private final Material icon;
    private final String description;

    ServerType(String displayName, String scoreboardHeader, Material icon, String description) {
        this.scoreboardHeader = scoreboardHeader;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public static ServerType getType(String string) {
        return Arrays.stream(getAll()).filter(type -> type.toString().equalsIgnoreCase(string)).findFirst().orElse(NONE);
    }

    private static ServerType[] getAll() {
        return ServerType.class.getEnumConstants();
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public String getScoreboardHeader() {
        return this.scoreboardHeader;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }
}

