package net.grandtheftmc.core.currency.trans;

import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.currency.component.CurrencySource;

public class CurrencyTransaction {

	/** The currency involved */
	private final Currency currency;
	/** The source of the transaction */
	private final CurrencySource source;
	/** The amount involved */
	private final int amount;
	/** Whether it was a deposit */
	private final boolean deposit;

	/**
	 * Construct a new CurrencyTransaction.
	 * <p>
	 * This is used to log currency transfers in a player's purse.
	 * </p>
	 * 
	 * @param currency - the currency involved in the transaction
	 * @param source - the source of the currency
	 * @param amount - the amount of the currency
	 * @param deposit - what type of transaction this was
	 */
	public CurrencyTransaction(Currency currency, CurrencySource source, int amount, boolean deposit) {
		this.currency = currency;
		this.source = source;
		this.amount = amount;
		this.deposit = deposit;
	}

	/**
	 * Get the currency involved in the transaction.
	 * 
	 * @return The currency involved in the transaction.
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Get the source of the currency.
	 * 
	 * @return The source of the currency.
	 */
	public CurrencySource getSource() {
		return source;
	}

	/**
	 * Get the amount of the currency in the transaction.
	 * 
	 * @return The amount of currency in the transaction.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Get whether or not this is a deposit.
	 * 
	 * @return {@code true} if the transaction was a deposit, {@code false} if
	 *         it was a withdraw.
	 */
	public boolean isDeposit() {
		return deposit;
	}
}
