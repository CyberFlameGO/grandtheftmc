package net.grandtheftmc.core.anticheat.util;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class DummyPlayer {

    private int entityID;
    private Map<UUID, Location> prevPlayerBotLocations;

    public DummyPlayer(int entityID) {
        this.entityID = entityID;
        prevPlayerBotLocations = Maps.newHashMap();
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public int getEntityID() {
        return entityID;
    }


    public void spawnForPlayerWithIdentity(final Player p, final Location location, final DummyIdentity identity) {
        if (!identity.isAlreadyOnline && identity.type == EntityType.PLAYER)
            sendBotInfo(p, identity);
        //DummyPacketGen.getIdentityPlayerSpawnPacket(identity, entityID, location).sendPacket(p);
        DummyPacketGen.sendSpawnPacket(p, identity, entityID, location);
    }

    public void sendBotInfo(Player p, DummyIdentity id) {
        DummyPacketGen.getInfoAddPacket(id.uuid, id.name).sendPacket(p);
    }

    public void destroyForPlayer(Player p) {
        DummyPacketGen.getDestroyPacket(entityID).sendPacket(p);
    }

    public void despawnTablistForPlayer(Player p, DummyIdentity identity) {
        if (identity.type == EntityType.PLAYER)
            DummyPacketGen.getInfoRemovePacket(identity.uuid, identity.name).sendPacket(p);
    }

    public void moveTo(Player p, Location loc) {
        UUID pUUID = p.getUniqueId();
        if (prevPlayerBotLocations.containsKey(pUUID)) {
            Location prevLoc = prevPlayerBotLocations.get(pUUID);

            double deltaX = loc.getX() - prevLoc.getX();
            double deltaY = loc.getY() - prevLoc.getY();
            double deltaZ = loc.getZ() - prevLoc.getZ();
            double dist = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

            if (dist > 4)
                DummyPacketGen.getTeleportPacket(entityID, loc).sendPacket(p);
            else
                DummyPacketGen.getTeleportPacket(entityID, loc).sendPacket(p);
        } else
            DummyPacketGen.getTeleportPacket(entityID, loc).sendPacket(p);

        prevPlayerBotLocations.put(p.getUniqueId(), loc);

    }

    public void moveAround(Player p, double angle, double distance) {
        moveTo(p, getAroundPos(p, angle, distance));
    }

    public static Location getAroundPos(Player p, double angle, double distance) {
        Location loc = p.getLocation().clone();
        double realAngle = angle + 90;

        float deltaX = (float) (distance * Math.cos(Math.toRadians(loc.getYaw() + realAngle)));
        float deltaZ = (float) (distance * Math.sin(Math.toRadians(loc.getYaw() + realAngle)));

        loc.add(deltaX, -0.1, deltaZ);

        return loc;
    }
}
