package net.grandtheftmc.core.currency.component;

import java.util.Optional;

/**
 * This class should be treated as a static enum constant, where you can define
 * currency source here, or create new Objects later.
 * 
 * When this class is used in transaction history, it will log the source
 * (type), and the reason (if one exists).
 */
public class CurrencySource {

	public static final CurrencySource CUSTOM = new CurrencySource(new Source("CUSTOM"));
	public static final CurrencySource VOTE = new CurrencySource(new Source("VOTE"));
	public static final CurrencySource KILL_PLAYER = new CurrencySource(new Source("KILL_PLAYER"));
	public static final CurrencySource KILL_ENTITY = new CurrencySource(new Source("KILL_ENTITY"));

	/** The source for the currency */
	private final Source source;
	/** The reason for the currency transaction */
	private final String reason;

	/**
	 * Construct a new CurrencySource.
	 * 
	 * @param source - the source for the currency
	 * @param reason - the reason for the transaction.
	 */
	public CurrencySource(Source source, String reason) {
		this.source = source;
		this.reason = reason;
	}

	/**
	 * Construct a new CurrencySource.
	 * <p>
	 * Wrapper around {@link #CurrencySource(Source, String)}.
	 * </p>
	 * 
	 * @param source - the source for the currency
	 */
	public CurrencySource(Source source) {
		this(source, null);
	}

	/**
	 * Get the source of the currency.
	 * 
	 * @return The source of the currency.
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Get the reason for the transaction.
	 * 
	 * @return The reason for this currency to be given, if one exists.
	 */
	public Optional<String> getReason() {
		if (reason == null || reason.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(reason);
	}
}
