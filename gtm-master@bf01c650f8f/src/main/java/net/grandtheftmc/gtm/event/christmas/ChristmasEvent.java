package net.grandtheftmc.gtm.event.christmas;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.event.BaseEvent;
import net.grandtheftmc.core.event.EventType;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Timothy Lampen on 2017-12-07.
 */
public class ChristmasEvent extends BaseEvent implements Listener{


    private List<Location> spawnLocations = new ArrayList<>();

    private BossBar bossBar = null;

    private BukkitTask updateEventBarTask;
    private BukkitTask spawnSantaDropTask;

    public ChristmasEvent(Plugin plugin, long startTime, long endTime) {
        super(plugin, EventType.CHRISTMAS.getId(), startTime, endTime);
        GTM.getInstance().getServer().getPluginManager().registerEvents(this, GTM.getInstance());

    }

    @Override
    public void onInit() {

    }

    @Override
    public void onStart() {
        this.bossBar = Bukkit.createBossBar(Utils.f("&c&lC&2&lh&c&lr&2&li&c&ls&2&lt&c&lm&2&la&c&ls&2&l!"), BarColor.GREEN, BarStyle.SOLID);


        this.spawnLocations.addAll(Arrays.asList(
                new Location(Bukkit.getWorld("minesantos"), -188,69,226),
                new Location(Bukkit.getWorld("minesantos"), 401,68,31),
                new Location(Bukkit.getWorld("minesantos"), 32,86,-212),
                new Location(Bukkit.getWorld("minesantos"), -604,64,-170),
                new Location(Bukkit.getWorld("minesantos"), 256,84,430),
                new Location(Bukkit.getWorld("minesantos"), -320,74,158),
                new Location(Bukkit.getWorld("minesantos"), 7,73,197),
                new Location(Bukkit.getWorld("minesantos"), -9,153,-129),
                new Location(Bukkit.getWorld("minesantos"), 323,121,303),
                new Location(Bukkit.getWorld("minesantos"), -157,74,159),
                new Location(Bukkit.getWorld("minesantos"), 95,83,187),
                new Location(Bukkit.getWorld("minesantos"), -210,94,422),
                new Location(Bukkit.getWorld("minesantos"), -261,74,-16),
                new Location(Bukkit.getWorld("minesantos"), -293,74,369),
                new Location(Bukkit.getWorld("minesantos"), -116,73,57)
        ));

        this.spawnSantaDropTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnSantaDrop();
            }
        }.runTaskTimer(Core.getInstance(), 20*60*8,20*60*ThreadLocalRandom.current().nextInt(60,120));

        this.updateEventBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateBossBar();
            }
        }.runTaskTimerAsynchronously(getPlugin(), 0, 20*60);


        for(Player player : Bukkit.getOnlinePlayers())
            this.bossBar.addPlayer(player);


    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(this.bossBar!=null)
            this.bossBar.addPlayer(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(this.bossBar!=null)
            this.bossBar.removePlayer(player);
    }

    @Override
    public void onEnd() {
        this.updateEventBarTask.cancel();
        this.spawnSantaDropTask.cancel();

        this.bossBar.removeAll();
        this.bossBar = null;

    }

    private void spawnSantaDrop(){
        List<Location> copy = spawnLocations.stream().filter(loc -> loc.getChunk().isLoaded()).collect(Collectors.toList());
        if(copy.size() == 0) {
            GTM.error("Could not spawn a christmas drop because no players are currently near the drop locations");
            return;
        }
        new SpawnSantaDropTask(copy.get(ThreadLocalRandom.current().nextInt(0,copy.size())));
    }



    private void updateBossBar() {

        // how many seconds left until over
        int secondsLeft = (int) ((getEndTime() - System.currentTimeMillis()) / 1000.0);

        int day = (int) TimeUnit.SECONDS.toDays(secondsLeft);
        long hours = TimeUnit.SECONDS.toHours(secondsLeft) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(secondsLeft) - (TimeUnit.SECONDS.toHours(secondsLeft) * 60);

        String timeLeft = day + "d " + hours + "h " + minute + "m";
        String title = Utils.f("&c&lC&2&lh&c&lr&2&li&c&ls&2&lt&c&lm&2&la&c&ls&2&l! ") + ChatColor.GOLD + ChatColor.BOLD + timeLeft;

        double start = System.currentTimeMillis() - getStartTime();
        double end = getEndTime() - getStartTime();

        // this gives us how through we are
        double through = start / end;
        double progress = 1 - through;

        this.bossBar.setTitle(title);
        this.bossBar.setProgress(progress);
    }

    public static void removeCandyCanes(Player player, int amount){
        int deleted = 0;
        ItemStack candyCane = GTM.getItemManager().getItem("candycane").getItem();
        for(int i = 0 ; i<player.getInventory().getSize(); i++){
            ItemStack is = player.getInventory().getItem(i);

            if(is==null || is.getType()== Material.AIR)
                continue;
            if(candyCane.isSimilar(is)) {
                if(deleted + is.getAmount() >= amount) {
                    is.setAmount(is.getAmount()-(amount-deleted));
                    player.getInventory().setItem(i, is);
                    return;
                }
                else {
                    deleted += is.getAmount();
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
        player.updateInventory();
    }

    public static boolean hasCandyCanes(Player player, int amount){
        int candyCanes = 0;
        ItemStack candyCane = GTM.getItemManager().getItem("candycane").getItem();
        for(ItemStack is : player.getInventory().getContents()) {
            if(is==null || is.getType()==Material.AIR)
                continue;
            if(candyCane.isSimilar(is)) {
                if (candyCanes + is.getAmount() >= amount)
                    return true;
                candyCanes += is.getAmount();
            }
        }
        return false;
    }
}
