package net.grandtheftmc.vice.hologram;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerEntityDestroy;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerEntityMetadata;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerNamedEntitySpawn;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public final class CoreHologram implements Hologram {

    private final int id;
    private final Location origin;

    protected final List<HologramNode> nodes;

    public CoreHologram(int id, Location origin) {
        this.id = id;
        this.origin = origin;
        this.nodes = Lists.newArrayList();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Location getOrigin() {
        return origin;
    }

    @Override
    public List<HologramNode> getNodes() {
        return nodes;
    }

    @Override
    public HologramNode getNode(int id) {
        return this.nodes.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void spawn(Player player) {
//        for (HologramNode node : this.nodes) {
//            if (node.getEntity() == null || node.getEntity().isDead() || !node.getEntity().isValid()) {
//                System.out.println("Entity is null");
//                continue;
//            }
//
//            WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
//            spawn.setEntityID(node.getEntity().getEntityId());
//            spawn.setX(node.getEntity().getLocation().getX());
//            spawn.setY(node.getEntity().getLocation().getY());
//            spawn.setZ(node.getEntity().getLocation().getZ());
//
//            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
//            dataWatcher.setEntity(node.getEntity());
//
//            WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
//            WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
//            WrappedDataWatcher.Serializer stringSerializer = WrappedDataWatcher.Registry.get(String.class);
//
//            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), (byte) 0x20); //Invisible
//            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), node.getText()); // Set custom name
//            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), false); // Set custom name visible
//
//            spawn.setMetadata(dataWatcher);
//
//            spawn.sendPacket(player);
//        }

        refresh();
    }

    @Override
    public void refresh() {
        this.nodes.forEach(node -> refresh(node.getId()));
    }

    @Override
    public void refresh(int nodeId) {
        HologramNode node = this.getNode(nodeId);
        if (node == null) return;
        if (node.getEntity() == null) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        packet.setEntityID(node.getEntity().getEntityId());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) continue;
            if (packet == null) continue;
            if (node == null) continue;
            if (node.getEntity() == null) continue;
            if (!player.getLocation().getWorld().getName().equals(origin.getWorld().getName()))
                continue;

            packet.sendPacket(player);
        }
    }

    @Override
    public void refresh(Player player) {
        this.nodes.forEach(node -> refresh(node.getId(), player));
    }

    @Override
    public void refresh(int nodeId, Player player) {
        HologramNode node = this.getNode(nodeId);
        if (node == null) return;
        if (node.getEntity() == null) return;
        if (!player.getLocation().getWorld().getName().equals(origin.getWorld().getName()))
            return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        packet.setEntityID(node.getEntity().getEntityId());

        packet.sendPacket(player);
    }

    @Override
    public void destroy() {
        for (HologramNode node : this.nodes) {
            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityId(node.getEntity().getEntityId());
            for (Player player : Bukkit.getOnlinePlayers()) {
                destroy.sendPacket(player);
            }
            node.getEntity().remove();
        }
        nodes.clear();
    }

    @Override
    public HologramNode addNode(int id) throws HologramDuplicateNodeException {
        if (nodes.stream().anyMatch(n -> n.getId() == id))
            throw new HologramDuplicateNodeException(this, id);

        HologramNode node = new CoreHologramNode(id, origin.clone().subtract(0, id * 0.25, 0));
        nodes.add(node);
        return node;
    }

    @Override
    public HologramNode addNode(int id, String text) throws HologramDuplicateNodeException {
        HologramNode node = this.addNode(id);
        node.setText(text);
        return node;
    }

    @Override
    public HologramNode addNode(HologramNode node) throws HologramDuplicateNodeException {
        if (nodes.stream().anyMatch(n -> n.getId() == id))
            throw new HologramDuplicateNodeException(this, id);

        nodes.add(node);
        return node;
    }
}
