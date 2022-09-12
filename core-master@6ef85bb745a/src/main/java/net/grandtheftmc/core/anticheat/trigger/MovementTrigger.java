package net.grandtheftmc.core.anticheat.trigger;

import net.grandtheftmc.core.anticheat.data.ClientData;
import org.bukkit.Location;

public class MovementTrigger extends Trigger {

    private boolean flying;
    private Location from, to;

    /** Distance */
    private double xD, yD, zD;

    /** Velocity */
    private double xV, yV, zV;

    private boolean sprinting, sneaking, onground;

    /**
     * Construct new Trigger.
     */
    public MovementTrigger(ClientData data, Location from, Location to, boolean flying) {
        super(data, System.currentTimeMillis());

        this.flying = flying;
        if (!flying) {
            this.from = from;
            this.to = to;

            this.xD = to.getX() - from.getX();
            this.yD = to.getY() - from.getY();
            this.zD = to.getZ() - from.getZ();

            data.xSpeed = xD;
            data.zSpeed = zD;

            data.lastX = to.getX();
            data.lastY = to.getY();
            data.lastZ = to.getZ();
            data.lastWorld = to.getWorld();
            data.lastYaw = to.getYaw();
            data.lastPitch = to.getPitch();

            this.xV = data.getPlayer().getVelocity().getX();
            this.yV = data.getPlayer().getVelocity().getY();
            this.zV = data.getPlayer().getVelocity().getZ();

            this.sprinting = data.getPlayer().isSprinting();
            this.sneaking = data.getPlayer().isSneaking();
            this.onground = data.getPlayer().isOnGround();
        }
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public boolean isFlying() {
        return flying;
    }

    public boolean isOnground() {
        return onground;
    }

    public Location getTo() {
        return to;
    }

    public double getxD() {
        return xD;
    }

    public double getyD() {
        return yD;
    }

    public double getzD() {
        return zD;
    }

    public double getxV() {
        return xV;
    }

    public double getyV() {
        return yV;
    }

    public double getzV() {
        return zV;
    }
}
