package net.grandtheftmc.vice.world.obsidianbreaker;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.world.obsidianbreaker.nms.NMS;
import net.grandtheftmc.vice.world.obsidianbreaker.nms.Reflection;
import net.grandtheftmc.vice.world.obsidianbreaker.tasks.RegenTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy Lampen on 7/2/2017.
 */
//note that this code has been borrowed from https://raw.githubusercontent.com/oggehej/ObsidianBreaker
public class ObsidianManager {
    private DamageStorage damageStorage;
    private NMS nmsHandler;


    public ObsidianManager(){
        this.damageStorage = new DamageStorage();
        setupNMS();
        new RegenTask().runTaskTimerAsynchronously(Vice.getInstance(), 0,20*60*5);//every minute 2 damage is regenned.
    }

    /**
     * Check if we even handle the explosion for the specified block
     *
     * @param block Block to check
     * @return Whether we're handling these kind of blocks
     */
    private boolean isValidBlock(Block block) {
        return block!=null && block.getType()== Material.OBSIDIAN;
    }

    public  void updateRange(double radius, Entity explosive, int damage){
        for(double x = explosive.getLocation().getBlockX()-radius; x<explosive.getLocation().getBlockX()+radius; x++){
            for(double y = explosive.getLocation().getBlockY()-radius; y<explosive.getLocation().getBlockY()+radius; y++){
                for(double z = explosive.getLocation().getBlockZ()-radius; z<explosive.getLocation().getBlockZ()+radius; z++){
                    Block b = explosive.getWorld().getBlockAt(new Location(explosive.getWorld(), x, y, z));
                    if(!isValidBlock(b))
                        continue;
                    if(explosionThroughLiquid(explosive.getLocation(), b.getLocation()))
                        continue;
                    BlockStatus status = getDamageStorage().getBlockStatus(b, true);
                    if(status.getDamage()+damage>=status.getTotalDurability()) {
                        b.breakNaturally();
                        getDamageStorage().removeBlockStatus(status);
                        getNMSHandler().sendCrackEffect(b.getLocation(), -1);
                    }
                    else {
                        status.setDamage(status.getDamage() + damage);
                        getDamageStorage().renderCracks(b);
                    }
                }
            }
        }
    }


    private boolean explosionThroughLiquid(Location explosionSource, Location block) {
        if(explosionSource.getWorld().getBlockAt(block).getType()== Material.BEDROCK || explosionSource.getBlock().getType()==Material.WATER || explosionSource.getBlock().getType()==Material.STATIONARY_WATER)
            return true;
        List<Block> list = new ArrayList<Block>();
        int i, dx, dy, dz, l, m, n, x_inc, y_inc, z_inc, err_1, err_2, dx2, dy2, dz2;
        int pixelX, pixelY, pixelZ;

        pixelX = explosionSource.getBlockX();
        pixelY = explosionSource.getBlockY();
        pixelZ = explosionSource.getBlockZ();
        dx = block.getBlockX() - explosionSource.getBlockX();
        dy = block.getBlockY() - explosionSource.getBlockY();
        dz = block.getBlockZ() - explosionSource.getBlockZ();
        x_inc = (dx < 0) ? -1 : 1;
        l = Math.abs(dx);
        y_inc = (dy < 0) ? -1 : 1;
        m = Math.abs(dy);
        z_inc = (dz < 0) ? -1 : 1;
        n = Math.abs(dz);
        dx2 = l << 1;
        dy2 = m << 1;
        dz2 = n << 1;

        if ((l >= m) && (l >= n)) {
            err_1 = dy2 - l;
            err_2 = dz2 - l;
            for (i = 0; i < l; i++) {
                list.add(explosionSource.getWorld().getBlockAt(pixelX, pixelY, pixelZ));
                if (err_1 > 0) {
                    pixelY += y_inc;
                    err_1 -= dx2;
                }
                if (err_2 > 0) {
                    pixelZ += z_inc;
                    err_2 -= dx2;
                }
                err_1 += dy2;
                err_2 += dz2;
                pixelX += x_inc;
            }
        } else if ((m >= l) && (m >= n)) {
            err_1 = dx2 - m;
            err_2 = dz2 - m;
            for (i = 0; i < m; i++) {
                list.add(explosionSource.getWorld().getBlockAt(pixelX, pixelY, pixelZ));
                if (err_1 > 0) {
                    pixelX += x_inc;
                    err_1 -= dy2;
                }
                if (err_2 > 0) {
                    pixelZ += z_inc;
                    err_2 -= dy2;
                }
                err_1 += dx2;
                err_2 += dz2;
                pixelY += y_inc;
            }
        } else {
            err_1 = dy2 - n;
            err_2 = dx2 - n;
            for (i = 0; i < n; i++) {
                list.add(explosionSource.getWorld().getBlockAt(pixelX, pixelY, pixelZ));
                if (err_1 > 0) {
                    pixelY += y_inc;
                    err_1 -= dz2;
                }
                if (err_2 > 0) {
                    pixelX += x_inc;
                    err_2 -= dz2;
                }
                err_1 += dy2;
                err_2 += dx2;
                pixelZ += z_inc;
            }
        }
        list.add(explosionSource.getWorld().getBlockAt(pixelX, pixelY, pixelZ));

        return list.stream().filter(Block::isLiquid).findFirst().isPresent();
    }

    public DamageStorage getDamageStorage() {
        return damageStorage;
    }

    private void setupNMS() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            final Class<?> clazz = Class.forName(getClass().getPackage().getName() + ".nms." + version);
            if (NMS.class.isAssignableFrom(clazz)) {
                Vice.log("Using NMS version " + version);
                this.nmsHandler = (NMS) clazz.getConstructor().newInstance();
            }
        } catch (final Exception e) {
            Vice.log("Could not find NMS version " + version + ". Falling back to reflections. Are you sure you're using the latest version?\n"
                    + "If you are using the latest verion and an error appears later on or block cracks don't work, contact the plugin author. "
                    + "Otherwise everything should function normally.");
            this.nmsHandler = new Reflection();
        }
    }

    public NMS getNMSHandler() {
        return nmsHandler;
    }
}
