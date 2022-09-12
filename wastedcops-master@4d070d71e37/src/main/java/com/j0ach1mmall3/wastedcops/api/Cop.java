package com.j0ach1mmall3.wastedcops.api;

import com.j0ach1mmall3.jlib.integration.Placeholders;
import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import com.j0ach1mmall3.jlib.nms.pathfinding.WrappedPathfinderGoalSelector;
import com.j0ach1mmall3.wastedcops.Main;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedguns.api.weapons.Weapon;
import com.j0ach1mmall3.wastedguns.api.weapons.ranged.RangedWeapon;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 4/06/2016
 */
public final class Cop {
    private static final Class ENTITY_CREATURE_CLASS = ReflectionAPI.getNmsClass("EntityCreature");
    private static final Class ENTITY_INSENTIENT_CLASS = ReflectionAPI.getNmsClass("EntityInsentient");
    private static final Class ENTITY_HUMAN_CLASS = ReflectionAPI.getNmsClass("EntityHuman");
    private static final Class PATHFINDER_GOAL_FLOAT_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalFloat");
    private static final Class PATHFINDER_GOAL_MELEE_ATTACK_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalMeleeAttack");
    private static final Class PATHFINDER_GOAL_MOVE_TOWARDS_RESTRICTION_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalMoveTowardsRestriction");
    private static final Class PATHFINDER_GOAL_RANDOM_STROLL_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalRandomStroll");
    private static final Class PATHFINDER_GOAL_LOOK_AT_PLAYER_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalLookAtPlayer");
    private static final Class PATHFINDER_GOAL_RANDOM_LOOKAROUND_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalRandomLookaround");
    private static final Class PATHFINDER_GOAL_HURT_BY_TARGET_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalHurtByTarget");
    private static final Class PATHFINDER_GOAL_NEAREST_ATTACKABLE_TARGET_CLASS = ReflectionAPI.getNmsClass("PathfinderGoalNearestAttackableTarget");

    private final Main plugin;
    private final CopProperties copProperties;
    private Weapon weapon;
    private boolean weaponCooldown;

    public Cop(Main plugin, CopProperties copProperties) {
        this.plugin = plugin;
        this.copProperties = copProperties;
    }

    public Main getPlugin() {
        return this.plugin;
    }

    public CopProperties getCopProperties() {
        return this.copProperties;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean isWeaponCooldown() {
        return this.weaponCooldown;
    }

    public void setWeaponCooldown(boolean weaponCooldown) {
        this.weaponCooldown = weaponCooldown;
    }

    public void onSpawn(LivingEntity livingEntity, Player target) {
        if(!this.copProperties.getWeapons().isEmpty()) {
            String w = this.copProperties.getWeapons().get(ThreadLocalRandom.current().nextInt(this.copProperties.getWeapons().size()));
            Optional<Weapon> weapon = ((com.j0ach1mmall3.wastedguns.Main) Bukkit.getPluginManager().getPlugin("WastedGuns")).getWeapon(w);
            if (weapon.isPresent()) this.weapon = weapon.get();
        }
        livingEntity.setCustomName(Placeholders.parse(this.copProperties.getName(), target));
        livingEntity.setCustomNameVisible(true);
        livingEntity.setMaxHealth(this.copProperties.getHealth());
        livingEntity.setHealth(livingEntity.getMaxHealth());
        livingEntity.setCanPickupItems(false);
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setAI(true);
        if(livingEntity.getType() == EntityType.WOLF) {
            ((Wolf) livingEntity).setAngry(true);
            ((Wolf) livingEntity).setAdult();
            ((Wolf) livingEntity).setSitting(false);
            ((Wolf) livingEntity).setTarget(target);
        }
        if(livingEntity.getType() == EntityType.ZOMBIE || livingEntity.getType() == EntityType.PIG_ZOMBIE) {
            ((Zombie) livingEntity).setBaby(false);
            ((Zombie) livingEntity).setTarget(target);
        }
        if (this.weapon != null) livingEntity.getEquipment().setItemInMainHand(this.weapon.getItemStack());
        livingEntity.getEquipment().setHelmet(new ItemStack(Material.STONE_BUTTON));

        try {
            WrappedPathfinderGoalSelector goalSelector = new WrappedPathfinderGoalSelector(WrappedPathfinderGoalSelector.Type.GOAL_SELECTOR, (Creature)livingEntity);
            goalSelector.getActive().clear();
            goalSelector.getInactive().clear();
            Object handle = ReflectionAPI.getHandle((Object) livingEntity);

            goalSelector.add(1, PATHFINDER_GOAL_FLOAT_CLASS.getConstructor(ENTITY_INSENTIENT_CLASS).newInstance(handle));
            goalSelector.add(2, PATHFINDER_GOAL_MELEE_ATTACK_CLASS.getConstructor(ENTITY_CREATURE_CLASS, double.class, boolean.class).newInstance(handle, 1.0D, false));
            goalSelector.add(5, PATHFINDER_GOAL_MOVE_TOWARDS_RESTRICTION_CLASS.getConstructor(ENTITY_CREATURE_CLASS, double.class).newInstance(handle, 1.0D));
            goalSelector.add(7, PATHFINDER_GOAL_RANDOM_STROLL_CLASS.getConstructor(ENTITY_CREATURE_CLASS, double.class).newInstance(handle, 1.0D));
            goalSelector.add(8, PATHFINDER_GOAL_LOOK_AT_PLAYER_CLASS.getConstructor(ENTITY_INSENTIENT_CLASS, Class.class, float.class).newInstance(handle, ENTITY_HUMAN_CLASS, 8.0F));
            goalSelector.add(8, PATHFINDER_GOAL_RANDOM_LOOKAROUND_CLASS.getConstructor(ENTITY_INSENTIENT_CLASS).newInstance(handle));
            goalSelector.apply((Creature)livingEntity);

            WrappedPathfinderGoalSelector targetSelector = new WrappedPathfinderGoalSelector(WrappedPathfinderGoalSelector.Type.TARGET_SELECTOR, (Creature)livingEntity);
            targetSelector.getActive().clear();
            targetSelector.getInactive().clear();
            targetSelector.add(1, PATHFINDER_GOAL_HURT_BY_TARGET_CLASS.getConstructor(ENTITY_CREATURE_CLASS, boolean.class, Class[].class).newInstance(handle, false, new Class[0]));
            targetSelector.add(2, PATHFINDER_GOAL_NEAREST_ATTACKABLE_TARGET_CLASS.getConstructor(ENTITY_CREATURE_CLASS, Class.class, boolean.class).newInstance(handle, ENTITY_HUMAN_CLASS, false));
            targetSelector.apply((Creature)livingEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Creature)livingEntity).setTarget(target);
        livingEntity.teleport(Main.faceLocation(livingEntity, target.getEyeLocation()));
    }

    public void onDestroy(LivingEntity zombie) {
        zombie.setHealth(0);
        zombie.remove();
        this.plugin.getCops().values().forEach(s -> s.remove(zombie));
    }

    public boolean onTick(LivingEntity cop, Player player) {
        if (cop == null || cop.isDead() || player.getWorld() != cop.getWorld()) {
            this.onDestroy(cop);
            return false;
        }
        if (cop.isInsideVehicle()) cop.getVehicle().eject();

        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (houseUser.isInsideHouse() || houseUser.isInsidePremiumHouse()) {
            this.onDestroy(cop);
            return false;
        }

        if(!Main.canSeeTarget(cop, player)) {
            return false;
        }

        boolean valid = !player.isDead() && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE && player.getWorld().getName().equals(cop.getWorld().getName()) && this.plugin.getWantedLevel(player) > 0 && cop.hasLineOfSight(player);
        double distance = cop.getLocation().distance(player.getLocation());
        if (valid && !this.weaponCooldown && this.weapon instanceof RangedWeapon && distance <= ((RangedWeapon) this.weapon).getRange()) {
            WeaponRightClickEvent weaponRightClickEvent = new WeaponRightClickEvent(cop, this.weapon);
            Bukkit.getPluginManager().callEvent(weaponRightClickEvent);
            this.weaponCooldown = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.weaponCooldown = false, 4L);
        }
        return valid;
    }
}
