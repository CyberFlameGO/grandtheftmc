package net.grandtheftmc.Bungee.redisbungee;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.grandtheftmc.Bungee.utils.HelpLog;
import net.grandtheftmc.Bungee.utils.PlaytimeManager;
import net.grandtheftmc.Bungee.utils.UUIDUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RedisListener implements Listener {

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if (event.getChannel().equalsIgnoreCase(Bungee.getRedisManager().getMessageChannel())) {
            JSONObject serialized = new JSONObject(event.getMessage());

            String dataTypeString = serialized.getString("datatype");
            DataType typeEnum = DataType.valueOf(dataTypeString);

            if (typeEnum == null) {
                //Invalid datatype
                return;
            }

            DataType dataType = DataType.valueOf(serialized.getString("datatype"));

            switch (dataType) {
                case GMSG:
                    String target = serialized.getString("target");
                    if (Bungee.getInstance().getProxy().getPlayer(target) == null) return;
                    String sender = serialized.getString("sender");
                    String message = serialized.getString("message");
                    String senderColName = Utils.f(serialized.getString("senderCol"));
                    ProxiedPlayer targetPlayer = Bungee.getInstance().getProxy().getPlayer(target);
                    String url = "";
                    for (String string : message.split(" ")) {
                        if (string.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")) {
                            url = string;
                            break;
                        }
                    }
                    BaseComponent[] a = new ComponentBuilder(Lang.GMSG.f("&7[" + senderColName + "&7 -> me] &r" + message))
                            .event(url.isEmpty() ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + sender + " ")
                                    : new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                            .create();
                    targetPlayer.sendMessage(a);
                    break;

                case STAFFCHAT:
                    //A message has been distributed to the staff chat, send this message to all online staff
                    Utils.staffChat(serialized.getString("sender"), serialized.getString("message"));
                    break;

                case STAFF_JOIN:
                    PlaytimeManager.beginSession(serialized.getString("uuid"));
                    break;

                case HELP_CLOSE:
                    String staffResponseName = serialized.getString("helper");

                    String helpRequester = serialized.getString("sender");

                    if (staffResponseName.equalsIgnoreCase("null")) {
                        //If a player logs out their ticket is automatically closed.
                        HelpLog.closeHelpTicket(helpRequester);
                        return;
                    }

                    String staffResponseUUID = serialized.getString("helperUUID");

                    //If no ticket exists skip this part.
                    if (!HelpLog.helpTicketExists(helpRequester)) return;

                    boolean receiveTokens = HelpLog.closeHelpTicket(helpRequester);

                    ProxiedPlayer pp = Bungee.getInstance().getProxy().getPlayer(UUID.fromString(staffResponseUUID));

                    ProxyServer.getInstance().getPlayers()
                            .stream()
                            .filter(player -> player.hasPermission("staffchat.use"))
                            .forEach(player ->
                                    player.sendMessage(Lang.HELP.ft(staffResponseName
                                            + " &7answered " + helpRequester)));

                    if (pp == null) {
                        Bungee.log(staffResponseUUID);
                        return;
                    }

                    if (receiveTokens) {
                        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(pp.getUniqueId());
                        userOptional.ifPresent(user -> {
                            Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
                                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                    try (PreparedStatement statement = connection.prepareStatement("update users set tokens=tokens+" + 1 + " where uuid=?;")) {
                                        statement.setString(1, pp.getUniqueId().toString());
                                        statement.execute();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                        });
                    }
                    break;

                case HELP:
                    String playerName = serialized.getString("sender"),
                            server = serialized.getString("server"),
                            msg = serialized.getString("message");
                    Utils.redisHelp(playerName, msg, server);
                    HelpLog.requestHelp(playerName);
                    break;

                case SOCIALSPY:
                    String mess = Utils.f(serialized.getString("message"));
                    String[] exclude = serialized.getString("exclude").split(",");
                    Set<String> ex = new HashSet<>();
                    Arrays.stream(exclude).forEach(e -> ex.add(e.toLowerCase()));
                    //Stop players receiving social spy msgs if they have been msged or sent the msg

                    for (ProxiedPlayer proxP : ProxyServer.getInstance().getPlayers()) {
                        Optional<User> u = Bungee.getUserManager().getLoadedUser(proxP.getUniqueId());
                        if (u.isPresent() && u.get().getSocialSpy()) {
                            //User is non null has has social spy enabled.
                            proxP.sendMessage(mess);
                        }
                    }
                    break;

                case MOTD:
                    sender = serialized.getString("sender");
                    if (serialized.has("reload") && serialized.getBoolean("reload")) {
                        //we want to reload the MOTD from the config.
                        Bungee.getSettings().setMotdConfig(Utils.loadConfig("motd"));
                        Bungee.getSettings().setMotd(Bungee.getSettings().getMotdConfig().getString("motd"));

                        if (sender.equals("CONSOLE")) {
                            Bungee.getInstance().getLogger().info("MOTD was reloaded successfully.");
                        } else {
                            pp = Bungee.getInstance().getProxy().getPlayer(sender);

                            if (pp == null) return;

                            pp.sendMessage(Utils.ft("&7Bungee MOTD reloaded successfully."));
                        }
                    } else if (serialized.has("motd")) {
                        //we want to set a new MOTD, colour codes are translated in the ping event so we dont do that here.
                        String motd = serialized.getString("motd");
                        Bungee.getSettings().setMotd(motd);

                        if (sender.equals("CONSOLE")) {
                            Bungee.getInstance().getLogger().info("You have set a temporary MOTD! Note that it will reset to the motd.yml value when Bungee restarts.");
                            Bungee.getInstance().getLogger().info(motd);
                        } else {
                            pp = Bungee.getInstance().getProxy().getPlayer(sender);

                            if (pp == null) return;

                            pp.sendMessage(Utils.ft("&7You have set a temporary MOTD! Note that it will reset to the motd.yml value when Bungee restarts."));
                            pp.sendMessage(Utils.ft(motd));
                        }
                    }
                    break;

                case PERMS:
                    String issuer = serialized.getString("sender");
                    pp = Bungee.getInstance().getProxy().getPlayer(issuer);

                    if (serialized.has("reload") && serialized.getBoolean("reload")) {
                        Bungee.getSettings().setPermsConfig(Utils.loadConfig("perms"));
                        Bungee.getUserManager().loadPerms();

                        if (pp != null) {
                            pp.sendMessage(Utils.ft("&7GPerms config reloaded."));
                        } else {
                            Bungee.getInstance().getLogger().info("Perms config reloaded.");
                        }

                    } else if (serialized.has("target")) {
                        String targetUser = serialized.getString("target");

                        ProxiedPlayer targ = Bungee.getInstance().getProxy().getPlayer(targetUser);

                        if (pp != null) {
                            pp.sendMessage(Lang.PERMS.ft("&a" + targetUser + " &aupdated."));
                        } else {
                            Bungee.getInstance().getLogger().info(targ.getDisplayName() + " updated.");
                        }

                        if (targ != null) {
                            Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(targ.getUniqueId());
                            if (userOptional.isPresent()) {
                                userOptional.get().update();
                            } else {
                                Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
                                    try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                        try (PreparedStatement statement = connection.prepareStatement("SELECT HEX(UP.uuid) AS uid, UP.rank, U.name FROM user_profile UP, user U WHERE UP.uuid=UNHEX(?) AND UP.uuid=U.uuid;")) {
                                            statement.setString(1, targ.getUniqueId().toString().replaceAll("-", ""));
                                            try (ResultSet result = statement.executeQuery()) {
                                                while (result.next()) {
                                                    String username = result.getString("name");
                                                    UUID uuid = UUIDUtil.createUUID(result.getString("uid")).orElse(null);
                                                    UserRank ur = UserRank.getUserRank(result.getString("rank"));

                                                    User user = Bungee.getUserManager().getLoadedUsersMap().computeIfAbsent(uuid, User::new);

                                                    user.setUserRank(ur);
                                                    user.setUsername(username);
                                                }
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }
                    break;

                case LOG:
                    String logType = serialized.getString("type");

                    if (logType.equals("staff")) {
                        String logSender = serialized.getString("sender");
                        String logMessage = serialized.getString("message");
                        Utils.chatLog(logSender, logMessage);
                    } else {
                        String logMessage = serialized.getString("message");
                        String logName = serialized.getString("logname");
                        Utils.log(logMessage, logName);
                    }
                default:
                    break;
            }
        }
    }
}
