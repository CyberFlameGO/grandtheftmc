package net.grandtheftmc.hub.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.wrapper.packet.AbstractPacket;
import net.grandtheftmc.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class PortalComponent implements Component<PortalComponent, Hub> {

    private CustomPlayServerBlockChange[][] positions;
    private final Random random;

    public PortalComponent(JavaPlugin plugin) {
        this.positions = new CustomPlayServerBlockChange[23][17];
        this.random = new Random();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        World world = Bukkit.getWorld("world");
        Location loc1 = new Location(world, 351.5, 134, -130.5);
        Location loc2 = new Location(world, 337.5, 112, -130.5);

        int arr1 = -1;
        for (int y = loc1.getBlockY(); y > loc2.getBlockY() - 1; y--) {
            arr1++;

            int arr2 = -1;
            for (int x = loc1.getBlockX(); x > loc2.getBlockX() - 1; x--) {
                arr2++;

                boolean b = random.nextInt(2) == 1;
                WrappedBlockData blockData = WrappedBlockData.createData(b ? Material.STAINED_GLASS : Material.STAINED_GLASS_PANE, b ? (random.nextBoolean() ? 5 : 13) : 8);

                CustomPlayServerBlockChange info = new CustomPlayServerBlockChange();
                info.setLocation(new BlockPosition(x, y, loc1.getBlockZ()));
                info.setBlockData(blockData);

                this.positions[arr1][arr2] = info;
            }
        }
    }

    @Override
    public PortalComponent onEnable(Hub plugin) {

        //Scrolling portal effect
//        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
//            for (CustomPlayServerBlockChange[] array : this.positions) {
//                for (CustomPlayServerBlockChange packet : array) {
//                    if (packet == null) continue;
//                    if (!packet.isVisible()) continue;
//
//                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        packet.sendPacket(player);
//                    }
//                }
//            }
//
//            moveDown();
//        }, 100, 1);
        return this;
    }

    private final Location loc1 = new Location(Bukkit.getWorld("world"), 351.5, 134, -131.5);
    private final Location loc2 = new Location(Bukkit.getWorld("world"), 337.5, 112, -130.5);
    private Location spawn = null;

    @EventHandler
    protected final void onPlayerMove(PlayerMoveEvent event) {
        Location loc = event.getTo();
//        if (event.getPlayer().getOpenInventory() != null) {
//            if (event.getPlayer().getOpenInventory().getTopInventory() != null) {
//                return;
//            }
//        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        if ((loc.getBlockX() <= loc1.getBlockX() && loc.getBlockX() >= loc2.getBlockX()) &&
                (loc.getBlockY() <= loc1.getBlockY() && loc.getBlockY() >= loc2.getBlockY()) &&
                (Math.abs(loc.getBlockZ()) <= Math.abs(loc1.getBlockZ()) && Math.abs(loc.getBlockZ()) >= Math.abs(loc2.getBlockZ()))) {
            Hub.getInstance().getTranzitMenu().openInventory(event.getPlayer());

            if (spawn == null) {
                spawn = ((Location) Hub.getInstance().getSpawnPoints().toArray()[0]).clone();
            }

            Vector to = spawn.toVector().subtract(loc.toVector()).normalize();
            to.setY(to.getY() + 1);

            event.getPlayer().setVelocity(to);
        }
    }

    private void moveDown() {
        CustomPlayServerBlockChange[][] temp = new CustomPlayServerBlockChange[this.positions.length][this.positions[0].length];

        for (int i = this.positions.length - 1; i > 0; i--) {
            temp[i - 1] = this.positions[i];
        }

        for (int x = 0; x < this.positions[this.positions.length - 1].length; x++) {
            CustomPlayServerBlockChange blockChange = this.positions[0][x];
            if (blockChange == null) continue;

            boolean b = random.nextInt(2) == 1;
            blockChange.setBlockData(WrappedBlockData.createData(b ? Material.STAINED_GLASS : Material.STAINED_GLASS_PANE, b ? (random.nextBoolean() ? 5 : 13) : 8));

            temp[this.positions.length - 1][x] = blockChange;
        }

        this.positions = temp;
    }

    /**
     * This is a Wrapped block change packet,
     * I have a custom one for visibility option.
     */
    private static class CustomPlayServerBlockChange extends AbstractPacket {
        public static final PacketType TYPE;
        private boolean existing;

        public CustomPlayServerBlockChange() {
            super(new PacketContainer(TYPE), TYPE);
            this.handle.getModifier().writeDefaults();
        }

        public CustomPlayServerBlockChange(PacketContainer packet) {
            super(packet, TYPE);
        }

        public BlockPosition getLocation() {
            return (BlockPosition)this.handle.getBlockPositionModifier().read(0);
        }

        public void setLocation(BlockPosition value) {
            this.handle.getBlockPositionModifier().write(0, value);
        }

        public Location getBukkitLocation(World world) {
            return this.getLocation().toVector().toLocation(world);
        }

        public WrappedBlockData getBlockData() {
            return (WrappedBlockData)this.handle.getBlockData().read(0);
        }

        public void setBlockData(WrappedBlockData value) {
            this.handle.getBlockData().write(0, value);
        }

        public boolean isExisting() {
            return existing;
        }

        public void setExisting(boolean existing) {
            this.existing = existing;
        }

        public boolean isVisible() {
            World world = Bukkit.getWorld("world");
            Location loc = getBukkitLocation(world);
            Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            boolean b = block.getType() == Material.STAINED_GLASS || block.getType() == Material.STAINED_GLASS_PANE;
            return b;
        }

        static {
            TYPE = PacketType.Play.Server.BLOCK_CHANGE;
        }
    }
}
