package net.grandtheftmc.vice.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.massivecraft.factions.P;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerEntityDestroy;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.hologram.event.HologramReceiveEvent;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateException;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class HologramManager implements Component<HologramManager, Vice> {

    protected static final HashSet<Chunk> CHUNKS = Sets.newHashSet();
    private final List<Hologram> holograms;

    public HologramManager(JavaPlugin plugin) {
        this.holograms = Lists.newArrayList();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Vice.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int id = event.getPacket().getIntegers().read(0);
                HologramData data = getById(id);
                if (data == null) {
                    ArmorStand found = getEntityById(id);
                    if (found == null) return;
                    if (found.getCustomName() == null) return;

                    String[] components = found.getCustomName().split("-");
                    if (components.length == 5) {

                        if (found.hasMetadata("CoreHologram")) {
                            CoreHologramNode node = (CoreHologramNode) found.getMetadata("CoreHologram").get(0).value();
                            if (node == null) {
                                System.out.println("Node is null!");
                                return;
                            }

                            System.out.println("Found - " + node.getText());
                            return;
                        }

                        System.out.println("Doesn't consist of 'CoreHologram' --- " + found.getCustomName());

                        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
                        destroy.setEntityId(found.getEntityId());
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            destroy.sendPacket(player);
                        }
                        found.remove();
                    }
                    return;
                }

                HologramReceiveEvent receiveEvent = new HologramReceiveEvent(event.getPlayer(), data.getHologram(), data.getNode());
                Bukkit.getPluginManager().callEvent(receiveEvent);
                if (receiveEvent.isCancelled()) {
                    event.setCancelled(true);
                    WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
                    destroy.setEntityId(id);
                    destroy.sendPacket(event.getPlayer());
                }

                WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
                dataWatcher.setEntity(data.getNode().getEntity());

                WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
                WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
                WrappedDataWatcher.Serializer stringSerializer = WrappedDataWatcher.Registry.get(String.class);

                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), (byte) 0x20); //Invisible
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), receiveEvent.getText()); // Set custom name
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), receiveEvent.doDisplay()); // Set custom name visible

                event.getPacket().getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
            }
        });
    }

    @Override
    public HologramManager onDisable(Vice plugin) {
        this.holograms.forEach(Hologram::destroy);
        return this;
    }

    /**
     * Create a Hologram.
     *
     * @return Hologram
     */
    public Hologram create(int id, Location origin, HologramNode... nodes) throws HologramDuplicateException {
        if (this.holograms.stream().anyMatch(h -> h.getId() == id))
            throw new HologramDuplicateException(id);

        for (Entity entity : origin.getWorld().getNearbyEntities(origin, 0.5, 15, 0.5)) {
            if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                String[] components = entity.getCustomName().split("-");
                if (components.length == 5) {

                    WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
                    destroy.setEntityId(entity.getEntityId());
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        destroy.sendPacket(player);
                    }

                    entity.remove();
                }
            }
        }

        Hologram hologram = new CoreHologram(id, origin);
        for (HologramNode node : nodes) {
            try {
                hologram.addNode(node);
            } catch (HologramDuplicateNodeException e) {
                e.printStackTrace();
            }
        }

        this.holograms.add(hologram);
        System.out.println("HOLOGRAM CREATED, ID " + hologram.getId());

        return hologram;
    }

    public Hologram getHologram(int id) {
        return this.holograms.stream().filter(h -> h.getId() == id).findFirst().orElse(null);
    }

    public HologramData getByUniqueId(UUID uuid) {
        for (Hologram hologram : this.holograms) {
            for (HologramNode node : hologram.getNodes()) {
                if (node.getEntity() == null) continue;
                if (node.getEntity().getUniqueId().equals(uuid)) {
                    return new HologramData(hologram, node);
                }
            }
        }
        return null;
    }

    public HologramData getById(int entityId) {
        for (Hologram hologram : this.holograms) {
            for (HologramNode node : hologram.getNodes()) {
                if (node.getEntity() == null) continue;
                if (node.getEntity().getEntityId() == entityId) {
                    return new HologramData(hologram, node);
                }
            }
        }
        return null;
    }

    public boolean isHologramEntity(ArmorStand entity) {
        for (Hologram hologram : this.holograms) {
            for (HologramNode node : hologram.getNodes()) {
                if (node.getEntity() == null) continue;
                if (node.getEntity().getUniqueId().equals(entity.getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler (ignoreCancelled = true)
    protected final void onArmorstandManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked() == null) return;
        if (this.isHologramEntity(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    protected final void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.ARMOR_STAND) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM && this.isHologramEntity(((ArmorStand) event.getEntity()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    protected final void onEntitySpawn(EntitySpawnEvent event) {
        if (!event.isCancelled()) return;
        if (event.getEntity().getType() != EntityType.ARMOR_STAND) return;

        if (this.isHologramEntity(((ArmorStand) event.getEntity()))) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    protected final void onChunkUnload(ChunkUnloadEvent event) {
        for (Chunk chunk : CHUNKS) {
            if (event.getChunk().equals(chunk)) {
                event.setCancelled(true);
                break;
            }
        }

//        for (Hologram hologram : this.holograms) {
//            for (HologramNode node : hologram.getNodes()) {
//                if (node.getEntity() == null) continue;
//                if (node.getEntity().getLocation().getChunk().equals(event.getChunk())) {
//                    event.setCancelled(false);
//                    break;
//                }
//            }
//        }
    }

    private class HologramData {
        private final Hologram hologram;
        private final HologramNode node;

        HologramData(Hologram hologram, HologramNode node) {
            this.hologram = hologram;
            this.node = node;
        }

        Hologram getHologram() {
            return hologram;
        }

        HologramNode getNode() {
            return node;
        }
    }

    public ArmorStand getEntityById(int id) {
        for (World world : Bukkit.getWorlds()) {
            for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class)) {
                if (armorStand.getEntityId() == id) return armorStand;
            }
        }

        return null;
    }
}
