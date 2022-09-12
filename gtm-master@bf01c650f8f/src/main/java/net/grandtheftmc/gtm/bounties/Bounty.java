package net.grandtheftmc.gtm.bounties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Bounty {

    private UUID uuid;
    private String name;
    private List<BountyPlacer> placers = new ArrayList<>();
    private long lastUpdate;

    public Bounty(UUID uuid, String name, List<BountyPlacer> placers) {
        this.uuid = uuid;
        this.name = name;
        this.placers = placers;
        this.lastUpdate = System.currentTimeMillis();
    }

    public Bounty(UUID uuid, String name, List<BountyPlacer> placers, long lastUpdate) {
        this.uuid = uuid;
        this.name = name;
        this.placers = placers;
        this.lastUpdate = lastUpdate;
    }

    public Bounty(UUID uuid, String name, BountyPlacer bountyPlacer) {
        this.uuid = uuid;
        this.name = name;
        this.placers.add(bountyPlacer);
        this.lastUpdate = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID u) {
        this.uuid = u;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public List<BountyPlacer> getPlacers() {
        return this.placers;
    }

    public void setPlacers(List<BountyPlacer> l) {
        this.placers = l;
    }

    public double getAmount() {
        return this.placers.stream().mapToDouble(BountyPlacer::getAmount).sum();
    }

    public BountyPlacer getConsolePlacer() {
        return this.placers.stream().filter(BountyPlacer::isConsole).findFirst().orElse(null);
    }

    public BountyPlacer getPlacer(UUID uniqueId) {
        return this.placers.stream().filter(placer -> Objects.equals(uniqueId, placer.getUUID())).findFirst().orElse(null);
    }

    public void addPlacer(BountyPlacer bountyPlacer) {
        this.placers.add(bountyPlacer);
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate() {
        this.lastUpdate = System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return this.lastUpdate + 86400000 < System.currentTimeMillis();
    }

    public long getTimeUntilExpiryInMillis() {
        return this.lastUpdate + 86400000 - System.currentTimeMillis();
    }

}
