package net.grandtheftmc.vice.hologram;

import java.util.ArrayList;
import java.util.Arrays;

public class TypeWriter {

    private static final char[] COLORS = {'l', 'm', 'n', 'k', 'o', 'r', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final String text;
    private int progress = 0, waitTicks = 0;

    public TypeWriter(String text, int waitTicks) {
        this.text = text;
        this.waitTicks = waitTicks;
    }

    public String next() {
        if (this.progress >= this.text.length() - 1) {
            if (this.progress >= (this.text.length() - 1) + this.waitTicks) {
                this.progress = 0;
            } else {
                this.progress++;
                return this.text;
            }
        }

        if (this.isColor(this.progress)) {
            this.progress += 2;
            return next();
        }

        return this.text.substring(0, this.progress++ + 1);
    }

    private boolean isColor(int prog) {
        char[] chars = this.text.toCharArray();
        if (chars[prog] == '&') {
            char next = chars[prog + 1];
            for (char color : COLORS) {
                if (next == color) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
//        TypeWriter writer = new TypeWriter("&d&lVice&f&lMC Season &d&l2", 5);
//        for (int i = 0; i < 50; i++)
//            System.out.println(writer.next());

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 63; i++)
            arrayList.add("" + i);

        arrayList.subList(3, 27).clear();
        System.out.println(Arrays.toString(arrayList.toArray(new String[arrayList.size()])));

        //CACTUS,COAL,AIR,RECORD_7,RECORD_7,AIR,AIR,CHEST,COAL,AIR,CHEST,CHEST,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,AIR,COAL,CHEST,AIR,AIR,AIR,AIR,AIR,AIR,WATCH
    }
}
