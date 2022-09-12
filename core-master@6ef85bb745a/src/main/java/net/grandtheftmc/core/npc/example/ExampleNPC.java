package net.grandtheftmc.core.npc.example;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class ExampleNPC extends CoreNPC implements ClickableNPC {
    /**
     * @param loc
     * @apiNote default constructor for the CoreNPC, does generateNewNPC after it is completed.
     */
    public ExampleNPC(Location loc) {
        super(loc, EntityType.PLAYER, "Example");
    }


    @Override
    protected void generateNewNPC() {
        ServerUtil.debug("overriden method");
        setSkin("TimLampen", true);
        setLookClose(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage("hey dud");
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {

    }
}
