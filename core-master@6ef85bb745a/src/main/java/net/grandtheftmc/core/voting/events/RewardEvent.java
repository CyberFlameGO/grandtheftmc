package net.grandtheftmc.core.voting.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.Reward.RewardType;

public abstract class RewardEvent extends Event implements Cancellable {
	
	/** The reward involve in this event */
	private Reward reward;
    private Reward.RewardType rewardType;
    private boolean cancelled;
    
    protected RewardEvent(Reward reward, RewardType rewardType) {
    	this.reward = reward;
        this.rewardType = rewardType;
    }

    protected RewardEvent(Reward.RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public Reward getReward() {
		return reward;
	}

	public Reward.RewardType getRewardType() {
        return rewardType;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}