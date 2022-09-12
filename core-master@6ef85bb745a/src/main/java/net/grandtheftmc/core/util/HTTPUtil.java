package net.grandtheftmc.core.util;

import com.google.gson.Gson;

/**
 * Created by Luke Bingham on 28/08/2017.
 */
public class HTTPUtil {

    private static final Gson GSON = new Gson();

    /**
     * Transform Json text to a class object.
     */
    public static <T> T transform(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
