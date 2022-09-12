package net.grandtheftmc.core.currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.grandtheftmc.core.currency.component.CurrencySource;
import net.grandtheftmc.core.currency.trans.CurrencyTransaction;

public class Purse {

	/** Maps currency to amount of that currency */
	private Map<Currency, Integer> currencyToAmount;
	/** List of currency transactions to log */
	private List<CurrencyTransaction> trans;

	/**
	 * Construct a new Purse to hold currencies.
	 */
	public Purse() {
		this.currencyToAmount = new HashMap<>();
		this.trans = new ArrayList<>();
	}

	/**
	 * Register this currency to the purse.
	 * 
	 * @param currency - the currency to register
	 * @result {@code true} if the currency was registered, {@code false} if it
	 *         already exists.
	 */
	public boolean registerCurrency(Currency currency) {
		if (!currencyToAmount.containsKey(currency)) {
			currencyToAmount.put(currency, 0);
			return true;
		}

		return false;
	}

	/**
	 * Get the balance of the specified currency.
	 * 
	 * @param currency - the currency to get the balance of
	 * 
	 * @return The amount of the currency currently within this purse, if it
	 *         exists, otherwise -1.
	 */
	public int getBalance(Currency currency) {
		if (currencyToAmount.containsKey(currency)) {
			return currencyToAmount.get(currency);
		}

		return -1;
	}

	/**
	 * Deposit the selected currency in the purse.
	 * 
	 * @param source - the reason for the deposit
	 * @param currency - the currency to deposit
	 * @param amount - the amount to deposit
	 * 
	 * @return The new balance for the currency, if it exists, otherwise -1.
	 */
	public int deposit(CurrencySource source, Currency currency, int amount) {

		if (currencyToAmount.containsKey(currency)) {
			int initial = currencyToAmount.get(currency);

			if (initial + amount < Integer.MAX_VALUE) {
				currencyToAmount.put(currency, initial + amount);
			}
			else {
				currencyToAmount.put(currency, Integer.MAX_VALUE);
			}

			int newBal = currencyToAmount.get(currency);

			// log transaction
			trans.add(new CurrencyTransaction(currency, source, amount, true));

			return newBal;
		}

		return -1;
	}

	/**
	 * Withdraw the selected currency from the purse.
	 * 
	 * @param source - the reason for the withdraw
	 * @param currency - the currency to withdraw
	 * @param amount - the amount to withdraw
	 * 
	 * @return The new balance for the currency, if it exists, otherwise -1. If
	 *         we cannot withdraw that amount, we will set the balance to 0.
	 */
	public int withdraw(CurrencySource source, Currency currency, int amount) {

		if (currencyToAmount.containsKey(currency)) {
			int initial = currencyToAmount.get(currency);
			int newValue = initial - amount;

			if (newValue >= 0) {
				currencyToAmount.put(currency, newValue);
			}
			else {
				currencyToAmount.put(currency, 0);
			}

			// log transaction
			trans.add(new CurrencyTransaction(currency, source, -1 * amount, false));

			return currencyToAmount.get(currency);
		}

		return -1;
	}

	/**
	 * Sets the selected currency to the specified amount.
	 * 
	 * @param currency - the currency to set
	 * @param value - the value to set the currency at
	 * 
	 * @return {@code true} if the currency was set to the specified amount,
	 *         {@code} false otherwise.
	 */
	public boolean set(Currency currency, int value) {

		if (currencyToAmount.containsKey(currency)) {
			currencyToAmount.put(currency, value);
			return true;
		}

		return false;
	}

	/**
	 * Get all the currencies that this Purse knows about.
	 * 
	 * @return An unmodifiable map of all the currencies this purse knows about.
	 */
	public Map<Currency, Integer> getCurrencies() {
		return Collections.unmodifiableMap(currencyToAmount);
	}

	/**
	 * Get a list of currency transactions that this purse has kept track of.
	 * 
	 * @return An unmodifiable list of currency transactions that this purse has
	 *         kept track of.
	 */
	public List<CurrencyTransaction> getTransactionLog() {
		return Collections.unmodifiableList(trans);
	}
}
