package net.grandtheftmc.gtm.event.christmas;

import de.slikey.effectlib.effect.*;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.users.targets.TrackedEntity;
import net.grandtheftmc.core.users.targets.TrackedLocation;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-12-07.
 */
public class SpawnSantaDropTask{

    private ArmorStand giftPackage;
    private final Location dropPoint;

    public SpawnSantaDropTask(Location loc) {
        this.dropPoint = loc;
        beingStage1();
    }

    private void constructPackage(Location loc){
        giftPackage = (ArmorStand)this.dropPoint.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        giftPackage.setMetadata("CUSTOM", new FixedMetadataValue(GTM.getInstance(), "fuck vehicles"));
        giftPackage.setItemInHand(GTM.getItemManager().getItem("bigpresent").getItem());
        giftPackage.setCollidable(false);
        giftPackage.setBasePlate(false);
        giftPackage.setVisible(false);
        giftPackage.setRightArmPose(new EulerAngle(-Math.PI/2,0,0));
        giftPackage.setGravity(false);
        giftPackage.setRemoveWhenFarAway(false);
    }


    /**
     * @apiNote The beginning of the whole animation. This method handles up to the point of the package reaching the ground.
     */
    private void beingStage1(){
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.translateAlternateColorCodes('&', Lang.CHRISTMAS.toString()), ChatColor.AQUA + "A christmas care package has spawned on the map!", 20, 60, 20));
        double rateOfDecline = 50.0/240;//the rate at decline/2ticks so that it takes a minute for the box to reach the bottom.

        constructPackage(this.dropPoint.clone().add(0,50,0));
        for(Player player : Bukkit.getOnlinePlayers()) {
            Core.getUserManager().getLoadedUser(player.getUniqueId()).addBossBarTarget(new TrackedLocation(this.dropPoint));
        }

        StarEffect star = new StarEffect(GTM.getEffectLib());
        DynamicLocation dLoc = new DynamicLocation(this.giftPackage);
        star.setDynamicOrigin(dLoc);
        star.particle = ParticleEffect.FLAME;
        star.iterations = Integer.MAX_VALUE;
        star.particles = 4;
        star.period = 2;
        star.visibleRange = 55f;
        star.start();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!giftPackage.isValid()) {
                    Location loc = giftPackage.getLocation().clone();
                    giftPackage.remove();
                    constructPackage(loc);
                    star.setDynamicOrigin(new DynamicLocation(giftPackage));
                }
                if(dropPoint.getY()-1>=giftPackage.getLocation().getY()) {
                    cancel();
                    star.cancel();
                    beginStage2();
                    return;
                }


                giftPackage.teleport(giftPackage.getLocation().clone().add(0,-rateOfDecline, 0));
            }
        }.runTaskTimer(GTM.getInstance(), 0, 5);
    }

    private int inverseShotCandyCanes = ThreadLocalRandom.current().nextInt(10,26);
    /**
     * @apiNote The middle of the whole animation. This method handles up to the point of the package shoots all of the candy canes.
     */
    private void beginStage2(){

        SphereEffect effect =  new SphereEffect(GTM.getEffectLib());
        effect.particle = ParticleEffect.FIREWORKS_SPARK;
        effect.iterations = Integer.MAX_VALUE;
        effect.setLocation(this.giftPackage.getLocation().clone().add(.2,1,.2));
        effect.radius = 5;
        effect.particles = 30;
        effect.radiusIncrease = 0;
        effect.start();


        new BukkitRunnable() {
            @Override
            public void run() {

                if(inverseShotCandyCanes<=0){
                    cancel();
                    effect.cancel();
                    beginStage3();
                    return;
                }
                double ranX = ThreadLocalRandom.current().nextDouble(-1.5,1.5);
                double ranY = ThreadLocalRandom.current().nextDouble(1,2);
                double ranZ = ThreadLocalRandom.current().nextDouble(-1.5,1.51);//make velocity in random direction
                FlameEffect trace = new FlameEffect(GTM.getEffectLib());
                Item item = giftPackage.getWorld().dropItem(giftPackage.getLocation().clone().add(0,1.2,0), GTM.getItemManager().getItem("candycane").getItem());

                trace.setEntity(item);
                trace.iterations = 60;
                trace.period = 2;
                trace.particleCount = 1;
                trace.start();

                item.setVelocity(new Vector(ranX, ranY, ranZ));
                item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 10);

                inverseShotCandyCanes--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 30*1);
    }
    /**
     * @apiNote The end of the whole animation. This method cleans everything up.
     */
    private void beginStage3(){
        final long startTime = System.currentTimeMillis();


        new BukkitRunnable() {
            boolean flip = true;
            float cRadius = 2f;
            @Override
            public void run() {
                if(startTime+8000<System.currentTimeMillis()) {
                    cancel();
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        Core.getUserManager().getLoadedUser(player.getUniqueId()).removeBossBarTarget(dropPoint);
                    }
                    giftPackage.remove();
                    return;
                }

                giftPackage.getWorld().playSound(giftPackage.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 10, 10);

                VortexEffect vortex = new VortexEffect(GTM.getEffectLib());
                vortex.setLocation(giftPackage.getLocation().clone());
                vortex.particle = ParticleEffect.SPELL_MOB;
                vortex.color = flip ? Color.GREEN : Color.RED;
                vortex.iterations = 20;
                vortex.period = 2;
                vortex.circles = 2;
                vortex.radius = cRadius;
                vortex.particleCount = 10;
                cRadius -= .095f;
                flip = !flip;
                vortex.start();

                giftPackage.teleport(giftPackage.getLocation().clone().add(0,-.05, 0));
            }
        }.runTaskTimer(GTM.getInstance(), 0, 4);
    }
}
