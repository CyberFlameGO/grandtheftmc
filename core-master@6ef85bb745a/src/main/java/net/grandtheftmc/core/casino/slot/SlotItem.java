package net.grandtheftmc.core.casino.slot;

public enum SlotItem {
    DILDO("Dildo", new int[]{0, 2, 20}),
    MDMA("MDMA", new int[]{0, 2, 5}),
    LSD("LSD", new int[]{0, 2, 10}),
    SEVEN("7", new int[]{0, 2, 500}),
    CHERRY("Cherry", new int[]{1, 4, 35});

    private String name;
    private int[] reward;

    SlotItem(String name, int[] reward) {
        this.name = name;
        this.reward = reward;
    }

    public String getName() {
        return name;
    }

    public int[] getReward() {
        return reward;
    }
}
