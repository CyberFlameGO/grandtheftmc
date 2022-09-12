package net.grandtheftmc.vice.season;

import java.sql.Timestamp;

public class Season {

    private final int number;
    private final Timestamp start, expire;
    private final SeasonData seasonData;
    private final boolean current;

    public Season(int number, Timestamp start, Timestamp expire, SeasonData seasonData, boolean current) {
        this.number = number;
        this.start = start;
        this.expire = expire;
        this.seasonData = seasonData;
        this.current = current;
    }

    public int getNumber() {
        return number;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getExpire() {
        return expire;
    }

    public SeasonData getSeasonData() {
        return seasonData;
    }

    public boolean isCurrent() {
        return current;
    }
}
