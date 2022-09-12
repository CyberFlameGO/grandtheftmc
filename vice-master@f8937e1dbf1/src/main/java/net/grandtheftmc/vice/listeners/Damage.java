package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.guns.weapon.ThrowableWeapon;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Damage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity victimEntity = e.getEntity();
        if (!(victimEntity instanceof Player)) return;
        Player victim = (Player) victimEntity;
        if (victim.getGameMode() == GameMode.SPECTATOR || victim.getWorld().equals(Vice.getWorldManager().getWarpManager().getSpawn().getLocation().getWorld())) {
            e.setCancelled(true);
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            ViceUser user = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
            if (user.hasTeleportProtection())
                e.setCancelled(true);
        }
        else if(e.getCause()== EntityDamageEvent.DamageCause.FALL) {
            ViceUser user = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
            if(user.getCheatCodeState(CheatCode.JELLYLEGS).getState()== State.ON){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeaponExplode(ExplosionDamageEntityEvent event){
        Entity entity = event.getProjectile();
        if(entity.hasMetadata("Rocket")){
            double range = ((LauncherWeapon)entity.getMetadata("Rocket").get(0).value()).getExplosionSize();
            Vice.getWorldManager().getObsidianManager().updateRange(range, entity, 1);
        }
        else if(entity.hasMetadata("ProximityExplosive")){
            double range = ((ThrowableWeapon)entity.getMetadata("ProximityExplosive").get(0).value()).getExplosionSize();
            Vice.getWorldManager().getObsidianManager().updateRange(range, entity, 1);
        }
        else if(entity.hasMetadata("StickyExplosive")){
            double range = ((ThrowableWeapon)entity.getMetadata("StickyExplosive").get(0).value()).getExplosionSize();
            Vice.getWorldManager().getObsidianManager().updateRange(range, entity, 1);
        }
        else if(entity.hasMetadata("Explosive")){
            double range = ((LauncherWeapon)entity.getMetadata("Explosive").get(0).value()).getExplosionSize();
            Vice.getWorldManager().getObsidianManager().updateRange(range, entity, 1);
        }
    }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onExplode(EntityExplodeEvent event){
       Entity e = event.getEntity();
       if(!event.isCancelled() && event.getEntityType()==EntityType.PRIMED_TNT){
           Vice.getWorldManager().getObsidianManager().updateRange(3, e, 1);
       }
   }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageMonitor(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        user.updateTintHealth(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
    }

    @EventHandler
    public void onExplosionDamageEntityEvent(ExplosionDamageEntityEvent event) {
        if (event.getLivingEntity().getType() != EntityType.PLAYER) return;
        Player damager = (Player) event.getLivingEntity();
        for (LivingEntity livingEntity : event.getVictims()) {
            if (livingEntity.getType() != EntityType.PLAYER) continue;
            Player victim = (Player) livingEntity;
            ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
            if (victimGameUser.hasTeleportProtection()) {
                event.getVictims().remove(livingEntity);
                return;
            }
            ViceUser damagerGameUser = Vice.getUserManager().getLoadedUser(damager.getUniqueId());
            if (damagerGameUser.hasTeleportProtection()) {
                event.getVictims().remove(livingEntity);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() == null) return;
        Entity entity = e.getDamager();
        if (entity == null) return;
        if (entity instanceof Projectile) {
            entity = (Entity) ((Projectile) entity).getShooter();
        } else if (entity instanceof Tameable) {
            entity = (Entity) ((Tameable) entity).getOwner();
        }
        if (!(e.getEntity() instanceof Player && entity instanceof Player)) return;
        Player damager = (Player) entity;
        Player victim = (Player) e.getEntity();
        ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
        User coreVictimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
        if (victimGameUser.hasTeleportProtection()) {
            e.setCancelled(true);
            long expires = TimeUnit.MILLISECONDS.toSeconds(victimGameUser.getTimeUntilTeleportProtectionExpires());
            if (expires <= 1) {
                victimGameUser.setLastTeleport(0);
                return;
            }
            damager.sendMessage(Lang.GTM.f(coreVictimUser.getColoredName(victim) + " &7has teleport protection for &a" + expires + "&7 seconds!"));
            return;
        }
        ViceUser damagerGameUser = Vice.getUserManager().getLoadedUser(damager.getUniqueId());
        if (damagerGameUser.hasTeleportProtection()) {
            e.setCancelled(true);
            damagerGameUser.setLastTeleport(0);
            damager.sendMessage(Lang.COMBATTAG.f("&7Your teleport protection has ended!"));
        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageByEntityMonitor(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();
        switch (entity.getType()) {
            case PLAYER: {
                if (e.isCancelled())
                    return;
                Player player = (Player) entity;
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if (damager == null)
                    return;
                if (damager instanceof Projectile)
                    damager = (Entity) ((Projectile) damager).getShooter();
                else if (entity instanceof Tameable)
                    damager = (Entity) ((Tameable) entity).getOwner();
                if (!(damager instanceof Player && entity instanceof Player))
                    return;
                if (!user.isInCombat())
                    player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are now in combat! Do not log out for 10 seconds!"));
//                Core.getUserManager().getLoadedUser(player.getUniqueId()).hidePet(player);
                user.setLastTag(System.currentTimeMillis());
                if (Vice.getWorldManager().getWarpManager().cancelTaxi(player, user))
                    player.sendMessage(Utils.f(Lang.TAXI + "&eYour cab was cancelled!"));
                Player dmger = (Player) damager;
                ViceUser damagerUser = Vice.getUserManager().getLoadedUser(dmger.getUniqueId());
                if (!damagerUser.isInCombat())
                    dmger.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are now in combat! Do not log out for 20 seconds!"));
//                Core.getUserManager().getLoadedUser(dmger.getUniqueId()).hidePet(dmger);
                damagerUser.setLastTag(System.currentTimeMillis());
                if (Vice.getWorldManager().getWarpManager().cancelTaxi(dmger, damagerUser))
                    player.sendMessage(Utils.f(Lang.TAXI + "&eYour cab was cancelled!"));
                if (dmger.getInventory().getItemInMainHand() != null) {
                    Optional<Drug> heroin = ((DrugService) Vice.getDrugManager().getService()).getDrug("heroin");
                    if (heroin.isPresent()) {
                        if (dmger.getInventory().getItemInMainHand().getDurability() == 5 && dmger.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
                            heroin.get().apply(player);
                            dmger.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        }
                    }
                }
                return;

            }
            case ITEM_FRAME: {
                if (!(damager instanceof Player))
                    return;
                Player player = (Player) damager;
                ItemFrame frame = (ItemFrame) entity;
                if (Core.getUserManager().getLoadedUser(player.getUniqueId()).hasEditMode())
                    return;
                ItemStack item = frame.getItem();
                if (item == null)
                    return;
                Vice.getShopManager().buy(player, frame.getItem());
                return;
            }
            case VILLAGER:
                if(!(damager instanceof Player))
                    return;
                NPC npc = (Villager)entity;
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(damager.getUniqueId());
                if(damager.getWorld().getName().equalsIgnoreCase("spawn") && !viceUser.isCop()) {
                    damager.sendMessage(Lang.COMBATTAG.f("&7You cannot pickup the items from this NPC as they are in spawn and you are not a cop!"));
                    return;
                }

                List<ItemStack> contents = Vice.getCombatLogManager().getPlayerInventory(npc);
                if(contents==null)
                    return;
                for(ItemStack is : contents)
                    if(is!=null)
                        entity.getWorld().dropItemNaturally(entity.getLocation(), is);
                Vice.getCombatLogManager().addDestroyedNPC(npc);
                Vice.getCombatLogManager().removeNPC(npc);
                npc.remove();
                return;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHealthChangeMonitor(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Vice.getUserManager().getLoadedUser(player.getUniqueId()).updateTintHealth(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
    }

}