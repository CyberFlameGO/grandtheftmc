package net.grandtheftmc.vice.world.warps;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.users.ViceUser;

/**
 * Created by Luke Bingham.
 */
public class WarpCache {

	private final User user;
	private final ViceUser viceUser;
	private final TaxiTarget target;
	private final int price, delay;

	public WarpCache(User user, ViceUser viceUser, TaxiTarget target, int price, int delay) {
		this.user = user;
		this.viceUser = viceUser;
		this.target = target;
		this.price = price;
		this.delay = delay;
	}

	public User getUser() {
		return user;
	}

	public ViceUser getViceUser() {
		return viceUser;
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
