package net.grandtheftmc.core.util.particles;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerWorldParticles;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleEffects {

    private WrapperPlayServerWorldParticles wrappedPacket;

    public ParticleEffects(EnumWrappers.Particle particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count) {
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        float z = (float) loc.getZ();

        this.wrappedPacket = new WrapperPlayServerWorldParticles()
                .setParticleType(particle)
                .setLongDistance(false)
                .setX(x).setY(y).setZ(z)
                .setOffsetX(xOffset).setOffsetY(yOffset).setOffsetZ(zOffset)
                .setParticleData(speed)
                .setNumberOfParticles(count)
                .setData(new int[] {});
    }

    public void show(Player p) {
        wrappedPacket.sendPacket(p);
    }
}