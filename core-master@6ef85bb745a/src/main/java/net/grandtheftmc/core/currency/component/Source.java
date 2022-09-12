package net.grandtheftmc.core.currency.component;

import net.grandtheftmc.core.util.Identifiable;

public class Source implements Identifiable<String> {

	/** The id of the source */
	private final String id;

	/**
	 * Construct a new Source object.
	 * <p>
	 * This is useful in conjunction with CurrencySource to determine the reason
	 * for a currency transaction.
	 *
	 * @param id - the id of the source
	 */
	public Source(String id) {
		this.id = id;
	}

	/**
	 * Get the id of the source.
	 * 
	 * @return The id of the source.
	 */
	@Override
	public String getId() {
		return id;
	}
}
