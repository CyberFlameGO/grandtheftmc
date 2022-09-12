package net.grandtheftmc.vice;

import org.bukkit.configuration.file.YamlConfiguration;

public class ViceSettings {
    private YamlConfiguration viceConfig;
    private YamlConfiguration warpsConfig;
    private YamlConfiguration bountiesConfig;
    private YamlConfiguration itemsConfig;
    private YamlConfiguration kitsConfig;
    private YamlConfiguration lootConfig;
    private YamlConfiguration lootCratesConfig;
    private YamlConfiguration barrelsConfig;
    private YamlConfiguration lotteryConfig;
    private YamlConfiguration drugDealersConfig;
    private YamlConfiguration drugBlocksConfig;
    private YamlConfiguration viceShopMenuConfig;
    private YamlConfiguration pickersConfig;
    private YamlConfiguration homesConfig;
    private YamlConfiguration upgradeContainersConfig;
    private YamlConfiguration playerCacheConfig;
    private YamlConfiguration zoneConfig;

    private String oneTenRespack;
    private String oneTenHash;

    private String oneElevenRespack;
    private String oneElevenHash;

    private String map;

    public YamlConfiguration getZoneConfig() {
        return zoneConfig;
    }

    public void setZoneConfig(YamlConfiguration zoneConfig) {
        this.zoneConfig = zoneConfig;
    }

    public YamlConfiguration getPlayerCacheConfig() {
        return playerCacheConfig;
    }

    public void setPlayerCacheConfig(YamlConfiguration playerCacheConfig) {
        this.playerCacheConfig = playerCacheConfig;
    }

    public void setHomesConfig(YamlConfiguration homesConfig) {
        this.homesConfig = homesConfig;
    }

    public YamlConfiguration getHomesConfig() {
        return homesConfig;
    }

    public void setViceShopMenuConfig(YamlConfiguration viceShopMenuConfig) {
        this.viceShopMenuConfig = viceShopMenuConfig;
    }

    public void setUpgradeContainersConfig(YamlConfiguration upgradeContainersConfig) {
        this.upgradeContainersConfig = upgradeContainersConfig;
    }

    public YamlConfiguration getUpgradeContainersConfig() {
        return upgradeContainersConfig;
    }

    public YamlConfiguration getViceShopMenuConfig() {
        return viceShopMenuConfig;
    }

    public YamlConfiguration getDrugBlocksConfig() {
        return drugBlocksConfig;
    }

    public YamlConfiguration getDrugDealerConfig() {
        return null;
    }//todo: added to allow jar to file

    public void setDrugDealerConfig(YamlConfiguration c) {
    }

    public void setDrugBlocksConfig(YamlConfiguration drugBlocksConfig) {
        this.drugBlocksConfig = drugBlocksConfig;
    }

    public YamlConfiguration getDrugDealersConfig() {
        return drugDealersConfig;
    }

    public void setDrugDealersConfig(YamlConfiguration drugDealersConfig) {
        this.drugDealersConfig = drugDealersConfig;
    }

    public YamlConfiguration getViceConfig() {
        return this.viceConfig;
    }

    public void setViceConfig(YamlConfiguration viceConfig) {
        this.viceConfig = viceConfig;
    }

    public YamlConfiguration getWarpsConfig() {
        return this.warpsConfig;
    }

    public void setWarpsConfig(YamlConfiguration warpsConfig) {
        this.warpsConfig = warpsConfig;
    }

    public YamlConfiguration getBountiesConfig() {
        return this.bountiesConfig;
    }

    public void setBountiesConfig(YamlConfiguration bountiesConfig) {
        this.bountiesConfig = bountiesConfig;
    }

    public YamlConfiguration getItemsConfig() {
        return this.itemsConfig;
    }

    public void setItemsConfig(YamlConfiguration itemsConfig) {
        this.itemsConfig = itemsConfig;
    }

    public YamlConfiguration getKitsConfig() {
        return this.kitsConfig;
    }

    public void setKitsConfig(YamlConfiguration kitsConfig) {
        this.kitsConfig = kitsConfig;
    }

    public YamlConfiguration getBarrelsConfig() {
        return this.barrelsConfig;
    }

    public void setBarrelsConfig(YamlConfiguration barrelsConfig) {
        this.barrelsConfig = barrelsConfig;
    }

    public String getMap() {
        return this.map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public YamlConfiguration getLootCratesConfig() {
        return this.lootCratesConfig;
    }

    public void setLootCratesConfig(YamlConfiguration lootCratesConfig) {
        this.lootCratesConfig = lootCratesConfig;
    }

    public YamlConfiguration getLootConfig() {
        return this.lootConfig;
    }

    public void setLootConfig(YamlConfiguration lootConfig) {
        this.lootConfig = lootConfig;
    }

    public YamlConfiguration getLotteryConfig() {
        return this.lotteryConfig;
    }

    public void setLotteryConfig(YamlConfiguration lotteryConfig) {
        this.lotteryConfig = lotteryConfig;
    }

    public String getOneElevenRespack() {
        return oneElevenRespack;
    }

    public String getOneElevenHash() {
        return oneElevenHash;
    }

    public String getOneTenRespack() {
        return oneTenRespack;
    }

    public String getOneTenHash() {
        return oneTenHash;
    }

    public void setOneElevenRespack(String oneElevenRespack) {
        this.oneElevenRespack = oneElevenRespack;
    }

    public void setOneElevenHash(String oneElevenHash) {
        this.oneElevenHash = oneElevenHash;
    }

    public void setOneTenRespack(String oneTenRespack) {
        this.oneTenRespack = oneTenRespack;
    }

    public void setOneTenHash(String oneTenHash) {
        this.oneTenHash = oneTenHash;
    }

    public YamlConfiguration getPickersConfig() {
        return this.pickersConfig;
    }

    public void setPickersConfig(YamlConfiguration pickersConfig) {
        this.pickersConfig = pickersConfig;
    }
}
