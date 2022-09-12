package net.grandtheftmc.core.util;

public class TopValue implements Identifiable<String>, Comparable<TopValue>{
	
	/** The identifier of this top value*/
	private String id;
	/** The amount for this value */
	private int amount;
	
	/**
	 * Construct a new TopValue, useful for sorting top scores.
	 * <p>
	 * The id can be any string, like a username or a uuid string.
	 * </p>
	 * 
	 * @param id - the id for the top value
	 * @param amount - the amount of the value
	 */
	public TopValue(String id, int amount){
		this.id = id;
		this.amount = amount;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * Get the amount of this top value.
	 * 
	 * @return The amount for this top value.
	 */
	public int getAmount(){
		return amount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(TopValue o) {
		if (this.amount > o.getAmount()){
			return 1;
		}
		else if (this.amount < o.getAmount()){
			return -1;
		}
		
		return 0;
	}
}
