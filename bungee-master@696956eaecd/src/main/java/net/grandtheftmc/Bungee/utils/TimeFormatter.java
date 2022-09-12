package net.grandtheftmc.Bungee.utils;

import java.util.concurrent.TimeUnit;

public class TimeFormatter {
    private final TimeUnit timeUnit;
    private Long time;

    public TimeFormatter(TimeUnit timeUnit, Long time) {
        this.timeUnit = timeUnit;
        this.time = time;
    }

    public Long getTime() {
        return new Long(this.time);
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public Long getSeconds() {
        return this.timeUnit.toSeconds(this.time) - (this.timeUnit.toMinutes(this.time) * 60);
    }

    public Long getMinutes() {
        return this.timeUnit.toMinutes(this.time) - (this.timeUnit.toHours(this.time) * 60);
    }

    public Long getHours() {
        return this.timeUnit.toHours(this.time) - (this.timeUnit.toDays(this.time) * 24);
    }

    public Long getDays() {
        return this.timeUnit.toDays(this.time);
    }

    public Long getMillis() {
        return this.timeUnit.toMillis(this.time);
    }
}