package net.grandtheftmc.vice.world.obsidianbreaker;

/**
 * Created by Timothy Lampen on 7/2/2017.
 */
/**
 * Stores damage data concerning one block
 *
 * @author oggehej
 */
public class BlockStatus {
    private float damage = 0;
    private float maxDamage;
    private boolean modified = true;
    private final String blockHash;
    private final String chunkHash;

    /**
     * An object that contains information about the
     * damage taken and id it was recently modified.
     */
    BlockStatus(String blockHash, String chunkHash, float maxDamage) {
        this.blockHash = blockHash;
        this.chunkHash = chunkHash;
        this.maxDamage = maxDamage;
    }

    /**
     * Get current damage to block
     *
     * @return Damage
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Set current damage to block
     *
     * @param damage Damage
     */
    public void setDamage(float damage) {
        this.damage = damage;
    }

    /**
     * Check whether the block was recently modified or not
     *
     * @return Recently modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Set whether the block was recently modified or not
     *
     * @param modified Recently modified
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * Get the maximum amount of damage the block can take
     *
     * @return Max damage
     */
    float getTotalDurability() {
        return this.maxDamage;
    }

    /**
     * Get the block hash associated with this object
     *
     * @return Block hash
     */
    public String getBlockHash() {
        return blockHash;
    }

    /**
     * Get the chunk hash associated with this object
     *
     * @return Chunk hash
     */
    public String getChunkHash() {
        return chunkHash;
    }
}