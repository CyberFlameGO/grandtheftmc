package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public final class DrugComponent implements Component<DrugComponent, Vice> {

    private final Random random;
    private BukkitTask task;

    public DrugComponent(Vice vice) {
        this.random = new Random();

        SeedType.init();

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                for (SeedType type : SeedType.values()) {
                    for (SeedDrop drop : type.getDrops()) {
                        if (System.currentTimeMillis() < drop.getLast()) continue;
                        drop.next();

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if (!player.getLocation().getWorld().getName().equals("spawn") && player.getLocation().getBlock().getBiome() == drop.getBiome()) {
                                dropItem(type.getItemStack(), player.getLocation(), drop.getRange());
                            }
                        });
                    }
                }
            }
        }.runTaskTimer(vice, 1000, 20 * 10);
    }

    @Override
    public DrugComponent onDisable(Vice plugin) {
        this.task.cancel();
        return this;
    }

    private void dropItem(ItemStack itemStack, Location location, int radius) {
        int x = this.random.nextInt(radius);
        int z = this.random.nextInt(radius);
        if (this.random.nextBoolean()) x = -x;
        if (this.random.nextBoolean()) z = -z;

        Location loc = location.clone().add(x, 0, z);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);

        loc.getWorld().dropItem(loc, itemStack.clone());
    }

    @EventHandler
    protected final void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() == null) return;

        switch (event.getBlock().getType()) {
            case CACTUS:
            case SUGAR_CANE_BLOCK: {
                int i = this.random.nextInt(100);
                if (i <= 2) this.destroyPlant(event.getBlock(), event.getBlock().getType());
                break;
            }

            case POTATO: {
                int i = this.random.nextInt(100);
                event.setDropItems(false);
                if (i <= 90) event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.POTATO_ITEM));
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.POTATO_ITEM));
                break;
            }

            case MELON_BLOCK: {
                event.setDropItems(false);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Vice.getItemManager().getItem("cocaleaf").getItem());
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Vice.getItemManager().getItem("cocaleaf").getItem());

                int i = this.random.nextInt(100);
                if (i <= 3) {
                    for (BlockFace face : new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                        if (event.getBlock().getRelative(face).getType() == Material.MELON_STEM) {
                            event.getBlock().getRelative(face).setType(Material.AIR);
                            break;
                        }
                    }
                }
            }

            case NETHER_STALK:
                event.setDropItems(false);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Vice.getItemManager().getItem("ergotfungi").getItem());

                int i = this.random.nextInt(100);
                if (i <= 2) {
                    for (BlockFace face : new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                        if (event.getBlock().getRelative(face).getType() == Material.NETHER_STALK) {
                            event.getBlock().getRelative(face).setType(Material.AIR);
                            break;
                        }
                    }
                }
                break;

            case LONG_GRASS:
                event.setDropItems(false);
                break;
        }
    }

    private void destroyPlant(Block block, Material material) {
        Location origin = block.getLocation().clone();
        for (int i = block.getY() - 5; i < block.getY() + 5; i++) {
            Block found = origin.getWorld().getBlockAt(origin.getBlockX(), i, origin.getBlockZ());
            if (found.getType() == material) {
                found.setType(Material.AIR);
            }
        }
    }
}
