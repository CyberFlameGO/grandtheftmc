package net.grandtheftmc.core.transaction.state;

import net.grandtheftmc.core.transaction.TransactionEvent;

public abstract class StateTransactionEvent<T extends StateTransaction> extends TransactionEvent<T> {

	/**
	 * Create a new StateTransactionEvent.
	 * 
	 * @param transaction - the transaction involved in the event
	 */
	public StateTransactionEvent(T transaction) {
		super(transaction);
	}
}
