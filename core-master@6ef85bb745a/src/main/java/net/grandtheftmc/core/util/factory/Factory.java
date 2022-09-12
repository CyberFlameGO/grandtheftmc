package net.grandtheftmc.core.util.factory;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public abstract class Factory<T> {
    protected T object;
    public abstract T build();
}
