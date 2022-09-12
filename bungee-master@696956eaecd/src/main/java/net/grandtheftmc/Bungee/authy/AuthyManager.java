package net.grandtheftmc.Bungee.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Hash;
import com.authy.api.Token;
import com.authy.api.User;
import net.grandtheftmc.Bungee.Bungee;

public class AuthyManager {
    private final String apiKey = Bungee.getSettings().getGtmConfig().getString("authy-api-key");
    private AuthyApiClient authyApiClient;

    public AuthyManager() {
        init();
    }

    public void init() {
        this.authyApiClient = new AuthyApiClient(apiKey);
    }

    public User createUser(String email, String phoneNumber, String countryCode) {
        return this.authyApiClient.getUsers().createUser(email, phoneNumber, countryCode);
    }

    public String verifyToken(int authy_id, String userToken) {
        Token verification = this.authyApiClient.getTokens().verify(authy_id, userToken);
        if (verification.isOk()) {
            return "400";
        } else {
            return verification.getError().toString();
        }
    }

    public String sendSMSToken(int authy_id) {
        Hash sms = this.authyApiClient.getUsers().requestSms(authy_id);
        if (sms.isOk()) {
            return "400";
        } else {
            return sms.getError().toString();
        }
    }
}
