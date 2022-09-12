package net.grandtheftmc.guns;

public class TESTING {

    public static void main(String[] args) {
        int best = 80;
        double result = getPercentBetweenValues(best, 36);
        System.out.println(result);

        int result2 = (int) Math.floor(result) / 10;
        System.out.println(result2);
    }

    public static double getPercentBetweenValues(double goal, double value) {
        return 100 - Math.abs(((goal - value) / goal) * 100);
    }
}
