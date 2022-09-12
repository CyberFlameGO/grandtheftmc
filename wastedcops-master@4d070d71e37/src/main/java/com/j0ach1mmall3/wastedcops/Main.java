package com.j0ach1mmall3.wastedcops;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;

import com.j0ach1mmall3.jlib.commands.Command;
import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.jlib.plugin.JLibPlugin;
import com.j0ach1mmall3.wastedcops.api.Cop;
import com.j0ach1mmall3.wastedcops.api.CopProperties;
import com.j0ach1mmall3.wastedcops.api.events.CopSpawnEvent;
import com.j0ach1mmall3.wastedcops.commands.WCReloadCommandHandler;
import com.j0ach1mmall3.wastedcops.config.Config;
import com.j0ach1mmall3.wastedcops.listeners.CopListener;
import com.j0ach1mmall3.wastedcops.listeners.EntityListener;

import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;

public final class Main extends JLibPlugin<Config> {
    private final Map<Player, Set<LivingEntity>> cops = new ConcurrentHashMap<>();
    private final Map<Player, Long> noTargetTicks = new ConcurrentHashMap<>();

    private final List<Material> disallowedBlocks = Arrays.asList(Material.LEAVES,
            Material.LEAVES_2, Material.COBBLE_WALL, Material.WOOD_STEP, Material.WOOD);

    public static boolean canSeeTarget(LivingEntity cop, Player target) {
        return cop.hasLineOfSight(target);
    }

    public static Location faceLocation(LivingEntity entity, Location to) {
        if (entity.getWorld() != to.getWorld()) {
            return null;
        }
        Location fromLocation = entity.getLocation();

        double xDiff = to.getX() - fromLocation.getX();
        double yDiff = to.getY() - fromLocation.getY();
        double zDiff = to.getZ() - fromLocation.getZ();

        double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

        double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
        double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0D;
        if (zDiff < 0.0D) {
            yaw += Math.abs(180.0D - yaw) * 2.0D;
        }
        Location loc = entity.getLocation();
        loc.setYaw((float) (yaw - 90.0F));
        loc.setPitch((float) (pitch - 90.0F));
        return loc;
    }

    @Override
    public void onEnable() {
        this.reload();
        new EntityListener(this);
        new CopListener(this);
        new WCReloadCommandHandler(this).registerCommand(new Command("WCReload", "wc.reload", ChatColor.RED + "/wcreload"));
    }

    @Override
    public void onDisable() {
        this.cops.values().forEach(s -> s.forEach(LivingEntity::remove));
    }

    public Map<Player, Set<LivingEntity>> getCops() {
        return this.cops;
    }

    public Map<Player, Long> getNoTargetTicks() {
        return this.noTargetTicks;
    }

    public void reload() {
        this.config = new Config(this);
    }

    public Config getCustomConfig() {
        return this.config;
    }

    public void setWantedLevel(Player player, int level) {
        switch(level) {
            case 0:
                level = 0;
                break;
            case 1:
                level = 1;
                break;
            case 2:
                level = 2;
                break;
            case 3:
                level = 4;
                break;
            case 4:
                level = 10;
                break;
            case 5:
                level = 25;
                break;
            default:
                level = 0;
                break;
        }
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (gtmUser != null){
        	gtmUser.setKillCounter(level);
        	GTMUtils.updateBoard(player, gtmUser);
        }
    }

    public int getWantedLevel(Player player) {
    	
        int wantedLevel = 0;
        int[] wantedLevels = new int[]{0, 1, 2, 4, 10, 25};
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (gtmUser != null){
            int killCounter = gtmUser.getKillCounter();
            for (int i = 0; i < wantedLevels.length; i++)
                if (killCounter >= wantedLevels[i])
                    wantedLevel = i;
        }

        return wantedLevel;
    }

    public void resetWantedLevel(Player player) {
    	
    	GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
    	if (gtmUser != null){
    		gtmUser.setKillCounter(0);
    		GTMUtils.updateBoard(player, gtmUser);
    	}
    }

    public void addKill(Player player) {
    	
    	GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
    	if (gtmUser != null){
    		gtmUser.addKillCounter(1);
    		GTMUtils.updateBoard(player, gtmUser);
    	}
    }

    public void addMoney(Player player, int amount) {
        if(amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE){
        	return;
        }
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (gtmUser != null){
        	gtmUser.addMoney(amount);
        	GTMUtils.updateBoard(player, gtmUser);
        }
    }

    private Location getLocationInRange(Location location, int minRange, int maxRange) {
        return location.add(Random.getInt(minRange, maxRange) * (Random.getBoolean() ? 1 : -1), 0, Random.getInt(minRange, maxRange) * (Random.getBoolean() ? 1 : -1));
    }

    public void spawnCopAtLocation(Location location, CopProperties copProperties, Player target) {
        Cop cop = new Cop(this, copProperties);
        CopSpawnEvent copSpawnEvent = new CopSpawnEvent(cop, target, location);
        Bukkit.getPluginManager().callEvent(copSpawnEvent);
        if(copSpawnEvent.isCancelled()) return;
        String entityName = copProperties.getEntity();
        LivingEntity livingEntity;
        location = copSpawnEvent.getLocation();
        location = location.getWorld().getHighestBlockAt(this.getLocationInRange(location, 0, 4)).getLocation();
        if (Objects.equals(entityName, "husk")) {
            livingEntity = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            ((Zombie)livingEntity).setVillagerProfession(Villager.Profession.HUSK);
        } else {
            livingEntity = (Creature) location.getWorld().spawnEntity(location, EntityType.valueOf(entityName));
        }
        livingEntity.setMetadata("Cop", new FixedMetadataValue(this, cop));
        Set<LivingEntity> cops = this.cops.getOrDefault(target, new HashSet<>());
        cops.add(livingEntity);
        this.cops.put(target, cops);
        cop.onSpawn(livingEntity, copSpawnEvent.getTarget());
        if (this.disallowedBlocks.contains(location.getBlock().getType())) {
            livingEntity.setHealth(0);
            livingEntity.remove();
            cop.onDestroy(livingEntity);
            return;
        }
        if(location.getBlockY() > 100) {
            cop.onDestroy(livingEntity);
        }
    }
}
