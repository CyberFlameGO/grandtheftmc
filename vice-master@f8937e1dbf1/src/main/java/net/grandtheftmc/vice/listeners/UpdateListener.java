package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.*;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.events.PlayerVoteEvent;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import net.grandtheftmc.vice.events.TPEvent;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.items.Head;
import net.grandtheftmc.vice.items.ItemManager;
import net.grandtheftmc.vice.items.Kit;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.Stats;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UpdateListener implements Listener {
    private final Map<UUID, Integer> voteCounts = new HashMap<>();

    @EventHandler
    public void voteEvent(PlayerVoteEvent event) {
        UUID uuid = event.getUUID();
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        this.voteCounts.put(uuid, this.voteCounts.getOrDefault(uuid, 0) + 1);
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(uuid);
        User user = Core.getUserManager().getLoadedUser(uuid);
        Kit kit = Vice.getItemManager().getKit("vote");
        if (this.voteCounts.get(uuid) == 5) {
            player.sendMessage(Lang.VOTE.f("&7Thank you for voting on all 5 sites! Here is a special vote kit!"));
            Vice.getItemManager().giveKitItems(player, viceUser, kit);
            user.addCrowbars(1);
        }
    }

    @EventHandler
    public void tpEvent(TPEvent event) {
        if (event.getType() == TPEvent.TPType.HOUSE_ENTER
                || event.getType() == TPEvent.TPType.PREMIUM_HOUSE_ENTER) {
            Player player = event.getPlayer();
            ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            if (viceUser.isInCombat()) {
                event.setCancelled(Utils.f("&7You cannot enter a house while in combat!"));
            }
        }
    }

    @EventHandler
    public void serverSaveEvent(ServerSaveEvent event) {
        Vice.getShopManager().getHeads().forEach(Head::update);
        Vice.getUserManager().getLoadedUsers().forEach(gtmUser -> {
            if (gtmUser == null) return;
            gtmUser.checkAchievements();
        });
    }

    @EventHandler
    public void chatEvent(ChatEvent event) {
        Player player = event.getSender();
        TextComponent message = event.getTextComponent();
        List<String> hover = Stats.getInstance().getStats(player);
        String url = "";

        for (String string : message.getText().split(" ")) {
            if (ViceUtils.isValidURL(string)) {
                url = string;
                break;
            }

            if (ViceUtils.getUser(player).isRank(UserRank.VIP) && (string.equalsIgnoreCase(":hand:")
                    || string.equalsIgnoreCase(":items:") || string.equalsIgnoreCase(":item:"))) {
                if (player.getInventory().getItemInMainHand() == null) continue;
                ItemStack hand = player.getInventory().getItemInMainHand().clone();
                String json = ViceUtils.convertItemStackToJson(hand);
                if (json == null) continue;
                if (!hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) {
                    ItemMeta meta = hand.getItemMeta();
                    meta.setDisplayName(WordUtils.capitalize(hand.getType().name().toLowerCase().replace("_", " ")));
                    hand.setItemMeta(meta);
                }
                message.setText(message.getText().replace(string,
                        hand.getItemMeta().getDisplayName() + message.getColor()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{
                        new TextComponent(json)
                }));
                break;
            }
        }

        if (url.isEmpty()) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + ' '));
        } else {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        }
        if (message.getHoverEvent() == null) {
            message.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(StringUtils.join(hover, "\n"))
                                    .create()));
        }
        event.setTextComponent(message);
    }

    @EventHandler
    public void onDisplayNameUpdate(DisplayNameUpdateEvent e) {
        Player player = e.getPlayer();
        ViceUser u = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        e.setPrefix(u.isCop() ? u.getCopRank().getColoredNameBold() : u.getRank().getColoredNameBold());
    }

    @EventHandler
    public void onGetPerms(GetPermsEvent e) {
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getUUID());
        if (user != null && user.getRank() != null) {
            user.getRank().getAllPerms().forEach(e::addPerm);
            if (user.isCop()) user.getCopRank().getAllPerms().forEach(e::addPerm);
        }
    }

    @EventHandler
    public void onNametagChange(NametagUpdateEvent e) {
        Player player = e.getPlayer();
        if (player == null)
            return;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());

        // TODO achievements
    }

    @EventHandler
    public void onTutorialEvent(TutorialEvent e) {
        Player player = e.getPlayer();
        switch (e.getType()) {
            case PRE_START:
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.isInCombat()) {
                    e.setCancelled("&7You can't join tutorials in combat!");
                    return;
                }
                if (user.isArrested()) {
                    e.setCancelled("&7You can't join tutorials in jail!");
                    return;
                }
                if (!Objects.equals("spawn", player.getWorld().getName())) {
                    e.setCancelled("&7You can only join tutorials at spawn!");
                    return;
                }
                break;
            case START:
                ViceUtils.removeBoard(player);
                break;
            default:
                break;
        }

    }

    @EventHandler
    public void onReward(RewardEvent e) {
        Player player = e.getPlayer();
        Reward reward = e.getReward();
        if (reward.getCustomType() != null)
            switch (reward.getCustomType()) {
                case "drug": {
                    Optional<Drug> drug = ((DrugService) Vice.getDrugManager().getService()).getDrug(reward.getCustomName());
                    if (drug.isPresent()) {
                        DrugItem item = DrugItem.getByDrug(drug.get());
                        if (item != null) {
                            Utils.giveItems(player, item.getItemStack());
                        } else {
                            player.sendMessage(Lang.GTM + "" + ChatColor.RED + "Unable to give you the drug " + reward.getCustomName() + ", couldn't find it.");
                        }
                    }
                    return;
                }
                case "item": {
                    String[] a = reward.getCustomName().split(":");
                    GameItem item = Vice.getItemManager().getItem(a[0]);
                    if (item == null)
                        return;
                    ItemStack stack = item.getItem();
                    if (a.length > 1)
                        try {
                            stack.setAmount(Integer.parseInt(a[1]));
                        } catch (NumberFormatException ignored) {
                        }
                    if (Utils.giveItems(player, stack))
                        player.sendMessage(
                                Utils.f(Lang.VOTE + "&7Your inventory was full so the item was dropped on the ground!"));
                    e.setSuccessfull(true);
                    return;
                }
                case "items":
                    ItemManager im = Vice.getItemManager();
                    boolean successfull = true;
                    List<ItemStack> items = new ArrayList<>();
                    for (String s : reward.getCustomList()) {
                        String[] a = s.split(":");
                        GameItem item = im.getItem(a[0]);
                        if (item == null) {
                            successfull = false;
                            continue;
                        }
                        ItemStack stack = item.getItem();
                        if (a.length > 1)
                            try {
                                stack.setAmount(Integer.parseInt(a[1]));
                            } catch (NumberFormatException ignored) {
                            }
                        items.add(stack);
                    }
                    if (Utils.giveItems(player, Utils.toArray(items)))
                        player.sendMessage(
                                Utils.f(Lang.VOTE + "&7Your inventory was full so some items were dropped on the ground!"));
                    e.setSuccessfull(successfull);
                    return;
                case "kit":
                    Kit kit = Vice.getItemManager().getKit(reward.getCustomName());
                    if (kit != null) {
                        Vice.getItemManager().giveKitItems(player, Vice.getUserManager().getLoadedUser(player.getUniqueId()), kit);
                        e.setSuccessfull(true);
                    }
                    return;
                case "bonds":
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                    user.giveBonds((int) reward.getAmount());
                    e.setSuccessfull(true);
                    return;
                default:
                    break;
            }
    }

    @EventHandler
    public void onMoneyEvent(MoneyEvent e) {
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getUUID());
        switch (e.getType()) {
            case ADD:
                user.addMoney(e.getAmount());
                e.setSuccessfull();
                break;
            case BALANCE:
                e.setBalance(user.getMoney());
                break;
            case TAKE:
                user.takeMoney(e.getAmount());
                e.setSuccessfull();
                break;
        }
        ViceUtils.updateBoard(Bukkit.getPlayer(e.getUUID()), user);
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        switch (e.getReason()) {
            case BOARD:
            case MONEY:
            case OTHER:
            case RANK:
                ViceUtils.updateBoard(e.getPlayer(), Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()));
                break;
            case PREF:
                switch (e.getPref()) {
                    case USE_SCOREBOARD:
                        ViceUtils.updateBoard(e.getPlayer(), Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()));
                        break;
                    case TINT_HEALTH:
                        Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).updateTintHealth(e.getPlayer(), Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()));
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }

}
