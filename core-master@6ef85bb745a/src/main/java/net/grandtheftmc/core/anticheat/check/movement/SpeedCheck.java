package net.grandtheftmc.core.anticheat.check.movement;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.anticheat.Severity;
import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.check.Check;
import net.grandtheftmc.core.anticheat.data.ClientData;
import net.grandtheftmc.core.anticheat.event.MovementCheatEvent;
import net.grandtheftmc.core.anticheat.trigger.MovementTrigger;
import net.grandtheftmc.core.anticheat.trigger.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedCheck extends Check {

    public SpeedCheck() {
        super("Speed");
    }

    public double getMaxSpeed(MovementTrigger trigger) {
        double speed = 0.30D;//0.29D
        //SPRINT 0.561208202072919 1.3x faster
        //SNEAK 0.12951234466191863 0.3x faster
        //SPDI 0.5180519387595268 1.2x faster
        //SPDII 0.6044049676671079 1.4x faster

        if (trigger.getData().getPlayer().getWalkSpeed() > 0.2F)
            speed *= (trigger.getData().getPlayer().getWalkSpeed() * 5);

        for (PotionEffect potionEffect : trigger.getData().getPlayer().getActivePotionEffects()) {
            if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                int amp = potionEffect.getAmplifier() + 1;
                speed = (speed * (1.3 * amp));

                //Speed potions >4 tend to false positive; give an extra bit of running speed
                if (amp > 3) speed *= 1.8;
            }
        }

        if (trigger.isSneaking()) speed *= 0.3;

        //Players under blocks can rapidly press space to achieve very fast speeds
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Location plo = trigger.getTo().clone().add(x, 2.55, z);

                if (!plo.getBlock().getType().equals(Material.AIR)) {
                    speed *= 3;
                }
            }
        }

        boolean d = false;
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Location plo = trigger.getTo().clone().add(x, -1, z);

                if (plo.getBlock().getType().equals(Material.ICE)) {
                    d = true;
                    speed *= 3;
                }
            }
        }

        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Location plo = trigger.getTo().clone().add(x, -2, z);

                if (!d) {
                    if (plo.getBlock().getType().equals(Material.ICE)) {
                        speed *= 3;
                    }
                }
            }
        }

        if (trigger.getData().justJumped) {
            if (trigger.getData().jumpTicks == 0)
                speed *= 1.45;

            trigger.getData().justJumped = false;
        }

        //Sprint-jumping values
        double jumpVals[] = {2.11, 1.64, 1.57, 1.51, 1.43};
        if (trigger.getData().jumpTicks > 0) {
            if (trigger.getData().jumpTicks <= 3) {
                speed *= jumpVals[trigger.getData().jumpTicks + 1];
            }
        }

        //Account for server-side velocities
        double xV = Math.abs(trigger.getxV());
        double zV = Math.abs(trigger.getzV());

        double sD = ((xV + zV) / 0.44);
        if (sD > 0) speed += speed * sD;

        return speed;
    }

    @Override
    public double analyse(Trigger trigger) {
        double vL = 0;

        if (trigger instanceof MovementTrigger) {
            ClientData data = trigger.getData();

            if (data.sLastPacketTime == 0)
                data.sLastPacketTime = trigger.getTimeCreated();

            double lag = Math.abs(data.sLastPacketTime - trigger.getTimeCreated());
            data.sLastPacketTime = trigger.getTimeCreated();

            if (!((MovementTrigger) trigger).isFlying()) {

                //If the player teleported, can fly, or is in a vehicle, don't flag them and reset their locations
                if ((Math.abs(trigger.getData().timeSinceLastTeleport - System.currentTimeMillis()) < 2000) || data.getPlayer().getAllowFlight() || data.getPlayer().getVehicle() != null) {
                    data.xBefore = ((MovementTrigger) trigger).getTo().getX();
                    data.zBefore = ((MovementTrigger) trigger).getTo().getZ();
                    data.xCurrent = data.xBefore;
                    data.zCurrent = data.zBefore;
                } else {
                    data.xBefore = data.xCurrent;
                    data.xCurrent = ((MovementTrigger) trigger).getTo().getX();
                    data.zBefore = data.zCurrent;
                    data.zCurrent = ((MovementTrigger) trigger).getTo().getZ();


                    double sLag = lag / 2;
                    if (sLag < 50 && sLag > 0) sLag = 50;

                    //Depending on how long they haven't sent a packet, add the theoretical max speed the player could go at that moment to a buffer
                    //(Not sending packets for 1 second is lag, allow them to move ~6 or so blocks)
                    if (Math.abs(data.xBefore - data.xCurrent) > 0)
                        data.maxDistanceBufferX += (sLag / 50) * this.getMaxSpeed((MovementTrigger) trigger);

                    if (Math.abs(data.zBefore - data.zCurrent) > 0)
                        data.maxDistanceBufferZ += (sLag / 50) * this.getMaxSpeed((MovementTrigger) trigger);

                    //If the player isn't currently spamming packets to catch up on lag
                    if (lag > 0) {
                        data.maxDistanceBufferX -= Math.abs(data.xBefore - data.xCurrent);
                        data.maxDistanceBufferZ -= Math.abs(data.zBefore - data.zCurrent);

                        //TODO: This is kinda exploitable; the player can go faster to go get allowed to go further for a longer distance...
                        //If the player is going slower than the maximum speed, subtracting from the buffer isnt an issue, so lower the buffer so that they can't speed up a lot after racking up a buffer
                        //	if(Math.abs(player.xBefore - player.xCurrent) < this.getMaxSpeed((MovementEvent) e)) {
                        data.maxDistanceBufferX *= 0.95;
                        //	}
                        //		if(Math.abs(player.zBefore - player.zCurrent) < this.getMaxSpeed((MovementEvent) e)) {
                        data.maxDistanceBufferZ *= 0.95;
                        //	}

                        //The player shouldn't be able to teleport more than 200 blocks... or go an insane speed for a long-er amount of time. Lower it drastically...
                        //The player would have to lag for ~40 seconds to get this amount of buffer, or an attempt to bypass, which neither are possible in vanilla
                        if (data.maxDistanceBufferX > 200) data.maxDistanceBufferX *= 0.1;
                        if (data.maxDistanceBufferZ > 200) data.maxDistanceBufferZ *= 0.1;

                        //When the buffer is lower than 0 (faster speed than average)
                        if (data.maxDistanceBufferX < -0.1) {
                            data.sBuffer += 10;
                            data.maxDistanceBufferX = -0.1;

                            if (data.sBuffer > 30) {
                                data.sBuffer = 30;
                                vL += 1;

                                MovementCheatEvent<Double> event = new MovementCheatEvent<>(data, CheatType.Movement.SPEED, this.getMaxSpeed((MovementTrigger) trigger));
                                Bukkit.getPluginManager().callEvent(event);
                                if(event.isCancelled()) return vL;

                                data.setDetectedHack(CheatType.Movement.SPEED);
                                data.setDetectedHackAttribute("X");
                                data.setDetectedHackBanMessage("SPEED_X");
                                data.lastSeverity = Severity.MEDIUM;
                                return vL;
                            }
                        }

                        if (data.maxDistanceBufferZ < -0.1) {
                            data.sBuffer += 10;
                            data.maxDistanceBufferZ = -0.1;

                            if (data.sBuffer > 30) {
                                data.sBuffer = 30;
                                vL += 1;

                                MovementCheatEvent<Double> event = new MovementCheatEvent<>(data, CheatType.Movement.SPEED, this.getMaxSpeed((MovementTrigger) trigger));
                                Bukkit.getPluginManager().callEvent(event);
                                if(event.isCancelled()) return vL;

                                data.setDetectedHack(CheatType.Movement.SPEED);
                                data.setDetectedHackAttribute("Z");
                                data.setDetectedHackBanMessage("SPEED_Z");
                                data.lastSeverity = Severity.MEDIUM;
                                return vL;
                            }
                        }

                    }
                }
            }
        }

        return vL;
    }
}
