package net.grandtheftmc.Creative.users;

import net.grandtheftmc.core.util.Utils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum CreativeRank {

    DEFAULT(),
    TRAINEE("plots.limit.100", "plots.plot.100", "worldedit.navigation.thru.command",
            "worldedit.wand",
            "worldedit.history.undo",
            "worldedit.region.set",
            "worldedit.region.walls",
            "worldedit.selection.pos",
            "voxelsniper.sniper"),
    NOVICE("plots.limit.200", "plots.plot.200", "worldedit.region.replace",
            "worldedit.history.redo",
            "worldedit.clipboard.copy",
            "worldedit.clipboard.paste",
            "worldedit.region.stack",
            "worldedit.selection.expand",
            "worldedit.clipboard.rotate",
            "worldedit.region.move",
            "worldeedit.generation.cylinder",
            "worldedit.generation.sphere",
            "worldedit.brush.smooth",
            "worldedit.fill",
            "worldedit.region.smooth"),
    ARTIST("plots.limit.300", "plots.plot.300", "worldedit.*",
            "voxelsniper.goto",
            "voxelsniper.brush.ball",
            "voxelsniper.brush.cylinder",
            "voxelsniper.brush.voxel",
            "voxelsniper.brush.stencil",
            "voxelsniper.brush.stencillist",
            "Voxelsniper.brush.ruler",
            "voxelsniper.brush.generatetree",
            "voxelsniper.sniper",
            "voxelsniper.goto",
            "voxelsniper.brush.ball",
            "voxelsniper.brush.cylinder",
            "voxelsniper.brush.voxel",
            "voxelsniper.brush.set",
            "voxelsniper.brush.blendball",
            "voxelsniper.brush.blenddisc",
            "voxelsniper.brush.blendvoxel",
            "voxelsniper.brush.blendvoxeldisc",
            "voxelsniper.brush.move",
            "voxelsniper.brush.stencil",
            "voxelsniper.brush.stencillist",
            "voxelsniper.brush.rot2d",
            "voxelsniper.brush.rot2dvert",
            "voxelsniper.brush.rot3d",
            "Voxelsniper.brush.ruler",
            "Voxelsniper.brush.generatetree",
            "voxelsniper.brush.eraser",
            "Voxelsniper.brush.erode",
            "voxelsniper.brush.disc",
            "voxelsniper.brush.discface",
            "voxelsniper.brush.biome",
            "voxelsniper.brush.splatterball",
            "voxelsniper.brush.splatterdisc",
            "voxelsniper.brush.splatteroverlay",
            "voxelsniper.brush.splattervoxel",
            "voxelsniper.brush.splattervoxeldisc"),
    CREATOR("plots.limit.1000", "plots.plot.1000", "Voxelsniper.sniper",
            "voxelsniper.goto",
            "voxelsniper.brush.blob",
            "voxelsniper.brush.blockreset",
            "voxelsniper.brush.blockresetsurface",
            "voxelsniper.brush.canyon",
            "voxelsniper.brush.canyonselection",
            "voxelsniper.brush.checkervoxeldisc",
            "voxelsniper.brush.cleansnow",
            "voxelsniper.brush.clonestamp",
            "voxelsniper.brush.copypasta",
            "voxelsniper.brush.cylinder",
            "voxelsniper.brush.dome",
            "voxelsniper.brush.drain",
            "voxelsniper.brush.ellipse",
            "voxelsniper.brush.ellipsoid",
            "voxelsniper.brush.extrude",
            "voxelsniper.brush.filldown",
            "voxelsniper.brush.flatocean",
            "voxelsniper.brush.heatray",
            "voxelsniper.brush.jaggedlinev",
            "voxelsniper.brush.line",
            "voxelsniper.brush.overlay",
            "voxelsniper.brush.pull",
            "voxelsniper.brush.randomerode",
            "voxelsniper.brush.regeneratechunk",
            "voxelsniper.brush.ring",
            "voxelsniper.brush.scanner",
            "voxelsniper.brush.set",
            "voxelsniper.brush.setredstoneflip",
            "voxelsniper.brush.setredstonerotate",
            "voxelsniper.brush.shellball",
            "voxelsniper.brush.shellset",
            "voxelsniper.brush.shellvoxel",
            "voxelsniper.brush.signoverwrite",
            "voxelsniper.brush.snipe",
            "voxelsniper.brush.snowcone",
            "voxelsniper.brush.spiralstaircase",
            "voxelsniper.brush.spline",
            "voxelsniper.brush.stamp",
            "voxelsniper.brush.threepointcircle",
            "voxelsniper.brush.treesnipe",
            "voxelsniper.brush.triangle",
            "voxelsniper.brush.underlay",
            "voxelsniper.brush.voltmeterv",
            "voxelsniper.brush.voxel",
            "voxelsniper.brush.voxeldisc",
            "voxelsniper.brush.voxeldiscface",
            "voxelsniper.brush.warp");

    private final List<String> perms;

    CreativeRank(String... perms) {
        this.perms = Arrays.asList(perms);
    }

    public List<String> getAllPerms() {
        List<String> permissions = new ArrayList<>();
        for (CreativeRank uc : getCreativeRanks()) {
            permissions.addAll(uc.perms);
            if (uc == this)
                return permissions;
        }
        return permissions;
    }

    private List<String> getPerms() {
        return this.perms;
    }

    public String getName() {
        return this.toString();
    }

    public ChatColor getColor() {
        return this == CreativeRank.DEFAULT ? ChatColor.GRAY : ChatColor.YELLOW;
    }

    public String getColoredName() {
        return Utils.f(this.getColor() + this.getName() + "&r");
    }

    public String getColoredNameBold() {
        return Utils.f(this.getColor() + "&l" + this.getName() + "&r");
    }

    public CreativeRank getNext() {
        String rankName = this.getName();
        if ("CREATOR".equalsIgnoreCase(rankName))
            return null;
        int go = 0;

        CreativeRank rank = null;
        for (CreativeRank r : getCreativeRanks())
            if (go == 0) {
                if (Objects.equals(r.getName(), rankName)) {
                    go = 1;
                }
            } else if (go == 1) {
                rank = r;
                break;
            }
        return rank;
    }

    public static CreativeRank[] getCreativeRanks() {
        return CreativeRank.class.getEnumConstants();
    }

    public static CreativeRank fromString(String string) {
        return Arrays.stream(CreativeRank.getCreativeRanks()).filter(uc -> uc.getName().equalsIgnoreCase(string)).findFirst().orElse(CreativeRank.DEFAULT);
    }


    public static CreativeRank getRankOrNull(String name) {
        if (name == null)
            return null;
        return Arrays.stream(getCreativeRanks()).filter(r -> r.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isHigherThan(CreativeRank rank) {
        if (rank == null)
            return false;
        for (CreativeRank r : getCreativeRanks())
            if (r == this)
                return false;
            else if (r == rank)
                return true;
        return false;
    }

}
