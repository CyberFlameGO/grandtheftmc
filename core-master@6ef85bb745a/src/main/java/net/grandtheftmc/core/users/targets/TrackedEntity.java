package net.grandtheftmc.core.users.targets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Created by Timothy Lampen on 12/11/2017.
 */
public class TrackedEntity extends TrackedTarget{

    public final Entity entity;

    public TrackedEntity(Entity entity) {
        this.entity = entity;
    }

    public TrackedEntity(Entity entity, char pointer) {
        super(pointer);
        this.entity = entity;
    }

    @Override
    public Location getLocation() {
        return this.entity.getLocation().clone().add(0,1,0);
    }
}
