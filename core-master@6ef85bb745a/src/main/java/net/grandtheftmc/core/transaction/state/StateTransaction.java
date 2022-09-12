package net.grandtheftmc.core.transaction.state;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.json.simple.JSONObject;

import net.grandtheftmc.core.transaction.Transaction;

public abstract class StateTransaction extends Transaction {

	/** The payload involved in this transaction */
	private final JSONObject payload;
	/** The time to process the transaction at */
	private final long processAt;

	/**
	 * Create a new StateTransaction.
	 * <p>
	 * This is used to represent a JSON object that be parsed to do something
	 * with.
	 * 
	 * @param id - the id of the transaction
	 * @param payload - the payload json involved in this transaction
	 * @param creation - the creation timestamp
	 * @param processAt - the process timestamp
	 */
	public StateTransaction(int id, JSONObject payload, long creation, long processAt) {
		super(id, creation);
		this.payload = payload;
		this.processAt = processAt;
	}

	/**
	 * Get the payload involved in this transaction.
	 * 
	 * @return The payload involved in this transaction.
	 */
	public JSONObject getPayload() {
		return payload;
	}

	/**
	 * Get the time, in milliseconds, to process the transaction.
	 * 
	 * @return The time to process the transaction.
	 */
	public long getProcessAt() {
		return processAt;
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

		return super.equals(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 37).append(getId()).toHashCode();
	}
}
