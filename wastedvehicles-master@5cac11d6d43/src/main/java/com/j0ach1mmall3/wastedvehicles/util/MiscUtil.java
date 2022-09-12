package com.j0ach1mmall3.wastedvehicles.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MiscUtil {
    private static MiscUtil miscUtil;

    private MiscUtil() {
        miscUtil = this;
    }

    public static MiscUtil getMiscUtil() {
        if(miscUtil == null) {
            miscUtil = new MiscUtil();
        }
        return miscUtil;
    }

    public String getCardinalDirection(Location location) {
        double rot = (location.getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return this.getDirection(rot);
    }

    public Set<Block> getBlocksInRadius(Location o, int radius) {
        Set<Block> blocks = new HashSet<>();
        for(int x = o.getBlockX()-radius; x<=o.getBlockX()+radius; x++)
            for(int y = o.getBlockY()-radius; y<=o.getBlockY()+radius; y++)
                for(int z = o.getBlockZ()-radius; z<=o.getBlockZ()+radius; z++)
                    blocks.add(o.getWorld().getBlockAt(x,y,z));
        return blocks;
    }

    private String getDirection(double rot) {
        if (rot <= 0 && rot < 22.5) {
            return "North";
        } else if (rot <= 22.5 && rot < 67.5) {
            return "North";
        } else if (rot <= 67.5 && rot < 112.5) {
            return "East";
        } else if (rot <= 112.5 && rot < 157.5) {
            return "South";
        } else if (rot <= 157.5 && rot < 202.5) {
            return "South";
        } else if (rot <= 202.5 && rot < 247.5) {
            return "South";
        } else if (rot <= 247.5 && rot < 292.5) {
            return "West";
        } else if (rot <= 292.5 && rot < 337.5) {
            return "North";
        } else if (rot <= 337.5 && rot < 360.0) {
            return "North";
        } else {
            return null;
        }
    }
}


