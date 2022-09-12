package net.grandtheftmc.hub;

import java.util.Arrays;

public class Testing {

    private static Integer[][] array = new Integer[5][5];
    private static int idk = 6;

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            for (int x = 0; x < 5; x++) {
                array[i][x] = i + 1;
            }
        }

        for (int i = 0; i < 5; i ++) {

            for (int x = 0; x < array.length; x++) {
                System.out.println(Arrays.toString(array[x]));
            }

            move();
            System.out.println("");
        }
    }

    private static void move() {
        Integer[][] temp = new Integer[array.length][array[0].length];
        for (int i = array.length - 1; i > 0; i--) {
            temp[i - 1] = array[i];
        }

        for (int x = 0; x < array[array.length - 1].length; x++) {
            temp[array.length - 1][x] = idk;
        }

        idk += 1;
        array = temp;
    }
}
