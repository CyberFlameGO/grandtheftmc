package net.grandtheftmc.core.giftcard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Timothy Lampen on 1/5/2018.
 */
public class Data {

    private Double id;
    private String code;
    private Balance balance;

    @SerializedName("void")
    private Boolean _void;

    public Balance getBalance() {
        return balance;
    }

    public Boolean get_void() {
        return _void;
    }

    public Double getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
