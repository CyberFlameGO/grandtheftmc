package net.grandtheftmc.gtm.users;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class CompassTarget {

    private TargetType type;

    private UUID targetPlayer;
    private UUID targetEntity;
    private Location location;

    public CompassTarget() {
        this.type = TargetType.NONE;
    }

    public CompassTarget(Player player) {
        this.type = TargetType.PLAYER;
        this.targetPlayer = player.getUniqueId();
    }

    public CompassTarget(Entity entity) {
        this.type = TargetType.ENTITY;
        this.targetEntity = entity.getUniqueId();
    }

    public CompassTarget(Location position) {
        this.type = TargetType.LOCATION;
        this.location = position;
    }

    public CompassTarget(PersonalVehicle vehicle) {
        this.type = TargetType.VEHICLE;
        this.targetEntity = vehicle.getEntityUUID();
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

    public Location getExactLocation(Player player) {
        Location loc = null;
        switch (this.type) {
            case LOCATION:
                loc = this.location;
                break;
            case ENTITY:
                Entity entity = this.getTargetEntity();
                if (entity != null)
                    loc = entity.getLocation();
                break;
            case PLAYER:
                Player targetPlayer = this.getTargetPlayer();
                if (targetPlayer != null)
                    loc = targetPlayer.getLocation();
                break;
            case VEHICLE:
                Entity vehicle = this.getTargetEntity();
                if (vehicle != null)
                    loc = vehicle.getLocation();
                break;
            default:
                break;
        }
        if (loc == null || !Objects.equals(player.getLocation().getWorld(), loc.getWorld())) {
            return player.getLocation();
        }
        return loc;
    }

    public Location getApproximateLocation(Player player, int radius) {
        Random rand = Utils.getRandom();
        return this.getExactLocation(player).add(rand.nextInt(radius << 1) - radius, 0, rand.nextInt(radius << 1) - radius);
    }

    public Location getApproximateLocation(Player player, User u) {
        return this.getApproximateLocation(player, GTMUtils.getCompassRadius(u.getUserRank()));
    }


    public enum TargetType {
        NONE,
        PLAYER,
        ENTITY,
        LOCATION,
        VEHICLE
    }

}
