package com.j0ach1mmall3.wastedguns;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 12/09/2016
 */
public final class MathUtil {
    private static final Set<Material> TRANSPARENT_BLOCKS = Arrays.asList(Material.values()).stream().filter(m -> m == Material.BARRIER || !m.isSolid()).collect(Collectors.toSet());

    private MathUtil() {
    }

    public static double getCrossProduct(LivingEntity livingEntity, Location target) {
        if (livingEntity.getWorld() != target.getWorld()) return 1000;

        Location head = livingEntity.getLocation().add(0, livingEntity.getEyeHeight(), 0);

        Vector look = livingEntity.getLocation().getDirection().normalize();
        Vector direction = head.subtract(target).toVector().normalize();
        Vector cp = direction.crossProduct(look);

        return cp.length();
    }

    public static Stream<LivingEntity> getNearbyEntities(Entity entity, double range) {
        return entity.getNearbyEntities(range, range, range).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e);
    }

    public static Stream<LivingEntity> getNearbyEntities(Entity entity, double x, double y, double z) {
        return entity.getNearbyEntities(x, y, z).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e);
    }

    public static Object[] getNearestTarget(LivingEntity livingEntity, Location origin, Vector direction, int range) {
        LivingEntity target = null;
        Location intersection = null;
        double distance = Double.MAX_VALUE;
        for (LivingEntity e : getNearbyEntities(livingEntity, range).collect(Collectors.toList())) {
            if (e.getType() == EntityType.PLAYER) {
                Player player = (Player) e;
                if (player.getGameMode() != GameMode.ADVENTURE && player.getGameMode() != GameMode.SURVIVAL) continue;
            }
            Location i = raytrace(origin, direction, getBoundingBox(e), range);
            if (i != null && i.distance(origin) < distance) {
                target = e;
                intersection = i;
                distance = i.distance(origin);
            }
        }
        return target == null || intersection == null ? null : new Object[]{target, intersection};
    }

    @SuppressWarnings("deprecation")
    public static Block getTargetBlock(Location origin, Vector direction, int range) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        try {
            BlockIterator blockIterator = new BlockIterator(origin.getWorld(), origin.toVector(), direction, 0, range);
            while (blockIterator.hasNext()) {
                Block b = blockIterator.next();
                if((b.getType() == Material.SIGN || b.getType().name().contains("FENCE")) && random.nextBoolean())
                    continue;

                if (!TRANSPARENT_BLOCKS.contains(b.getType()) && !(b.getType().name().endsWith("DOOR") && (((b.getData() & 0x04) != 0) || ((b.getData() & 0x08) != 0 && b.getRelative(BlockFace.DOWN).getType() != Material.TRAP_DOOR && b.getRelative(BlockFace.DOWN).getType() != Material.IRON_TRAPDOOR && (b.getRelative(BlockFace.DOWN).getData() & 0x04) != 0))))
                    return b;
            }
        } catch (IllegalStateException exception) {
        }
        return null;
    }

    private static Location raytrace(Location origin, Vector direction, double[] boundingBox, int range) {
        double invX = 1 / direction.getX();
        double invY = 1 / direction.getY();
        double invZ = 1 / direction.getZ();

        double txmin = ((invX < 0 ? boundingBox[0] : boundingBox[3]) - origin.getX()) * invX;
        double txmax = ((invX < 0 ? boundingBox[3] : boundingBox[0]) - origin.getX()) * invX;
        double tymin = ((invY < 0 ? boundingBox[1] : boundingBox[4]) - origin.getY()) * invY;
        double tymax = ((invY < 0 ? boundingBox[4] : boundingBox[1]) - origin.getY()) * invY;
        double tzmin = ((invZ < 0 ? boundingBox[2] : boundingBox[5]) - origin.getZ()) * invZ;
        double tzmax = ((invZ < 0 ? boundingBox[5] : boundingBox[2]) - origin.getZ()) * invZ;

        if (txmin > tymax || tymin > txmax) return null;
        if (tymin > txmin) txmin = tymin;
        if (tymax < txmax) txmax = tymax;

        if (txmin > tzmax || tzmin > txmax) return null;
        if (tzmin > txmin) txmin = tzmin;
        if (tzmax < txmax) txmax = tzmax;

        return txmin < range && txmax > 0 ? origin.clone().add(direction.clone().multiply(txmin - 0.1)) : null;
    }

    private static double[] getBoundingBox(Entity entity) {
        net.minecraft.server.v1_12_R1.Entity en = ((CraftEntity) entity).getHandle();
        AxisAlignedBB box = en.getBoundingBox();
        return new double[] {box.d, box.e, box.f, box.a, box.b, box.c};

//        try {
//            Object handle = ReflectionAPI.getHandle((Object) entity);
//            Object boundingBox = handle.getClass().getMethod("getBoundingBox").invoke(handle);
//            return new double[]{(double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("d").get(boundingBox),
// (double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("e").get(boundingBox),
// (double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("f").get(boundingBox),
// (double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("a").get(boundingBox),
// (double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("b").get(boundingBox),
// (double) ReflectionAPI.getNmsClass("AxisAlignedBB").getField("c").get(boundingBox)};
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public static Vector getVelocity(Location from, Location to) {
        double dX = from.getX() - to.getX();
        double dY = from.getY() - to.getY();
        double dZ = from.getZ() - to.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + 3.141592653589793D;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);
        return new Vector(x, z, y);
    }

    //
    public static double getExplosionResistanceMultiplier(LivingEntity e) {
        double total = 1;
        ItemStack[] armor = e.getEquipment().getArmorContents();
        for(ItemStack is : armor) {
            if(is==null)
                continue;
            if(!is.hasItemMeta() || !is.getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS))
                continue;
            total += is.getItemMeta().getEnchantLevel(Enchantment.PROTECTION_EXPLOSIONS);
        }
        return total / 2.0;
    }
}
