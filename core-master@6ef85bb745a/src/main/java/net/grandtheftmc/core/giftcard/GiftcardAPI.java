package net.grandtheftmc.core.giftcard;

import com.google.gson.Gson;
import net.buycraft.plugin.internal.okhttp3.FormBody;
import net.buycraft.plugin.internal.okhttp3.Request;
import net.buycraft.plugin.internal.okhttp3.Response;
import net.buycraft.plugin.internal.okhttp3.ResponseBody;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.HTTPUtil;
import net.grandtheftmc.core.util.ServerUtil;

import java.io.IOException;

/**
 * Created by Luke Bingham on 28/08/2017.
 */
public class GiftcardAPI {
    private static final Gson GSON = new Gson();

    /**
     * @param callback called when the task is completed. Returns the code of the giftcard just created.
     */
    public static void postGiftCard(final double amount, net.grandtheftmc.core.util.Callback<Giftcard> callback) {
        ServerUtil.runTaskAsync(() -> {
            try {
                Response response = Core.getOkHttpClient().newCall(
                        new Request.Builder().url("https://plugin.buycraft.net/gift-cards")
                                .addHeader("X-Buycraft-Secret", Core.getInstance().getBuycraftSecret())
                                .addHeader("Accept", "application/json")
                                .addHeader("User-Agent", "BuycraftX")
                                .post(new FormBody.Builder().add("amount", String.valueOf(amount)).build())
                                .build()
                ).execute();

                try(ResponseBody responseBody = response.body()) {
                    if(response.isSuccessful()) {
                        String body = responseBody.string();
                        Giftcard card = GSON.fromJson(body, Giftcard.class);
                        callback.call(card);
                        return;
                    }
                    callback.call(null);
                }
            } catch (IOException e) {
                callback.call(null);
                e.printStackTrace();
            }
        });
    }

    public static void getGiftCard(final int id, net.grandtheftmc.core.util.Callback<GiftcardAPI> callback) {
        ServerUtil.runTaskAsync(() -> {
            try {
                Response response = Core.getOkHttpClient().newCall(
                        new Request.Builder().url("https://plugin.buycraft.net/gift-cards/" + id)
                                .addHeader("X-Buycraft-Secret", Core.getInstance().getBuycraftSecret())
                                .addHeader("Accept", "application/json")
                                .addHeader("User-Agent", "BuycraftX")
                                .get()
                                .build()
                ).execute();

                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String body = responseBody.string();
                        callback.call(HTTPUtil.transform(body, GiftcardAPI.class));
                        return;
                    }

                    callback.call(null);
                }
            } catch (IOException e) {
                callback.call(null);
                e.printStackTrace();
            }
        });
    }

    public static void topupGiftCard(final int id, final double amount, net.grandtheftmc.core.util.Callback<GiftcardAPI> callback) {
        ServerUtil.runTaskAsync(() -> {
            try {
                Response response = Core.getOkHttpClient().newCall(
                        new Request.Builder().url("https://plugin.buycraft.net/gift-cards/" + id)
                                .addHeader("X-Buycraft-Secret", Core.getInstance().getBuycraftSecret())
                                .addHeader("Accept", "application/json")
                                .addHeader("User-Agent", "BuycraftX")
                                .post(new FormBody.Builder().add("amount", String.valueOf(amount)).build())
                                .build()
                ).execute();

                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String body = responseBody.string();
                        callback.call(HTTPUtil.transform(body, GiftcardAPI.class));
                        return;
                    }

                    callback.call(null);
                }
            } catch (IOException e) {
                callback.call(null);
                e.printStackTrace();
            }
        });
    }

    /**
     * GiftCards cannot be removed from the API,
     * So to 'delete' them we set them as VOID.
     */
    public static void voidGiftCard(final int id, net.grandtheftmc.core.util.Callback<GiftcardAPI> callback) {
        ServerUtil.runTaskAsync(() -> {
            try {
                Response response = Core.getOkHttpClient().newCall(
                        new Request.Builder().url("https://plugin.buycraft.net/gift-cards/" + id)
                                .addHeader("X-Buycraft-Secret", Core.getInstance().getBuycraftSecret())
                                .addHeader("Accept", "application/json")
                                .addHeader("User-Agent", "BuycraftX")
                                .delete()
                                .build()
                ).execute();

                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String body = responseBody.string();
                        callback.call(HTTPUtil.transform(body, GiftcardAPI.class));
                        return;
                    }

                    callback.call(null);
                }
            } catch (IOException e) {
                callback.call(null);
                e.printStackTrace();
            }
        });
    }
}
