package net.grandtheftmc.core.npc.interfaces;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCPushEvent;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public interface CollideableNPC {

    void onCollide(NPCCollisionEvent event);

    void onPush(NPCPushEvent event);
}
