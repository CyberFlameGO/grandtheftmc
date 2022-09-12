package net.grandtheftmc.core.npc.interfaces;

import net.citizensnpcs.api.event.NPCCombustByBlockEvent;
import net.citizensnpcs.api.event.NPCCombustByEntityEvent;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public interface CombustableNPC {

    void onCombustByBlock(NPCCombustByBlockEvent event);

    void onCombustByEntity(NPCCombustByEntityEvent event);
}
