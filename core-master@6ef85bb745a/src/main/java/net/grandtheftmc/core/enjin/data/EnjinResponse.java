package net.grandtheftmc.core.enjin.data;

/**
 * Created by Adam on 10/06/2017.
 */
public abstract class EnjinResponse {

    public abstract void callback(EnjinResult response, String user, String tag);

}
