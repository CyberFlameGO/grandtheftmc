package net.grandtheftmc.vice.users;

/**
 * Created by Timothy Lampen on 2017-08-11.
 */
public enum Prestige {
    I(6000000),
    II(7000000),
    III(8000000),
    IV(9000000),
    V(10000000),
    VI(11000000),
    VII(12000000),
    VIII(13000000),
    IX(14000000),
    X(15000000);

    private double cost;

    Prestige(double cost){
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }
}
