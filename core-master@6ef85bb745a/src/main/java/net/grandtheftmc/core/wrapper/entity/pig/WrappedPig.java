package net.grandtheftmc.core.wrapper.entity.pig;

import net.grandtheftmc.core.wrapper.entity.AbstractEntity;

/**
 * Created by Luke Bingham on 15/09/2017.
 */
public class WrappedPig extends AbstractEntity<CorePig> {

    private CorePig corePig;

    public WrappedPig(CorePig pig) {
        this.corePig = pig;
    }

    @Override
    public CorePig getCoreEntity() {
        return this.corePig;
    }
}
