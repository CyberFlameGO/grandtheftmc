package net.grandtheftmc.core.npc.interfaces;

import net.citizensnpcs.api.event.NPCDamageByBlockEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;

/**
 * Created by Timothy Lampen on 1/13/2018.
 */
public interface DamageableNPC {

    void onDeath(NPCDeathEvent event);

    void onDamageByEntity(NPCDamageByEntityEvent event);

    void onDamageByBlock(NPCDamageByBlockEvent event);
}
