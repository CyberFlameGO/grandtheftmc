package net.grandtheftmc.core.transaction;

import org.bukkit.event.Event;

public abstract class TransactionEvent<T extends Transaction> extends Event {

	/** The transaction assigned to this event */
	private final T transaction;
	/** Whether or not the transaction has been processed */
	private boolean processed = false;

	/**
	 * Construct a new TransactionEvent.
	 * 
	 * @param transaction - the transaction to process
	 */
	public TransactionEvent(T transaction) {
		this.transaction = transaction;
	}

	/**
	 * Get the transaction involved in this event.
	 * 
	 * @return The transaction involved in this event.
	 */
	public T getTransaction() {
		return transaction;
	}

	/**
	 * Get whether or not this transaction was processed.
	 * 
	 * @return {@code true} if transaction was processed, {@code false}
	 *         otherwise.
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * Set whether or not the transaction has been processed.
	 * 
	 * @param processed - {@code true} if it was processed
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
