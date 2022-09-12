package net.grandtheftmc.vice.items;

import net.grandtheftmc.core.util.Utils;

public enum Schedule {

    // IF YOU CHANGE THE DISPLAY NAMES, THE OLD DISPLAY NAMES WILL STILL BE IN THE LORE OF OLD ITEMS AND THE NEW ONE WILL BE ADDED AS WELL! its fucky but whatevz
    I("&cSchedule I", 1, 3.0, "magicmushroomred", "magicmushroombrown", "mdma", "lsd", "weed", "marijuanaflower", "joint", "potbrownie", "heroin", "heroinsyringe"),
    II("&cSchedule II", 2, 2.0, "meth", "methbaggy", "cocaine", "opium"),
    III("&cSchedule III", 3, 1.0, "steroids", "vodka", "alcohol"),
    IV("&eSchedule IV", 4, 0.75),
    V("&eSchedule V", 5, 0.5),
    LIST_II("&eList II", 6, 0.5, "ergotfungi", "safrole", "ephedrasinica", "cocaleaves", "cocaseeds"),
    LIST_I("&eList I", 7, 0.25, "opiumpoppies", "ephedrasinicaseeds", "ocoteacymbarum"),
    NONE("", -1, 0);//"hop", "hopplant", "potato"

    private final String disp;
    private final int priority;
    private final double jailMultiplier;
    private final String[] substances;

    Schedule(String disp, int priority, double jailMultiplier, String... substances) {
        this.disp = Utils.f(disp);
        this.priority = priority;
        this.jailMultiplier = jailMultiplier;
        this.substances = substances;
    }

    public String getDisp() {
        return this.disp;
    }

    public int getPriority() {
        return this.priority;
    }

    public double getJailMultiplier() {
        return this.jailMultiplier;
    }

    public String[] getSubstances() {
        return this.substances;
    }

    public static Schedule of(String name) {
        for (Schedule schedule : Schedule.values())
            for (String s : schedule.substances)
                if (s.equalsIgnoreCase(name)) return schedule;
        return Schedule.NONE;
    }
}
