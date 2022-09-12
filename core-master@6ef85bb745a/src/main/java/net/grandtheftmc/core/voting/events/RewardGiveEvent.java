package net.grandtheftmc.core.voting.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.voting.Reward;

public final class RewardGiveEvent extends RewardEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final String identifier;

    public RewardGiveEvent(Player player, Reward.RewardType rewardType, String identifier) {
        super(rewardType);
        this.player = player;
        this.identifier = identifier;
    }
    
    public RewardGiveEvent(Player player, Reward reward, Reward.RewardType rewardType, String identifier) {
        super(reward, rewardType);
        this.player = player;
        this.identifier = identifier;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}