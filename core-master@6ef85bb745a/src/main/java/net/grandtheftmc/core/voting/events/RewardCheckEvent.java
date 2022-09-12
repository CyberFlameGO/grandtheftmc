package net.grandtheftmc.core.voting.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.voting.Reward;

public class RewardCheckEvent extends RewardEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final String identifier;
    private boolean result;

    public RewardCheckEvent(Player player, Reward.RewardType rewardType, String identifier) {
        super(rewardType);
        this.player = player;
        this.identifier = identifier;
    }
    
    public RewardCheckEvent(Player player, Reward reward, Reward.RewardType rewardType, String identifier) {
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

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}