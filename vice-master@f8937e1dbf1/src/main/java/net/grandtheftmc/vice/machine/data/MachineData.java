package net.grandtheftmc.vice.machine.data;

public class MachineData {

    private final DataFlag flag;
    private final double max;
    private double current;

    private int[] textures;
    private int[] slots;

    public MachineData(DataFlag flag, double max, double current) {
        this.flag = flag;
        this.max = max;
        this.current = current;
    }

    public MachineData(DataFlag flag, double max) {
        this.flag = flag;
        this.max = max;
        this.current = max;
    }

    public DataFlag getFlag() {
        return flag;
    }

    public double getMax() {
        return max;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public int[] getTextures() {
        return textures;
    }

    public void setTextures(int... textures) {
        this.textures = textures;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int... slots) {
        this.slots = slots;
    }

    public boolean isSlot(int slot) {
        for (int i : this.slots)
            if (i == slot) return true;
        return false;
    }

    public boolean isEmpty() {
        return this.current <= 0;
    }

    public boolean isFull() {
        return this.current >= this.max;
    }

    public void take(int amount) {
        if (this.current - amount < 0) {
            this.current = 0;
            return;
        }

        this.current -= amount;
    }

    public void add(int amount) {
        if (this.current + amount >= this.max) {
            this.current = this.max;
            return;
        }

        this.current += amount;
    }

    public int getTexture() {
        return this.textures[this.getPercentBetweenValues(this.max, this.current, this.textures.length - 1)];
    }

    private int getPercentBetweenValues(double goal, double value, int bars) {
        return (int) Math.round(value >= goal ? bars : bars - Math.abs((goal - value) / goal * bars));
    }

    public static enum DataFlag {
        UP, DOWN,
        ;
    }
}
