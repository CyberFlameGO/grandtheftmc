package net.grandtheftmc.vice.areas;

import com.google.common.collect.Sets;
import com.massivecraft.factions.integration.Worldguard;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.builder.AreaBuilder;
import net.grandtheftmc.vice.areas.dao.AreaDAO;
import net.grandtheftmc.vice.areas.listeners.AreaBuilderListener;
import net.grandtheftmc.vice.areas.listeners.AreaListener;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.areas.obj.AreaUser;
import net.grandtheftmc.vice.areas.tasks.AreaUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AreaManager implements Component<AreaManager, Vice>{

    private final Vice plugin;
    private BukkitTask updateTask;

    private Set<AreaUser> areaUsers;
    private Set<Area> areas;
    private Set<AreaBuilder> builders;

    private final AreaListener areaListener;
    private final AreaBuilderListener areaBuilderListener;

    public AreaManager(Vice plugin) {
        this.plugin = plugin;

        this.areaUsers = Sets.newConcurrentHashSet();
        this.areas = Sets.newHashSet();
        this.builders = Sets.newConcurrentHashSet();

        this.areaListener = new AreaListener(plugin, this);
        this.areaBuilderListener = new AreaBuilderListener(plugin, this);

        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, new AreaUpdater(this), 20L, 20L);

        Vice.getCrateManager().getCrates().clear();
        Vice.getCrateManager().saveCrates();

        WorldGuardPlugin worldguard = WorldGuardPlugin.inst();
        ServerUtil.runTaskAsync(() -> {
            Set<Area> set = AreaDAO.loadBySeason(plugin.getSeasonManager().getCurrentSeason().getNumber());
            for (Area a : set) {
                RegionManager manager = worldguard.getRegionManager(a.getWorld());

                ProtectedRegion region = null;
                boolean exists = manager.hasRegion(a.getName());
                if (!exists) region = new ProtectedCuboidRegion(a.getName(), new BlockVector(a.getMaxX(), a.getWorld().getMaxHeight(), a.getMaxZ()), new BlockVector(a.getMinX(), 1, a.getMinZ()));
                else region = manager.getRegion(a.getName());

                if (region != null) {
                    region.setFlag(new BooleanFlag("build"), true);
                    region.setFlag(new BooleanFlag("entity-item-frame-destroy"), true);
                    region.setFlag(new BooleanFlag("vehicle-place"), true);
                    region.setFlag(new BooleanFlag("creeper-explosion"), true);
                    region.setFlag(new BooleanFlag("enderdragon-block-damage"), true);
                    region.setFlag(new BooleanFlag("ghast-fireball"), true);
                    region.setFlag(new BooleanFlag("other-explosion"), true);
                    region.setFlag(new BooleanFlag("fire-spread"), true);
                    region.setFlag(new BooleanFlag("enderman-grief"), true);
                    region.setFlag(new BooleanFlag("entity-painting-destroy"), true);
                    region.setFlag(new BooleanFlag("entity-item-frame-destroy"), true);
                    region.setFlag(new BooleanFlag("leaf-decay"), true);

                    if (!exists) {
                        manager.addRegion(region);
                        System.out.println("Region created: " + a.getName());
                    }
                }

                this.areas.add(a);


                ServerUtil.runTask(() -> {
                    for (int x = a.getRealMinX(); x < a.getRealMaxX(); x++) {
                        for (int z = a.getRealMinZ(); z < a.getRealMaxZ(); z++) {
                            for (int y = 0; y < a.getWorld().getMaxHeight(); y++) {

                                Block block = a.getWorld().getBlockAt(x, y, z);
                                if (block.getType() != Material.CHEST) continue;
                                if (block.getState() == null) continue;
                                if (!(block.getState() instanceof Chest)) continue;
                                if (block.getState() instanceof DoubleChest) continue;

                                a.addChest((Chest) block.getState());
                                Vice.getCrateManager().addCrate(block.getState().getLocation());
                                System.out.println("Added chest(" + a.getChests().size() + ") for Area: " + a.getName());
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public AreaManager onDisable(Vice plugin) {
        this.updateTask.cancel();
        return this;
    }

    /**
     * Returns a new Area object based on the given info
     * @param name
     * @param c1
     * @param c2
     * @return
     */
    public Area createArea(String name, Location c1, Location c2) {
        return new Area(
                (areas.size() + 1),
                plugin.getSeasonManager().getCurrentSeason().getNumber(),
                name,
                c1.getWorld().getName(),
                c1.getBlockX(),
                c2.getBlockX(),
                c1.getBlockZ(),
                c2.getBlockZ());
    }

    /**
     * Returns a new AreaBuilder object based on the given info
     * @param name
     * @param creator
     * @return
     */
    public AreaBuilder createBuilder(String name, UUID creator) {
        return new AreaBuilder(
                name,
                creator
        );
    }

    /**
     * Returns a new Area object from the given AreaBuilder object
     * @param builder
     * @return
     */
    public Area convertBuilderToArea(AreaBuilder builder) {
        return createArea(builder.getName(), builder.getCorner1(), builder.getCorner2());
    }

    /**
     * Returns true if the given UUID is currently are the given Area
     * @param uuid
     * @param area
     * @return
     */
    public boolean isAt(UUID uuid, Area area) {
        AreaUser user = getUserByUUID(uuid);

        if(user == null || user.getCurrent() == -1) return false;

        int resultId = user.getCurrent();
        int id = area.getID();

        return resultId == id;
    }

    /**
     * Returns true if the given UUID is building an Area
     * @param uuid
     * @return
     */
    public boolean isBuilding(UUID uuid) {
        return getBuilderByUserUUID(uuid) != null;
    }

    /**
     * Returns an Area object matching the given ID
     * @param id
     * @return
     */
    public Area getAreaById(int id) {
        return areas.stream().filter(area -> area.getID() == id).findFirst().orElse(null);
    }

    /**
     * Returns an Area object matching the given name
     * @param name
     * @return
     */
    public Area getAreaByName(String name) {
        return areas.stream().filter(area -> area.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns an AreaUser object matching the given UUID
     * @param uuid
     * @return
     */
    public AreaUser getUserByUUID(UUID uuid) {
        return areaUsers.stream().filter(user -> user.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns an AreaBuilder object matching the given Area Creator UUID
     * @param uuid
     * @return
     */
    public AreaBuilder getBuilderByUserUUID(UUID uuid) {
        return builders.stream().filter(builder -> builder.getCreator().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns a fancy claiming stick in the form of an ItemStack
     * @return
     */
    public ItemStack getAreaClaimStick() {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Area Claiming Stick");
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "Step 1" + ChatColor.WHITE + ": Left-click the first corner of the claim",
                ChatColor.BLUE + "Step 2" + ChatColor.WHITE + ": Right-click the second corner of the claim",
                ChatColor.BLUE + "Step 3" + ChatColor.WHITE + ": Left-click while sneaking to confirm the claim",
                ChatColor.DARK_RED + "Optional" + ChatColor.WHITE + ": Right-click the air while sneaking to clear claim selection"));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Inefficiently sends perimeter block changes to the given player
     * @param player
     */
    public void drawNearby(Player player) {
        final Location location = player.getLocation();
        Set<Location> toRefresh = Sets.newHashSet();

        int areaColor = 1;

        for (Area area : areas) {
            if (!area.getWorld().equals(player.getWorld())) continue;

            for (int i = location.getBlockY() - 2; i < location.getBlockY() + 5; i++) {
                List<Location> perimeter = area.getPerimeter(area.getWorld().getUID(), i);

                for (Location blocks : perimeter) {
                    if (blocks.distance(location) > 40 || blocks.getBlock().getType().equals(Material.AIR)) continue;

                    player.sendBlockChange(blocks, Material.WOOL, (byte)areaColor);
                    toRefresh.add(blocks);
                }
            }

            areaColor++;
        }

        // Refreshing the blockstate 15 seconds later
        ServerUtil.runTaskLater(() -> toRefresh.forEach(refreshedBlock -> refreshedBlock.getBlock().getState().update()), 15 * 20L);
    }

    public Set<AreaUser> getAreaUsers() {
        return this.areaUsers;
    }

    public Set<Area> getAreas() {
        return this.areas;
    }

    public Set<AreaBuilder> getBuilders() {
        return this.builders;
    }

    public AreaListener getAreaListener() {
        return this.areaListener;
    }

    public AreaBuilderListener getAreaBuilderListener() {
        return this.areaBuilderListener;
    }

}
