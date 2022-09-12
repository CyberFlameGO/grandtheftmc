package net.grandtheftmc.gtm.holidays.halloween.dao;

/**
 * Created by Timothy Lampen on 2017-10-15.
 */
public class ServerCoupon {
    private int couponID;
    private String couponName;
    private long creationTime;

    public ServerCoupon(int couponID, String couponName, long creationTime) {
        this.couponID = couponID;
        this.couponName = couponName;
        this.creationTime = creationTime;
    }

    public int getCouponID() {
        return couponID;
    }

    public String getCouponName() {
        return couponName;
    }

    public long getCreationTime() {
        return creationTime;
    }
}
