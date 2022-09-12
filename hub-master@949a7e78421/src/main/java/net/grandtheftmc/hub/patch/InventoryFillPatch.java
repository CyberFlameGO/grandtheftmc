package net.grandtheftmc.hub.patch;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import net.grandtheftmc.hub.Hub;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Luke Bingham on 12/08/2017.
 */
public class InventoryFillPatch implements PacketListener {

    public InventoryFillPatch() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
        PacketContainer packet = packetEvent.getPacket();
        Player player = packetEvent.getPlayer();

        if(packet.getType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
            packet.getItemListModifier().read(0).forEach(item -> {
                if(item.getTypeId() == 386) {
                    item.setType(Material.AIR);
                    packetEvent.setCancelled(true);
                }
            });
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent) {
        //Nothing here.
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder().normal().gamePhase(GamePhase.PLAYING).types(
                PacketType.Play.Server.WINDOW_ITEMS
        ).build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public Plugin getPlugin() {
        return Hub.getInstance();
    }
}
