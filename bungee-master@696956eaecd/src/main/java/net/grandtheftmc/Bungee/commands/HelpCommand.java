package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.help.data.HelpCategory;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.utils.RequestRateLimiter;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;
import java.util.regex.Pattern;

public class HelpCommand extends Command {

    private static final Pattern PATTERN = Pattern.compile(".*(?:h.?ck).*", Pattern.CASE_INSENSITIVE);

    //Allow players to still bypass help if the topic for help isn't listed.
    private HashMap<UUID, Long> bypassHelp = new HashMap<>();

    /*
        Redis procedure:
        ----------------
        1) Forward the help request to the staff chat of all staff members across all bungee instances using redis.
        2) Clicking on the message should allow the staff member to reply by prompting /gmsg <player>...
     */

    public HelpCommand() {
        super("help", null, "helpop", "ask", "question", "howto", "admin", "mod");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof ProxiedPlayer)) {
            //It doesn't make sense for the console to ask for help.
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) s;

        if (args.length == 0) {
            player.sendMessage(Lang.HELP.f("&7Try something like this: &a/help how do I use my car?"));
            //Bungee.getInstance().getHelpCore().getMainHelpMenu().stream().forEach(bc -> player.sendMessage(bc));
            return;
        }

        String msg = String.join(" ", args);

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("reload")) {
                //reload the configuration
                if (player.hasPermission("gtm.generic.admin")) {
                    //reload, for this perm only
                    Bungee.getInstance().getHelpCore().reload();
                    player.sendMessage(Utils.f("&aHelp configuration file has been reloaded."));
                }

                return;
            }

            if (args[0].equalsIgnoreCase("view-null")) {
                //Workaround to prevent clicking on the last list items
                return;
            }

            HelpCategory cat = Bungee.getInstance().getHelpCore().getAssociatedCategory(args[0].toLowerCase());

            if (args[0].equalsIgnoreCase("help")) {
                //link to main help menu
                Bungee.getInstance().getHelpCore().getMainHelpMenu().forEach(player::sendMessage);
                return;
            }

            if (cat != null) {
                cat.getDisplay().forEach(player::sendMessage);
            } else {

                if (msg.split(" ").length <= 1) {
                    player.sendMessage(Lang.HELP.f("&7Only one word? Try to describe your problem more accurately."));
                    return;
                }

            }

        }

        if(PATTERN.matcher(msg).find()) {
            player.sendMessage(new ComponentBuilder(Utils.f(" &c&lWATCHDAWG&8&l> &7Please use &f/report &7<&fplayer&7> <&freason&7>")).create());
            return;
        }


        boolean skipCheck = true;

        /*if(bypassHelp.containsKey(player.getUniqueId())){
            long last = bypassHelp.get(player.getUniqueId());
            long ms = System.currentTimeMillis() - last;
            if (ms <= (1000 * 60)) {
                //if less than a minute ago allow it
                skipCheck = true;
            }
        }*/

        if (skipCheck) {
            bypassHelp.remove(player.getUniqueId());
        }

        if (!skipCheck) {

            List<HelpCategory> cats = new ArrayList<>();
            Set<String> existingCats = new HashSet<>();

            //check for matches
            for (String a : args) {
                HelpCategory hc = Bungee.getInstance().getHelpCore().getAssociatedCategory(a.toLowerCase());

                if (hc != null && !existingCats.contains(hc.getSectionName())) {
                    cats.add(hc);
                    //prevent duplicates
                    existingCats.add(hc.getSectionName());
                }
            }

            if (!cats.isEmpty()) {
                //Prompt them to review help before pushing to staff.
                //Your query matches n help topics.
                // Click on any of these for further information or do /help.
                //If this still hasnt answered your question click HERE to ask a staff member.
                ComponentBuilder b = new ComponentBuilder(Bungee.getInstance().getHelpCore().getHelpMatch());

                for (int i = 0; i < cats.size(); i++) {
                    HelpCategory hc = cats.get(i);
                    b.append(hc.getDisplayName()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + hc.getSectionName()));


                    if ((i + 1) != cats.size()) {
                        //comma delimiters
                        b.append(", ").color(ChatColor.WHITE);
                    }
                }


                //add headers
                Bungee.getInstance().getHelpCore().getHeader().forEach(h -> player.sendMessage(new ComponentBuilder(Utils.f(h)).create()));

                //matching categories
                player.sendMessage(b.create());

                //generic msg
                player.sendMessage(new ComponentBuilder(" " + Bungee.getInstance().getHelpCore().getSendHelp()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + msg)).create());

                //add footers
                Bungee.getInstance().getHelpCore().getFooter().forEach(h -> player.sendMessage(new ComponentBuilder(Utils.f(h)).create()));

                bypassHelp.put(player.getUniqueId(), System.currentTimeMillis());

                return;
            }
        }

        if (!RequestRateLimiter.requestCmd(player.getUniqueId())) {
            s.sendMessage(Utils.ft("&cYou have issued this command recently, please wait a second."));
            return;
        }

        //broadcast to redis
        Map<String, Object> map = new HashMap<>();
        map.put("sender", player.getName());
        map.put("server", player.getServer().getInfo().getName());
        map.put("message", msg);

        String ser = Bungee.getRedisManager().serialize(DataType.HELP, map);
        //Send this serialised object to other redis servers for handling...
        Bungee.getRedisManager().sendMessage(ser);

        player.sendMessage(Lang.HELP.f("&7Your message has been sent to all online staff. Use &a&l\"/gmsg <name>\"&7 to talk to them individually."));

        // log
        String logMessage = "[HELP] " + player.getName() + ": " + msg;

        Map<String, Object> chatLogSerializd = new HashMap<>();
        chatLogSerializd.put("type", "help");
        chatLogSerializd.put("message", logMessage);
        chatLogSerializd.put("logname", "help");
        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.LOG, chatLogSerializd));
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }
}
