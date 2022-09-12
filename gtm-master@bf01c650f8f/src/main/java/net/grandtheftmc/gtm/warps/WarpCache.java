package net.grandtheftmc.gtm.warps;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.TaxiTarget;

import java.util.UUID;

/**
 * Created by Luke Bingham on 13/08/2017.
 */
public class WarpCache {

    private final User user;
    private final GTMUser gtmUser;
    private final TaxiTarget target;
    private final int price, delay;

    public WarpCache(User user, GTMUser gtmUser, TaxiTarget target, int price, int delay) {
        this.user = user;
        this.gtmUser = gtmUser;
        this.target = target;
        this.price = price;
        this.delay = delay;
    }

    public User getUser() {
        return user;
    }

    public GTMUser getGtmUser() {
        return gtmUser;
    }

    public TaxiTarget getTarget() {
        return target;
    }

    public int getPrice() {
        return price;
    }

    public int getDelay() {
        return delay;
    }
}
