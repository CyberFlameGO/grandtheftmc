package net.grandtheftmc.core.voting;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.events.RewardEvent;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.nametags.Nametag;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.events.RewardCheckEvent;
import net.grandtheftmc.core.voting.events.RewardGiveEvent;
import net.grandtheftmc.core.voting.events.RewardInfoEvent;

public class Reward {
    private final RewardType type;
    private final String name;

    private ItemStack[] items;
    private double amount = 1;
    private String customType;
    private String customName;
    private List<String> customList;
    private int minTokens;
    private int maxTokens;
    private UserRank rank;
    private int days;
    private Nametag nametag;
    private Achievement achievement;
    /** The skin id for the weapon */
    private short weaponSkinId;
    /** The weapon star rating to give */
    private int stars;

    public Reward(String name, ItemStack[] items) {
        this.name = name;
        this.items = items;
        this.type = RewardType.ITEMS;
    }

    public Reward(String name, RewardType type, String customName){
        this.name = name;
        this.type = type;
        this.customName = customName;
    }

    public Reward(String name, double amount, RewardType type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    public Reward(String name, String customType, String customName, double amount) {
        this.name = name;
        this.type = RewardType.CUSTOM;
        this.customType = customType;
        this.customName = customName;
        this.amount = amount;
    }

    public Reward(String name, String customType, List<String> customList) {
        this.name = name;
        this.type = RewardType.CUSTOM;
        this.customType = customType;
        this.customList = customList;
    }

    public Reward(String name, UserRank trialRank, int days) {
        this.type = RewardType.TRIAL_RANK;
        this.name = name;
        this.rank = trialRank;
        this.days = days;
    }

    public Reward(String name, UserRank permanentRank) {
        this.type = RewardType.RANK;
        this.name = name;
        this.rank = permanentRank;
    }

    public Reward(String name, Nametag nametag) {
        this.type = RewardType.NAMETAG;
        this.name = name;
        this.nametag = nametag;
    }

    public Reward(String name, String customName, RewardType rewardType) { // FOR COMMANDS AND PERMISSIONS, {uuid} and {name} placeholders
        this.type = rewardType;
        this.name = name;
        this.customName = customName;
    }

    public Reward(String name, Achievement achievement) {
        this.type = RewardType.ACHIEVEMENT;
        this.name = name;
        this.achievement = achievement;
    }
    
    public Reward(String name, String customName, int stars, short weaponSkinID, RewardType rewardType) { // FOR COMMANDS AND PERMISSIONS, {uuid} and {name} placeholders
        this.type = rewardType;
        this.name = name;
        this.customName = customName;
        this.stars = stars;
        this.weaponSkinId = weaponSkinID;
    }

    public RewardType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public  String getDisplayName() {
        switch (this.type) {
            case ITEMS:
                break;
            case BUCKS:
                return "&a&l" + (int) this.amount + " Bucks";
            case MONEY:
                return "&a$&l" + (int) this.amount;
            case TOKENS:
                return "&a&l" + (int) this.amount + "&e&l Token" + (this.amount > 1 ? "s" : "");
            case NAMETAG:
                return "&e&l" + this.nametag.getDisplayName();
            case RANK:
                return this.rank.getColor() + "&lPermanent " + this.rank.getColoredNameBold();
            case TRIAL_RANK:
                return "&a&l" + this.days + " day" + (this.days > 1 ? "s " : " ") + this.rank.getColoredNameBold() + "&a&l Trial";
            case CUSTOM:
                break;
//            case COSMETIC:
//                return this.cosmetic == null ? this.cosmeticType == null ? "&e&lRandom Cosmetic " +
//                        "&7(&e" + (this.maxTokens > 0 ? this.minTokens + '-' + this.maxTokens : "min " + this.minTokens) + " tokens&7)" :
//                        '&' + this.cosmeticType.getColor() + "&lRandom " + this.cosmeticType.getColoredDisplayNameSingle()
//                                + " &7(&e" + (this.maxTokens > 0 ? this.minTokens + '-' + this.maxTokens : "min " + this.minTokens) + " tokens&7)" :
//                        this.cosmetic.getColoredDisplayName();
            case PERMISSION:
                return this.name == null ? "&a&l" + this.customName : this.name;
            case CROWBARS:
                return "&9&l" + (int) this.amount + " Crowbar" + (this.amount > 1 ? "s" : "");
            case COMMAND:
                break;
            case ACHIEVEMENT:
                return this.achievement.getTitle();
            case WEAPON:
            case SKIN:
            	return "&9&l" + this.name;

        }
        return this.name;
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getCustomType() {
        return this.customType;
    }

    public String getCustomName() {
        return this.customName;
    }

    public List<String> getCustomList() {
        return this.customList;
    }

    public int getMinTokens() {
        return this.minTokens;
    }

    public int getMaxTokens() {
        return this.maxTokens;
    }

    public UserRank getTrialRank() {
        return this.rank;
    }

    public UserRank getRank() {
        return this.rank;
    }

    public int getDays() {
        return this.days;
    }

    public Nametag getNametag() {
        return this.nametag;
    }

    public String getPermission() {
        return this.customName;
    }

    public short getWeaponSkinId() {
		return weaponSkinId;
	}

	public int getStars() {
		return stars;
	}

	public ItemStack getDisplayItem() {
        switch (this.type) {
            case ITEMS:
                return this.items[0];
            case BUCKS:
            case MONEY:
                return new ItemStack(Material.PAPER, this.amount > 64 ? 64 : (int) this.amount);
            case TOKENS:
                return new ItemStack(Material.DOUBLE_PLANT, this.amount > 64 ? 64 : (int) this.amount);
            case CROWBARS:
                return Utils.addItemFlags(new ItemStack(Material.FLINT_AND_STEEL, this.amount > 64 ? 64 : (int) this.amount, (short) 45), ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
//            case COSMETIC:
//                return this.cosmeticType == null ? new ItemStack(Material.ENDER_CHEST) : this.cosmetic == null ? this.cosmeticType.getMenuItem() : this.cosmetic.getItem();
            case TRIAL_RANK:
                return new ItemStack(this.rank.getMaterial(), this.days > 64 ? 64 : this.days);
            case NAMETAG:
                return new ItemStack(Material.NAME_TAG);
            case RANK:
                return new ItemStack(this.rank.getMaterial());
            default:
                RewardInfoEvent rewardInfoEvent = new RewardInfoEvent(this, this.type, this.customName);
                Bukkit.getPluginManager().callEvent(rewardInfoEvent);
                return rewardInfoEvent.getDisplayItem();
        }
    }

    public boolean hasReward(Player player, User user) {
        switch (this.type) {
//            case COSMETIC:
//                return this.cosmetic == null ? this.cosmeticType == null ? Arrays.stream(CosmeticType.values()).allMatch(type -> type.getCosmetics().stream()
//                        .filter(c -> c.getTokens() > this.minTokens && (this.maxTokens <= 0 || c.getTokens() < this.maxTokens)).allMatch(user::hasCosmetic))
//                        : this.cosmeticType.getCosmetics().stream().filter(c -> c.getTokens() > this.minTokens && (this.maxTokens <= 0 || c.getTokens() < this.maxTokens)).allMatch(user::hasCosmetic)
//                        : user.hasCosmetic(this.cosmetic);
            case RANK:
                return user.getUserRankNonTrial() == this.rank || user.getUserRankNonTrial().isHigherThan(this.rank);
            case TRIAL_RANK:
                return user.getUserRankNonTrial() == this.rank || user.getUserRank().isHigherThan(this.rank);
            case NAMETAG:
                return user.hasNametag(this.nametag);
            case PERMISSION:
                if (Core.getPermsManager().hasPerm(player.getUniqueId(), this.customName)) return true;
            case ACHIEVEMENT:
                return user.hasAchievement(this.achievement);
            default:
                RewardCheckEvent rewardCheckEvent = new RewardCheckEvent(player, this, this.type, this.customName);
                Bukkit.getPluginManager().callEvent(rewardCheckEvent);
                return rewardCheckEvent.getResult();
        }
    }

    public void give(Player player, User user, boolean sendMessage) {
    	
        switch (this.type) {
            case ITEMS:
                if (sendMessage)
                    player.sendMessage(Lang.REWARDS.f("&a" + this.getDisplayName()));
                if (Utils.giveItems(player, this.items))
                    player.sendMessage(Utils.
                            f(Lang.TOKEN_SHOP + "&7Your inventory was full so some items were dropped on the ground!"));
                break;
            case BUCKS: {
                if (sendMessage) {
                    player.sendMessage(Lang.BUCKS_ADD.f(String.valueOf((int) this.amount)));
                }
                int amnt = (int) this.amount;
                user.addBucks(amnt);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.BUCKS));
                break;
            }
            case TOKENS: {
                if (sendMessage) {
                    player.sendMessage(Lang.TOKENS_ADD.f(String.valueOf((int) this.amount)));
                }
                int amnt = (int) this.amount;
                user.addTokens(amnt);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS));
                break;
            }
            case CROWBARS:
                if (sendMessage) {
                    player.sendMessage(Lang.CROWBARS_ADD.f(String.valueOf((int) this.amount)));
                }
                int amnt = (int) this.amount;
                user.addCrowbars(amnt);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.CROWBARS));
                break;
            case MONEY:
                if (sendMessage) {
                    player.sendMessage(Lang.MONEY_ADD.f(String.valueOf(this.amount)));
                }
                if (!user.addMoney(this.amount)) {
                    Core.log(Lang.TOKEN_SHOP.f(
                            "&cOops " + player.getName() + "! Something went wrong with getting the reward:" + getCustomName() + "! Contact an admin with a screenshot of this message to get it. Time: " + new Date().toGMTString()));
                }
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.MONEY));
                break;
            case CUSTOM:
                if (sendMessage)
                    player.sendMessage(Lang.REWARDS.f("&a" + this.getDisplayName()));
                RewardEvent event = new RewardEvent(player, this);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isSuccessful())
                    Core.log(Lang.TOKEN_SHOP.f(
                            "&cOops " + player.getName() + "! Something went wrong with getting the reward:" + getCustomName() + "! Contact an admin with a screenshot of this message to get it. Time: " + new Date().toGMTString()));
                break;
//            case COSMETIC:
//                Cosmetic c = this.cosmetic;
//                if (c == null) {
//                    if (this.cosmeticType == null) {
//                        List<Cosmetic> cosmetics = new ArrayList<>();
//                        Arrays.stream(CosmeticType.values()).forEach(t -> cosmetics.addAll(t.getCosmetics().stream().filter(co -> !user.hasCosmetic(co) && co.getTokens() > 0 && co.getTokens() > this.minTokens && (this.maxTokens < 0 || co.getTokens() <= this.maxTokens)).collect(Collectors.toList())));
//                        if (cosmetics.isEmpty()) {
//                            player.sendMessage(Lang.COSMETICS.f("Wow! You already own ALL cosmetics!"));
//                            break;
//                        }
//                        c = cosmetics.get(ThreadLocalRandom.current().nextInt(cosmetics.size()));
//                    } else {
//                        List<Cosmetic> cosmetics = this.cosmeticType.getCosmetics().stream().filter(co -> !user.hasCosmetic(co)).collect(Collectors.toList());
//                        c = cosmetics.get(ThreadLocalRandom.current().nextInt(cosmetics.size()));
//                        if (c == null) {
//                            player.sendMessage(Lang.COSMETICS.f("Wow! You already own ALL " + this.cosmeticType.getDisplayName() + "s!"));
//                            break;
//                        }
//                    }
//                }
//                if (sendMessage)
//                    player.sendMessage(Lang.COSMETICS.f("&a" + this.getDisplayName() + this.cosmetic == null ? "&7: " + c.getColoredDisplayName() : ""));
//                user.giveCosmetic(player, this.cosmetic);
//                break;
            case TRIAL_RANK:
                if (user.hasTrialRank() && user.getTrialRank().isHigherThan(this.rank)) {
                    if (sendMessage)
                        player.sendMessage(Lang.RANKS.f("&7You already have a trial rank higher than the one you won (" + this.rank.getColoredNameBold() + "&7, &a" + this.days + "&7 days)!"));
                    return;
                }
                if (user.hasTrialRank() && user.getTrialRank() == this.rank) {
                    if (sendMessage)
                        player.sendMessage(Lang.RANKS.f("&a" + this.getDisplayName() + " &7(extended)"));
                    user.setTrialRank(this.rank, user.getTrialRankExpiry() + 86400000L * this.days);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                }
                if (sendMessage)
                    player.sendMessage(Lang.RANKS.f("&a" + this.getDisplayName()));
                user.setTrialRank(this.rank, System.currentTimeMillis() + 86400000L * this.days);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                break;
            case NAMETAG:
                if (sendMessage)
                    player.sendMessage(Lang.NAMETAGS.f("&a" + this.getDisplayName()));
                user.giveNametag(this.nametag);
                break;
            case PERMISSION:
                if (sendMessage)
                    player.sendMessage(Lang.REWARDS.f("&a" + this.getDisplayName()));
                Core.getPermsManager().addPerm(player.getUniqueId(), this.customName);
                break;
            case RANK:
                if (sendMessage)
                    player.sendMessage(Lang.RANKS.f("&a" + this.getDisplayName()));
                user.setUserRank(this.rank);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                break;
            case COMMAND:
                if (sendMessage)
                    player.sendMessage(Lang.REWARDS.f("&a" + this.getDisplayName()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.customName.replace("{name}", player.getName()).replace("{uuid}", player.getUniqueId().toString()));
                break;
            case ACHIEVEMENT:
                if (sendMessage) {
                    player.sendMessage(Lang.REWARDS.f("&aACHIEVEMENT " + this.achievement.getTitle()));
                }
                user.addAchievement(this.achievement);
                break;
            case WEAPON:
            case SKIN:
            	if (sendMessage)
                    player.sendMessage(Lang.REWARDS.f("&a" + this.getDisplayName()));
                event = new RewardEvent(player, this);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isSuccessful())
                    Core.log(Lang.REWARDS.f(
                            "&cOops " + player.getName() + "! Something went wrong with getting the reward:" + getCustomName() + "! Contact an admin with a screenshot of this message to get it. Time: " + new Date().toGMTString()));
                break;
            default:
                RewardGiveEvent rewardGiveEvent = new RewardGiveEvent(player, this.type, this.customName);
                Bukkit.getPluginManager().callEvent(rewardGiveEvent);
                break;
        }
    }

    public enum RewardType {
        ITEMS, BUCKS, TOKENS, MONEY, CUSTOM, COSMETIC, TRIAL_RANK, NAMETAG, PERMISSION, CROWBARS, RANK, COMMAND, ACHIEVEMENT,
        VEHICLE, CHEATCODE, WEAPON, SKIN;

        public static RewardType fromString(String string) {
            return Arrays.stream(RewardType.class.getEnumConstants()).filter(type -> type.toString().equalsIgnoreCase(string)).findFirst().orElse(null);
        }

    }

}
