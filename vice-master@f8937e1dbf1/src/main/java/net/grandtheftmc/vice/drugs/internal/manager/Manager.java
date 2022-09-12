package net.grandtheftmc.vice.drugs.internal.manager;

import net.grandtheftmc.vice.drugs.internal.service.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Manager <S extends Service> {

    private final S service;
    private volatile String name;
    private AtomicInteger id;

    public Manager(String name, AtomicInteger id, S service) {
        this.service = service;
        this.name = name;
        this.id = id;
    }

    public abstract void start();
    public abstract void stop();
    public abstract boolean destroy();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicInteger getId() {
        return id;
    }

    public void setId(AtomicInteger id) {
        this.id = id;
    }

    public S getService() {
        return service;
    }
}

