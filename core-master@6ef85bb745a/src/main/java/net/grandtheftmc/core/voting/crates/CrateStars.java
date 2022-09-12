package net.grandtheftmc.core.voting.crates;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.Utils;

/**
 * Created by Liam on 24/04/2017.
 */
public enum CrateStars {


    ONE(1, 1, "&6"),
    TWO(2, 5, "&a"),
    THREE(3, 50, "&b"),
    FOUR(4, 250, "&5"),
    FIVE(5, 1000, "&c"),
    SIX(6, 45, null);

    private final int stars;
    private final int crowbars;
    private final String color;


    CrateStars(int stars, int crowbars, String color) {
        this.stars = stars;
        this.crowbars = crowbars;
        this.color = color;
    }

    public int getStars() {
        return this.stars;
    }

    public int getCrowbars() {
        return this.crowbars;
    }

    public String getColor() {
        return this.color;
    }

    public String getDisplayName() {
        return this.stars == 6 ? Utils.f("&c&lSkin Crate") : Utils.f( this.color + "&l" + this.stars + " Star " + (this.stars == 5 ? "Briefcase" : this.stars >= 3 ? "Vault" : "Crate"));
    }

    public String getStarsString() {
        return new String[]{"✩✩✩✩✩", "✮✩✩✩✩", "✮✮✩✩✩", "✮✮✮✩✩", "✮✮✮✮✩", "✮✮✮✮✮", ""}[this.stars];
    }

    public short getOpenHead() {
        return Core.getSettings().getType() != ServerType.VICE ? new short[]{0, 803, 805, 807, 811, 816, 824}[this.stars] : new short[]{0, 803, 805, 807, 811, 816, 824}[this.stars];
    }

    public double getHeight(){
        return new double[]{0, 2, 2, 2.5, 3.75, 1.5, 2}[this.stars];
    }

    public short getClosedHead() {
        return Core.getSettings().getType() != ServerType.VICE ? new short[]{0, 802, 804, 806, 808, 812, 823}[this.stars] : new short[]{0, 802, 804, 806, 808, 812, 823}[this.stars];
    }

    public static CrateStars getCrateStars(int stars) {
        return CrateStars.values()[stars - 1];
    }

    public String getType(){
        switch (this.stars){
            case 1:
            case 2:
                return "Crate";
            case 3:
            case 4:
                return "Vault";
            case 5:
                return "Briefcase";
        }
        return "Crate";
    }
}