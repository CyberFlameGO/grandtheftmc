package net.grandtheftmc.core.util;

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
        return timeUnit.toSeconds(time) - (timeUnit.toMinutes(time) * 60);
    }

    public Long getMinutes() {
        return timeUnit.toMinutes(time) - (timeUnit.toHours(time) * 60);
    }

    public Long getHours() {
        return timeUnit.toHours(time) - (timeUnit.toDays(time) * 24);
    }

    public Long getDays() {
        return timeUnit.toDays(time);
    }

    public Long getMillis() {
        return timeUnit.toMillis(time);
    }
}