package net.grandtheftmc.gtm.users;

import net.grandtheftmc.gtm.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class TaxiTarget {
    private TargetType type;

    private UUID targetPlayer;
    private UUID targetEntity;
    private Location location;
    private Warp warp;

    public TaxiTarget() {
        this.type = TargetType.NONE;
    }

    public TaxiTarget(Player player) {
        this.type = TargetType.PLAYER;
        this.targetPlayer = player.getUniqueId();
    }

    public TaxiTarget(Entity entity) {
        this.type = TargetType.ENTITY;
        this.targetEntity = entity.getUniqueId();
    }

    public TaxiTarget(Location position) {
        this.type = TargetType.LOCATION;
        this.location = position;
    }

    public TaxiTarget(Warp warp) {
        this.type = TargetType.WARP;
        this.warp = warp;
    }

    public TargetType getType() {
        return this.type;
    }

    public void setType(TargetType type) {
        this.type = type;
    }

    public UUID getTargetPlayerUUID() {
        return this.targetPlayer;
    }

    public Player getTargetPlayer() {
        if (this.targetPlayer == null)
            return null;
        return Bukkit.getPlayer(this.targetPlayer);
    }

    public void setTargetPlayer(UUID targetPlayer) {
        this.targetPlayer = targetPlayer;
        this.type = TargetType.PLAYER;
    }

    public UUID getTargetEntityUUID() {
        return this.targetEntity;
    }

    public Entity getTargetEntity() {
        if (this.targetEntity == null)
            return null;
        for (World w : Bukkit.getWorlds())
            for (Entity e : w.getEntities())
                if (Objects.equals(e.getUniqueId(), this.targetEntity))
                    return e;
        this.targetEntity = null;
        return null;
    }

    public void setTargetEntity(UUID targetEntity) {
        this.targetEntity = targetEntity;
        this.type = TargetType.ENTITY;
    }

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity == null ? null : targetEntity.getUniqueId();
        this.type = TargetType.ENTITY;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Warp getWarp() {
        return this.warp;
    }

    public void setWarp(Warp w) {
        this.warp = w;
    }

    public Location getExactLocation() {
        switch (this.type) {
        case LOCATION:
            return this.location;
        case ENTITY:
            Entity e = this.getTargetEntity();
            return e == null ? null : e.getLocation();
        case PLAYER:
            Player p = this.getTargetPlayer();
            return p == null ? null : p.getLocation();
        case WARP:
            return this.warp == null ? null : this.warp.getLocation();
        default:
            return null;
        }
    }

    public enum TargetType {
        NONE,
        PLAYER,
        ENTITY,
        LOCATION,
        WARP
    }

}
