package net.grandtheftmc.vice.hologram.exception;

public class HologramDuplicateException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     */
    public HologramDuplicateException(int id) {
        super("A Hologram with id " + id + " already exists!");
    }
}
