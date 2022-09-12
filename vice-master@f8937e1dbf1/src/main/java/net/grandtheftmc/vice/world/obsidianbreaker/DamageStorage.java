package net.grandtheftmc.vice.world.obsidianbreaker;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Timothy Lampen on 7/2/2017.
 */
public class DamageStorage {

    // The HashMaps inside the HashMap represent chunks
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, BlockStatus>> damage = new ConcurrentHashMap<String, ConcurrentHashMap<String, BlockStatus>>();

    /**
     * Generate a unique {@code String} for the {@code Block} {@code Location}
     *
     * @param loc Block location
     * @return Unique string
     */
    protected String generateBlockHash(Location loc) {
        return loc.getWorld().getUID().toString() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    /**
     * Generate a unique {@code String} for the {@code Chunk} {@code Location}
     *
     * @return Unique string
     */
    protected String generateChunkHash(Chunk chunk) {
        return chunk.getWorld().getUID().toString() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    /**
     * Generate a {@code Location} from the unique {@code String}
     *
     * @param blockHash
     * @return Location
     */
    public Location generateLocation(String blockHash) {
        try {
            String[] s = blockHash.split(":");
            return new Location(Bukkit.getWorld(UUID.fromString(s[0])), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
        } catch(Exception e) {
            Core.error("Couldn't generate hash from location (hash: " + blockHash + ")");
            return null;
        }
    }


    /**
     * Get the {@code BlockStatus} object of the block
     *
     * @param block The block
     * @param create Whether we should create the object if it doesn't exist
     * @return The {@code BlockStatus}, or null if it doesn't exist and create==false OR invalid block
     */
    public BlockStatus getBlockStatus(Block block, boolean create) {
        String chunkHash = generateChunkHash(block.getLocation().getChunk());
        Map<String, BlockStatus> chunkMap = null;

        if (damage.containsKey(chunkHash))
            chunkMap = damage.get(chunkHash);
        else if (create) {
            damage.put(chunkHash, new ConcurrentHashMap<String, BlockStatus>());
            chunkMap = damage.get(chunkHash);
        } else
            return null;

        String blockHash = generateBlockHash(block.getLocation());

        if (chunkMap.containsKey(blockHash))
            return chunkMap.get(blockHash);
        else if (create) {
            chunkMap.put(blockHash, new BlockStatus(blockHash, chunkHash, 10));
            return chunkMap.get(blockHash);
        } else {
            return null;
        }
    }

    /**
     * Remove the {@code BlockStatus} object from the map
     *
     * @param blockStatus
     */
    public void removeBlockStatus(BlockStatus blockStatus) {
        String chunkHash = blockStatus.getChunkHash();
        Map<String, BlockStatus> chunk = damage.get(chunkHash);
        if(chunk == null)
            return;

        chunk.remove(blockStatus.getBlockHash());

        if(chunk.isEmpty())
            damage.remove(chunkHash);
    }

    /**
     * Render cracks in {@code Block}
     *
     * @param block Block
     */
    public void renderCracks(Block block) {
        BlockStatus status = getBlockStatus(block, false);

        if(status == null || status.getTotalDurability() <= 0)
            return;

        int durability = 10 - (int) Math.ceil((status.getTotalDurability() - status.getDamage()) / status.getTotalDurability() * 10);
        Vice.getWorldManager().getObsidianManager().getNMSHandler().sendCrackEffect(block.getLocation(), durability);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, BlockStatus>> getBlocks() {
        return damage;
    }
}
