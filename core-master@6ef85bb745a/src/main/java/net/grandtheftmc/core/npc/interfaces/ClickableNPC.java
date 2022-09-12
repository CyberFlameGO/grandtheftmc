package net.grandtheftmc.core.npc.interfaces;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

/**
 * Created by Timothy Lampen on 1/13/2018.
 */
public interface ClickableNPC {

    void onRightClick(NPCRightClickEvent event);

    void onLeftClick(NPCLeftClickEvent event);

}
