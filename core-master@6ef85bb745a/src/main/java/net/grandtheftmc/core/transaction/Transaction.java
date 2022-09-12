package net.grandtheftmc.core.transaction;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class Transaction {

	/** The ID of the transaction */
	private final int id;
	/** The creation of the transaction */
	private final long creation;

	/**
	 * Create a new Transaction.
	 * 
	 * @param id - the id of the transaction 
	 * @param creation - the timestamp in millis for the transaction
	 */
	public Transaction(int id, long creation) {
		this.id = id;
		this.creation = creation;
	}

	/**
	 * Get the id of the transaction.
	 * 
	 * @return The id of the transaction.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the creation, in milliseconds, or the timestamp, for the transaction.
	 * 
	 * @return The timestamp, in milliseconds, for this transaction.
	 */
	public long getCreation() {
		return creation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Transaction that = (Transaction) o;

		return new EqualsBuilder()
				.append(id, that.id)
				.isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(id)
				.toHashCode();
	}
}



