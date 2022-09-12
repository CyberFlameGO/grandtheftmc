package net.grandtheftmc.core.users;

import java.sql.Timestamp;

/**
 * Created by Timothy Lampen on 2/4/2018.
 */
public class CooldownPayload {
    private final String id;
    private final Timestamp expireTime;
    private final boolean saveToMySQL, serverSpecific;

    public CooldownPayload(String id, long expireTime, boolean serverSpecific, boolean saveToMySQL) {
        this.id = id;
        this.expireTime = new Timestamp(expireTime);
        this.serverSpecific = serverSpecific;
        this.saveToMySQL = saveToMySQL;
    }

    public boolean isServerSpecific() {
        return serverSpecific;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public String getId() {
        return id;
    }

    public boolean isSaveToMySQL() {
        return saveToMySQL;
    }
}
