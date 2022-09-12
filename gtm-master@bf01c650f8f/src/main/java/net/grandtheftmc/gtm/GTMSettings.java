package net.grandtheftmc.gtm;

import org.bukkit.configuration.file.YamlConfiguration;

import net.grandtheftmc.core.Core;

public class GTMSettings {
	private YamlConfiguration gtmConfig;
	private YamlConfiguration warpsConfig;
	private YamlConfiguration bountiesConfig;
	private YamlConfiguration itemsConfig;
	private YamlConfiguration kitsConfig;
	private YamlConfiguration lootConfig;
	private YamlConfiguration lootCratesConfig;
	private YamlConfiguration barrelsConfig;
	private YamlConfiguration lotteryConfig;
	private YamlConfiguration drugDealerConfig;
	private YamlConfiguration drugBlocksConfig;
	private YamlConfiguration gtmShopMenuConfig;
	private YamlConfiguration christmasDropsConfig;

	private String oneTenRespack;
	private String oneTenHash;

	private String oneElevenRespack;
	private String oneElevenHash;

	/** The name of the main map */
	private String map;
	/** Are transfers to other servers allowed */
	private boolean serverTransfer;
	/** Are players allowed to pvp eachother */
	private boolean pvp;
	/** Are all global cheatcodes enabled */
	private boolean globalCheatcodes;
	/** Is /pay enabled */
	private boolean payCommand;
	/** Can players transfer between bank accounts */
	private boolean bankToBankTransfer;
	/** Can players buy items/weapons/etc. */
	private boolean buy;
	/** Can players trade eachother */
	private boolean trade;
	/** Can players use the bounty system */
	private boolean bountySystem;
	/** Can we tax the bounties placed  */
	private boolean bountySystemTax;
	/** The percent of tax applied to new bounties placed */
	private double bountySystemTaxPercent;
	/** Can players use the kit system */
	private boolean kitSystem;
	/** Players money lost upon death to server */
	private boolean serverDeathTax;
	/** Base percent of money lost to the death tax */
	private double serverDeathBasePercent;
	/** Minimum amount of money lost per death */
	private int serverDeathTaxMin;
	/** Maximum amount of money lost per death */
	private int serverDeathTaxMax;
	/** Does the death tax scale per GTMRank */
	private boolean serverDeathTaxScaled;

	public void setGtmShopMenuConfig(YamlConfiguration gtmShopMenuConfig) {
		this.gtmShopMenuConfig = gtmShopMenuConfig;
	}

	public void setChristmasDropsConfig(YamlConfiguration christmasDropsConfig) {
		this.christmasDropsConfig = christmasDropsConfig;
	}

	public YamlConfiguration getChristmasDropsConfig() {
		return this.christmasDropsConfig;
	}

	public YamlConfiguration getGtmShopMenuConfig() {
		return gtmShopMenuConfig;
	}

	public YamlConfiguration getDrugBlocksConfig() {
		return drugBlocksConfig;
	}

	public void setDrugBlocksConfig(YamlConfiguration drugBlocksConfig) {
		this.drugBlocksConfig = drugBlocksConfig;
	}

	public YamlConfiguration getDrugDealerConfig() {
		return drugDealerConfig;
	}

	public void setDrugDealerConfig(YamlConfiguration drugDealerConfig) {
		this.drugDealerConfig = drugDealerConfig;
	}

	public YamlConfiguration getGtmConfig() {
		return this.gtmConfig;
	}

	public void setGtmConfig(YamlConfiguration gtmConfig) {
		this.gtmConfig = gtmConfig;
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

	/**
	 * Whether or not server transferring is allowed.
	 * 
	 * @return {@code true} if server transferring is allowed, {@code false}
	 *         otherwise.
	 */
	public boolean isServerTransfer() {
		return serverTransfer;
	}

	/**
	 * Set whether or not server transferring is allowed.
	 * 
	 * @param serverTransfer - {@code true} if players can transfer server data,
	 *            {@code false} otherwise.
	 */
	public void setServerTransfer(boolean serverTransfer) {
		this.serverTransfer = serverTransfer;
	}

	/**
	 * Get whether or not players are allowed to pvp.
	 * <p>
	 * This is used to cancel damage events of any source.
	 * </p>
	 * 
	 * @return {@code true} if pvp damage events are allowed, {@code false}
	 *         otherwise.
	 */
	public boolean isPvp() {
		return pvp;
	}

	/**
	 * Set whether players are allowed to pvp.
	 * <p>
	 * This is used to cancel damage events of any source.
	 * </p>
	 * 
	 * @param pvp - {@code true} if players can take damage, {@code false}
	 *            otherwise.
	 */
	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	/**
	 * Get whether or not cheatcodes are enabled.
	 * 
	 * @return {@code true} if cheatcodes are enabled, {@code false} otherwise.
	 */
	public boolean isGlobalCheatcodes() {
		return globalCheatcodes;
	}

	/**
	 * Set whether cheatcodes are enabled or not.
	 * 
	 * @param globalCheatcodes - {@code true} if cheatcodes are enabled,
	 *            {@code false} otherwise.
	 */
	public void setGlobalCheatcodes(boolean globalCheatcodes) {
		this.globalCheatcodes = globalCheatcodes;
	}

	/**
	 * Get whether or not the pay command is enabled.
	 * 
	 * @return {@code true} if players can use the pay command, {@code false}
	 *         otherwise.
	 */
	public boolean isPayCommand() {
		return payCommand;
	}

	/**
	 * Set whether or not the pay command is enabled.
	 * 
	 * @param payCommand - {@code true} if the pay command is enabled,
	 *            {@code false} otherwise.
	 */
	public void setPayCommand(boolean payCommand) {
		this.payCommand = payCommand;
	}

	/**
	 * Get whether or not players can transfer between bank accounts.
	 * 
	 * @return {@code true} if they can transfer between bank accounts,
	 *         {@code false} otherwise.
	 */
	public boolean isBankToBankTransfer() {
		return bankToBankTransfer;
	}

	/**
	 * Set whether or not players can transfer between bank accounts.
	 * 
	 * @param bankToBankTransfer - {@code true} if players can transfer between
	 *            bank accounts
	 */
	public void setBankToBankTransfer(boolean bankToBankTransfer) {
		this.bankToBankTransfer = bankToBankTransfer;
	}

	/**
	 * Get whether or not players can buy items/weapons/armor/etc.
	 * 
	 * @return {@code true} if players can buy things, {@code false} otherwise.
	 */
	public boolean canBuy() {
		return buy;
	}

	/**
	 * Set whether or not players can buy items/weapons/armor/etc.
	 * 
	 * @param buy - {@code true} if players can buy things, {@code false}
	 *            otherwise.
	 */
	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	/**
	 * Get whether or not players can trade eachother.
	 * 
	 * @return {@code true} if players can trade eachother, {@code false}
	 *         otherwise.
	 */
	public boolean canTrade() {
		return trade;
	}

	/**
	 * Set whether or not players can trade eachother.
	 * 
	 * @param trade - {@code true} if players can trade eachother
	 */
	public void setTrade(boolean trade) {
		this.trade = trade;

		if (Core.getInstance().getTradeManager() != null)
			Core.getInstance().getTradeManager().setEnabled(trade);
	}

	/**
	 * Get whether or not players can use the bounty system.
	 * 
	 * @return {@code true} if players can use the bounty system.
	 */
	public boolean isBountySystem() {
		return bountySystem;
	}

	/**
	 * Set whether or not players can use the bounty system.
	 * 
	 * @param bountySystem - {@code true} if the bounty system can be used.
	 */
	public void setBountySystem(boolean bountySystem) {
		this.bountySystem = bountySystem;
	}
	
	/**
	 * Get whether or not there is a tax on placing new bounties.
	 * <p>
	 * Note: When players place new bounties on players, some of the money
	 * will be taken by the server.
	 * </p>
	 * 
	 * @return {@code true} if there is a bounty tax, {@code false}
	 *         otherwise.
	 */
	public boolean isBountyTax() {
		return bountySystemTax;
	}

	/**
	 * Set whether or not there is a bounty tax.
	 * <p>
	 * Note: When players place new bounties on players, some of the money
	 * will be taken by the server.
	 * </p>
	 * 
	 * @param bountySystemTax - {@code true} if there is a bounty tax,
	 *            {@code false} otherwise.
	 */
	public void setBountyTax(boolean bountySystemTax) {
		this.bountySystemTax = bountySystemTax;
	}
	
	/**
	 * Get the base amount of tax percent that is taken from the player's
	 * bounty when they create a new bounty.
	 * <p>
	 * Note: If this balance is set to 22.5, 22.5% of the money that would've
	 * been added to the bounty is removed from the server.
	 * </p>
	 * 
	 * @return The base amount of tax, as a percent, to be removed from the bounty when it is placed.
	 */
	public double getBountyTaxPercent() {
		return bountySystemTaxPercent;
	}

	/**
	 * Set the base amount of tax percent that is taken from the player's
	 * bounty when they create a new bounty.
	 * <p>
	 * Note: If this balance is set to 22.5, 22.5% of the money that would've
	 * been added to the bounty is removed from the server.
	 * </p>
	 * 
	 * @param bountySystemTaxPercent - the new base percent
	 */
	public void setBountyTaxPercent(double bountySystemTaxPercent) {
		this.bountySystemTaxPercent = bountySystemTaxPercent;
	}

	/**
	 * Get whether or not the kit system is enabled for players.
	 * 
	 * @return {@code true} if the kit system is enabled, {@code false}
	 *         otherwise.
	 */
	public boolean isKitSystem() {
		return kitSystem;
	}

	/**
	 * Set whether or not the kit system is enabled for players.
	 * 
	 * @param kitSystem - {@code true} if players can use kits
	 */
	public void setKitSystem(boolean kitSystem) {
		this.kitSystem = kitSystem;
	}

	/**
	 * Get whether or not there is a server death tax.
	 * <p>
	 * Note: If a player dies, they have money to drop to other players, if this
	 * is set to {@code true}, some of the money will be taken by the server.
	 * </p>
	 * 
	 * @return {@code true} if there is a server death tax, {@code false}
	 *         otherwise.
	 */
	public boolean isServerDeathTax() {
		return serverDeathTax;
	}

	/**
	 * Set whether or not there is a server death tax.
	 * <p>
	 * Note: If a player dies, they have money to drop to other players, if this
	 * is set to {@code true}, some of the money will be taken by the server.
	 * </p>
	 * 
	 * @param serverDeathTax - {@code true} if there is a server death tax,
	 *            {@code false} otherwise.
	 */
	public void setServerDeathTax(boolean serverDeathTax) {
		this.serverDeathTax = serverDeathTax;
	}

	/**
	 * Get the base amount of tax percent that is taken from the player's
	 * available balance.
	 * <p>
	 * Note: If this balance is set to 22.5, 22.5% of the money that would've
	 * been dropped is lost forever to the server.
	 * </p>
	 * 
	 * @return The base amount of tax, as a percent, to be stolen from the
	 *         player's available balance.
	 */
	public double getServerDeathBasePercent() {
		return serverDeathBasePercent;
	}

	/**
	 * Set the base amount of tax percent that is taken from the player's
	 * available balance.
	 * <p>
	 * Note: If this balance is set to 22.5, 22.5% of the money that would've
	 * been dropped is lost forever to the server.
	 * </p>
	 * 
	 * @param serverDeathBasePercent - the new base percent
	 */
	public void setServerDeathBasePercent(double serverDeathBasePercent) {
		this.serverDeathBasePercent = serverDeathBasePercent;
	}

	/**
	 * Get the minimum amount of money a player must lose to the server.
	 * <p>
	 * Note: If a player would drop $500, and 50% is available to the serverTax,
	 * which is $250, and this value is set to $400, $400 would be taken and
	 * only $100 would be dropped.
	 * </p>
	 * 
	 * @return The amount, as an integer, that a player will lose to the server
	 *         upon death.
	 */
	public int getServerDeathTaxMin() {
		return serverDeathTaxMin;
	}

	/**
	 * Set the minimum amount of money that a player must lose to the server.
	 * <p>
	 * Note: If a player would drop $500, and 50% is available to the serverTax,
	 * which is $250, and this value is set to $400, $400 would be taken and
	 * only $100 would be dropped.
	 * </p>
	 * 
	 * @param serverDeathTaxMin - the minimum amount of money, as an integer
	 */
	public void setServerDeathTaxMin(int serverDeathTaxMin) {
		this.serverDeathTaxMin = serverDeathTaxMin;
	}

	/**
	 * Get the max amount of money a player must lose to the server.
	 * <p>
	 * Note: If a player would drop $500, and 50% is available to the serverTax,
	 * which is $250, and this value is set to $200, $200 would be taken and
	 * only $300 would be dropped.
	 * </p>
	 * 
	 * @return The amount, as an integer, that a player will lose to the server
	 *         upon death.
	 */
	public int getServerDeathTaxMax() {
		return serverDeathTaxMax;
	}

	/**
	 * Set the max amount of money that a player must lose to the server.
	 * <p>
	 * Note: If a player would drop $500, and 50% is available to the serverTax,
	 * which is $250, and this value is set to $200, $200 would be taken and
	 * only $300 would be dropped.
	 * </p>
	 * 
	 * @param serverDeathTaxMax - the max amount of money, as an integer
	 */
	public void setServerDeathTaxMax(int serverDeathTaxMax) {
		this.serverDeathTaxMax = serverDeathTaxMax;
	}

	/**
	 * Get whether or not the server death tax scales with the GTMRank.
	 * <p>
	 * Note: GTMRank would increase the base tax rate, so if a GTMRank had an
	 * additional 5% tax, it would be the base tax rate, say 20%, plus
	 * additional 5%.
	 * </p>
	 * 
	 * @return {@code true} if the server death tax scales with GTMRank,
	 *         {@code false} otherwise.
	 */
	public boolean isServerDeathTaxScaled() {
		return serverDeathTaxScaled;
	}

	/**
	 * Sets whether or not the server death tax scales with the GTMRank.
	 * <p>
	 * Note: GTMRank would increase the base tax rate, so if a GTMRank had an
	 * additional 5% tax, it would be the base tax rate, say 20%, plus
	 * additional 5%.
	 * </p>
	 * 
	 * @param serverDeathTaxScaled - {@code true} if the death tax scales with
	 *            GTMRank, {@code false} otherwise.
	 */
	public void setServerDeathTaxScaled(boolean serverDeathTaxScaled) {
		this.serverDeathTaxScaled = serverDeathTaxScaled;
	}
}
