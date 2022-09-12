package net.grandtheftmc.core.announcer;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AnnouncerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.isOp()) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&a/announcer add <lines>"));
            s.sendMessage(Utils.f("&a/announcer remove <id>"));
            s.sendMessage(Utils.f("&a/announcer reload"));
            s.sendMessage(Utils.f("&a/announcer save"));
            s.sendMessage(Utils.f("&a/announcer show <id>"));
            s.sendMessage(Utils.f("&a/announcer list"));
            s.sendMessage(Utils.f("&a/announcer test <text>"));
            s.sendMessage(Utils.f("&a/announcer say <text>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/announcer add <lines>"));
                    return true;
                }
                String text = args[1];
                for (int i = 2; i < args.length; i++)
                    text = text + ' ' + args[i];
                String[] lines = text.split("#");
                Announcement an = Core.getAnnouncer().addAnnouncement(lines);
                s.sendMessage(Utils.f("&7An announcement with ID &a" + an.getId() + " was created!"));

                return true;
            }
            case "remove":
                if (args.length < 2) {
                    s.sendMessage(Utils.f("/announcer remove <id>"));
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    if (Core.getAnnouncer().getAnnouncement(id) == null) {
                        s.sendMessage(Utils.f("&cThat announcement does not exist!"));
                        return true;
                    }
                    s.sendMessage(Utils.f("&7An announcement with ID &a" + id + " was created!"));
                    Core.getAnnouncer().removeAnnouncement(id);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThat is not an ID!"));
                }
                return true;
            case "reload":
                Core.getSettings().setAnnouncerConfig(Utils.loadConfig("announcer"));
                Core.getAnnouncer().loadAnnouncements();
                Core.getAnnouncer().startSchedule();
                s.sendMessage(Utils.f("&7The announcer config was reloaded!"));
                return true;
            case "save":
                Core.getAnnouncer().saveAnnouncements(false);
                s.sendMessage(Utils.f("&7The announcer config was saved!"));
                return true;
            case "show": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/announcer show <id>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe ID must be a number!"));
                    return true;
                }
                Announcer a = Core.getAnnouncer();
                Announcement an = a.getAnnouncement(id);
                s.sendMessage(Utils.f(a.getHeader()));
                s.sendMessage(Utils.fc(an.getLines()));
                s.sendMessage(Utils.f(a.getFooter()));
                return true;

            }
            case "list":
                Announcer a = Core.getAnnouncer();
                List<Announcement> ann = a.getAnnouncements();
                if (ann.isEmpty()) {
                    s.sendMessage(Utils.f("&aThere are no announcements!"));
                    return true;
                }
                String st = "&aAnnouncements&7: &a" + ann.get(0).getId();
                for (int i = 1; i < ann.size(); i++)
                    st = st + "&7, &a" + ann.get(i).getId();
                s.sendMessage(Utils.f(st));
                return true;
            case "test": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/announcer test <text>"));
                    return true;
                }
                String text = args[1];
                for (int i = 2; i < args.length; i++)
                    text = text + ' ' + args[i];
                s.sendMessage(Utils.fc(text));
                return true;
            }
            case "say":
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/announcer say <text>"));
                    return true;
                }
                String text = args[1];
                for (int i = 2; i < args.length; i++)
                    text = text + ' ' + args[i];
                Utils.broadcast(text);
                return true;
            default:
                s.sendMessage(Utils.f("/announcer add <lines>"));
                s.sendMessage(Utils.f("/announcer remove <id>"));
                s.sendMessage(Utils.f("/announcer list"));
                return true;
        }
    }
}
