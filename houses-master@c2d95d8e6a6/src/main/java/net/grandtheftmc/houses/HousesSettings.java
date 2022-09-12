package net.grandtheftmc.houses;

import org.bukkit.configuration.file.YamlConfiguration;

public class HousesSettings {
    private YamlConfiguration housesConfig;
    private YamlConfiguration premiumHousesConfig;

    public YamlConfiguration getHousesConfig() {
        return this.housesConfig;
    }

    public void setHousesConfig(YamlConfiguration c) {
        this.housesConfig = c;
    }
    public YamlConfiguration getPremiumHousesConfig() {
        return this.premiumHousesConfig;
    }

    public void setPremiumHousesConfig(YamlConfiguration c) {
        this.premiumHousesConfig = c;
    }

}
