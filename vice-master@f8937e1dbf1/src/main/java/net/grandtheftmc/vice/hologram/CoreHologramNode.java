package net.grandtheftmc.vice.hologram;

import net.grandtheftmc.core.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public final class CoreHologramNode implements HologramNode {

    private final int id;
    private final ArmorStand entity;
    private final Location location;

    private String text = UUID.randomUUID().toString();

    protected CoreHologramNode(int id, Location location) {
        this.id = id;
        this.location = location;
        this.entity = this.craftEntity();
    }

    protected CoreHologramNode(int id, Location location, String text) {
        this(id, location);
        this.text = text;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ArmorStand getEntity() {
        return entity;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = Utils.f(text);
        this.entity.setCustomName(this.text);
    }

    private ArmorStand craftEntity() {
        HologramManager.CHUNKS.add(this.location.getChunk());
        this.location.getChunk().load();
        ArmorStand a = this.location.getWorld().spawn(this.location, ArmorStand.class);
        a.setGravity(false);
        a.setVisible(false);
        a.setCustomName(this.text);
        a.setCustomNameVisible(true);
        a.setRemoveWhenFarAway(false);
        a.setInvulnerable(true);
        a.setAI(false);
        a.setMarker(true);
        a.setSmall(true);

        a.setMetadata("CoreHologram", new FixedMetadataValue(Vice.getInstance(), this));

        return a;
    }
}
