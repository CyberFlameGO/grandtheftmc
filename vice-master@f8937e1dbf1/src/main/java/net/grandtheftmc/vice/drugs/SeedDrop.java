package net.grandtheftmc.vice.drugs;

import org.bukkit.block.Biome;

public final class SeedDrop {
    private final int uniqueId;

    private final int range;
    private final int interval;
    private final Biome biome;

    private long last;

    SeedDrop(int uniqueId, int range, int interval, Biome biome) {
        this.uniqueId = uniqueId;
        this.range = range;
        this.interval = interval;
        this.biome = biome;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getRange() {
        return range;
    }

    public int getInterval() {
        return interval;
    }

    public Biome getBiome() {
        return biome;
    }

    public long getLast() {
        return last;
    }

    public void next() {
        this.last = System.currentTimeMillis() + (this.interval * 1000);
    }
}
