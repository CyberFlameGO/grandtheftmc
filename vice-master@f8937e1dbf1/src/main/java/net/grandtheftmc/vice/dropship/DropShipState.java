package net.grandtheftmc.vice.dropship;

public enum DropShipState {

    /** This is active while initialising */
    IDLE,

    /** This is active when the init process is complete */
    STARTING,

    /** This is active when the drop ship is in progress */
    IN_PROGRESS,

    /** This is active when the drop ship is finishing */
    ENDING,
    ;
}
