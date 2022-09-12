package net.grandtheftmc.gtm.drugs.example.lsd;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.DrugUtil;
import net.grandtheftmc.gtm.drugs.categories.examples.Hallucinogens;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LSD extends Hallucinogens {

    private final Hashtable<UUID, HashSet<Location>> changedBlocks = new Hashtable<>();
    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private int task;

    public LSD() {
        super("LSD",30);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        final long addTime = System.currentTimeMillis();
        final int stageDuration = getDuration() + (int)Math.round(getDuration()*ThreadLocalRandom.current().nextDouble(-.25, .25));

        DrugEffect effect = (drug, duration, player) -> {
            final DrugService service = (DrugService) GTM.getDrugManager().getService();
            if (checkCooldown(uuid)) {
                Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
                if (ThreadLocalRandom.current().nextInt(0, 100) <= 4 && !GTM.getDrugManager().inOD(player.getUniqueId())) {
                    GTM.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                    GTM.getDrugManager().addOD(player.getUniqueId());
                    badTrip(player, addTime, duration);
                    return;
                }
                stageOne(player, addTime, stageDuration);
            } else {
                player.sendMessage(Lang.DRUGS.f("&7Hey man, I don't think that you should use it so soon, wait a while bro."));
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }

    private void stageOne(Player player, long addTime, int length) {
        if(player.isValid()) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, length*2*20, 2)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, length*2*20, 1)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.REGENERATION, length*2*20, 1)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, length*2*20, 1)));
            player.setPlayerTime(18000, false);
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                        return;
                    stageTwo(player, addTime, length);
                }
            }.runTaskLater(GTM.getInstance(), length*20);
        }
    }

    private void stageTwo(Player player, long addTime, int length) {
        if(player.isValid()) {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW_DIGGING, length*2*20, 1)));
            final long endTime = System.currentTimeMillis() + (length*1000) + 10000;
            BukkitScheduler scheduler = GTM.getInstance().getServer().getScheduler();
            scheduler.scheduleAsyncRepeatingTask(GTM.getInstance(), new Runnable() {

                @Override
                public void run() {
                    if (System.currentTimeMillis()>=endTime || !GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                        scheduler.cancelTask(task);
                        player.resetPlayerTime();
                        return;
                    }
                    player.playSound(player.getLocation(), DrugUtil.getRandomAmbientSound(), 3.0F, 1.0F);
                }
            }, 0, 20L);
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                        return;
                    stageThree(player, addTime, length);
                }
            }.runTaskLater(GTM.getInstance(), length*20);
        }
    }

    private void stageThree(Player player, long addTime, int length) {
        final double stopTime = System.currentTimeMillis() + (length*1000);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis()>=stopTime|| !GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                    if(player.isOnline() && changedBlocks.containsKey(player.getUniqueId())){
                        changedBlocks.get(player.getUniqueId()).forEach(block -> {
                            player.sendBlockChange(block, block.getWorld().getBlockAt(block).getType(), block.getWorld().getBlockAt(block).getData());
                        });
                    }
                    changedBlocks.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                HashSet<Location> blocks = changedBlocks.containsKey(player.getUniqueId()) ? changedBlocks.get(player.getUniqueId()) : new HashSet<>();
                Collection<Block> nearbyBlocks = DrugUtil.getNearbyBlocks(player.getLocation(), 10);

                nearbyBlocks.forEach(block -> {
                    if(!blocks.contains(block.getLocation())){
                        blocks.add(block.getLocation());
                    }
                    player.sendBlockChange(block.getLocation(), Material.WOOL, (byte)ThreadLocalRandom.current().nextInt(0, 15));
                });
                changedBlocks.put(player.getUniqueId(), blocks);
            }
        }.runTaskTimer(GTM.getInstance(), 0, 20);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                    return;
                stageFive(player, addTime, length);
            }
        }.runTaskLater(GTM.getInstance(), length*20);
    }

    private void stageFive(Player player, long addTime, int length) {
        if(GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, length*20*2, 2)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, length*20*2, 1)));
        }

    }

    public void badTrip(Player player, long addTime, int length) {
        if(GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, length*4, 2)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, length*4, 1)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, length*4, 2)));
            BukkitScheduler scheduler = GTM.getInstance().getServer().getScheduler();
            task = scheduler.scheduleAsyncRepeatingTask(GTM.getInstance(), new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    count += 1;
                    if (count >= 70 || !GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                        scheduler.cancelTask(task);
                        GTM.getDrugManager().removeOD(player.getUniqueId());
                        return;
                    }
                    player.playSound(player.getLocation(), DrugUtil.getRandomParanoiaSound(), 3.0F, 1.0F);
                    player.playSound(player.getLocation(), DrugUtil.getRandomAmbientSound(), 3.0F, 1.0F);
                    player.playEffect(EntityEffect.WITCH_MAGIC);
                    player.playEffect(EntityEffect.ZOMBIE_TRANSFORM);
                }
            }, 10L, 20L);
        }
    }

    private boolean checkCooldown(UUID uuid){
        if(cooldown.containsKey(uuid)){
            if(System.currentTimeMillis()>=(cooldown.get(uuid)+(60*15*1000))){//cooldown has expired
                cooldown.put(uuid, System.currentTimeMillis());
                return true;
            }
            return false;
        }
        cooldown.put(uuid, System.currentTimeMillis());
        return true;
    }
}
