package net.grandtheftmc.vice.holidays;

import net.grandtheftmc.vice.holidays.easter.Easter;

public class HolidayManager {
    private Easter easter;

    public HolidayManager() {
        this.easter = new Easter();
    }

    public Easter getEaster() {
        if (this.easter == null) this.easter = new Easter();
        return this.easter;
    }
}
