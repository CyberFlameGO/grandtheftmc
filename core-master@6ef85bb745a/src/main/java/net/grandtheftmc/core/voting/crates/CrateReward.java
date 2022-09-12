package net.grandtheftmc.core.voting.crates;

import com.j0ach1mmall3.jlib.methods.Parsing;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.voting.RewardPack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Liam on 25/04/2017.
 */
public class CrateReward {

    private final ItemStack item;
    private final RewardPack pack;
    private final double weight;
    private final boolean announce;

    public CrateReward(RewardPack pack, String item, double weight, boolean announce) {
        this.pack = pack;
        this.item = item == null ? null : Utils.addItemFlags(Parsing.parseItemStack(item), ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        this.weight = weight;
        this.announce = announce;
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

    public double getWeight() {
        return this.weight;
    }

    public void give(Player player, User user, CrateStars rank) {
        if (this.announce)
            Utils.broadcastExcept(player, Lang.CRATES.f(user.getColoredName(player) + "&7 won a rare reward while opening a " + rank.getDisplayName() + "&7: " + this.pack.getDisplayName() + "&7!"));
        this.pack.give(player, user, this.announce ? null : "crateReward", rank.getCrowbars(), true);

    }
}
