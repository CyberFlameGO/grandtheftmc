package net.grandtheftmc.core.casino.slot;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.casino.game.CasinoGameAttribute;
import net.grandtheftmc.core.util.AngleUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

public class CasinoSpinData {

    private final ArmorStand armorStand;
    private final int id;
    private final SlotWheelType wheelType;
    private long ticks = 0, maxTicks = 100;
    private double speed = 100;
    private SpinState spinState = SpinState.SPINNING;
    private float pitch = 0.25f;

    public CasinoSpinData(SlotMachine machine, Location location, int id, SlotWheelType wheelType, double defaultAngle) {
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone(), EntityType.ARMOR_STAND);
        this.armorStand.setCustomName(machine.getClass().getAnnotation(CasinoGameAttribute.class).id() + ";" + id);
        this.armorStand.setCustomNameVisible(false);
        if(id == 2) {
            this.armorStand.setArms(true);
            this.armorStand.getEquipment().setItemInOffHand(machine.getMachine(true));
            this.armorStand.setLeftArmPose(new EulerAngle(0, 0, 0));
        }

        this.armorStand.setCanPickupItems(false);
        this.armorStand.setGravity(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setVisible(false);
        this.armorStand.setInvulnerable(true);
        this.armorStand.setRemoveWhenFarAway(false);
        this.armorStand.setMarker(false);
        this.armorStand.setMetadata("SLOT_MACHINE", new FixedMetadataValue(Core.getInstance(), machine));

        this.wheelType = wheelType;
        this.armorStand.setHelmet(wheelType.getModel());
        this.armorStand.getLocation().setPitch(90);
        this.id = id;

        this.armorStand.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(defaultAngle), 0, 0));
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public int getId() {
        return this.id;
    }

    public SlotWheelType getWheelType() {
        return this.wheelType;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTicks() {
        return this.ticks;
    }

    public void setTicks(long tick) {
        this.ticks = tick;
    }

    public SpinState getSpinState() {
        return this.spinState;
    }

    public void setSpinState(SpinState spinState) {
        this.spinState = spinState;
    }

    public long getMaxTicks() {
        return this.maxTicks;
    }

    public void spin(boolean forward) {
        if(this.armorStand == null) return;
        EulerAngle c = this.armorStand.getHeadPose();
        this.armorStand.setHeadPose(new EulerAngle(Math.toRadians(Math.toDegrees(c.getX()) + (forward ? speed : -speed)), 0, 0));
    }

    public void playNote() {
        Location l = this.armorStand.getEyeLocation();
        if(this.wheelType == SlotWheelType.THREE && this.speed <= 40) {
            if(this.pitch >= 1.0)
                this.pitch += 0.1f;
            else
                this.pitch += 0.25f;
            l.getWorld().playSound(l, Sound.BLOCK_NOTE_PLING, 0.8f, this.pitch);
            return;
        }

        if(this.pitch >= 1.0)
            this.pitch = 0.25f;
        this.pitch += 0.25f;
        l.getWorld().playSound(l, Sound.BLOCK_NOTE_PLING, 0.8f, this.pitch);
    }

    protected void reset() {
        this.ticks = 0;
        this.maxTicks = 100;
        this.speed = 100;
        this.pitch = 0.1f;
        this.spinState = SpinState.SPINNING;
    }
}
