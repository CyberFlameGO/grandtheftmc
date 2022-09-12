package net.grandtheftmc.vice.listeners;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.resourcepack.ResourcePack;
import net.grandtheftmc.core.resourcepack.ResourcePackEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.combatlog.CombatLogger;
import net.grandtheftmc.vice.commands.AntiAuraCommand;
import net.grandtheftmc.vice.hologram.HologramManager;
import net.grandtheftmc.vice.items.ArmorUpgrade;
import net.grandtheftmc.vice.items.Head;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Join implements Listener {

    private final HashMap<UUID, Integer> resourcePackTries;
    private final HologramManager hologramManager;
//    private final BeerBottle beerBottle;

    public Join(HologramManager hologramManager) {
        resourcePackTries = Maps.newHashMap();
        this.hologramManager = hologramManager;
//        this.beerBottle = new BeerBottle();
    }

//    @EventHandler
//    protected final void onCraft(PrepareItemCraftEvent event) {
//        this.beerBottle.getAttribute().onEvent(event);
//    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(null);
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUtils.giveGameItems(player);
        viceUser.setBooleanToStorage(BooleanStorageType.KICKED, false);
        if (viceUser.getPlaytime() == 0L) {
            int playOneTick = player.getStatistic(Statistic.PLAY_ONE_TICK);
            viceUser.setPlaytime(Long.valueOf(playOneTick / 20));
        }
        viceUser.setJointime(System.currentTimeMillis());
        if (player.hasPermission("antiaura.admin") && !AntiAuraCommand.TOGGLED_PLAYERS.contains(player.getName())) {
            AntiAuraCommand.TOGGLED_PLAYERS.add(player.getName());
        }

        //Force gamemode SURVIVAL
        player.setGameMode(GameMode.SURVIVAL);

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.getActivePotionEffects().clear();
            player.setFoodLevel(20);
            player.setFlying(false);
            player.setFlySpeed(0.1F);
            if (!viceUser.isArrested())
                player.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
        }

        ViceUtils.sendJoinMessage(player, user);
        if (!player.hasPlayedBefore()) {
            Utils.broadcastExcept(player,
                    Lang.VICE.f("&7Welcome " + user.getColoredName(player) + "&7 to &7&lGrand Theft Minecart&r!"));
            player.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
            player.chat("/tutorial start Vice");
        } else if (viceUser.isArrested())
            player.teleport(Vice.getWorldManager().getWarpManager().getJail().getLocation());
        Head head = Vice.getShopManager().getHead(player.getName());
        if (head != null && head.getBidderUUID() != null && !Objects.equals(head.getBidderUUID(), player.getUniqueId()) && !head.isDone())
            player.sendMessage(Lang.HEAD_AUCTION.f("&7Your head is currently being auctioned by &a&l" + head.getSellerName() + "&7! The last bidder was &a&l" + head.getBidderName() + "&7 for &a$&l" + head.getBid() + "&7!"));

        // Vice.getLottery().joinCheck(player, user, viceUser);

        ViceUtils.updateBoard(player, viceUser);
        player.setWalkSpeed(0.2F);
        if (Objects.equals(player.getUniqueId().toString(), "0e4a6028-3d9a-4a2e-9797-eb1ddcb0aca9")) {
            Bukkit.getOnlinePlayers().forEach(target -> {
                User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                targetUser.addAchievement(Achievement.Witness);
            });
        }
        NMSVersion version = NMSVersion.getVersion(player);
        if (version == NMSVersion.MC_1_8) {
            Utils.sendTitle(player, "&4&lTexturepack Load Fail", "&cRecommended: 1.9.4+", 20, 120, 20);
            player.sendMessage("");
            player.sendMessage(Utils.f("&4&lUnable to load the texture pack for the version that you have joined with. Use 1.9.4+ for an optimal experience."));
            player.sendMessage("");
        } else {
            sendPack(player, Vice.getResourcePackManager().getResourcePack(version));
        }

        NPC spawnedLoggerNPC = Vice.getCombatLogManager().getSpawnedNPCFromPlayer(player.getUniqueId());
        if (spawnedLoggerNPC != null) {
            Vice.getCombatLogManager().removeNPC(spawnedLoggerNPC);
            spawnedLoggerNPC.remove();
        }

        Optional<CombatLogger> logger = Vice.getCombatLogManager().getDestroyedCombatLogger(player.getUniqueId());
        if (logger.isPresent()) {
            List<ItemStack> contents;
            if (logger.get().getContents() == null) {
                contents = new ArrayList<>();
                for (ItemStack is : player.getInventory().getContents()) {
                    if (is == null || is.getType() == Material.AIR || is.getType() == Material.WATCH)
                        continue;
                    if (ViceUtils.isDefaultPlayerItem(is))
                        continue;
                    if (logger.get().isFromSpawn() && (Vice.getItemManager().getItem(is) == null || !Vice.getItemManager().getItem(is).isScheduled()))
                        continue;
                    contents.add(is);
                }
            } else {
                contents = logger.get().getContents();
            }
            for (ItemStack is : contents) {
                if (is != null)
                    player.getInventory().removeItem(is);
            }
            for (int i = 36; i < 40; i++) {
                ItemStack is = player.getInventory().getItem(i);
                if (is == null || is.getType() == Material.AIR)
                    continue;
                if (contents.contains(is)) {
                    player.getInventory().setItem(i, null);
                }
            }
            player.updateInventory();
            Vice.getCombatLogManager().clearRemovedItems(player.getUniqueId());
            player.sendMessage(Lang.COMBATTAG.f("&e&lYour combatlog NPC was killed so items were removed from your inventory."));
            player.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
        }
        ArmorUpgrade.reloadArmorUpgrades(player);
        if (viceUser.getCheatCodeState(CheatCode.NIGHTVIS).getState() == State.ON) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        }
    }

    private void sendPack(Player player, ResourcePack pack) {
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null || !p.isOnline()) return;

                if (!p.isValid()) sendPack(p, pack);

                if (pack != null) {
                    p.setResourcePack(pack.getPack());
                }
            }
        }.runTaskLater(Vice.getInstance(), 20L);
    }

    @EventHandler
    protected final void onResourcePack(ResourcePackEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (viceUser == null) return;

        player.sendTitle(Utils.f("&d&lWelcome to &oVice City!"),
                "",
                60,
                30,
                40);

        if (viceUser.getKills() < player.getStatistic(Statistic.PLAYER_KILLS))
            viceUser.setKills(player.getStatistic(Statistic.PLAYER_KILLS));
        if (viceUser.getDeaths() < player.getStatistic(Statistic.DEATHS))
            viceUser.setDeaths(player.getStatistic(Statistic.DEATHS));
        viceUser.checkAchievements();
    }
}
