package net.grandtheftmc.vice.areas.obj;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Area {

    private int id;
    private final int season;
    private final String name;
    private final String worldName;
    private final int xMin, xMax, zMin, zMax;
    private final Set<Chest> chests;

    public Area(int id, int season, String name, String worldName, int x1, int x2, int z1, int z2) {
        this.id = id;
        this.season = season;
        this.name = name;
        this.worldName = worldName;
        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.zMin = Math.min(z1, z2);
        this.zMax = Math.max(z1, z2);

        this.chests = Sets.newHashSet();
    }

    /**
     * Returns true if the given values are inside this area
     * @param x
     * @param z
     * @param world
     * @param isEntity
     * @return
     */
    public boolean isInside(double x, double z, String world, boolean isEntity) {
        if(this.worldName == null || !this.worldName.equals(world)) return false;

        int xMin = getMinX(); int xMax = getMaxX(); int zMin = getMinZ(); int zMax = getMaxZ();

        if(isEntity) {
            ++xMax;
            ++zMax;
        }

        return x >= xMin && x <= xMax && z >= zMin && z <= zMax;
    }

    /**
     * Returns true if the given values overlap this area
     * @param x1
     * @param x2
     * @param z1
     * @param z2
     * @param world
     * @return
     */
    public boolean isOverlapping(int x1, int x2, int z1, int z2, String world) {
        if(this.worldName == null || !this.worldName.equals(world)) return false;

        double[] values = new double[2];

        values[0] = x1;
        values[1] = x2;

        Arrays.sort(values);

        if(getMinX() > values[1] || getMaxX() < values[0]) return false;

        values[0] = z1;
        values[1] = z2;

        Arrays.sort(values);

        return !(getMinZ() > values[1]) && !(getMaxZ() < values[0]);
    }

    /**
     * Returns a list containing every block location surrounding the perimeter of an Area on the given Y level
     * @param world
     * @param y
     * @return
     */
    public List<Location> getPerimeter(UUID world, int y) {
        List<Location> result = Lists.newArrayList();

        for(int x = getMinX(); x <= getMaxX(); x++) {
            for(int z = getMinZ(); z <= getMaxZ(); z++) {

                if(x == getMinX() || x == getMaxX() || z == getMinZ() || z == getMaxZ())
                    result.add(new Location(Bukkit.getWorld(world), x, y, z));

            }
        }

        return result;
    }

    public int getID() {
        return this.id;
    }

    public int getSeason() {
        return this.season;
    }

    public String getName() {
        return this.name;
    }

    public World getWorld() {
        if (this.worldName == null) return null;
        return Bukkit.getWorld(this.worldName);
    }

    public int getMinX() {
        return this.xMin;
    }

    public int getMaxX() {
        return this.xMax;
    }

    public int getMinZ() {
        return this.zMin;
    }

    public int getMaxZ() {
        return this.zMax;
    }

    public Set<Chest> getChests() {
        return chests;
    }

    public void setID(int newId) {
        this.id = newId;
    }

    public void addChest(Chest chest) {
        this.chests.add(chest);
    }

    public AreaType getAreaType() {
//        return this.chests.size() >= 30 ? AreaType.TOWN : AreaType.VILLAGE;
        return this.getName().contains("_") ? AreaType.TOWN : AreaType.VILLAGE;
    }

    public static enum AreaType {
        TOWN, VILLAGE,
        ;
    }

    public static enum DropType {
        MAJOR, MINOR, DEFAULT, ;
    }

    public int getRealMinX() {
        return this.xMin <= this.xMax ? this.xMin : this.xMax;
    }

    public int getRealMaxX() {
        return this.xMax >= this.xMin ? this.xMax : this.xMin;
    }

    public int getRealMinZ() {
        return this.zMin <= this.zMax ? this.zMin : this.zMax;
    }

    public int getRealMaxZ() {
        return this.zMax >= this.zMin ? this.zMax : this.zMin;
    }
}
