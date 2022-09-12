package net.grandtheftmc.gtm.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class DrugBlockRemovalListener implements PacketListener {

    private final List<Location> locations = new ArrayList<>();

    public DrugBlockRemovalListener() {
    }

    @Override
    public void onPacketSending(PacketEvent event) {
//        net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk
        if (event.isAsync()) System.out.println("MAP_CHUNK ASYNC");

        int cx = event.getPacket().getIntegers().read(0), cz = event.getPacket().getIntegers().read(1);
        int[] count = {0};
        Chunk chunk = event.getPlayer().getWorld().getChunkAt(cx, cz);

        ServerUtil.runTaskAsync(() -> {
            for (BlockState tile : chunk.getTileEntities()) {
                if (!(tile instanceof BrewingStand)) continue;
                locations.add(tile.getLocation());
                count[0] += 1;
            }

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 250; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType() == Material.CARPET && block.getData() == (byte) 5) {
                            locations.add(block.getLocation());
                            count[0] += 1;
                        }
                    }
                }
            }

            if (count[0] >= 1) {
                ServerUtil.runTask(() -> {
                    for (Location location : locations) {
                        location.getBlock().setType(Material.AIR);
                    }

                    event.getPlayer().sendMessage("Blocks removed: " + count[0]);
                });
            }
        });
    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent) {
        //404 DO NOTHING.
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .lowest()
                .types(PacketType.Play.Server.MAP_CHUNK)
                .build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public Plugin getPlugin() {
        return GTM.getInstance();
    }
}
