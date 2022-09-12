package net.grandtheftmc.gtm.holidays.halloween;

import net.buycraft.plugin.client.ApiException;
import net.buycraft.plugin.data.Coupon;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.holidays.halloween.dao.ServerCoupon;
import net.grandtheftmc.gtm.holidays.halloween.dao.ServerCouponDAO;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-09-28.
 */
public class PlayerScare implements Listener{

    public PlayerScare(){
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                List<ServerCoupon> coupons = ServerCouponDAO.getAllServerCoupons();
//                coupons.forEach(coupon -> {
//                    final long timeRemaining = coupon.getCreationTime() + 1000 * 60 * 60 * 24 - System.currentTimeMillis();
//                    if(timeRemaining <= 0) {
//                        ServerCouponDAO.deleteServerCoupon(coupon.getCouponID());
//                        try {
//                            GTM.getBuycraftX().getApiClient().deleteCoupon(coupon.getCouponID());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }.runTaskTimerAsynchronously(GTM.getInstance(), 0, 20*60*60);
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//
//        int rand = ThreadLocalRandom.current().nextInt(10, 300);
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                final Optional<ServerCoupon> optCoupon = ServerCouponDAO.getServerCoupon(player.getUniqueId());
//
//                if (!optCoupon.isPresent()) {//player not in there
//                    initScare(player);
//                } else {
//                    if (optCoupon.get().getCreationTime() == 0) return;
//                    final long timeRemaining = optCoupon.get().getCreationTime() + 1000 * 60 * 60 * 24 - System.currentTimeMillis();
//                    if (timeRemaining <= 0) {
//                        try {
//                            GTM.getBuycraftX().getApiClient().deleteCoupon(optCoupon.get().getCouponID());
//                        } catch (IOException | ApiException e) {
//                            e.printStackTrace();
//                        }
//                        ServerCouponDAO.deleteServerCoupon(player.getUniqueId());
//                        return;
//                    }
//
//                    Coupon coupon = null;
//                    try {
//                        coupon = GTM.getBuycraftX().getApiClient().getCoupon(optCoupon.get().getCouponID());
//                    } catch (IOException | ApiException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (coupon != null && coupon.getExpire().getLimit() <= 0) {//coupon has already been used
//                        if (optCoupon.get().getCreationTime() != 0) {
//                            ServerCouponDAO.deleteServerCoupon(player.getUniqueId());
//                        }
//                        return;
//                    }
//
//                    if(coupon == null) return;
//                    Coupon finalCoupon = coupon;
//
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            NMSTitle.sendTitle(player, Utils.f("&6&lHAPPY HALLOWEEN"), Utils.f("&eRead chat for more information & exciting offers!"), 20, 60, 20);
//
//                            for(int i = 0; i < 4; i++)
//                                player.sendMessage("");
//
//                            player.sendMessage(Utils.f("&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀"));//▔▀
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f(" &6&lHappy Halloween!&r"));
//                            player.sendMessage(Utils.f(" Your &c&l30&c% off coupon&r hasn't yet been used, hurry before it ends! Expires in " + Utils.timeInSecondsToText(timeRemaining / 1000L, C.DARK_RED + C.BOLD, C.DARK_RED, C.WHITE)));
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f(" &e&lCOUPON&r: &6" + finalCoupon.getCode()));
//                            player.sendMessage(Utils.f(" &e&lSTORE&r: &6https://store.grandtheftmc.net/halloweenremind"));
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f("&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄"));//▄▁
//                        }
//                    }.runTask(GTM.getInstance());
//                }
//            }
//        }.runTaskLaterAsynchronously(GTM.getInstance(), rand*20);//todo:change back to 15-20min
//    }

//    private void initScare(Player player) {
//        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
//        long delay = gtmUser.isInCombat() ? 20*15 : 0;
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if(!player.isOnline()) return;
//                if(gtmUser.isInCombat()) {
//                    initScare(player);
//                    return;
//                }
//
//                String couponName = "SPOOK" + player.getName() + ThreadLocalRandom.current().nextInt(100, 1000);
//                try {
//                    Coupon coupon = Coupon.builder()
//                            .code(couponName)
//                            .effective(new Coupon.Effective("cart", null, null))
//                            .basketType("both").startDate(new Date())
//                            .discount(new Coupon.Discount("percentage", new BigDecimal(30), new BigDecimal(0)))
//                            .expire(new Coupon.Expire("limit", 1, new Date()))
//                            .userLimit(1)
//                            .minimum(new BigDecimal(0))
//                            .build();
//                    Coupon generatedCoupon = GTM.getBuycraftX().getApiClient().createCoupon(coupon);
//
//                    if(generatedCoupon == null) {
//                        player.sendMessage(Lang.GTM.f("&cError: Couldn't generate your coupon: " + couponName + " contact an admin immediately."));
//                        return;
//                    }
//
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 1);
//                            player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10, 10);
//                            player.playSound(player.getLocation(), "custome.halloween", 10, 10);
//                            NMSTitle.sendTitle(player, Utils.f("&6&lHAPPY HALLOWEEN"), Utils.f("&eRead chat for more information & exciting offers!"), 20, 60, 20);
//
//                            for(int i = 0; i < 4; i++)
//                                player.sendMessage("");
//
//                            player.sendMessage(Utils.f("&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀&8▔&6▀"));//▔▀
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f(" &6&lHappy Halloween!&r As a celebration, and a &ethank you &rfor playing, here is a &a&l30&r&a% off&r, private, exclusive coupon for our store. It runs out in &c&l24&r &chours&f, and only you have it!"));
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f(" &e&lCOUPON&r: &6" + couponName));
//                            player.sendMessage(Utils.f(" &e&lSTORE&r: &6https://store.grandtheftmc.net/halloween"));
//                            player.sendMessage("");
//                            player.sendMessage(Utils.f("&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄&8▁&6▄"));//▄▁
//                        }
//                    }.runTask(GTM.getInstance());
//
//                    ServerCouponDAO.setServerCoupon(player.getUniqueId(), generatedCoupon.getId(), couponName, System.currentTimeMillis());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.runTaskLaterAsynchronously(GTM.getInstance(),  delay);
//    }
//
//    public void clearEvent(){
//        Core.sql.updateAsyncLater("TRUNCATE server_coupons");
//        Core.log("Successfully cleared the halloween event!");
//    }


}
