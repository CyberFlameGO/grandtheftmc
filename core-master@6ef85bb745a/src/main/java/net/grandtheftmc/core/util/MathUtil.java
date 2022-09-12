package net.grandtheftmc.core.util;

public class MathUtil {

    /**
     * Get the percentage based on the distance between 'goal' and 'value'
     *
     * @param goal  - Highest point
     * @param value - Your input value (lower than 'goal')
     * @return percentage
     */
    public static double getPercentBetweenValues(double goal, double value) {
        if(value >= goal) return 100.0;
        return 100 - Math.abs(((goal - value) / goal) * 100);
    }

    /**
     * Get the percentage based on the distance between 'goal' and 'value'
     *
     * @param goal  - Lowest point
     * @param value - Your input value (higher than 'goal')
     * @return percentage
     */
    public static double getPercentBetweenValuesReverse(double goal, double value) {
        if(goal >= value) return 100.0;
        return 100 - Math.abs(((value - goal) / value) * 100);
    }
}
