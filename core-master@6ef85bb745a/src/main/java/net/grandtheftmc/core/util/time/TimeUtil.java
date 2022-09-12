package net.grandtheftmc.core.util.time;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.Validate;

public class TimeUtil {

	/**
	 * The timezone to use for {@link LocalDateTime} instances created in this
	 * Util
	 */
	private static ZoneId CURRENT_TIMEZONE = ZoneId.systemDefault();

	/** A standard formatting of DateTime */
	public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	/**
	 * Set the timezone to use for {@link LocalDateTime} instances created in
	 * this Util
	 *
	 * @param zoneId The timezone
	 */
	public static void setCurrentTimezone(ZoneId zoneId) {
		Validate.notNull(zoneId);

		CURRENT_TIMEZONE = zoneId;
	}

	/**
	 * Converts a time in milliseconds to a LocalDateTime based on
	 * {@link #CURRENT_TIMEZONE}
	 *
	 * @param millis The time in milliseconds
	 *
	 * @return A {@link LocalDateTime} based on {@link #CURRENT_TIMEZONE}
	 */
	public static LocalDateTime getLocalDateTime(long millis) {
		return getLocalDateTime(millis, CURRENT_TIMEZONE);
	}

	/**
	 * Converts a time in milliseconds to a LocalDateTime based on the specified
	 * timezone
	 *
	 * @param millis The time in milliseconds
	 * @param zoneId The timezone to use
	 *
	 * @return A {@link LocalDateTime} based on the specified timezone
	 */
	public static LocalDateTime getLocalDateTime(long millis, ZoneId zoneId) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId);
	}

	/**
	 * Formats a time in milliseconds following the outline of
	 * {@link #DATE_TIME_FORMAT} and {@link #CURRENT_TIMEZONE}
	 *
	 * @param millis The time in milliseconds
	 *
	 * @return The time in a readable format
	 *
	 * @see #DATE_TIME_FORMAT
	 */
	public static String format(long millis) {
		return DATE_TIME_FORMAT.format(getLocalDateTime(millis));
	}

	/**
	 * Converts time into a String representation of minutes:seconds based off
	 * the integer provided.
	 * 
	 * @param time - Time to be converted.
	 * @return String in the format of minutes:seconds.
	 */
	public static String formatSeconds(int time) {
		int minutes = time / 60;
		int seconds = time - (minutes * 60);

		StringBuilder stringBuilder = new StringBuilder();

		if (minutes < 10) {
			stringBuilder.append("0");
		}

		stringBuilder.append(String.valueOf(minutes) + ":");

		if (seconds < 10) {
			stringBuilder.append("0");
		}

		stringBuilder.append(String.valueOf(seconds));
		return stringBuilder.toString();
	}

	/**
	 * Formats milliseconds to seconds to the given degree.
	 * 
	 * @param millis - the milliseconds to format
	 * @param degrees - the number of decimal places to format to
	 * 
	 * @return The formatted string that represents the seconds, from the
	 *         specified milliseconds.
	 */
	public static String formatMillisToSecs(long millis, int degrees) {
		double amount = millis / 1000.0;
		return String.format("%." + degrees + "f", amount);
	}

	/**
	 * Formats milliseconds to seconds to the tens decimal.
	 * <p>
	 * Wrapper around {@link #formatMillisToSecs(long, int)}.
	 * </p>
	 * 
	 * @param millis - the milliseconds to format
	 * 
	 * @return The formatted string that represents the seconds, from the
	 *         specified milliseconds.
	 */
	public static String formatMillisToSecs(long millis) {
		return formatMillisToSecs(millis, 1);
	}

	/**
	 * Converts the amount of seconds to a time readable format.
	 * 
	 * Wrapper around {@link #timeToString(long)}.
	 * 
	 * 330 seconds is represented as "5m 30s".
	 * 
	 * @param secs - the amount of seconds
	 * 
	 * @return A string format of the representation of the time.
	 */
	public static String timeToString(int secs) {
		return timeToString(secs * 1000L);
	}

	/**
	 * Converts the amount of seconds to a time readable format.
	 * 
	 * 86400000 is represented as "24h".
	 * 
	 * @param msec - the amount of milliseconds
	 * 
	 * @return A string format of the representation of the time.
	 */
	public static String timeToString(long msec) {
		if (msec < 1000) {
			return "0s";
		}

		StringBuilder text = new StringBuilder();

		if (msec >= 86400000) {
			text.append(msec / 86400000).append("d ");
			msec %= 86400000;
		}

		if (msec >= 3600000) {
			text.append(msec / 3600000).append("h ");
			msec %= 3600000;
		}

		if (msec >= 60000) {
			text.append(msec / 60000).append("m ");
			msec %= 60000;
		}

		if (msec >= 1000) {
			text.append(msec / 1000).append("s ");
			msec %= 1000;
		}

		return text.toString().trim();
	}

	/**
	 * Get the string representation of the time between time1 and time2.
	 * 
	 * Wrapper around {@link #timeToString(long)}.
	 * 
	 * 86400000 is represented as "24h".
	 * 
	 * @param t1 - the time in milliseconds
	 * @param t2 - the time in milliseconds
	 * 
	 * @return The string representation of the time between t1 and t2.
	 */
	public static String getTimeBetween(long t1, long t2) {
		return timeToString(Math.abs(t1 - t2));
	}

	/**
	 * Get whether or not the these are different day timestamp.
	 * 
	 * @param ts - the first timestamp
	 * @param ts2 - the second timestamp
	 * 
	 * @return {@code true} if the timestamps are different days.
	 */
	public static boolean isDifferentDay(Timestamp ts, Timestamp ts2) {

		// if same year, month, and day
		if (ts.getYear() == ts2.getYear()) {
			if (ts.getMonth() == ts2.getMonth()) {
				if (ts.getDay() == ts2.getDay()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Get the difference in hours between the two timestamps.
	 * <p>
	 * Note: This returns the whole number of hours.
	 * 
	 * @param current - the current timestamp
	 * @param last - the last timestamp
	 * 
	 * @return The difference in hours between the current timestamp and the
	 *         last timestamp.
	 */
	public static int getDifferenceInHours(Timestamp current, Timestamp last) {

		long diff = current.getTime() - last.getTime();
		long diffSeconds = diff / 1000;
		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);

		return (int) diffHours;
	}

}
