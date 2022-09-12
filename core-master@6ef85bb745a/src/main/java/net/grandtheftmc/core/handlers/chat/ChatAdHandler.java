package net.grandtheftmc.core.handlers.chat;

import java.util.regex.Pattern;

public class ChatAdHandler {
    private final String addressRegex;
    private final String domainRegex;

    public ChatAdHandler() {
        this.addressRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        this.domainRegex = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    }

    public boolean matchesAdvertisement(String text) {
        text = text.toLowerCase();
        for (String whitelisted : ChatManager.getSettings().getDomainWhitelist()) {
            if (text.contains(whitelisted.toLowerCase())) {
                return false;
            }
        }
        return Pattern.compile(addressRegex).matcher(text).matches() ||
                Pattern.compile(domainRegex).matcher(text).matches();
    }
}
