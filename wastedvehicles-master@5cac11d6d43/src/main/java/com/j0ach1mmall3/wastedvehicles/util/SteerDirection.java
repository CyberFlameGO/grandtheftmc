package com.j0ach1mmall3.wastedvehicles.util;

import com.comphenix.protocol.events.PacketContainer;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class SteerDirection {
    private final float forMotion;
    private final float sideMotion;
    private final boolean jump;

    public SteerDirection(PacketContainer packet) {
        this.forMotion = packet.getFloat().read(1);
        this.sideMotion = packet.getFloat().read(0);
        this.jump = packet.getBooleans().read(0);
    }

    public boolean isForward() {
        return this.forMotion > 0;
    }

    public boolean isBackward() {
        return this.forMotion < 0;
    }

    public boolean isLeft() {
        return this.sideMotion > 0;
    }

    public boolean isRight() {
        return this.sideMotion < 0;
    }

    public float getForMotion() {
        return this.forMotion;
    }

    public float getSideMotion() {
        return this.sideMotion;
    }

    public boolean isJump() {
        return this.jump;
    }
}
