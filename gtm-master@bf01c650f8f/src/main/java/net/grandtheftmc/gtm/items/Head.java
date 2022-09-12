package net.grandtheftmc.gtm.items;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserDAO;

/**
 * Created by Liam on 4/10/2016.
 */
public class Head {

    private final UUID sellerUUID;
    private final String sellerName;
    private final String head;
    private final long expiry;
    private boolean done;
    private boolean paid;
    private boolean gaveHead;
    private UUID bidderUUID;
    private String bidderName;
    private double bid = -1;

    public Head(UUID sellerUUID, String sellerName, String head) {
        this.sellerUUID = sellerUUID;
        this.sellerName = sellerName;
        this.head = head;
        this.expiry = System.currentTimeMillis() + 86400000;
//        Core.sql.updateAsyncLater("insert into " + Core.name() + "_heads(sellerUUID, sellerName, head, expiry) values ('" + this.sellerUUID + "','" + this.sellerName + "','" + this.head + "'," + this.expiry + ");");
        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("insert into " + Core.name() + "_heads(sellerUUID, sellerName, head, expiry) values ('" + this.sellerUUID + "','" + this.sellerName + "','" + this.head + "'," + this.expiry + ");"));

    }

    public Head(UUID sellerUUID, String sellerName, String head, long expiry, boolean done, boolean paid, boolean gaveHead, UUID bidderUUID, String bidderName, double bid) {
        this.sellerUUID = sellerUUID;
        this.sellerName = sellerName;
        this.head = head;
        this.expiry = expiry;
        this.done = done;
        this.paid = paid;
        this.gaveHead = gaveHead;
        this.bidderUUID = bidderUUID;
        this.bidderName = bidderName;
        this.bid = bid;
    }

    public UUID getSellerUUID() {
        return this.sellerUUID;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public String getHead() {
        return this.head;
    }

    public long getExpiry() {
        return this.expiry;
    }

    public UUID getBidderUUID() {
        return this.bidderUUID;
    }

    public String getBidderName() {
        return this.bidderName;
    }

    public double getBid() {
        return this.bid;
    }

    public boolean hasBid() {
        return this.bidderUUID != null;
    }

    public boolean hasExpired() {
        return this.hasBid() ? this.expiry < System.currentTimeMillis() : this.expiry - 82800000 < System.currentTimeMillis();
    }

    public boolean hasExpiredOverAWeekAgo() {
        return this.expiry + 604800000 < System.currentTimeMillis();
    }

    public Long getTimeUntilExpiry() {
        return this.expiry - System.currentTimeMillis();
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean isPaid() {
        return this.paid;
    }

    public boolean gaveHead() {
        return this.gaveHead;
    }

    public ItemStack getItem() {
//        ItemStack i = Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + this.head + "'s Head", "&7Value: &a$&l" + (this.hasBid() ? "10,000" : this.bid), "&7Sell me in the sewer!");
        ItemStack i = new ItemFactory(Material.SKULL_ITEM, (byte) 3)
                .setName(C.YELLOW + C.BOLD + this.head + "'s Head")
                .setLore(C.GRAY + "Value: " + C.GREEN + C.BOLD + (this.hasBid() ? "10,000" : this.bid), C.GRAY + "Sell me in the sewer!")
                .build();

        SkullMeta meta = (SkullMeta) i.getItemMeta();
        meta.setOwner(this.head);
        i.setItemMeta(meta);

        return i;
    }

    public boolean giveHead() {
        if (this.gaveHead) return false;
        if (!this.hasBid()) {
            this.gaveHead = true;
            return true;
        }

        Player bidder = Bukkit.getPlayer(this.bidderUUID);
        if (bidder == null) return false;

        if (bidder.getInventory().firstEmpty() < 0) {
            bidder.sendMessage(Lang.HEAD_AUCTION.f("&7The auction for &e&l" + this.head + "'s Head&7 has finished! Please clear a slot in your inventory and wait a few seconds."));
            return false;
        }

        bidder.getInventory().addItem(this.getItem());
        bidder.updateInventory();
        bidder.sendMessage(Lang.HEAD_AUCTION.f("&7The auction for &e&l" + this.head + "'s Head&7 has finished! Congratulations on winning the bid."));
        this.gaveHead = true;
        return true;
    }

    public boolean paySeller() {
        if (this.paid) return false;
        Player seller = Bukkit.getPlayer(this.sellerUUID);
        if (seller == null)
            return false;
        GTMUser user = GTM.getUserManager().getLoadedUser(seller.getUniqueId());
        if (!this.hasBid()) {
            seller.sendMessage(Lang.HEAD_AUCTION.f("&7The auction for &e&l" + this.head + "'s Head&7 has finished with no bids!"));
            this.paid = true;
            return true;
        }
        if (this.bid > 10000)
            user.addBank(this.bid - 10000);
        GTMUtils.updateBoard(seller, user);
        seller.sendMessage(Lang.HEAD_AUCTION.f("&7The auction for &e&l" + this.head + "'s Head&7 has finished with &a$&l" + this.bid + "&7! You received &a$&l" + (this.bid - 10000) + "&7 from &a&l" + this.bidderName + "&7!"));
        this.paid = true;
        return true;
    }

    public boolean delete() {
        if ((this.done && this.gaveHead && this.paid) || this.hasExpiredOverAWeekAgo()) {
//            Core.sql.updateAsyncLater("delete from " + Core.name() + "_heads where sellerUUID='" + this.sellerUUID + "' and head='" + this.head + "' and expiry=" + this.expiry + ';');
            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from " + Core.name() + "_heads where sellerUUID='" + this.sellerUUID + "' and head='" + this.head + "' and expiry=" + this.expiry + ';'));
            GTM.getShopManager().removeHead(this);
            return true;
        }
        return false;
    }

    public void update() {
        if (!this.hasExpired())
            return;
        boolean update = !this.done;
        this.done = true;
        if (this.giveHead()) update = true;
        if (this.paySeller()) update = true;
        if (this.delete()) update = true;
        if (update)
            this.updateDB();
    }


    public void updateDB() {
//        Core.sql.updateAsyncLater("update " + Core.name() + "_heads set paid=" + this.paid + ", gaveHead=" + this.gaveHead + ", done=" + this.done + ", bidderUUID=" + (this.bidderUUID == null ? null : "'" + this.bidderUUID + '\'') + ",bidderName = '" +
//                this.bidderName + "', bid=" + this.bid + " where sellerUUID='" + this.sellerUUID + "' and head='" + this.head + "' and expiry=" + this.expiry + ';');
        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_heads set paid=" + this.paid + ", gaveHead=" + this.gaveHead + ", done=" + this.done + ", bidderUUID=" + (this.bidderUUID == null ? null : "'" + this.bidderUUID + '\'') + ",bidderName = '" +
                this.bidderName + "', bid=" + this.bid + " where sellerUUID='" + this.sellerUUID + "' and head='" + this.head + "' and expiry=" + this.expiry + ';'));
    }

    public void returnBidderMoney() {
        Player bidder = Bukkit.getPlayer(this.bidderUUID);
        if (bidder == null) {
//            Core.sql.updateAsyncLater("update " + Core.name() + " set bank=bank+" + this.bid + " where uuid='" + this.bidderUUID + "';");
        	final UUID prevBidder = this.bidderUUID;
        	final double amt = this.bid;
            ServerUtil.runTaskAsync(() -> GTMUserDAO.addBank(prevBidder, amt));
            this.bidderName = null;
            this.bidderUUID = null;
            this.bid = -1;
            return;
        }
        GTMUser user = GTM.getUserManager().getLoadedUser(bidder.getUniqueId());
        user.addBank(this.bid);
        GTMUtils.updateBoard(bidder, user);
        bidder.sendMessage(Lang.HEAD_AUCTION.f("&7You were outbid for the &e&l" + this.head + "'s Head&7! Your bid of &a$&l" + this.bid + "&7 was returned to your bank account."));
        this.bidderName = null;
        this.bidderUUID = null;
        this.bid = -1;
    }

    public void bid(Player player, GTMUser user, double bid) {
        bid = Utils.round(bid);
        if (this.hasExpired() || this.done) {
            player.sendMessage(Lang.HEAD_AUCTION.f("&7The bidding has expired!"));
            return;
        }
        if (!user.hasMoney(bid)) {
            player.sendMessage(Lang.HEAD_AUCTION.f("&7You don't have &c$&l" + bid + "&7!"));
            return;
        }

        if (this.hasBid()) {
            if (this.bid * 1.05 > bid) {
                player.sendMessage(Lang.HEAD_AUCTION.f("&7You must bid at least &a&l5%&7 more than the current bid of &a$&l" + this.bid + "&7 (&a$&l" + (this.bid * 1.05) + "&7)!"));
                return;
            }
            this.returnBidderMoney();
        } else if (bid < 10000) {
            player.sendMessage(Lang.HEAD_AUCTION.f("&7You must bid at least the starting bid of &a$&l10,000&7!"));
            return;
        }
        this.setBid(player, bid);
        user.takeMoney(bid);
        GTMUtils.updateBoard(player, user);
        player.sendMessage(Lang.HEAD_AUCTION.f("&7You have bid &a$&l" + this.bid + "&7 for &e&l" + this.head + "'s Head&7! Please wait &c&l" + Utils.timeInMillisToText(this.getTimeUntilExpiry()) + "&7 for the auction to end."));
        Player seller = Bukkit.getPlayer(this.sellerUUID);
        if (seller != null && !Objects.equals(seller, player))
            seller.sendMessage(Lang.HEAD_AUCTION.f("&a&l" + player.getName() + "&7 has bid &a$&l" + this.bid + "&7 on &e&l" + this.head + "'s Head&7!"));
    }

    public void setBid(Player player, double bid) {
        this.bidderUUID = player.getUniqueId();
        this.bidderName = player.getName();
        this.bid = bid;
        this.updateDB();
    }


}
