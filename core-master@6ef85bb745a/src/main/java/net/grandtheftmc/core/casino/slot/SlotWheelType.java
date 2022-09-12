package net.grandtheftmc.core.casino.slot;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SlotWheelType {

    ONE(817, new SlotReward[] {
            new SlotReward(SlotItem.SEVEN, 4, 345, 0, 15),//0
            new SlotReward(SlotItem.CHERRY, 5, 15, 30, 45),//30
            new SlotReward(SlotItem.LSD, 6, 45, 60, 75),//60
            new SlotReward(SlotItem.MDMA, 7, 75, 90, 105),//90
            new SlotReward(SlotItem.LSD, 8, 105, 120, 135),//120
            new SlotReward(SlotItem.CHERRY, 9, 135, 150, 165),//150
            new SlotReward(SlotItem.DILDO, 10, 165, 180, 195),//180
            new SlotReward(SlotItem.MDMA, 11, 195, 210, 225),//210
            new SlotReward(SlotItem.LSD, 12, 225, 240, 255),//240
            new SlotReward(SlotItem.DILDO, 1, 255, 270, 285),//270
            new SlotReward(SlotItem.MDMA, 2, 285, 300, 315),//300
            new SlotReward(SlotItem.LSD, 3, 315, 330, 345),//330
    }),

    TWO(818, new SlotReward[] {
            new SlotReward(SlotItem.CHERRY, 4, 345, 0, 15),//0
            new SlotReward(SlotItem.SEVEN, 5, 15, 30, 45),//30
            new SlotReward(SlotItem.MDMA, 6, 45, 60, 75),//60
            new SlotReward(SlotItem.LSD, 7, 75, 90, 105),//90
            new SlotReward(SlotItem.DILDO, 8, 105, 120, 135),//120
            new SlotReward(SlotItem.MDMA, 9, 135, 150, 165),//150
            new SlotReward(SlotItem.LSD, 10, 165, 180, 195),//180
            new SlotReward(SlotItem.DILDO, 11, 195, 210, 225),//210
            new SlotReward(SlotItem.LSD, 12, 225, 240, 255),//240
            new SlotReward(SlotItem.CHERRY, 1, 255, 270, 285),//270
            new SlotReward(SlotItem.MDMA, 2, 285, 300, 315),//300
            new SlotReward(SlotItem.LSD, 3, 315, 330, 345),//330
    }),

    THREE(819, new SlotReward[] {
            new SlotReward(SlotItem.CHERRY, 4, 345, 0, 15),//0
            new SlotReward(SlotItem.LSD, 5, 15, 30, 45),//30
            new SlotReward(SlotItem.MDMA, 6, 45, 60, 75),//60
            new SlotReward(SlotItem.DILDO, 7, 75, 90, 105),//90
            new SlotReward(SlotItem.CHERRY, 8, 105, 120, 135),//120
            new SlotReward(SlotItem.LSD, 9, 135, 150, 165),//150
            new SlotReward(SlotItem.MDMA, 10, 165, 180, 195),//180
            new SlotReward(SlotItem.DILDO, 11, 195, 210, 225),//210
            new SlotReward(SlotItem.LSD, 12, 225, 240, 255),//240
            new SlotReward(SlotItem.MDMA, 1, 255, 270, 285),//270
            new SlotReward(SlotItem.LSD, 2, 285, 300, 315),//300
            new SlotReward(SlotItem.SEVEN, 3, 315, 330, 345),//330
    }),
    ;

    private int durability;
    private SlotReward[] slotRewards;

    SlotWheelType(int durability, SlotReward[] slotRewards) {
        this.durability = durability;
        this.slotRewards = slotRewards;
    }

    public int getDurability() {
        return durability;
    }

    public SlotReward[] getSlotRewards() {
        return slotRewards;
    }

    public SlotReward getRewardByAngle(double angle) {

        if (this.slotRewards.length < 1) {
            return null;
        }

        for(SlotReward reward : this.slotRewards) {
            if ((angle >= 0 && angle < 15 ) || (angle >= 345  && angle < 360))
                return reward;

            if (angle >= reward.getAngle()[0] && angle  < reward.getAngle()[2]) {
                return reward;
            }
        }

        return null;
    }

    public ItemStack getModel() {
        ItemStack model = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta modelMeta = model.getItemMeta();

        model.setDurability((short) (durability));

        if(Core.getSettings().getType() == ServerType.VICE) modelMeta.setUnbreakable(true);
        else modelMeta.spigot().setUnbreakable(true);
        modelMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        model.setItemMeta(modelMeta);

        return model;
    }
}
