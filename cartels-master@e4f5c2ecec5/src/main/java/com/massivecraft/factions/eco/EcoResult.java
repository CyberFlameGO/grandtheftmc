package com.massivecraft.factions.eco;

/**
 * Created by Luke Bingham on 31/07/2017.
 */
public enum EcoResult {

    /**
     * If the transaction goes well,
     * This shall be returned.
     */
    SUCCESS(),

    /**
     * If the balance is hitting the maximum capacity,
     * This shall be returned.
     */
    OVERWEIGHT(),

    /**
     * If the transaction cannot be made due to lack of funds,
     * This shall be returned.
     */
    LOW_FUNDS(),

    /**
     * This shall be returned when a Unknown issue occurs.
     */
    UNKNOWN(),
}
