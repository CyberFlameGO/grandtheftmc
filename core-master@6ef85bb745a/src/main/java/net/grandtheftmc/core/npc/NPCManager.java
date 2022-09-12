package net.grandtheftmc.core.npc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCCombustByBlockEvent;
import net.citizensnpcs.api.event.NPCCombustByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageByBlockEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.core.npc.interfaces.CombustableNPC;
import net.grandtheftmc.core.npc.interfaces.DamageableNPC;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.voting.crates.events.CrateNearbyPlayerEvent;

/**
 * Created by Timothy Lampen on 1/13/2018.
 */
public class NPCManager implements Component<NPCManager, Core> {

    private final Set<CoreNPC> npcs = new HashSet<>();

    @Override
    public NPCManager onEnable(Core plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        ServerUtil.runTaskLaterAsync(NPCDAO::loadNPCs, 20 * 10);

        new BukkitRunnable() {
            @Override
            public void run() {

                npcs.forEach(npc -> {

                    for (Entity nearby : npc.getStartingLoc().getWorld().getNearbyEntities(npc.getStartingLoc(), 0.4, 1, 0.4)) {

                        if (nearby instanceof Player) {
                            Player nearbyPlayer = (Player) nearby;
                            if (!Bukkit.getOnlinePlayers().contains(nearby)) continue;

                            CrateNearbyPlayerEvent event = new CrateNearbyPlayerEvent(nearbyPlayer, null);

                            if (event.isCancelled()) {
                                return;
                            }

                            Bukkit.getPluginManager().callEvent(event);
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0L, 5);
        return this;
    }

    @Override
    public NPCManager onDisable(Core plugin) {
        NPCDAO.clearRecords();
        
        for (CoreNPC npc : npcs){
        	
        	try{
            	// sync so it must run (before server shutdown), attempt to save
                NPCDAO.saveNPC(npc.getClass().getName(), npc.getStartingLoc());
                System.out.println("NPC Removed! " + npc.getID());
                npc.delete();
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
        return this;
    }

    /**
     * @param e the entity of the npc that you wish to delete.
     */
    public void deleteNPC(Entity e) {
        Set<CoreNPC> copy = new HashSet<>(this.npcs);
        copy.stream().filter(npc -> npc.getNPC().isSpawned() && npc.getNPC().getEntity().equals(e)).forEach(npc -> {
            npc.delete();
            this.npcs.remove(npc);
        });
    }

    /**
     * @param referenceName the class name of the entity at the location @param loc
     * @param loc           the location of the reference.
     */
    public void load(String referenceName, Location loc) {

        if (!loc.getChunk().isLoaded())
            loc.getChunk().load();

        try {
            Class c = Class.forName(referenceName);
            Constructor con = c.getConstructor(Location.class);
            Object o = con.newInstance(loc);
            Core.log("Successfully loaded " + referenceName + " at" + loc);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            Core.log("Unable to load " + referenceName + " at " + loc);
            e.printStackTrace();
        }
    }

    /**
     * @param npc the npc that is being added.
     */
    public void registerCoreNPC(CoreNPC npc) {
        this.npcs.add(npc);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (this.npcs.stream().anyMatch(npc -> npc.getNPC().isSpawned() && npc.getStartingLoc().getChunk().equals(event.getChunk()))) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPush(NPCPushEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof CollideableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((CollideableNPC) npc).onPush(event));
    }

    @EventHandler
    public void onCollide(NPCCollisionEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof CollideableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((CollideableNPC) npc).onCollide(event));
    }

    @EventHandler
    public void onDeath(NPCDeathEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof DamageableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((DamageableNPC) npc).onDeath(event));
    }

    @EventHandler
    public void onDamageBlock(NPCDamageByBlockEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof DamageableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((DamageableNPC) npc).onDamageByBlock(event));
    }

    @EventHandler
    public void onDamageEntity(NPCDamageByEntityEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof DamageableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((DamageableNPC) npc).onDamageByEntity(event));
    }

    @EventHandler
    public void onCombustBlock(NPCCombustByBlockEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof CombustableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((CombustableNPC) npc).onCombustByBlock(event));
    }

    @EventHandler
    public void onCombustEntity(NPCCombustByEntityEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof CombustableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((CombustableNPC) npc).onCombustByEntity(event));
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof ClickableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((ClickableNPC) npc).onRightClick(event));
    }

    @EventHandler
    public void onLeftClick(NPCLeftClickEvent event) {
        this.npcs.stream().filter(npc -> npc instanceof ClickableNPC).filter(npc -> npc.getID() == event.getNPC().getId()).forEach(npc -> ((ClickableNPC) npc).onLeftClick(event));
    }
}
