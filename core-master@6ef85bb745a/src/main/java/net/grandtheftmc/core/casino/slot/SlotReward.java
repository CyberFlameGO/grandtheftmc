package net.grandtheftmc.core.casino.slot;

public class SlotReward {
    private final SlotItem slotItem;
    private final int id;
    private final int[] angle;

    public SlotReward(SlotItem slotItem, int id, int... angle) {
        this.slotItem = slotItem;
        this.id = id;
        this.angle = angle;
    }

    public SlotItem getRewardItem() {
        return slotItem;
    }

    public int getId() {
        return id;
    }

    public int[] getAngle() {
        return angle;
    }
}
