package net.grandtheftmc.core.wrapper.entity.pig;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

/**
 * Created by Luke Bingham on 15/09/2017.
 */
public class CorePig_1_12 extends net.minecraft.server.v1_12_R1.EntityPig implements CorePig {

    public CorePig_1_12(World world) {
        super(((CraftWorld) world).getHandle());
    }

    @Override
    public void setEntityName(String name) {
        //TODO Set entity name
    }

    @Override
    public void setEntityNameVisible(boolean visible) {
        //TODO set entity name visible
    }
}
