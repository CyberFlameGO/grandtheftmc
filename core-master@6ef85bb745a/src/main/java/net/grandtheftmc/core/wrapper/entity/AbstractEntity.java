package net.grandtheftmc.core.wrapper.entity;

import net.grandtheftmc.core.wrapper.Wrapper;

/**
 * Created by Luke Bingham on 15/09/2017.
 */
public abstract class AbstractEntity<T extends CoreEntity> implements Wrapper {

    /**
     * (!)
     *
     * DON'T USE THIS, STILL TRYING TO FIND THE BEST WAY TO WRAP ENTITIES.
     *
     * (!)
     */

    public abstract T getCoreEntity();
}
