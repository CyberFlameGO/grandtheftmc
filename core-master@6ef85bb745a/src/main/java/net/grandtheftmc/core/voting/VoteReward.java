package net.grandtheftmc.core.voting;

import com.j0ach1mmall3.jlib.methods.Parsing;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Liam on 23/04/2017.
 */
public class VoteReward {

    private final ItemStack item;
    private final RewardPack pack;
    private final double chance;

    public VoteReward(RewardPack pack, String item, double chance) {
        this.pack = pack;
        this.item = item == null ? null : Parsing.parseItemStack(item);
        this.chance = chance;
    }

    public String getName() {
        return this.pack.getName();
    }

    public ItemStack getItem() {
        return this.item == null ? this.pack.get().get(0).getDisplayItem() : this.item;
    }


    public String getDisplayName() {
        return this.pack.getDisplayName();
    }

    public RewardPack getRewardPack() {
        return this.pack;
    }

    public double getChance() {
        return this.chance;
    }

    public void give(Player player, User user) {
        if (chance != 100)
            Utils.broadcastExcept(player, Lang.VOTE.f(user.getColoredName(player) + "&7 won a rare reward while &e&lvoting&7: " + this.pack.getDisplayName() + "&7 (&a" + this.chance + "%)"));
        this.pack.give(player, user, chance == 100 ? null : "voteReward", 0, true);

    }
}
