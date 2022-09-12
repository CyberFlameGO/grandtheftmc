package net.grandtheftmc.core.voting;

import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Liam on 23/04/2017.
 */
public class RewardPack {

    private final String name;
    private final List<Reward> rewards;
    private final String description;

    public RewardPack(Reward reward, String description) {
        this.name = reward.getName();
        this.rewards = Collections.singletonList(reward);
        this.description = description;
    }

    public RewardPack(String name, List<Reward> rewards, String description) {
        this.name = name;
        this.rewards = rewards;
        this.description = description;
    }

    public List<Reward> get() {
        return this.rewards;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.rewards.size() == 1 ? this.rewards.get(0).getDisplayName() : this.name;
    }

    public String getDescription() {
        // todo i want this shown in the crate rewards menu/tokenshop menu if not null
        return this.description;
    }

    public boolean hasAllRewards(Player player, User user) {
        return this.rewards.stream().allMatch(r -> r.hasReward(player, user));
    }

    public boolean hasAnyReward(Player player, User user) {
        return this.rewards.stream().anyMatch(r -> r.hasReward(player, user));
    }

    public int hasAnyRewardSize(Player player, User user) {
        return this.rewards.stream().filter(r -> r.hasReward(player, user)).collect(Collectors.toList()).size();
    }

    public void give(Player player, User user, String action, double price, boolean sendMessage) {
        for (Reward reward : this.rewards)
            reward.give(player, user, sendMessage);
        if (action != null) {
            user.insertLog(player, action, this.rewards.size() > 1 ? "PACK" : this.rewards.size() == 1 ? this.rewards.get(0).getType().toString() : "ERROR", this.name, this.rewards.size() > 1 ? 1 : this.rewards.size() == 1 ? this.rewards.get(0).getAmount() : 1, price);
        }
    }
}
