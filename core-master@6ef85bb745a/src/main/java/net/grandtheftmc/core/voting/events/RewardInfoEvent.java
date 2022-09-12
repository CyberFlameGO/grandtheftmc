package net.grandtheftmc.core.voting.events;

import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.voting.Reward;

public class RewardInfoEvent extends RewardEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final String identifier;
    private ItemStack displayItem;

    public RewardInfoEvent(Reward.RewardType rewardType, String identifier) {
        super(rewardType);
        this.displayItem = new ItemStack(Material.BARRIER);
        this.identifier = identifier;
    }
    
    public RewardInfoEvent(Reward reward, Reward.RewardType rewardType, String identifier) {
        super(reward, rewardType);
        this.displayItem = new ItemStack(Material.BARRIER);
        this.identifier = identifier;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}