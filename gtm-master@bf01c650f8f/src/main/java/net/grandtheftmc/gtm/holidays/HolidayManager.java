package net.grandtheftmc.gtm.holidays;

import net.grandtheftmc.gtm.holidays.easter.Easter;
import net.grandtheftmc.gtm.holidays.independenceday.IndependenceDay;

public class HolidayManager {
    private Easter easter;
    private IndependenceDay independenceDay;

    public HolidayManager() {
        this.easter = new Easter();
        this.independenceDay = new IndependenceDay();
    }

    public Easter getEaster() {
        if (this.easter == null) this.easter = new Easter();
        return this.easter;
    }

    public IndependenceDay getIndependenceDay() {
        if (this.independenceDay == null) this.independenceDay = new IndependenceDay();
        return this.independenceDay;
    }
}
