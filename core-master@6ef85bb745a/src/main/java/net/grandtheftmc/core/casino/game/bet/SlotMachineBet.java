package net.grandtheftmc.core.casino.game.bet;

import net.grandtheftmc.core.casino.coins.ChipAmount;

public enum SlotMachineBet {
    TINY("Tiny Roller", ChipAmount.TEN),
    SMALL("Small Roller",ChipAmount.FIFTY),
    HIGH("High Roller",ChipAmount.HUNDRED),
    INSANE("Insane Roller",ChipAmount.THREE_HUNDRED),
    ALL_IN("All In!",ChipAmount.EIGHT_HUNDRED),
    ;

    private final String name;
    private final ChipAmount cost;

    SlotMachineBet(String name, ChipAmount cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public ChipAmount getCost() {
        return this.cost;
    }
}
