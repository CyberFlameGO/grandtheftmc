package net.grandtheftmc.Bungee.tasks;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.users.UserRank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class AuthyTask {

    public AuthyTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> {
            Bungee.getUserManager().getLoadedUsers().forEach(user -> {
                if (!user.isRank(UserRank.BUILDER)) return;

                ProxiedPlayer player = Bungee.getInstance().getProxy().getPlayer(user.getUUID());
                if (player == null) return;

                if (!user.isAuthyVerified()) {
                    if (user.getAuthyId() == 0) {
                        player.sendMessage(Lang.VERIFICATION.ft("&7You must register with 2FA &7before &7continuing! " +
                                "&a/authy register <email> <phone number> &a<countrycode> " +
                                "&7Need help? &a/authy help &7or contact a &4&lManager"));
                    }

                    else {
                        TextComponent textComponent = Lang.VERIFICATION.ft("&7Please enter your 2 factor authentication &7code before continuing! &a/authy verify <code>");
                        textComponent.setColor(ChatColor.GRAY);
                        player.sendMessage(textComponent);
                    }
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }
}
