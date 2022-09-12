package net.grandtheftmc.core.events;

import net.grandtheftmc.core.voting.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Liam on 16/11/2016.
 */
public class RewardEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Reward reward;
    private boolean successful;

    public RewardEvent(Player player, Reward reward) {
        super(player);
        this.reward = reward;
    }

    public Reward getReward() {
        return this.reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setSuccessfull(boolean successful) {
        this.successful = successful;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
