package net.grandtheftmc.vice.machine;

public class MachineTesting {

    static String[] symbols = {"A", "B", "C", "D", "E"};

    public static void main(String[] args) {
//        int max = 100;
//        int current = 95;
//        int bars = symbols.length - 1;
//        int result = (int) Math.round(getPercentBetweenValues(max, current, bars));
//        System.out.println("" + result);
//
//        String str = "";
//        for (int i = 0; i < result; i++)
//            str += 'x';
//
//        for (int i = result; i < bars; i++)
//            str += 'o';
//
//        System.out.println("" + str);
//        System.out.println("" + symbols[result]);


        //NEW
//        long next = System.currentTimeMillis() + ((10 * 1000) / 26);
//        int i = 1;
//        while (i < 27) {
//            if (System.currentTimeMillis() > next) {
//                System.out.println(i);
//                next = System.currentTimeMillis() + ((10 * 1000) / 26);
//                i++;
//            }
//        }


        //NEW
        System.out.println(getPercentBetweenValues(100, 0.76 * 100, 100));
    }

    public static int getPercentBetweenValues(double goal, double value, int bars) {
        return (int) Math.round(value >= goal ? bars : bars - Math.abs((goal - value) / goal * bars));
    }
}
