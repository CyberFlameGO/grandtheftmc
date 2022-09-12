package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.users.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class AuthyCommand extends Command {

    public AuthyCommand() {
        super("authy", "authy.admin");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) s;
        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        if (!userOptional.isPresent()) return;
        User user = userOptional.get();
        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help"))
                sendHelp(player);

            else if (args[0].equalsIgnoreCase("countrycodes"))
                sendCountryCodes(player);

            return;
        }

        else {
            if (user.isAuthyVerified()) {
                player.sendMessage(Lang.VERIFICATION.ft("&7You are already verified!"));
                return;
            }

            if (args[0].equalsIgnoreCase("verify")) {
                if (args.length != 2) {
                    sendHelp(player);
                } else {
                    String token = args[1];
                    if (user.getAuthyId() == 0) {
                        player.sendMessage(Lang.VERIFICATION.ft("&cYou must register first!"));
                    } else {
                        String result = Bungee.getAuthyManager().verifyToken(user.getAuthyId(), token);
                        if (result.equals("400")) {
                            user.setAuthyVerified(true);
                            player.sendMessage(Lang.VERIFICATION.ft("&aVerification Successful"));
                            user.setLastIPAddress(player.getAddress().getAddress().getHostAddress());
                        } else {
                            player.sendMessage(Lang.VERIFICATION.ft("&cError! Verification Failed: " + result));
                        }
                        return;
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("sendsms")) {
                if (user.getAuthyId() == 0) {
                    player.sendMessage(Lang.VERIFICATION.ft("&cYou must register first!"));
                } else {
                    String result = Bungee.getAuthyManager().sendSMSToken(user.getAuthyId());
                    if (result.equals("400")) {
                        player.sendMessage(Lang.VERIFICATION.ft("&aToken sent via SMS"));
                    } else {
                        player.sendMessage(Lang.VERIFICATION.ft("&cError: " + result));
                    }
                    return;
                }
            }

            else if (args[0].equalsIgnoreCase("register")) {
                if (args.length != 4) {
                    sendHelp(player);
                } else {
                    String email = args[1];
                    String phoneNumber = args[2];
                    String countryCode = args[3];
                    com.authy.api.User authyUser = Bungee.getAuthyManager().createUser(email, phoneNumber, countryCode);
                    if (authyUser.isOk()) {
                        user.setAuthyId(authyUser.getId());
                        player.sendMessage(Lang.VERIFICATION.ft("&aRegistration Successful"));
                    }

                    else {
                        player.sendMessage(Lang.VERIFICATION.ft("&cError! Registration Failed: " + authyUser.getError().toString()));
                    }
                    return;
                }
            }
        }
    }

    public void sendHelp(ProxiedPlayer player) {
        player.sendMessage(Lang.VERIFICATION.ft("&7Help"));
        player.sendMessage(Utils.ft("&a/authy help &7- Display this information"));
        player.sendMessage(Utils.ft("&a/authy verify <token> &7- Verify yourself using your Authy &7<token> (you must be registered)"));
        player.sendMessage(Utils.ft("&a/authy sendsms &7- If not using Authy app request your verification token to be sent via SMS"));
        player.sendMessage(Utils.ft("&a/authy register <email> <phone number> <country code> &7- &7Register for verification. " +
                "Example command: &a/authy register &bme@grandtheftmc.net 5276449341 1"));
        player.sendMessage(Utils.ft("&a/authy countrycodes &7- List all valid country codes"));
    }

    public void sendCountryCodes(ProxiedPlayer player) {
        player.sendMessage(Lang.VERIFICATION.ft("&7Country Codes"));
        player.sendMessages(
                "United States of America: 1" ,
                "Canada: 1" ,
                "Russia: 7" ,
                "Netherlands: 31" ,
                "Belgium: 32" ,
                "Spain: 34" ,
                "Italy: 39" ,
                "United Kingdom: 44" ,
                "Mexico: 52" ,
                "Australia: 61" ,
                "Korea (+South): 82" ,
                "Korea (+North): gtfo crue"
        );
    }

}