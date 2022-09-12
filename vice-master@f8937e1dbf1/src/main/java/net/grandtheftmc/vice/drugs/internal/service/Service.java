package net.grandtheftmc.vice.drugs.internal.service;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Service {

    private volatile Service instance;
    private volatile Helper helper;

    private final String name;

    public Service(String name, Helper helper) {
        this.name = name;
        this.helper = helper;
    }

    protected synchronized void setHelper(Helper helper){
        this.helper = helper;
    }

    protected Helper getHelper(){
        return helper;
    }

    public String getName() {
        return name;
    }

    protected Service getInstance(){
        if(instance == null){
            synchronized(Service.class) {
                try {
                    instance = Service.class.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }
}
