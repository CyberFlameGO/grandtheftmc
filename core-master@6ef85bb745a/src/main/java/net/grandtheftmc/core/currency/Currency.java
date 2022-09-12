package net.grandtheftmc.core.currency;

import java.util.Optional;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Identifiable;

public enum Currency implements Identifiable<String> {

	CROWBAR("CROWBAR", "crowbar", "crowbars"),
	TOKEN("TOKEN", "token", "tokens"),
	MONEY("MONEY", "money", "money"),
	PERMIT("PERMIT", "permit", "permits"),
	COUPON_CREDIT("COUPON_CREDIT", "coupon credit", "coupon credits"),
	VOTE_TOKEN("VOTE_TOKEN", "vote token", "vote tokens");

	/** The id of the currency */
	private final String id;
	/** The singular form for the currency */
	private final String singular;
	/** The plural form for the currency */
	private final String plural;
	/** The server_key that this currency is for */
	private String serverKey;

	/**
	 * Construct a new currency enum constant.
	 * 
	 * @param id - the id of the currency
	 * @param singular - the singular representation for this currency
	 * @param plural - the plurarl representation for this currency
	 */
	Currency(String id, String singular, String plural) {
		this.id = id;
		this.singular = singular;
		this.plural = plural;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the currency id representation as a singular id.
	 * <p>
	 * Note: "coin" is now "coin"
	 * </p>
	 * 
	 * @return The currency id representation as a singular id.
	 */
	public String asSingular() {
		return singular;
	}

	/**
	 * Get the currency id representation as a plural id.
	 * <p>
	 * Note: "coin" is now "coins"
	 * </p>
	 * 
	 * @return The currency id representation as a plural id.
	 */
	public String asPlural() {
		return plural;
	}

	/**
	 * Get the server key that this currency is for.
	 * <p>
	 * Note: This must be set by the plugin or project for each currency.
	 * </p>
	 * 
	 * @return If a currency is a global one, "GLOBAL" is returned, or if it's a
	 *         per server currency, the server will be returned, i.e. "GTM1".
	 */
	public String getServerKey() {
		
		// if not set, assume per server
		if (serverKey == null || serverKey.isEmpty()){
			return Core.name().toUpperCase();
		}
		
		return serverKey;
	}

	/**
	 * Set the server key that this currency is for.
	 * <p>
	 * Note: This must be set by the plugin or project for each currency.
	 * </p>
	 * If a currency is a global currency, set this as "GLOBAL". If a currency is per server, set this as the name of the server, "GTM1".
	 * 
	 * @param serverKey - the server key for this currency
	 */
	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

	/**
	 * Get the specified currency based off the id.
	 * 
	 * @param id - the id of the currency
	 * 
	 * @return The specified currency based off the id, if one exists, otherwise
	 *         empty.
	 */
	public static Optional<Currency> fromID(String id) {
		for (Currency c : values()) {
			if (c.getId().equalsIgnoreCase(id)) {
				return Optional.of(c);
			}
		}

		return Optional.empty();
	}
}
