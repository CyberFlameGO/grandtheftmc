package com.j0ach1mmall3.wastedguns.listeners;

import com.j0ach1mmall3.wastedguns.Main;
import org.bukkit.event.Listener;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class EntityListener implements Listener {
    private final Main plugin;

    public EntityListener(Main plugin) {
        this.plugin = plugin;
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
//            for (World world : Bukkit.getWorlds()) {
//                for(Entity entity : world.getEntities()){
//                    if(entity.hasMetadata("Explosive") && entity.isOnGround()){
//                        ((Explosive) entity.getMetadata("Explosive").get(0).value()).onLand(entity);
//                    }else if(entity.hasMetadata("Airstrike") && entity.isOnGround()){
//                        ((Airstrike) entity.getMetadata("Airstrike").get(0).value()).onLand(entity, (LivingEntity) entity.getMetadata("Shooter").get(0).value());
//                    }else if(entity.hasMetadata("AirstrikeBomb") && entity instanceof ArmorStand && entity.isOnGround()){
//                        ((Airstrike) entity.getMetadata("AirstrikeBomb").get(0).value())
//                                .onExplode(entity, (LivingEntity) entity.getMetadata("Shooter").get(0).value());
//                        entity.remove();
//                    }
//                }
//            }
//        }, 5, 5);
    }

//    @EventHandler
//    public void onProjectileHit(ProjectileHitEvent e) {
//        Projectile projectile = e.getEntity();
//
//        if(!(e.getEntity().getShooter() instanceof LivingEntity)) return;
//
//        LivingEntity who = (LivingEntity)e.getEntity().getShooter();
//
//        if(projectile.hasMetadata("Rocket")) ((Launcher) projectile.getMetadata("Rocket").get(0).value()).onHit(projectile);
//        if(projectile.hasMetadata("Explosive")) ((Explosive) projectile.getMetadata("Explosive").get(0).value()).onExplode(projectile, who);
//    }
//
//    //TODO see if the every 1/4 second method is ok for this
//    /*
//    @EventHandler
//    public void onEntityDamage(EntityDamageEvent event){
//        if(event.getEntity().hasMetadata("AirstrikeBomb") && event.getEntity() instanceof ArmorStand){
//            event.setDamage(0);
//            Entity entity = event.getEntity();
//            ((Airstrike) entity.getMetadata("AirstrikeBomb").get(0).value())
//                    .onExplode(entity, (LivingEntity) entity.getMetadata("Shooter").get(0).value());
//            event.getEntity().remove();
//        }
//    }*/
//
//    @EventHandler
//    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
//        if(e.getDamager().hasMetadata("StickyExplosive") && e.getDamager() instanceof Arrow && e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof ArmorStand)) {
//            e.setDamage(0);
//            Entity entity = e.getEntity();
//            Sounds.broadcastSound(Sound.ENTITY_ARROW_HIT, entity.getLocation());
//            Player player = (Player) e.getDamager().getMetadata("Shooter").get(0).value();
//            entity.setMetadata("StickyExplosive", new FixedMetadataValue(this.plugin, e.getDamager().getMetadata("StickyExplosive").get(0).value()));
//            entity.setMetadata("Shooter", new FixedMetadataValue(this.plugin, player));
//            Set<Entity> stickyBombs = this.plugin.getStickyBombs().containsKey(player) ? this.plugin.getStickyBombs().get(player) : new HashSet<>();
//            stickyBombs.remove(e.getDamager());
//            stickyBombs.add(entity);
//            this.plugin.getStickyBombs().put(player, stickyBombs);
//            this.plugin.getEntityQueue().remove(e.getDamager());
//            e.getDamager().remove();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    @EventHandler
//    public void onExplode(BlockExplodeEvent e) {
//        if(this.plugin.getBabies().isFakeExplosionDebris()) {
//            for (Block b : e.blockList()) {
//                if(!b.getType().isSolid()) continue;
//                Location l = b.getLocation();
//                l.setPitch(-Random.getInt(180));
//                l.setYaw(Random.getInt(360));
//                FallingBlock fb = l.getWorld().spawnFallingBlock(l, b.getType(), b.getData());
//                fb.setDropItem(false);
//                fb.setHurtEntities(false);
//                float x = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
//                float y = -2.0F + (float)(Math.random() * 4.0D + 1.0D);
//                float z = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
//                fb.setVelocity(new Vector(x, y, z));
//                this.plugin.getEntityQueue().add(fb);
//                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> b.getState().update(), 5);
//            }
//        }
//        e.blockList().clear();
//    }
//
//    @SuppressWarnings("deprecation")
//    @EventHandler
//    public void onExplode(EntityExplodeEvent e) {
//        if(this.plugin.getBabies().isFakeExplosionDebris()) {
//            for (Block b : e.blockList()) {
//                if(!b.getType().isSolid()) continue;
//                Location l = b.getLocation();
//                l.setPitch(-Random.getInt(180));
//                l.setYaw(Random.getInt(360));
//                FallingBlock fb = l.getWorld().spawnFallingBlock(l, b.getType(), b.getData());
//                fb.setDropItem(false);
//                fb.setHurtEntities(false);
//                float x = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
//                float y = -2.0F + (float)(Math.random() * 4.0D + 1.0D);
//                float z = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
//                fb.setVelocity(new Vector(x, y, z));
//                this.plugin.getEntityQueue().add(fb);
//                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> b.getState().update(), 5);
//            }
//        }
//        e.blockList().clear();
//    }
//
//    @EventHandler
//    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
//        if(e.getEntity() instanceof FallingBlock) {
//            this.plugin.getEntityQueue().remove(e.getEntity());
//            e.setCancelled(true);
//            Block b = e.getBlock();
//            if(this.plugin.getBabies().isFakeExplosionDebrisParticles()) b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, e.getTo());
//        }
//    }
}
