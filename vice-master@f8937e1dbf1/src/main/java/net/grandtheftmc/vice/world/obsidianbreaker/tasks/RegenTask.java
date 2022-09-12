package net.grandtheftmc.vice.world.obsidianbreaker.tasks;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.world.obsidianbreaker.BlockStatus;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Timothy Lampen on 7/3/2017.
 */
public class RegenTask extends BukkitRunnable{
    @Override
    public void run() {
        try {
            for(ConcurrentHashMap<String, BlockStatus> map : Vice.getWorldManager().getObsidianManager().getDamageStorage().getBlocks().values()) {
                for(BlockStatus status : map.values()) {
                    if(status.isModified())
                        status.setModified(false);
                    else {
                        status.setDamage(status.getDamage() - 2);
                        Block b = Vice.getWorldManager().getObsidianManager().getDamageStorage().generateLocation(status.getBlockHash()).getBlock();
                        if(b==null)
                            continue;
                        Vice.getWorldManager().getObsidianManager().getDamageStorage().renderCracks(b);
                        if(status.getDamage()<=0)
                            Vice.getWorldManager().getObsidianManager().getDamageStorage().removeBlockStatus(status);
                    }
                }
            }
        } catch(Exception e) {
            Vice.error("Error occured while trying to regen block (tasks "+getTaskId()+")");
        }
    }
}
