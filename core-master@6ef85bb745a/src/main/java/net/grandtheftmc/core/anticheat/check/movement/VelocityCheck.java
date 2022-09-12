package net.grandtheftmc.core.anticheat.check.movement;

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
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VelocityCheck extends Check {

    public VelocityCheck() {
        super("Velocity");
    }

    @Override
    public double analyse(Trigger trigger) {
        double vL = 0;

        if (trigger instanceof MovementTrigger) {
            MovementTrigger mTrigger = (MovementTrigger) trigger;

            if (!mTrigger.isFlying()) {
                ClientData data = trigger.getData();

                double dif = Math.abs(mTrigger.getyV() - mTrigger.getyD());
                if (dif > 0.8) {

                    if (!data.getPlayer().getAllowFlight()) {
                        if (Math.abs(data.timeSinceLastTeleport - System.currentTimeMillis()) > 2000) {
                            //Lagging players + jump boost == false positives...
                            boolean jumpBoost = false;
                            for (PotionEffect potionEffect : trigger.getData().getPlayer().getActivePotionEffects()) {
                                if (potionEffect.getType().equals(PotionEffectType.JUMP)) {
                                    jumpBoost = true;
                                }
                            }

                            //TODO: Recreate the check to NOT false positive, but still have lag compensation
                            if (!jumpBoost) {
                                //If the player should be falling, and the player isn't going down, flag them after a while
                                if (mTrigger.getyD() == 0.0D) {
                                    data.hovertime += 1;
                                } else {
                                    if (data.hovertime >= 0) {//TODO >
                                        data.hovertime -= 1;
                                    }
                                }

                                if (data.hovertime > 15) {
                                    data.hovertime = 5;
                                    vL += 3;

                                    data.setDetectedHack(CheatType.Movement.FLIGHT);
                                    data.setDetectedHackAttribute("Hover");
                                    data.lastSeverity = Severity.LOW;
                                    return vL;
                                }


                                if (mTrigger.getyD() > 0) {
                                    data.vBuffer += 1;

                                    if (mTrigger.getyD() > 0.8) {
                                        data.vBuffer += 3;
                                    }

                                    //Stairs seem to not register with bukkit very well; exempt them if they're around stairs by 0.5 blocks
                                    boolean check = true;
                                    for (double xC = -0.5; xC < 0.5; xC += 0.1) {
                                        for (double zC = -0.5; zC < 0.5; zC += 0.1) {
                                            String block = data.getPlayer().getLocation().clone().add(xC, -1, zC).getBlock().getType().name();

                                            if (block.contains("STAIR") || block.contains("STEP")) {
                                                check = false;
                                                data.hovertime = 0;
                                                data.vBuffer = 0;
                                            }
                                        }
                                    }

                                    if (check) {
                                        if (data.vBuffer > 5) {
                                            data.vBuffer = 5;
                                            vL += 6;

                                            Block block = data.getPlayer().getLocation().clone().add(0, -0.5, 0).getBlock();
                                            if (block.getType() == Material.STATIONARY_LAVA
                                                    || block.getType() == Material.LAVA
                                                    || block.getType() == Material.STATIONARY_WATER
                                                    || block.getType() == Material.WATER) {
                                                MovementCheatEvent event = new MovementCheatEvent(data, CheatType.Movement.JESUS);
                                                Bukkit.getPluginManager().callEvent(event);
                                                if(event.isCancelled()) return vL;

                                                data.setDetectedHack(CheatType.Movement.JESUS);
                                                data.setDetectedHackBanMessage("WATER_WALK");
                                                data.setDetectedHackAttribute("*");
                                                data.lastSeverity = Severity.MEDIUM;
                                                return vL;
                                            } else {
                                                if (mTrigger.isOnground()) {
                                                    MovementCheatEvent event = new MovementCheatEvent(data, CheatType.Movement.FLIGHT);
                                                    Bukkit.getPluginManager().callEvent(event);
                                                    if(event.isCancelled()) return vL;

                                                    data.setDetectedHack(CheatType.Movement.FLIGHT);
                                                    data.setDetectedHackBanMessage("FLY_STEP");
                                                    data.setDetectedHackAttribute("*");
                                                    data.lastSeverity = Severity.MEDIUM;
                                                    return vL;
                                                } else {
                                                    MovementCheatEvent event = new MovementCheatEvent(data, CheatType.Movement.FLIGHT);
                                                    Bukkit.getPluginManager().callEvent(event);
                                                    if(event.isCancelled()) return vL;

                                                    data.setDetectedHack(CheatType.Movement.FLIGHT);
                                                    data.setDetectedHackBanMessage("FLY");
                                                    data.setDetectedHackAttribute("*");
                                                    data.lastSeverity = Severity.MEDIUM;
                                                    return vL;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (data.vBuffer > 0) {
                        data.vBuffer -= 1;
                    }
                }
            }
        }

        return vL;
    }
}
