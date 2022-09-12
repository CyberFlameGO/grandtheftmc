package net.grandtheftmc.core.anticheat.data;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.anticheat.Anticheat;
import net.grandtheftmc.core.anticheat.Severity;
import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.trigger.MovementTrigger;
import net.grandtheftmc.core.anticheat.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientData {

    public Map<Trigger, Boolean> queuedEvents = new ConcurrentHashMap<>();
    public HashMap<CheatType.Type, Integer> countEvent = Maps.newHashMap();

    private CheatType detectedHack = null;
    private String detectedHackAttribute;
    private String detectedHackBanMessage;
    public Severity lastSeverity;


    private int CPS;
    private int Hits;
    private int Misses;
    private double lastReachDistance;

    public double vL;
    public double autoBanVL;

    private final Player player;
    private final Anticheat anticheat;

    public ClientData(Player player, Anticheat anticheat) {
        this.player = player;
        this.anticheat = anticheat;
    }

    public Player getPlayer() {
        return this.player;
    }

    private double lastViolationTime;
    public double lastAttackTime;

    public void readQueue() {
        int maxChecks = 100;

        if (Math.abs(System.currentTimeMillis() - lastAttackTime) > 2000) {
            this.Hits = 0;
            this.Misses = 0;
        }

        double newVL = 0.0D;


        for (Trigger trigger : this.queuedEvents.keySet()) {
            if (maxChecks < 0) break;
            maxChecks -= 1;

//            if (trigger instanceof CombatTrigger) {
//                if (((CombatTrigger) trigger).getAction().equals(CombatTrigger.FightAction.HIT)) {
//                    this.lastAttackTime = trigger.getTimeCreated();
//                    this.Hits += 1;
//                    if (this.Misses > 0) {
//                        this.Misses -= 1;
//                    }
//                }
//
//                if (((CombatTrigger) trigger).getAction().equals(CombatTrigger.FightAction.SWING)) {
//                    this.Misses += 1;
//                }
//            }

            double vL = this.anticheat.getCheckManager().check(trigger);
            if (vL > 0) newVL += vL;

            this.queuedEvents.remove(trigger);
        }

        if (newVL == 0.0D) {
            if (Math.abs(lastViolationTime - System.currentTimeMillis()) > 1500)
                this.vL *= 0.99D;

            if (Math.abs(lastViolationTime - System.currentTimeMillis()) > 2500)
                this.autoBanVL *= 0.8D;
        } else {
            lastViolationTime = System.currentTimeMillis();
            this.vL += newVL;
        }

        double maxVL = 20;/*Aeron.getAPI().getConfig().getDoubleValue("core.MaxVL");*/
        if (this.vL > maxVL) {
            this.anticheat.getClientHandler().notifyCheck(this);
            this.vL = maxVL / 2;
        }

//        double maxAutoBanVL = Aeron.getAPI().getConfig().getDoubleValue("AutoBan.MaxVL");
//        if (this.autoBanVL > maxAutoBanVL) {
//            this.autoBanVL = 0;
//            if (Aeron.getAPI().getConfig().getBooleanValue("AutoBan.Enabled")) {
//                if (!autoBanning) {
//                    int delay = Aeron.getAPI().getConfig().getIntegerValue("AutoBan.Delay");
//                    this.autoBanTime = System.currentTimeMillis() + (delay * 1000);
//                    this.autoBanning = true;
//                    api.getPlayerDataHandler().notifyAutoBan(this);
//                }
//            } else {
//                api.getPlayerDataHandler().notifyAdminsCustom(this.getPlayer().getName() + " would have been auto-banned.");
//            }
//        }
    }

    public double autoBanTime = -1;
    public boolean autoBanning;

    public void addToEventQueue(Trigger trigger) {
        if (trigger instanceof MovementTrigger) {
            MovementTrigger mTrigger = (MovementTrigger) trigger;

            this.addEvent(mTrigger);

//            if(!mTrigger.isFlying()) {
//                if(this.autoBanning) {
//                    if(System.currentTimeMillis() > this.autoBanTime) {
//                        if(this.autoBanTime != -1) {
//                            String banReason = Aeron.getAPI().getConfig().getStringValue("AutoBan.BanMessage").replace("&", "�").replace("%reason%", this.getDetectedHackBanMessage());
//                            Timestamp curTime = new Timestamp(new Date().getTime());
//                            Aeron.getAPI().getPlayerDataHandler().notifyAdminsCustom("�8[�l�cSloth�8] �c" + this.getPlayer().getName() + " �6has been banned.");
//                            Aeron.getAPI().getLogger().log("autobans.log", curTime + ": " + this.getPlayer().getName() + " should have been automatically banned. (" + this.getDetectedHackBanMessage() + ")", true);
//                            Aeron.getAPI().getPlugin().getServer().dispatchCommand(Aeron.getAPI().getPlugin().getServer().getConsoleSender(), "ban " + this.getPlayer().getName() + " " + banReason);
//                            autoBanning = false;
//                            this.autoBanTime = -1;
//                        }
//                    }
//                }
//            }
        }
        this.queuedEvents.put(trigger, false);
    }

    private MovementTrigger[] recording = new MovementTrigger[30];

    public double lastSwingTime;
    public double lastEnderpearlTime;
    private ClientData lastTarget;
    public double timeSinceLastTeleport;
    public double lastRightClickTime;
    public int clicks;
    public int cpsPackets;
    public double reachBuffer;
    public float aimbotLastCamera;
    public float aimbotBuffer;
    public int aimbotHits;
    public Location aimbotPreviousTargetLocation;
    public int aimbotVL;
    public double swingBuffer;
    public double swingLastCamera;
    public double noSwingLastSwingTime;
    public int aimbotDetections;
    public int swingVL;
    public double aimbotDelta;
    public double xSpeed;
    public double zSpeed;
    public double lastX, lastY, lastZ;
    public World lastWorld;
    public float lastYaw;
    public float lastPitch;
    public double improbableLastBukkitHitTime;
    public double improbableLastDifference;
    public double improbableLastFullDifference;
    public int jumpTicks;
    public boolean justJumped;
    public double xBefore;
    public double zBefore;
    public double xCurrent;
    public double zCurrent;
    public double sLastPacketTime;
    public double maxDistanceBufferX;
    public double maxDistanceBufferZ;
    public int sBuffer;
    public int hovertime;
    public int vBuffer;

    public double lastDifficulty;

    public boolean difficultyChanged;

    public int morepacketsPackets;

    public double lastPacketTime;

    public double mpDelay;


    public void addEvent(MovementTrigger e) {
        if (!e.isFlying()) {
            boolean needsShifted = true;
            for (int i = 0; i < recording.length; i++) {
                if (recording[i] == null) {
                    recording[i] = e;
                    needsShifted = false;
                    break;
                }
            }

            if (needsShifted) {
                for (int i = 0; i < recording.length; i++) {
                    if (i != recording.length - 1) {
                        recording[i] = recording[i + 1];
                    } else {
                        recording[i] = e;
                    }
                }
            }
        }
    }

    public int getPing() {
        return ((CraftPlayer) this.getPlayer().getPlayer()).getHandle().ping;
    }

    public Map<Trigger, Boolean> getQueuedEvents() {
        return queuedEvents;
    }

    public void setQueuedEvents(Map<Trigger, Boolean> queuedEvents) {
        this.queuedEvents = queuedEvents;
    }

    public CheatType getDetectedHack() {
        return detectedHack;
    }

    public void setDetectedHack(CheatType detectedHack) {
        if (this.detectedHack != null && detectedHack.getName().equalsIgnoreCase(this.detectedHack.getName())) return;
        this.detectedHack = detectedHack;
        addCount(detectedHack);
    }

    public void resetDetection() {
        this.detectedHack = null;
    }

    public String getDetectedHackAttribute() {
        return detectedHackAttribute;
    }

    public void setDetectedHackAttribute(String detectedHackAttribute) {
        this.detectedHackAttribute = detectedHackAttribute;
    }

    public String getDetectedHackBanMessage() {
        return detectedHackBanMessage;
    }

    public void setDetectedHackBanMessage(String detectedHackBanMessage) {
        this.detectedHackBanMessage = detectedHackBanMessage;
    }

    public Severity getLastSeverity() {
        return lastSeverity;
    }

    public void setLastSeverity(Severity lastSeverity) {
        this.lastSeverity = lastSeverity;
    }

    public double getvL() {
        return vL;
    }

    public void setvL(double vL) {
        this.vL = vL;
    }

    public double getAutoBanVL() {
        return autoBanVL;
    }

    public void setAutoBanVL(double autoBanVL) {
        this.autoBanVL = autoBanVL;
    }

    public MovementTrigger[] getRecording() {
        return recording;
    }

    public void setRecording(MovementTrigger[] recording) {
        this.recording = recording;
    }

    public int getCPS() {
        return CPS;
    }

    public void setCPS(int cPS) {
        CPS = cPS;
    }

    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }

    public int getMisses() {
        return Misses;
    }

    public void setMisses(int misses) {
        Misses = misses;
    }

    public double getLastReachDistance() {
        return lastReachDistance;
    }

    public void setLastReachDistance(double lastReachDistance) {
        this.lastReachDistance = lastReachDistance;
    }

    public void setLastTarget(ClientData target) {
        this.lastTarget = target;
    }

    public double getLastSwingTime() {
        return lastSwingTime;
    }

    public void setLastSwingTime(double lastSwingTime) {
        this.lastSwingTime = lastSwingTime;
    }

    public double getLastEnderpearlTime() {
        return lastEnderpearlTime;
    }

    public void setLastEnderpearlTime(double lastEnderpearlTime) {
        this.lastEnderpearlTime = lastEnderpearlTime;
    }

    public ClientData getLastTarget() {
        return lastTarget;
    }

    public double getAutoBanTime() {
        return autoBanTime;
    }

    public void setAutoBanTime(double autoBanTime) {
        this.autoBanTime = autoBanTime;
    }

    public boolean isAutoBanning() {
        return autoBanning;
    }

    public void setAutoBanning(boolean autoBanning) {
        this.autoBanning = autoBanning;
    }

    public double getTimeSinceLastTeleport() {
        return timeSinceLastTeleport;
    }

    public void setTimeSinceLastTeleport(double timeSinceLastTeleport) {
        this.timeSinceLastTeleport = timeSinceLastTeleport;
    }

    private void addCount(CheatType key) {
        countEvent.putIfAbsent(key.getType(), 0);
        countEvent.put(key.getType(), countEvent.get(key.getType()) + 1);
    }

    public int getCount(CheatType key) {
        return countEvent.getOrDefault(key.getType(), 1);
    }
}
