package net.grandtheftmc.core.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.dao.VoteDAO;
import net.grandtheftmc.core.giftcard.GiftcardAPI;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.playwire.PlaywireManager;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.servers.ServerManager;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.users.eventtag.EventTagDAO;
import net.grandtheftmc.core.users.eventtag.TagVisibility;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.RewardPack;
import net.grandtheftmc.core.voting.ShopItem;
import net.grandtheftmc.core.voting.VoteManager;
import net.grandtheftmc.core.voting.VoteReward;
import net.grandtheftmc.core.voting.crates.Crate;
import net.grandtheftmc.core.voting.crates.CrateReward;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisModule;
import net.grandtheftmc.jedis.message.ServerQueueMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("NestedSwitchStatement")
public class MenuListener implements Listener {

    @EventHandler
    public void onOpenMenu(MenuOpenEvent e) {
        Menu menu = e.getMenu();
        Player player = e.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
//        CosmeticType cosmeticType = CosmeticType.getType(menu.getName());
        /*if (Core.getSettings().loadCosmetics() && cosmeticType != null && cosmeticType.isEnabled(Core.getSettings().getType())) {
            this.setPhoneDefaults(e);
            int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
            List<Cosmetic> cosmetics = cosmeticType.getCosmetics(user);
            for (int i = 0; i < slots.length && i < cosmetics.size(); i++) {
                Cosmetic cos = cosmetics.get(i);
                e.setItem(slots[i], cos.getMenuItem(user));
            }
            e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the cosmetics menu!"));
            e.setItem(49, Utils.createItem(cosmeticType.getMaterial(), "&6&lCosmetics: " + cosmeticType.getDisplayName(), "&7Page 1"));
            if (cosmetics.size() > 20)
                e.setItem(50, Utils.createItem(Material.ARROW, "&6&lNext Page", "&7Page 2"));
            e.setItem(51, Utils.createItem(Material.WEB, "&c&lRemove Cosmetic", "&7Click to remove your current " + cosmeticType.toString().charAt(0) + cosmeticType.toString().substring(1).toLowerCase()));
            return;
        }*/
        switch (menu.getName()) {
            case "freecoupons": {
                this.setPhoneDefaults(e);
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.FLOOR);
                ItemStack is = new ItemStack(Material.PAPER);
                is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                ItemMeta im = is.getItemMeta();
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Your Ad Credits:&b " + user.getCouponCredits()));
                im.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Click to get the credit link")));
                is.setItemMeta(im);

                e.setItem(13, is);
                e.setItem(21, Utils.createItem(Material.GOLD_NUGGET, "&6Convert &b50 &6Credits:", "&a$0.10"));
                e.setItem(22, Utils.createItem(Material.GOLD_INGOT, "&6Convert &b100 &6Credits:", "&a$0.20"));
                e.setItem(23, Utils.createItem(Material.GOLD_BLOCK, "&6Convert &b500 &6Credits:", "&a$1.00"));
                e.setItem(31, Utils.createItem(Material.DIAMOND, "&6Convert &bAll &6Credits:", "&a$" + (df.format(user.getCouponCredits()/500.0))));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the rewards menu!"));
                break;
            }
            case "chooseeventtag": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24,29,30,31,32,33};
                int selector = 0;
                for(EventTag t : EventTag.values()) {
                    TagVisibility v = EventTagDAO.getTagVisibility(t);
                    if(v==TagVisibility.NO_ONE)
                        continue;
                    boolean unlocked = user.getUnlockedTags().contains(t);
                    if(v==TagVisibility.THOSE_WHO_HAVE_IT_UNLOCKED && !unlocked)
                        continue;

                    int slot = slots[selector];
                    boolean activated = user.getEquipedTag() == t;
                    if(unlocked) {
                        e.setItem(slot, Utils.createItem(Material.NAME_TAG, t.getBoldName(), "&6&lStatus: " + (activated ? "&a&lOn" : "&c&lOff")));
                    }
                    else
                        e.setItem(slot, Utils.createItem(Material.NAME_TAG, t.getBoldName(), "&6&lStatus: &4&lLOCKED"));
                    selector++;
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to your account!"));
                return;
            }
            case "topvoters": {
                this.setPhoneDefaults(e);

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the vote menu!"));

                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};

                ServerUtil.runTaskAsync(() -> {
                    VoteDAO.VoteUser[] topVoters = Core.getInstance().getVoteManager().getTopVoters();
                    if (topVoters == null) return;

                    List<ItemStack> items = new ArrayList<>();

                    for (VoteDAO.VoteUser voteUser : topVoters) {
                        ItemStack is = Utils.createItem(Material.SKULL_ITEM, 3, "&7#&6" + voteUser.getPossition() + "&7 " + voteUser.getName());
                        SkullMeta im = (SkullMeta) is.getItemMeta();
                        im.setOwner(voteUser.getName());
                        List<String> lore = new ArrayList<>();
                        lore.add(Utils.f("&6Votes: &a&l" + voteUser.getVotes()));
                        String reward = null;
                        switch (voteUser.getPossition()) {
                            case 1:
                                reward = "&6Expected Prize: &a$&l100 Store Credit";
                                break;
                            case 2:
                            case 3:
                                reward = "&6Expected Prize: &a$&l50 Store Credit";
                                break;
                            case 4:
                            case 5:
                                reward = "&6Expected Prize: &a$&l25 Store Credit";
                                break;
                        }
                        if (reward != null) lore.add(Utils.f(reward));
                        im.setLore(lore);
                        is.setItemMeta(im);
                        items.add(is);
                    }

                    ServerUtil.runTask(() -> {
                        for (int i = 0; i < items.size(); i++)
                            e.getMenu().getInventory().setItem(slots[i], items.get(i));

                        player.updateInventory();
                    });
                });

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultSet rs = Core.sql.prepareStatement("SELECT * FROM votes ORDER BY `votes`.`monthlyVotes` DESC LIMIT 10;").executeQuery();
//                            int counter = 1;
//                            List<ItemStack> items = new ArrayList<>();
//                            while (rs.next()) {
//                                String name = rs.getString("name");
//                                int votes = rs.getInt("monthlyVotes");
//                                ItemStack is = Utils.createItem(Material.SKULL_ITEM, 3, "&7#&6" + counter + "&7 " + name);
//                                SkullMeta im = (SkullMeta)is.getItemMeta();
//                                im.setOwner(name);
//                                List<String> lore = new ArrayList<>();
//                                lore.add(Utils.f("&6Votes: &a&l" + votes));
//                                String reward = null;
//                                switch (counter) {
//                                    case 1:
//                                        reward = "&6Expected Prize: &a$&l100 Store Credit";
//                                        break;
//                                    case 2:
//                                    case 3:
//                                        reward = "&6Expected Prize: &a$&l50 Store Credit";
//                                        break;
//                                    case 4:
//                                    case 5:
//                                        reward = "&6Expected Prize: &a$&l25 Store Credit";
//                                        break;
//                                }
//                                if(reward!=null) {
//                                    lore.add(Utils.f(reward));
//                                }
//                                im.setLore(lore);
//                                is.setItemMeta(im);
//                                items.add(is);
//                                counter++;
//                            }
//                            new BukkitRunnable() {
//                                @Override
//                                public void run() {
//                                    for(int i = 0; i < items.size(); i++) {
//                                        e.getMenu().getInventory().setItem(slots[i], items.get(i));
//                                    }
//                                    player.updateInventory();
//                                }
//                            }.runTask(Core.getInstance());
//                        } catch (SQLException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
            }
            case "confirmcratereward": {
                Crate crate = user.getSelectedCrate();
                CrateReward reward = user.getConfirmingCrateReward();
                if (crate == null || reward == null) {
                    player.closeInventory();
                    return;
                }
                List<String> confirmLore = new ArrayList<>(reward.getRewardPack().get().stream().map(Reward::getDisplayName).collect(Collectors.toList()));
                this.setConfirmDefaults(e, "&a&lClick to accept this reward:", "&c&lClick to cancel this reward and get refunded: &9&l" + crate.getCrateStars().getStars() + " crowbars&c&l.", confirmLore, null);
                return;
            }
            case "confirmexpensivecrate":
                Crate crate = user.getSelectedCrate();
                this.setConfirmDefaults(e, "&a&lClick to spend &9&l" + crate.getCrateStars().getCrowbars() + " Crowbar" + (crate.getCrateStars().getCrowbars() == 1 ? "" : "s") + "&a&l to open this " + crate.getCrateStars().getDisplayName() + "&7.", "&c&lCancel opening this " + crate.getCrateStars().getType() + "&7.");
                return;
            case "cosmetics":
                return;
                /*if (!Core.getSettings().loadCosmetics()) return;
                this.setPhoneDefaults(e);
                List<CosmeticType> types = Arrays.stream(new CosmeticType[]{CosmeticType.BLOCK, CosmeticType.HAT, CosmeticType.MORPH, CosmeticType.PARTICLE, CosmeticType.PET}).filter(t -> t.isEnabled(Core.getSettings().getType())).collect(Collectors.toList());
                int[] slots = types.size() > 6 ? new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42} : new int[]{11, 13, 15, 29, 31, 33};
                for (int i = 0; i < types.size(); i++) {
                    CosmeticType t = types.get(i);
                    e.setItem(slots[i], Utils.createItem(t.getMaterial(), t.getColoredDisplayName(), t.getDescription()));
                }
                e.setItem(49, Utils.createItem(Material.ENDER_CHEST, "&6&lCosmetics", "&7Click to select a cosmetic type!"));
                //e.setItem(51, Utils.createItem(Material.NAME_TAG, "&a&lNametags", "&7Click to apply nametags!"));
                return;*/
            case "buycosmetic":
                return;
                /*Cosmetic c = user.getBuyingCosmetic();
                if (c == null || user.hasCosmetic(c) || c.getTokens() < 0 || !user.hasTokens(c.getTokens())) return;
                this.setConfirmDefaults(e, "&a&lBuy " + c.getColoredDisplayName() + ' ' + c.getType().toString().charAt(0) + c.getType().toString().substring(1).toLowerCase() + " &a&lfor &e&l" + c.getTokens() + " Tokens", "&c&lCancel");
                return;*/
            case "petinfo":
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.CHEST, 3, "&c&lPet Data", "&7Click customize your pet!"));
                e.setItem(15, Utils.createItem(Material.BANNER, "&c&lPet Name", "&7Click to change your pet's name!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the pet menu!"));
                e.setItem(49, Utils.createItem(Material.LEASH, "&c&lGuard Pet Customizer", "&7Customize your friendly follower!"));
                return;
            /*case "petdata": {
                this.setPhoneDefaults(e);
                IPet pet = EchoPetAPI.getAPI().getPet(player);
                if (pet == null) {
                    player.closeInventory();
                    return;
                }
                List<PetData> list = pet.getPetType().getAllowedDataTypes();
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<PetData> it = list.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext()) break;
                    PetData data = it.next();
                    String name = "&c&l";
                    for (String s : data.toString().split("_"))
                        name += s.charAt(0) + s.substring(1).toLowerCase() + ' ';
                    if (name.endsWith(" ")) name = name.substring(0, name.length() - 1);
                    e.setItem(slots[i], Utils.createItem(Material.INK_SACK, pet.getPetData().contains(data) ? 10 : 8, name));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the pet customizer!"));
                e.setItem(49, Utils.createItem(Material.CHEST, "&c&lGuard Pet Data", "&7Customize your friendly follower!"));
                return;
            }*/
//            case "bannervariant":
//                this.setPhoneDefaults(e);
//                e.setItem(11, Utils.createItem(Material.SKULL_ITEM, 3, "&4&lBanner Hat", "&7Click to put the banner on your head!"));
//                e.setItem(15, Utils.createItem(Material.BANNER, "&4&lBanner Cape", "&7Click to wear the banner on your back!"));
//                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the banner menu!"));
//                e.setItem(49, Utils.createItem(CosmeticType.BANNER.getMaterial(), CosmeticType.BANNER.getDisplayName() + " Hat or Cape", "&7Click to select where to equip the banner!"));
//                return;
//            case "blockvariant":
//                this.setPhoneDefaults(e);
//                e.setItem(11, Utils.createItem(Material.LEASH, "&2&lBalloon", "&7Click to get the block on a piece of string!"));
//                e.setItem(15, Utils.createItem(Material.MONSTER_EGG, 3, "&2&lBlock Pet", "&7Click to get the block to follow you as a pet!"));
//                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the blocks menu!"));
//                e.setItem(49, Utils.createItem(CosmeticType.BANNER.getMaterial(), "&2&lBalloon or Block Pet", "&7Click to select how the block should follow you!"));
//                return;
//            case "particleshape": {
//                this.setPhoneDefaults(e);
//                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
//                Cosmetic.ParticleShape[] a = Cosmetic.ParticleShape.values();
//                for (int i = 0; i < slots.length && i < a.length; i++) {
//                    Cosmetic.ParticleShape shape = a[i];
//                    e.setItem(slots[i], Utils.createItem(Material.INK_SACK, i, "&a&l" + shape.getDisplayName(), "&7Click to select this shape!"));
//                }
//                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Click to return to the particles menu!"));
//                e.setItem(49, Utils.createItem(CosmeticType.PARTICLE.getMaterial(), "&a&lParticle Shape", "&7Click to select the particles' shape!"));
//                return;
//            }
//            case "nametags": {
//                this.setPhoneDefaults(e);
//                List<Nametag> list = Core.getNametagManager().getNametags(user);
//                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
//                Iterator<Nametag> it = list.iterator();
//                for (int i = 0; i < 20; i++) {
//                    if (!it.hasNext()) break;
//                    Nametag tag = it.next();
//                    e.setItem(slots[i], Utils.createItem(Material.NAME_TAG, tag.getDisplayName(), user.hasNametag(tag) ? "&7Click to apply this nametag!" : "&7Price: &e&l" + tag.getPrice() + " Tokens"));
//                }
//                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the cosmetics menu!"));
//                e.setItem(49, Utils.createItem(Material.CHEST, "&a&lNametags", "&7Select a nametag to apply to a pet!", "&7Soon, you will be able to apply nametags to", "&7your player character, vehicles and weapons.", "&7Page 1"));
//                if (list.size() > 20)
//                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
//
//                e.setItem(51, Utils.createItem(Material.WEB, "&c&lRemove Nametag", "&7Click to remove a nametag from a pet."));
//                return;
//            }
//            case "buynametag": {
//                Nametag tag = user.getBuyingNametag();
//                if (tag == null || !user.hasTokens(tag.getPrice())) return;
//                this.setConfirmDefaults(e, "&a&lBuy " + tag.getDisplayName() + "&a&l for &e&l" + tag.getPrice() + " Token" + (tag.getPrice() == 1 ? "" : "s"), "&c&lCancel");
//                return;
//            }
//            case "applynametag":
//                this.setPhoneDefaults(e);
//                Nametag tag = user.getActivatingNametag();
//                if (tag == null || !user.hasNametag(tag)) return;
//                Cosmetic pet = user.getLastPet(player);
//                if (pet == null)
//                    e.setItem(11, Utils.createItem(Material.LEASH, "&c&lGuard Pet", "&7You have no pet to name!"));
//                else e.setItem(11, pet.getMenuItem(user));
//                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lCancel", "&7Return to the nametags menu!"));
//                e.setItem(49, Utils.createItem(Material.NAME_TAG, tag.getDisplayName(), "&7Please select what you would like to name."));
//                return;
            case "prefs": {
                this.setPhoneDefaults(e);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                ServerType type = Core.getSettings().getType();
                int[] slots = new int[]{11, 12, 13, 14, 15, 29, 30, 31, 32, 33};

                int i = 0;
                for (Pref pref : Pref.values())
                    if (pref.isEnabled(player, u, type)) {
                        boolean b = u.getPref(pref);
                        ItemStack achievementItem = Utils.createItem(pref.getMaterial(), pref.getMaterialData(),
                                (b ? "&a" : "&c") + "&l" + pref.getDisplayName(), "&7Click to " + (b ? "disable." : "enable."));
                        Utils.applyItemFlags(achievementItem, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                        e.setItem(slots[i], achievementItem);
                        e.setItem(slots[i] + 9, Utils.createItem(Material.INK_SACK, b ? 10 : 8, (b ? "&a" : "&c") + "&l" + pref.getDisplayName(), "&7Click to " + (b ? "disable." : "enable.")));
                        i++;
                    }
                e.setItem(49, Utils.createItem(Material.REDSTONE_COMPARATOR, "&5&lPreferences", "&7Toggle your " + (Core.getSettings().isSister() ? "stuff" : "shit") + "!"));
                return;
            }
            case "rewards": {
                this.setPhoneDefaults(e);

                if(!Core.getSettings().isSister()) {
                    e.setItem(11, Utils.createItem(Material.DOUBLE_PLANT, "&e&lVote", user.getVotes(), "&7Earn awesome rewards!", "", "&7Total Votes: &e&l" + user.getVotes(), "", "&7Vote to get more rewards!"));
                    e.setItem(20, Utils.createItem(Material.INK_SACK, user.getVotes() > 0 ? 10 : 8, "&e&lVote Streak", user.getVoteRecord().getStreak() > 64 ? 64 : user.getVoteRecord().getStreak(),
                            "&7Vote at least once a day to", "&7increase your streak! ", "&7Every day gives you an extra 5%", "&7chance to get a double reward!", "&7Vote on all &e4 &7sites for maximum rewards!",
                            "", "&7Current Streak: &a&l" + user.getVoteRecord().getStreak(), "&7Double Reward Chance: &a&l" + user.getDoubleVoteChance() + '%', "",
                            user.canVoteStreak() ? "&7Vote &a&lnow&7 to raise your streak!" : "&7Time Left: &c&l" + Utils.timeInMillisToText(user.getTimeUntilVoteStreak().isPresent() ? user.getTimeUntilVoteStreak().get().getTime() : 0)));

                    e.setItem(22, Utils.createItem(Material.PAPER, "&a&lFree Store Giftcards", "&7Earn free store giftcards by", "&7watching ads! "));
                }

                UserRank rank = user.getUserRankNonTrial().isHigherThan(UserRank.SUPREME) ? UserRank.SUPREME : user.getUserRankNonTrial();
                boolean canClaimMonthly = user.canClaimMonthlyReward();
                if (rank == UserRank.DEFAULT) {
                    List<String> lore = new ArrayList<>(Arrays.asList("&7Buy a &6&lDonor Rank&7 at", "&a&n" + Core.getSettings().getStoreLink(), "", "&7to get monthly &e&lTokens&7!", ""));
                    for (UserRank r : new UserRank[]{UserRank.VIP, UserRank.PREMIUM, UserRank.ELITE, UserRank.SPONSOR, UserRank.SUPREME})
                        lore.add(r.getColoredNameBold() + "&7: &e&l" + r.getMonthlyTokens() + " Tokens");
                    Map<UserRank, List<RewardPack>> monthly = Core.getVoteManager().getMonthlyRewards();
                    if (!monthly.isEmpty()) {
                        lore.add("");
                        lore.add("&7Extra rewards:");
                        for (Map.Entry<UserRank, List<RewardPack>> userRankListEntry : monthly.entrySet()) {
                            lore.addAll(userRankListEntry.getValue().stream().map(r -> rank.getColoredNameBold() + "&7: " + r.getDisplayName()).collect(Collectors.toList()));
                        }
                    }
                    e.setItem(13, Utils.createItem(Material.COAL, "&c&lDonor Reward", lore));
                } else {
                    List<String> lore = new ArrayList<>(Arrays.asList("&7As a donator you are entitled to", "&e&l" + rank.getMonthlyTokens() + " Tokens&7 every month!", "&7Upgrade your rank for more!", "",
                            canClaimMonthly ? "&aClick to claim!" : "&7Next Reward: &c&l" + Utils.timeInMillisToText(user.getTimeUntilMonthlyReward())));
                    Map<UserRank, List<RewardPack>> monthly = Core.getVoteManager().getMonthlyRewards();
                    if (monthly.containsKey(rank)) {
                        lore.add("");
                        lore.add("&7Extra rewards:");
                        lore.addAll(monthly.get(rank).stream().map(RewardPack::getDisplayName).collect(Collectors.toList()));
                    }
                    e.setItem(13, Utils.createItem(canClaimMonthly ? rank.getMaterial() : Material.COAL, rank.getColoredNameBold() + " Reward",
                            lore));
                }
                List<String> lore = new ArrayList<>(Arrays.asList("&7Come back every day to claim:", "&e&l2 Tokens"));
                lore.addAll(Core.getVoteManager().getDailyRewards().stream().map(RewardPack::getDisplayName).collect(Collectors.toList()));
                lore.add("");
                lore.add(user.canClaimDailyReward() ? "&aClick to claim!" : "&7Next Reward: &c&l" + Utils.timeInMillisToText(user.getTimeUntilDailyReward()));
                e.setItem(15, Utils.createItem(Material.NETHER_STAR, "&a&lDaily Reward", lore));
                lore = new ArrayList<>(Arrays.asList("&7Claim your reward daily to", "&7increase your streak! ", "&7Every day gives you an extra 5%", "&7chance to increase your reward to:", "&e&l5 Tokens"));
                lore.addAll(Core.getVoteManager().getLuckyDailyRewards().stream().map(RewardPack::getDisplayName).collect(Collectors.toList()));
                lore.add("");
                lore.add("&7Current Streak: &a&l" + user.getDailyStreak());
                lore.add("&7Lucky Chance: &a&l" + user.getLuckyDailyChance() + '%');
                lore.add("");
                lore.add(user.canClaimDailyReward() ? "&7Claim &a&lnow&7 to raise your streak!" : "&7Next Reward: &c&l" + Utils.timeInMillisToText(user.getTimeUntilDailyReward()));
                e.setItem(24, Utils.createItem(Material.INK_SACK, user.canClaimDailyReward() ? 10 : 8, "&a&lDaily Reward Streak", lore, user.getDailyStreak() > 64 ? 64 : user.getDailyStreak()));
                e.setItem(49, Utils.createItem(Material.EXP_BOTTLE, "&a&lRewards", "&7Click to claim your rewards!"));
                return;
            }
            case "vote": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
                VoteManager vm = Core.getVoteManager();
                int i = 0;
                for (VoteReward reward : vm.getChanceVoteRewards()) {
                    List<String> l = new ArrayList<>();
                    l.add("");
                    l.add(Utils.f("&7Chance: &a&l" + reward.getChance() + "&a%"));
                    if (reward.getRewardPack().get().size() > 1) {
                        l.add(Utils.f("&7Contains rewards:"));
                        l.addAll(reward.getRewardPack().get().stream().map(r -> Utils.f(r.getDisplayName())).collect(Collectors.toList()));
                    }
                    l.add("");
                    ItemStack item = reward.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(Utils.f(reward.getDisplayName()));
                    meta.setLore(l);
                    item.setItemMeta(meta);
                    Utils.applyItemFlags(item, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                    e.setItem(slots[i], item);
                    i++;
                }
                //

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        Optional<VoteDAO.VoteUser> voteUser = VoteDAO.getTopVoter();
//                        if(!voteUser.isPresent()) return;
//
//                        ItemStack is = Utils.createItem(Material.SKULL_ITEM, 3, "&e&lThis Month's Top Voters", "&7The top voters of the month can win up to &a$&l100 Store Credit&7!");
//                        SkullMeta meta = (SkullMeta) is.getItemMeta();
//                        meta.setOwner(voteUser.get().getName());
//                        is.setItemMeta(meta);
//
//                        ServerUtil.runTask(() -> {
//                            e.getMenu().getInventory().setItem(39, is);
//                            player.updateInventory();
//                        });
//
//                        ItemStack item = Utils.createItem(Material.SKULL_ITEM, 3, "&e&lLast Month's Top Voters");
//                        SkullMeta im = (SkullMeta) item.getItemMeta();
//                        List<String> lore = new ArrayList<>();
//
//                        Optional<VoteDAO.VoteUser[]> optional = VoteDAO.getLastMonthsVoters();
//                        if(!optional.isPresent()) return;
//
//                        for(VoteDAO.VoteUser voter : optional.get()) {
//                            im.setOwner(voter.getName());
//                            lore.add(Utils.f("&7#&6" + voter.getPossition() + "&7 " + voter.getName()));
//                        }
//
//                        im.setLore(lore);
//                        item.setItemMeta(im);
//
//                        ServerUtil.runTask(() -> {
//                            e.getMenu().getInventory().setItem(41, item);
//                            player.updateInventory();
//                        });
//                    }
//                }.runTaskAsynchronously(Core.getInstance());

                ServerUtil.runTaskAsync(() -> {
                	VoteDAO.VoteUser[] topVoters = Core.getInstance().getVoteManager().getTopVoters();
                    if(topVoters == null || topVoters.length == 0) return;
                    
                    // get the first place guy
                    VoteDAO.VoteUser topVoter = topVoters[0];

                    ItemStack is = Utils.createItem(Material.SKULL_ITEM, 3, "&e&lThis Month's Top Voters", "&7The top voters of the month can win up to &a$&l100 Store Credit&7!");
                    SkullMeta meta = (SkullMeta) is.getItemMeta();
                    meta.setOwner(topVoter.getName());
                    is.setItemMeta(meta);

                    ServerUtil.runTask(() -> {
                        e.getMenu().getInventory().setItem(39, is);
                        player.updateInventory();
                    });

                    ItemStack item = Utils.createItem(Material.SKULL_ITEM, 3, "&e&lLast Month's Top Voters");
                    SkullMeta im = (SkullMeta) item.getItemMeta();
                    List<String> lore = new ArrayList<>();

                    VoteDAO.VoteUser[] lastMonthsVoters = Core.getInstance().getVoteManager().getLastTopVoters();
                    if(lastMonthsVoters == null || lastMonthsVoters.length == 1) return;

                    for(VoteDAO.VoteUser voter : lastMonthsVoters) {
                    	if (voter != null){
                    		im.setOwner(voter.getName());
                            lore.add(Utils.f("&7#&6" + voter.getPossition() + "&7 " + voter.getName()));
                    	}
                    }

                    im.setLore(lore);
                    item.setItemMeta(im);

                    ServerUtil.runTask(() -> {
                        e.getMenu().getInventory().setItem(41, item);
                        player.updateInventory();
                    });
                });

                //

                ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta im = (SkullMeta) is.getItemMeta();
                YearMonth lastMonthStr = YearMonth.now();
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
                im.setDisplayName(Utils.f("&e&lYour Votes"));
                im.setLore(Arrays.asList(Utils.f("&eTotal Votes Ever: &7" + lastMonthStr.format(monthFormatter)), Utils.f("&eVotes: &7" + user.getVoteRecord().getTotalVotes())));
                im.setOwner(player.getName());
                is.setItemMeta(im);
                e.setItem(40, is);


                e.setItem(i > 9 ? 38 : 29, Utils.createItem(Material.EMPTY_MAP,
                        "&e&lVote Link", "&7Click to go to the voting webpage!", "", "&a&nvote.grandtheftmc.net"));
                List<String> lore = new ArrayList<>(Arrays.asList("&7With every vote you are guaranteed",
                        "&7to receive these items:"));
                lore.addAll(vm.getGuaranteedVoteRewards().stream().map(r -> "&7" + r.getDisplayName()).collect(Collectors.toList()));
                lore.addAll(Arrays.asList("&7Get lucky and receive extra rare rewards!", "", "&7Votes: &e&l" + user.getVotes(), ""));

                if (user.getVotes() > 1)
                    lore.addAll(Arrays.asList("&aRight click to claim all votes!", "&aLeft click to claim 1 vote!"));
                else lore.add(user.getVotes() == 1 ? "&aClick to claim your rewards!" : "&7Vote on all &e4 &7sites for maximum rewards!");
                e.setItem(i > 9 ? 40 : 31, Utils.createItem(Material.CHEST, "&e&lVoting Rewards", lore));
                e.setItem(i > 9 ? 42 : 33, Utils.addItemFlags(Utils.createItem(Material.FLINT_AND_STEEL, 45,
                        "&9&lCrowbars: " + user.getCrowbars(), "&7Crowbars are used to open crates.", "",
                        "&7&oFind crates at spawn near the teleporter!"), ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));
                // TODO figure out if this removes durability bar

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the rewards menu!"));
                lore = new ArrayList<>(Arrays.asList("&7Earn awesome rewards!", "", "&7Votes: &e&l" + user.getVotes(), ""));
                if (user.getVotes() > 1)
                    lore.addAll(Arrays.asList("&aRight click to claim all votes!", "&aLeft click to claim 1 vote!"));
                else lore.add(user.getVotes() == 1 ? "&aClick to claim your rewards!" : "&7Vote on all &e4 &7sites  for maximum rewards!");
                e.setItem(49, Utils.createItem(Material.DOUBLE_PLANT, "&e&lVote", lore, user.getVotes()));
                e.setItem(51,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&e&lToken Shop", "&7Buy amazing items with your tokens!"));
                return;
            }
            case "tokenshop": {
                this.setPhoneDefaults(e);
                List<ShopItem> items = Core.getVoteManager().getShopItems();
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<ShopItem> it = items.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    ShopItem item = it.next();
                    ItemStack stack = item.getItem().clone();
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(Utils.f("&3&l" + item.getName()));
                    meta.setLore(Arrays.asList("",
                            Utils.f("&7Price: &" + (user.hasTokens(item.getPrice()) ? "e" : "c") + "&l" + item.getPrice() + (item.getPrice() == 1 ? " Token" : " Tokens"))));
                    stack.setItemMeta(meta);
                    e.setItem(slots[i], stack);
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the voting menu!"));
                e.setItem(49, Utils.createItem(Material.BOOK_AND_QUILL, "&e&lToken Shop", "&7Page 1"));
                if (items.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                return;
            }
            case "buyshopitem":
                ShopItem item = user.getBuyingShopItem();
                if (item == null || !user.hasTokens(item.getPrice())) return;
                this.setConfirmDefaults(e, "&a&lBuy " + item.getName() + "&a&l for &e&l" + item.getPrice() + " Token" + (item.getPrice() == 1 ? "" : "s"), "&c&lCancel");
                return;
            case "serverwarper":
                if (!Core.getSettings().serverWarperEnabled())
                    return;
                this.setPhoneDefaults(e);
                List<String> lore = Arrays.stream(new String[]{"gtm1", "gtm2", "gtm3", "gtm4", "gtm5", "gtm6"}).map(st -> Core.getServerManager().getServer(st)).filter(Objects::nonNull).map(s -> Utils.f("&7&lGTM &a&l" + s.getNumber() + "&7: " + (s.isOffline() ? "&c&lOffline" : "&a" + s.getOnlinePlayers() + "&7/&a" + s.getMaxPlayers()))).collect(Collectors.toList());
                lore.add(Utils.f("&0"));
                lore.add(ServerType.GTM.getDescription());
                e.setItem(11, Utils.createItem(ServerType.GTM.getIcon(), ServerType.GTM.getDisplayName(), lore));

                if (!Core.getSettings().isSister()) {
                    this.addServerIcon(e, "creative1", 15);
                    //                this.addServerIcon(e, "hub1", 47);
                    this.addServerIcon(e, "vice1", 13);
                }

                e.setItem(49, Utils.createItem(Material.COMPASS, "&e&lServer Warper", "&7Click to join a server!"));
                break;
            case "gtmservers":
                if (!Core.getSettings().serverWarperEnabled())
                    return;
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 13, 15, 29, 31, 33};
                List<Server> gtmServers = Core.getServerManager().getServers(ServerType.GTM);
                Iterator<Server> it = gtmServers.iterator();
                for (int i = 0; i < slots.length && it.hasNext(); i++) {
                    Server server = it.next();
                    this.addServerIcon(e, server, server.getType(), slots[i]);
                }
                e.setItem(49, Utils.createItem(Material.COMPASS, "&e&lServer Warper", "&7Click to join a server!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Go back to the server warper!"));
                break;

            case "hubservers":

                if (!Core.getSettings().serverWarperEnabled())
                    return;

                this.setPhoneDefaults(e);
                int[] hubSlots = new int[]{11, 13, 15, 20, 22, 24, 29, 31, 33, 38, 40, 42};
                List<Server> gtmHubServers = Core.getServerManager().getServers(ServerType.HUB);
                Iterator<Server> serverIterator = gtmHubServers.iterator();
                for (int i = 0; i < hubSlots.length && serverIterator.hasNext(); i++) {
                    Server server = serverIterator.next();
                    this.addServerIcon(e, server, server.getType(), hubSlots[i]);
                }
                e.setItem(49, Utils.createItem(Material.COMPASS, "&e&lHub Warper", "&7Click to join a server!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Go back to the server warper!"));
                return;
        }

    }

    public void addServerIcon(MenuOpenEvent e, String server, int slot) {
        Server s = Core.getServerManager().getServer(server);
        this.addServerIcon(e, s, s == null ? ServerType.NONE : s.getType(), slot);
    }

    public void addServerIcon(MenuOpenEvent e, Server s, ServerType type, int slot) {
        String lore = "";
        if (type == ServerType.GTM && s.getNumber() == 4)
            lore = "&8&lEpicStun &4&lCommunity &e&lServer &8&l(German/Deutsch)";
        e.setItem(slot, Utils.createItem(type.getIcon(),
                type.getDisplayName() + (s == null ? "" : " &a&l" + s.getNumber()),
                s == null || s.isOffline() ? "&c&lOffline!"
                        : "&a" + s.getOnlinePlayers() + "&7/&a" + s.getMaxPlayers(),
                "", Utils.f(lore), type.getDescription()));
    }

    @EventHandler
    public void onClickMenu(MenuClickEvent e) {
        Menu menu = e.getMenu();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack item = e.getItem();
        Inventory inv = e.getInv();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (item == null) return;
//        CosmeticType cosmeticType = CosmeticType.getType(menu.getName());
//        if (Core.getSettings().loadCosmetics() && cosmeticType != null) {
//            switch (item.getType()) {
//                case ARROW:
//                    int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
//                    this.setPhoneDefaults(inv);
//                    int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
//                    List<Cosmetic> cosmetics = cosmeticType.getCosmetics(user);
//                    for (int i = 0; i < slots.length && (page - 1) * 20 + i < cosmetics.size(); i++) {
//                        Cosmetic cos = cosmetics.get((page - 1) * 20 + i);
//                        inv.setItem(slots[i], cos.getMenuItem(user));
//                    }
//                    inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the cosmetics menu!"));
//                    if (page > 1)
//                        inv.setItem(48, Utils.createItem(Material.ARROW, "&6&lPrevious Page", "&7Page " + (page - 1)));
//                    inv.setItem(49, Utils.createItem(cosmeticType.getMaterial(), "&6&lCosmetics: " + cosmeticType.getDisplayName(), "&7Page " + page));
//                    if (cosmetics.size() > (20 * page))
//                        inv.setItem(50, Utils.createItem(Material.ARROW, "&6&lNext Page", "&7Page " + (page + 1)));
//                    inv.setItem(51, Utils.createItem(Material.WEB, "&c&lRemove Cosmetic", "&7Click to remove your current " + cosmeticType.toString().charAt(0) + cosmeticType.toString().substring(1).toLowerCase()));
//                    return;
//                case REDSTONE:
//                    MenuManager.openMenu(player, "cosmetics");
//                    return;
//                case WEB:
//                    switch (cosmeticType) {
//                        /*case PET:
//                            IPet pet = EchoPetAPI.getAPI().getPet(player);
//                            if (pet != null) {
//                                player.sendMessage(Lang.GUARDPETS.f("&7You removed your " + pet.getPetName() + "&7!"));
//                                EchoPetAPI.getAPI().removePet(player, false, false);
//                                Core.getUserManager().getLoadedUser(uuid).removeLastCosmetic(cosmeticType);
//                                return;
//                            }
//                            user.removeLastCosmetic(cosmeticType);
//                            player.sendMessage(Lang.GUARDPETS.f("&7You currently do not have a Pet activated!"));
//                            return;*/
//                        default:
//                            Set<com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic> set = Core.getUltimateCosmetics().getApi().getCosmetics(player);
//                            Iterator<com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic> it = set.iterator();
//                            for (int i = 0; i < set.size() && it.hasNext(); i++) {
//                                com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic c = it.next();
//                                if ((cosmeticType == CosmeticType.GADGET && Cosmetic.getGadgetByIdentifier(c.getCosmeticStorage().getIdentifier()) != null) || c.getCosmeticStorage().getIdentifier().startsWith(cosmeticType.toString().toLowerCase()))
//                                    c.remove(Core.getUltimateCosmetics());
//                            }
//                            user.removeLastCosmetic(cosmeticType);
//                            player.sendMessage(Lang.COSMETICS.f("&7You removed all cosmetics from the category " + cosmeticType.getDisplayName() + "&7!"));
//                            return;
//                    }
//                default:
//                    Cosmetic cos = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? cosmeticType.getCosmeticFromDisplayName(item.getItemMeta().getDisplayName()) : null;
//                    if (cos == null) return;
//                    if (!user.hasCosmetic(cos)) {
//                        if (cos.getTokens() < 0 || !user.hasTokens(cos.getTokens())) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You don't have access to this cosmetic! Buy cosmetics at &a&lstore.grandtheftmc.net&7!"));
//                            return;
//                        }
//                        user.setBuyingCosmetic(cos);
//                        MenuManager.openMenu(player, "buycosmetic");
//                        return;
//                    }
//                    switch (cosmeticType) {
//                        case BANNER:
//                        case BLOCK:
//                        case PARTICLE:
//                            user.setActivatingCosmetic(cos);
//                            MenuManager.openMenu(player, cosmeticType == CosmeticType.BANNER ? "bannervariant" : cosmeticType == CosmeticType.BLOCK ? "blockvariant" : "particleshape");
//                            return;
//                        case PET:
//                            player.closeInventory();
//                            cos.activate(user, player);
//                            MenuManager.openMenu(player, "petinfo");
//                            return;
//                        default:
//                            player.closeInventory();
//                            cos.activate(user, player);
//                            return;
//                    }
//            }
//        }
        switch (menu.getName()) {
            case "freecoupons": {
                int credits = 0;
                switch (item.getType()) {
                    case PAPER: {
                        TextComponent m0 = new TextComponent();
                        m0.addExtra(Lang.REWARDS.f(""));

                        TextComponent m1 = new TextComponent("[Click Here For Free Credits]");
                        m1.setColor(ChatColor.BLUE);
                        m1.setBold(true);
                        m1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, PlaywireManager.getPlaywireLink(player.getUniqueId())));


                        player.spigot().sendMessage(m0, m1);
                        player.closeInventory();
                        return;
                    }
                    case GOLD_NUGGET: {
                        credits = 50;
                        break;
                    }
                    case GOLD_INGOT: {
                        credits = 100;
                        break;
                    }
                    case GOLD_BLOCK: {
                        credits = 500;
                        break;
                    }
                    case DIAMOND: {
                        credits = user.getCouponCredits();
                        break;
                    }
                    case REDSTONE: {
                        MenuManager.openMenu(player, "rewards");
                        return;
                    }
                }
                
                if(user.getCouponCredits()<credits){
                    player.sendMessage(Lang.REWARDS.f("&cYou do not have enough coupon credits to redeem this!"));
                    return;
                }
                
                if (user.getCouponCredits() < 5){
                	player.sendMessage(Lang.REWARDS.f("&cYou need at least 5 coupon credits to get a giftcard!"));
                	return;
                }
                
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.FLOOR);
                double dollars = Double.parseDouble(df.format(credits/500.0));

                int usedCredits = (int)Math.floor(dollars * 500);

                player.sendMessage(Lang.REWARDS.f("&7Generating a giftcard code..."));
                GiftcardAPI.postGiftCard(dollars, obj -> {
                    if(obj==null || user.getCouponCredits()<usedCredits){
                        player.sendMessage(Lang.REWARDS.f("&7Sorry, but there was an error creating your giftcard, the credits have been refunded to your account."));
                        return;
                    }
                    user.setCouponCredits(user.getCouponCredits()-usedCredits);
                    player.sendMessage(Lang.REWARDS.f("&7You have redeemed your credits into a coupon!"));
                    player.sendMessage(Lang.REWARDS.f("&a&lCode: &6" + obj.getData().getCode()));
                    Utils.insertLog(player.getUniqueId(), player.getName(), "createAdCreditsGiftcard: " + obj.getData().getCode(), "GIFTCARD", obj.getData().getBalance().getRemaining(), -1, -1);
                });

                /*Coupon coupon = Coupon.builder()
                        .code(player.getName() + ThreadLocalRandom.current().nextInt(0,10000))
                        .effective(new Coupon.Effective("cart",null,null))
                        .discount(new Coupon.Discount("amount", new BigDecimal(0), dollars))
                        .startDate(new Date())
                        .basketType("both")
                        .expire(new Coupon.Expire("limit",1, new Date()))
                        .userLimit(1)
                        .minimum(new BigDecimal(0))
                        .build();
                try {
                    Core.getBuycraftX().getApiClient().createCoupon(coupon);
                } catch (IOException | ApiException e1) {
                    e1.printStackTrace();
                    player.sendMessage(Lang.REWARDS.f("&7Sorry, but there was an error creating your coupon, the credits have been refunded to your account."));
                    return;
                }*/
                player.closeInventory();
                return;
            }
            case "chooseeventtag" : {
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "account");
                        break;
                    case NAME_TAG:
                        EventTag tag = EventTag.getEventTagFromName(item.getItemMeta().getDisplayName());
                        if(tag==null){
                            player.sendMessage(Lang.REWARDS.f("&cCannot find that event tag"));
                            return;
                        }
                        if(!user.getUnlockedTags().contains(tag)) {
                            player.sendMessage(Lang.REWARDS.f("&cYou do not own this tag!"));
                            return;
                        }
                        if(user.getEquipedTag()==tag) {//is already activated
                            user.setEquipedTag(null);
                            player.sendMessage(Lang.REWARDS.f("&aYou have removed your equipped tag."));
                        }
                        else {
                            user.setEquipedTag(tag);
                            player.sendMessage(Lang.REWARDS.f("&aYou have changed your equipped tag."));
                        }
                        user.updateDisplayName(player);
                        NametagManager.updateNametagsTo(player, user);
                        NametagManager.updateNametag(player);
                        MenuManager.updateMenu(player, "chooseeventtag");
                        break;
                }
                return;
            }
            case "confirmcratereward":
                if (item.getType() == Material.STAINED_GLASS_PANE) {
                    CrateReward reward = user.getConfirmingCrateReward();
                    Crate crate = user.getSelectedCrate();
                    if (crate == null || reward == null) {
                        player.closeInventory();
                        return;
                    }
                    switch (item.getDurability()) {
                        case 5:
                            user.setConfirmingCrateReward(null);
                            user.setSelectedCrate(null);
                            reward.give(player, user, crate.getCrateStars());
                            player.closeInventory();
                            player.sendMessage(Lang.CRATES.f("&7You have opted to recieve the crate rewards."));
                            return;
                        case 14:
                            player.closeInventory();
                            return;
                        default:
                            return;
                    }

                }
            case "confirmexpensivecrate":
                if (item.getType() == Material.STAINED_GLASS_PANE) {
                    Crate crate = user.getSelectedCrate();
                    if (crate == null) {
                        player.closeInventory();
                        return;
                    }
                    switch (item.getDurability()) {
                        case 5:
                            if (crate.isBeingOpened()) {
                                user.setSelectedCrate(null);
                                player.closeInventory();
                                player.sendMessage(Lang.CRATES.f("&7This " + crate.getCrateStars().getType().toLowerCase() + " is already being opened!"));
                                return;
                            }
                            if (!user.hasCrowbars(crate.getCrateStars().getCrowbars())) {
                                user.setSelectedCrate(null);
                                player.closeInventory();
                                player.sendMessage(Lang.CRATES.f("&7You do not have enough crowbars to open this " + crate.getCrateStars().getType().toLowerCase() + "! You need &9&l" + crate.getCrateStars().getCrowbars() + " Crowbar" + (crate.getCrateStars().getCrowbars() == 1 ? "" : "s") + "&7!"));
                                return;
                            }
                            player.closeInventory();
                            user.takeCrowbars(crate.getCrateStars().getCrowbars());
                            crate.startAnimation(player, user);
                            return;
                        case 14:
                            user.setSelectedCrate(null);
                            player.closeInventory();
                            return;
                        default:
                            return;
                    }
                }
            case "cosmetics":
                return;
                /*switch (item.getType()) {
                    case NAME_TAG:
                        MenuManager.openMenu(player, "nametags");
                        return;
                    default:
                        CosmeticType type = CosmeticType.getType(item.getType());
                        if (type == null) return;
                        MenuManager.openMenu(player, type.toString().toLowerCase());
                        return;
                }*/
            case "buycosmetic":
                return;
                /*switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                User u = Core.getUserManager().getLoadedUser(uuid);
                                Cosmetic c = u.getBuyingCosmetic();
                                u.setBuyingCosmetic(null);
                                if (c == null) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.COSMETICS.f("&7You are not buying any cosmetic!"));
                                    return;
                                }
                                player.closeInventory();
                                if (c.getTokens() < 0 || !u.hasTokens(c.getTokens())) {
                                    player.sendMessage(Lang.COSMETICS.f("&7You can't afford &e&l" + c.getTokens() + " Token" + (c.getTokens() == 1 ? "" : "s") + "&7 to pay for this Cosmetic!"));
                                    player.closeInventory();
                                    return;
                                }
                                player.sendMessage(Lang.COSMETICS.f("&7You bought " + c.getColoredDisplayName() + "&7 for &e&l" + c.getTokens() + " Token" + (c.getTokens() == 1 ? "" : "s") + "&7!"));
                                u.takeTokens(c.getTokens());
                                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS));
                                u.giveCosmetic(player, c);
                                u.insertLog(player, "buyCosmetic", c.getType().toString(), c.getName(), 1, c.getTokens());
                                switch (c.getType()) {
                                    case BANNER:
                                    case BLOCK:
                                    case PARTICLE:
                                        u.setActivatingCosmetic(c);
                                        MenuManager.openMenu(player, c.getType() == CosmeticType.BANNER ? "bannervariant" : c.getType() == CosmeticType.BLOCK ? "blockvariant" : "particleshape");
                                        return;
                                    default:
                                        player.closeInventory();
                                        c.activate(Core.getUserManager().getLoadedUser(uuid), player);
                                        return;
                                }
                            case 14:
                                Core.getUserManager().getLoadedUser(uuid).setBuyingCosmetic(null);
                                MenuManager.openMenu(player, "cosmetics");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }*/
//            case "bannervariant":
//                switch (item.getType()) {
//                    case SKULL_ITEM: {
//                        Cosmetic cos = user.getActivatingCosmetic();
//                        if (cos == null || cos.getType() != CosmeticType.BANNER) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You have not chosen your cosmetic!"));
//                            MenuManager.openMenu(player, "banner");
//                            return;
//                        }
//                        user.setCosmeticVariant(0);
//                        player.closeInventory();
//                        cos.activate(user, player);
//                        return;
//                    }
//                    case BANNER:
//                        Cosmetic cos = user.getActivatingCosmetic();
//                        if (cos == null || cos.getType() != CosmeticType.BANNER) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You have not chosen your cosmetic!"));
//                            MenuManager.openMenu(player, "banner");
//                            return;
//                        }
//                        user.setCosmeticVariant(1);
//                        player.closeInventory();
//                        cos.activate(user, player);
//                        return;
//                    case REDSTONE:
//                        MenuManager.openMenu(player, "banner");
//                        return;
//                    default:
//                        return;
//                }
//            case "blockvariant":
//                switch (item.getType()) {
//                    case MONSTER_EGG: {
//                        Cosmetic cos = user.getActivatingCosmetic();
//                        if (cos == null || cos.getType() != CosmeticType.BLOCK) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You have not chosen your cosmetic!"));
//                            MenuManager.openMenu(player, "block");
//                            return;
//                        }
//                        user.setCosmeticVariant(0);
//                        player.closeInventory();
//                        cos.activate(user, player);
//                        return;
//                    }
//                    case LEASH:
//                        Cosmetic cos = user.getActivatingCosmetic();
//                        if (cos == null || cos.getType() != CosmeticType.BLOCK) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You have not chosen your cosmetic!"));
//                            MenuManager.openMenu(player, "block");
//                            return;
//                        }
//                        user.setCosmeticVariant(1);
//                        player.closeInventory();
//                        cos.activate(user, player);
//                        return;
//                    case REDSTONE:
//                        MenuManager.openMenu(player, "block");
//                        return;
//                    default:
//                        return;
//                }
//            case "particleshape":
//                switch (item.getType()) {
//                    case INK_SACK:
//                        Cosmetic cos = user.getActivatingCosmetic();
//                        if (cos == null || cos.getType() != CosmeticType.PARTICLE) {
//                            player.sendMessage(Lang.COSMETICS.f("&7You have not chosen your cosmetic!"));
//                            MenuManager.openMenu(player, "particle");
//                            return;
//                        }
//                        ParticleCosmeticStorage.Shape shape = Cosmetic.ParticleShape.values()[item.getDurability()].getShape();
//                        if (shape == null) {
//                            player.sendMessage(Lang.COSMETICS.f("&7Error while choosing this shape."));
//                            return;
//                        }
//                        user.setParticleShape(shape);
//                        player.closeInventory();
//                        cos.activate(user, player);
//                        return;
//                    case REDSTONE:
//                        MenuManager.openMenu(player, "particle");
//                        return;
//                    default:
//                        return;
//                }
//            case "nametags":
//                switch (item.getType()) {
//                    case ARROW:
//                        int page = Integer
//                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
//                        this.setPhoneDefaults(inv);
//                        List<Nametag> list = Core.getNametagManager().getNametags(user);
//                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
//                        Iterator<Nametag> it = list.iterator();
//                        for (int i = (page - 1) * 20; i < 20 * page; i++) {
//                            if (!it.hasNext())
//                                break;
//                            Nametag tag = it.next();
//                            inv.setItem(slots[i], Utils.createItem(Material.NAME_TAG, tag.getDisplayName(), user.hasNametag(tag) ? "&7Click to apply this nametag!" : "&7Price: &e&l" + tag.getPrice() + " Tokens"));
//                        }
//                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the cosmetics menu!"));
//                        if (page > 1)
//                            inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));
//                        inv.setItem(49, Utils.createItem(Material.CHEST, "&a&lNametags", "&7Select a nametag to apply to a pet!", "&7Soon, you will be able to apply nametags to", "&7your player character, vehicles and weapons.", "&7Page " + page));
//                        if (list.size() > (20 * page))
//                            inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));
//                        inv.setItem(51, Utils.createItem(Material.WEB, "&c&lRemove Nametag", "&7Click to remove a nametag from a pet."));
//                        return;
//                    case REDSTONE:
//                        MenuManager.openMenu(player, "cosmetics");
//                        return;
//                    default:
//                        Nametag tag = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? Core.getNametagManager().getNametagFromDisplayName(item.getItemMeta().getDisplayName()) : null;
//                        if (tag == null) return;
//                        if (user.hasNametag(tag)) {
//                            user.setActivatingNametag(tag);
//                            MenuManager.openMenu(player, "applynametag");
//                            return;
//                        }
//                        if (!user.hasTokens(tag.getPrice())) {
//                            player.sendMessage(Lang.NAMETAGS.f("&7You do not have the &e&l" + tag.getPrice() + " Token " + (tag.getPrice() == 1 ? "" : "s") + "&7!"));
//                            return;
//                        }
//                        user.setBuyingNametag(tag);
//                        MenuManager.openMenu(player, "buynametag");
//                        return;
//                }
//            case "buynametag":
//                switch (item.getType()) {
//                    case STAINED_GLASS_PANE:
//                        switch (item.getDurability()) {
//                            case 5:
//                                User u = Core.getUserManager().getLoadedUser(uuid);
//                                Nametag tag = u.getBuyingNametag();
//                                u.setBuyingNametag(null);
//                                if (tag == null) {
//                                    player.closeInventory();
//                                    player.sendMessage(Lang.NAMETAGS.f("&7You are not buying a nametag!"));
//                                    return;
//                                }
//                                player.closeInventory();
//                                if (tag.getPrice() > 0 && !u.hasTokens(tag.getPrice())) {
//                                    player.sendMessage(
//                                            Lang.TOKEN_SHOP.f("&7You do not have the &e&l" + tag.getPrice() + " Tokens&7 to pay for this nametag!"));
//
//                                    return;
//                                }
//                                player.sendMessage(
//                                        Lang.TOKEN_SHOP.f("&7You bought &a" + tag.getDisplayName() + "&7 for &e&l" + tag.getPrice() + " Token" + (tag.getPrice() == 1 ? "" : "s") + "&7!"));
//                                u.takeTokens(tag.getPrice());
//                                u.giveNametag(tag);
//                                u.insertLog(player, "buyNametag", Reward.RewardType.NAMETAG.toString(), tag.getName(), 1, tag.getPrice());
//                                UpdateEvent ev = new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS);
//                                Bukkit.getPluginManager().callEvent(ev);
//                                return;
//                            case 14:
//                                Core.getUserManager().getLoadedUser(uuid).setBuyingShopItem(null);
//                                MenuManager.openMenu(player, "nametags");
//                                return;
//                            default:
//                                return;
//                        }
//                    default:
//                        return;
//                }
//            case "applynametag":
//                switch (item.getType()) {
//                    case REDSTONE:
//                        MenuManager.openMenu(player, "nametags");
//                        return;
//                    default:
//                        Nametag tag = user.getActivatingNametag();
//                        if (tag == null) {
//                            MenuManager.openMenu(player, "nametags");
//                            return;
//                        }
//
//                        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
//                            Cosmetic pet = user.getLastPet(player);
//                            if (pet != null && CosmeticType.PET.getCosmeticFromDisplayName(item.getItemMeta().getDisplayName()) == pet) {
//                                user.setPetNametag(player, pet, tag);
//                                player.sendMessage(Lang.NAMETAGS.f("&7You have renamed your pet to " + tag.getDisplayName() + "&7!"));
//                                player.closeInventory();
//                                return;
//                            }
//                        }
//                        return;
//                }
            case "rewards":
                switch (item.getType()) {
                    case PAPER: {
                        if(Core.getSettings().isSister())
                            return;
                        MenuManager.openMenu(player, "freecoupons");
                        return;
                    }
                    case DOUBLE_PLANT:
                        MenuManager.openMenu(player, "vote");
                        return;
                    case NETHER_STAR:
                        Core.getVoteManager().claimDaily(player, Core.getUserManager().getLoadedUser(uuid));
                        return;
                    case INK_SACK:
                        switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                            case "Vote Streak":
                                MenuManager.openMenu(player, "vote");
                                return;
                            case "Daily Reward Streak":
                                Core.getVoteManager().claimDaily(player, Core.getUserManager().getLoadedUser(uuid));
                                return;
                            default:
                                return;
                        }
                    case EXP_BOTTLE:
                        if (user.canClaimDailyReward())
                            Core.getVoteManager().claimDaily(player, user);
                        if (user.canClaimMonthlyReward())
                            Core.getVoteManager().claimMonthly(player, user);
                        if (user.getVotes() > 0)
                            Core.getVoteManager().spendVote(player, user);
                    default:
                        if (item.getType() == Material.COAL || UserRank.getUserRank(item.getType()) != null)
                            Core.getVoteManager().claimMonthly(player, Core.getUserManager().getLoadedUser(uuid));
                        return;
                }
            case "prefs":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "account");
                        return;
                    default:
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
                            return;
                        User u = Core.getUserManager().getLoadedUser(uuid);//
                        Pref pref = Pref.getPrefByDisplayName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (pref == null) return;
                        if (!pref.isEnabled(player, u, Core.getSettings().getType())) {
                            player.sendMessage(Lang.PREFS.f("&7You can't use this pref!"));
                            return;
                        }
                        u.togglePref(player, pref);
                        player.sendMessage(Lang.PREFS
                                .f("&7You turned " + (u.getPref(pref) ? "&a&lon" : "&c&loff") + "&7 " + pref.getDisplayName() + '!'));
                        MenuManager.updateMenu(player, "prefs");
                        return;
                }
            case "topvoters": {
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "vote");
                        return;
                }
            }
            case "vote":
                switch (item.getType()) {
                    case SKULL_ITEM:
                        if (e.getSlot() == 39)
                            MenuManager.openMenu(player, "topvoters");
                        return;
                    case CHEST:
                    case EMPTY_MAP:
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.VOTE + "&7Vote for the server at &a&n" + Core.getVoteManager().getVoteLink() + "&7!"));
                        return;
                    case BOOK_AND_QUILL:
                        MenuManager.openMenu(player, "tokenshop");
                        return;
                    case DOUBLE_PLANT:
                        if (user.getVotes() == 0) {
                            player.closeInventory();
                            player.sendMessage(Utils.f(Lang.VOTE + "&7Vote for the server at &a&n" + Core.getVoteManager().getVoteLink() + "&7 to claim rewards!"));
                            return;
                        }
                        if (user.getVotes() > 1 && (e.getClickType() == ClickType.RIGHT || e.getClickType() == ClickType.SHIFT_RIGHT))
                            Core.getVoteManager().spendAllVotes(player, user);
                        else
                            Core.getVoteManager().spendVote(player, user);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "rewards");
                        return;
                    default:
                        return;
                }
            case "tokenshop":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        List<ShopItem> items = Core.getVoteManager().getShopItems();
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41,
                                42};
                        Iterator<ShopItem> it = items.iterator();
                        for (int i = (page - 1) * 20; i < 20 * page; i++) {
                            if (!it.hasNext())
                                break;
                            ShopItem shopItem = it.next();
                            ItemStack stack = shopItem.getItem().clone();
                            ItemMeta meta = stack.getItemMeta();
                            meta.setDisplayName(Utils.f(shopItem.getName()));
                            meta.setLore(Arrays.asList("",
                                    Utils.f("&7Price: &" + (Core.getUserManager().getLoadedUser(uuid).hasTokens(shopItem.getPrice()) ? "e" : "c") + "&l" + shopItem.getPrice() + (shopItem.getPrice() == 1 ? " Token" : " Tokens"))));
                            stack.setItemMeta(meta);
                            inv.setItem(slots[i], stack);
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the voting menu!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.createItem(Material.BOOK_AND_QUILL, "&e&lToken Shop", "&7Page " + page));
                        if (items.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "vote");
                        return;
                    default:
                        ShopItem shopItem = Core.getVoteManager().getShopItem(item.getItemMeta().getDisplayName());
                        if (shopItem != null) {
                            if (!user.hasTokens(shopItem.getPrice())) {
                                player.sendMessage(Lang.TOKEN_SHOP.f("&7You do not have &e&l" + shopItem.getPrice() + " Token" + (shopItem.getPrice() == 1 ? "" : "s") + "&7!"));
                                return;
                            }
                            user.setBuyingShopItem(shopItem);
                            MenuManager.openMenu(player, "buyshopitem");
                            return;
                        }
                        return;
                }
            case "buyshopitem":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                User u = Core.getUserManager().getLoadedUser(uuid);
                                ShopItem shopItem = u.getBuyingShopItem();
                                u.setBuyingShopItem(null);
                                if (shopItem == null) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.TOKEN_SHOP.f("&7You are not buying a token shop item!"));
                                    return;
                                }
                                player.closeInventory();
                                shopItem.buy(player, Core.getUserManager().getLoadedUser(uuid));
                                return;
                            case 14:
                                Core.getUserManager().getLoadedUser(uuid).setBuyingShopItem(null);
                                MenuManager.openMenu(player, "tokenshop");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "serverwarper":
                if (!Core.getSettings().serverWarperEnabled())
                    return;
                ServerManager sm = Core.getServerManager();
                switch (item.getType()) {
                    case REDSTONE:
                        sm.sendToServer(player, "hub1");
//                        Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
//                                new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.HUB, 1)),
//                                new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
//                        );
                        return;

                    case MINECART:
                        MenuManager.openMenu(player, "gtmservers");
                        MenuManager.openMenu(player, "gtmservers");
                        return;

                    case GOLD_BLOCK:
//                        sm.sendToServer(player, "creative1");
                        Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                                new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.CREATIVE, 1)),
                                new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
                        );
                        return;

                    case ELYTRA:
                        sm.sendToServer(player, "gliders1");
                        return;

                    case CHEST:
                        sm.sendToServer(player, "legacygtm");
                        return;

                    case SUGAR:
//                        sm.sendToServer(player, "vice1");
                        Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                                new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.VICE, 1)),
                                new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
                        );
                    default:
                        break;
                }
            case "gtmservers":
                if (!Core.getSettings().serverWarperEnabled()) return;

                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "serverwarper");
                        return;

                    case MINECART:
                        String name = item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace(Core.getSettings().getServer_GTM_name() + " ", "") : null : null;
                        if (name == null) return;
                        int number;
                        try {
                            number = Integer.parseInt(name);
                        } catch (NumberFormatException ex) {
                            return;
                        }

                        JedisModule module = Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE);
                        if (module == null) {
                            Core.getServerManager().sendToServer(player, "gtm" + number);
                            return;
                        }

                        module.sendMessage(
                                new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.GTM, number)),
                                new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
                        );
                        return;

                    default:
                        break;
                }


            case "hubservers":
                if (!Core.getSettings().serverWarperEnabled())
                    return;
                switch (item.getType()) {
                    case REDSTONE:

                        if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Back")) {
                            MenuManager.openMenu(player, "serverwarper");
                        } else {
                            String name = item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("Hub ", "") : null : null;
                            if (name == null)
                                return;

                            int number;
                            try {
                                number = Integer.parseInt(name);
                            } catch (NumberFormatException ex) {
                                return;
                            }

//                            Core.getServerManager().sendToServer(player, "hub" + number);
                            Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                                    new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.HUB, number)),
                                    new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
                            );
                        }
                        return;
                }
        }
    }

    @EventHandler
    public void onClose(MenuCloseEvent e) {
        Player player = e.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        Crate crate = user.getSelectedCrate();
        String s = e.getMenu().getName().toLowerCase();
        if (Objects.equals(s, "confirmcratereward") && user.getSelectedCrate() != null) {
            user.setConfirmingCrateReward(null);
            user.setSelectedCrate(null);
            user.addCrowbars(crate.getCrateStars().getCrowbars());
            player.sendMessage(Lang.CRATES.f("&7You have opted to not recieve the crate rewards."));
        }
    }


    public void setPhoneDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
    }

    public void setPhoneDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) inv.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) inv.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            inv.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
    }

    public void setGPSDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53}) e.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7}) e.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            e.setItem(i, grayGlass);
    }

    public void setGPSDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53})
            inv.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7})
            inv.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            inv.setItem(i, grayGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e) {
        this.setConfirmDefaults(e, "&a&lConfirm", "&c&lCancel");
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage, List<String> confirmLore, List<String> cancelLore) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage, confirmLore == null ? new ArrayList<>() : confirmLore);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage, cancelLore == null ? new ArrayList<>() : cancelLore);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }

}
