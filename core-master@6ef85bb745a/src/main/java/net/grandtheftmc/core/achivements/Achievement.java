package net.grandtheftmc.core.achivements;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;

import java.util.Optional;

public enum Achievement {

    Hobo("Hobo", "&7&lHobo", "&7Join " + Core.getSettings().getServer_GTM_shortName() + " for the first time!", "&7&l"),
    CRIMINAL("Criminal", "&e&lCriminal", "&7Rankup to CRIMINAL!", "&e&l"),
    HOMIE("Homie", "&e&lHomie", "&7Rankup to &e&lHOMIE&7!", "&e&l"),
    THUG("Thug", "&e&lThug", "&7Rankup to &e&lTHUG&7!", "&e&l"),
    GANGSTER("Gangster", "&a&lGangster", "&7Rankup to &e&lGANGSTER&7!", "&a&l"),
    MUGGER("Mugger", "&e&lMugger", "&7Rankup to &e&lMUGGER&7!", "&e&l"),
    HUNTER("Hunter", "&e&lHunter", "&7Rankup to &e&lHUNTER&7!", "&e&l"),
    DEALER("Dealer", "&e&lDealer", "&7Rankup to &e&lDEALER&7!", "&e&l"),
    PIMP("Pimp", "&d&lPimp", "&7Rankup to &d&lPIMP&7!", "&d&l"),
    MOBSTER("Mobster", "&e&lMobster", "&7Rankup to &e&lMOBSTER&7!", "&e&l"),
    GODFATHER("Godfather", "&e&lGodfather", "&7Rankup to &1&lGODFATHER&7!", "&e&l"),
    GTM_God(Core.getSettings().getServer_GTM_shortName() + "God", "&4&l" + Core.getSettings().getServer_GTM_name() + " God", "&7Have &a1000 &7or more hours total playtime!", "&4&l"),
    Psychopath("Psychopath", "&c&lPsychopath", "&7Have &a10000 &7or more total kills", "&c&l"),
    Witness("Witness", "&e&lWitness", "&7Witness &4&lPresidentx &7online!", "&e&l"),
    Memelord("Memelord", "&e&lMemelord", "&7Own the rare Haramabe pet!", "&e&l");

    private String shortName;
    private String title;
    private String description;
    private String color;

    Achievement(String shortName, String title, String description, String color) {
        this.shortName = shortName;
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public static Optional<Achievement> getAchivement(String search) {
        search = search.toLowerCase();
        for(Achievement achievement : Achievement.values()) {
            if(achievement.getShortName().toLowerCase().contains(search) || achievement.getTitle().toLowerCase().contains(search)) {
                return Optional.ofNullable(achievement);
            }
        }
        return Optional.empty();
    }

    public static Optional<Achievement> getAchivementExact(String search) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getShortName().equalsIgnoreCase(search) || achievement.getTitle().equalsIgnoreCase(search)) {
                return Optional.ofNullable(achievement);
            }
        }
        return Optional.empty();
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getColor() {
        return Utils.f(color);
    }
}
