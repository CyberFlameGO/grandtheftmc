package net.grandtheftmc.vice.hologram.exception;

import net.grandtheftmc.vice.hologram.Hologram;

public class HologramDuplicateNodeException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     */
    public HologramDuplicateNodeException(Hologram hologram, int id) {
        super("Hologram[" + hologram.getId() + "] already has a Node with id " + id);
    }
}
