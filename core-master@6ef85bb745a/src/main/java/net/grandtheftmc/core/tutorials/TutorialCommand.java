package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.SoundEffect;
import net.grandtheftmc.core.util.Title;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TutorialCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        User user = Core.getUserManager().getLoadedUser(uuid);
        if (args.length == 0) {
            if (!s.hasPermission("tutorials.admin")) {
                if (Core.getTutorialManager().getTutorials().isEmpty()) {
                    s.sendMessage(Lang.TUTORIALS.f("&7There are no tutorials!"));
                    return true;
                }
                Tutorial t = Core.getTutorialManager().getTutorials().get(0);
                if (t == null) {
                    s.sendMessage(Lang.TUTORIALS.f("&7There are no tutorials!"));
                    return true;
                }
                t.start(player, user);
                return true;
            }
            s.sendMessage(Lang.TUTORIALS.f("&2&lHelp Command"));
            s.sendMessage(Utils.f("&2/tutorial&7 start &a<tutorial>"));
            s.sendMessage(Utils.f("&2/tutorial&7 nextslide"));

            s.sendMessage(Utils.f("&2/tutorial&7 info/add/remove/edit &a<name>"));
            s.sendMessage(Utils.f("&2/tutorial&7 list/stop"));
            s.sendMessage(Utils.f("&2/tutorial&7 set name &a<name>"));
            s.sendMessage(Utils.f("&2/tutorial&7 set invisible &a<boolean>"));
            s.sendMessage(Utils.f("&2/tutorial&7 set playersInvisible &a<boolean>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide info &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide play &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide add &a[id]"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide remove &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide edit  &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide canConfirm &a<boolean>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide delay &a<ticks>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide location"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide actionBarMessage &a<msg>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide title &a<title>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide subtitle &a<subTitle>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide timings &a<fadeIn> <stay> <fadeOut> "));
            s.sendMessage(Utils.f("&2/tutorial&7 slide gamemode &a<gameM-mode> "));
            s.sendMessage(Utils.f("&2/tutorial&7 slide message add &a<message> &a[id]"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide message remove &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide headerAndFooter &a<boolean>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide sound add &a<sound> [volume] [pitch] [delay]"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide sound remove &a<id>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide slot &a<slot>"));
            s.sendMessage(Utils.f("&2/tutorial&7 slide menu &a<menu>"));
            s.sendMessage(Utils.f("&2/tutorial&7 load"));
            s.sendMessage(Utils.f("&2/tutorial&7 save"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/tutorial start <name>"));
                    return true;
                }
                Tutorial tutorial = Core.getTutorialManager().getTutorial(args[1]);
                if (tutorial == null) {
                    s.sendMessage(Utils.f(Lang.TUTORIALS + "&7That tutorial does not exist!"));
                    return true;
                }
                tutorial.start(player, Core.getUserManager().getLoadedUser(uuid));
                return true;
            case "nextslide":
                Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                if (tut == null) {
                    s.sendMessage(Lang.TUTORIALS.f("&7You are not in a tutorial!"));
                    return true;
                }
                if (tut.getSlide(user.getTutorialSlide()) != null && !tut.getSlide(user.getTutorialSlide()).isCanConfirm()) {
                    s.sendMessage(Lang.TUTORIALS.f("&7You can not skip this slide!"));
                    return true;
                }
                tut.playNextSlide(player, user);
                return true;
        }
        if (!s.hasPermission("tutorials.admin"))
            return true;
        switch (args[0].toLowerCase()) {
            case "load":
                Core.getSettings().setTutorialsConfig(Utils.loadConfig("tutorials"));
                Core.getTutorialManager().load();
                s.sendMessage(Lang.TUTORIALS.f("&7The tutorials config was reloaded!"));
                return true;
            case "save":
                Core.getTutorialManager().save(false);
                s.sendMessage(Lang.TUTORIALS.f("&7The tutorials config was saved!"));
                return true;
            case "info": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/tutorial info <name>"));
                    return true;
                }
                Tutorial tutorial = Core.getTutorialManager().getTutorial(args[1]);
                if (tutorial == null) {
                    s.sendMessage(Utils.f(Lang.TUTORIALS + "&7That tutorial does not exist!"));
                    return true;
                }
                s.sendMessage(Lang.TUTORIALS.f("&2&lTutorial Info &a" + tutorial.getName()));
                s.sendMessage(Utils.f("&2Invisible: &a" + tutorial.isInvisible()));
                s.sendMessage(Utils.f("&2Players Invisible: &a" + tutorial.isPlayersInvisible()));
                s.sendMessage(Utils.f("&2Slides: &a" + tutorial.getSlides().size()));
                return true;
            }
            case "list":
                int page = 1;
                if (args.length > 1)
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                int start = (page << 3) - 8;
                int end = (page << 3) - 1;
                List<Tutorial> tutorials = Core.getTutorialManager().getTutorials(start, end);
                s.sendMessage(Lang.TUTORIALS.f("&2&lPage " + page + " &2&lTotal Tutorials: &a" + Core.getTutorialManager().getTutorials().size()));
                for (Tutorial tut : tutorials)
                    s.sendMessage(Utils.f("&2&lTutorial: &a" + tut.getName() + " &2&lSlides: &a" + tut.getSlides().size()));
                return true;
            case "add": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/tutorial add <name>"));
                    return true;
                }
                if (Core.getTutorialManager().getTutorial(args[1]) != null) {
                    s.sendMessage(Lang.TUTORIALS.f("&7A tutorial with that name already exists!"));
                    return true;
                }
                Tutorial tut = new Tutorial(args[1]);
                Core.getTutorialManager().addTutorial(tut);
                user.setEditingTutorial(true);
                user.setTutorial(tut.getName());
                user.setTutorialSlide(0);
                s.sendMessage(Lang.TUTORIALS.f("&7You have added a tutorial with the name &a" + tut.getName() + "&7! You are now editing this tutorial."));
                return true;
            }
            case "remove": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/tutorial remove <name>"));
                    return true;
                }
                Tutorial tutorial = Core.getTutorialManager().getTutorial(args[1]);
                if (tutorial == null) {
                    s.sendMessage(Utils.f(Lang.TUTORIALS + "&7That tutorial does not exist!"));
                    return true;
                }
                Core.getTutorialManager().removeTutorial(tutorial);
                s.sendMessage(Lang.TUTORIALS.f("&7You have removed a tutorial with the name &a" + tutorial.getName() + "&7!"));
                return true;
            }
            case "edit": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/tutorial info <name>"));
                    return true;
                }
                Tutorial tutorial = Core.getTutorialManager().getTutorial(args[1]);
                if (tutorial == null) {
                    s.sendMessage(Utils.f(Lang.TUTORIALS + "&7That tutorial does not exist!"));
                    return true;
                }
                user.setEditingTutorial(true);
                user.setTutorial(tutorial.getName());
                user.setTutorialSlide(0);
                s.sendMessage(Lang.TUTORIALS.f("&7You are now editing tutorial &a" + tutorial.getName() + "&7!"));
                return true;
            }
            case "stop":
                user.setEditingTutorial(false);
                user.setTutorial(null);
                user.setTutorialSlide(-1);
                s.sendMessage(Lang.TUTORIALS.f("&7You are no longer editing a tutorial!"));
                return true;
            case "set":
                if (args.length == 1) {
                    s.sendMessage(Utils.f("&c/tutorial set name <name>"));
                    s.sendMessage(Utils.f("&c/tutorial set invisible <boolean>"));
                    s.sendMessage(Utils.f("&c/tutorial set playersInvisible <boolean>"));
                    return true;
                }
                String s1 = args[1].toLowerCase();
                if ("name:".equals(s1)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial set name <name>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    tut.setName(args[2]);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the name of your tutorial to &a" + tut.getName() + "&7!"));
                    return true;
                } else if ("invisible".equals(s1)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial set invisible <boolean>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    boolean invisible;
                    if ("true".equalsIgnoreCase(args[2]))
                        invisible = true;
                    else if ("false".equalsIgnoreCase(args[2]))
                        invisible = false;
                    else {
                        player.sendMessage(Lang.TUTORIALS.f("&7Please specify true/false."));
                        return true;
                    }
                    tut.setInvisible(invisible);
                    s.sendMessage(Lang.TUTORIALS.f("&7You will now be &a" + (invisible ? "invisible" : "visible") + "&7 during tutorial &a" + tut.getName() + "&7!"));
                    return true;
                } else if ("playersinvisible".equals(s1)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial set playersInvisible <boolean>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    boolean invisible;
                    if ("true".equalsIgnoreCase(args[2]))
                        invisible = true;
                    else if ("false".equalsIgnoreCase(args[2]))
                        invisible = false;
                    else {
                        player.sendMessage(Lang.TUTORIALS.f("&7Please specify true/false."));
                        return true;
                    }
                    tut.setPlayersInvisible(invisible);
                    s.sendMessage(Lang.TUTORIALS.f("&7You will&a" + (invisible ? " not" : "") + "&7be able to see players during tutorial &a" + tut.getName() + "&7!"));
                    return true;
                }
            case "slide":
                if (args.length == 1) {
                    s.sendMessage(Utils.f("&2/tutorial&7 slide info &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide play &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide add &a[id]"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide remove &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide edit  &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide canConfirm &a<boolean>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide delay &a<ticks>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide location"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide actionBarMessage &a<msg>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide title &a<title>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide subtitle &a<subTitle>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide timings &a<fadeIn> <stay> <fadeOut> "));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide gamemode &a<gameM-mode> "));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide message add &a<message> &a[id]"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide message remove &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide headerAndFooter &a<boolean>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide sound add &a<sound> [volume] [pitch] [delay]"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide sound remove &a<id>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide slot &a<slot>"));
                    s.sendMessage(Utils.f("&2/tutorial&7 slide menu &a<menu>"));
                    return true;
                }
                String s2 = args[1].toLowerCase();
                if ("info".equals(s2)) {
                    if (args.length != 4) {
                        s.sendMessage(Utils.f("&c/tutorial slide info <tutorial> <id>"));
                        return true;
                    }
                    Tutorial tutorial = Core.getTutorialManager().getTutorial(args[2]);
                    if (tutorial == null) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7That tutorial does not exist!"));
                        return true;
                    }
                    int id;
                    try {
                        id = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                        return true;
                    }
                    Slide slide = tutorial.getSlide(id);
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7That slide does not exist!"));
                        return true;
                    }
                    s.sendMessage(Lang.TUTORIALS.f("&2&lTutorial &a" + tutorial.getName() + "&2&l Slide &a" + id));
                    s.sendMessage(Utils.f("&2Can Confirm: &a" + slide.isCanConfirm()));
                    s.sendMessage(Utils.f("&2Delay: &a" + slide.getDelay()));
                    if (slide.getLocation() != null)
                        s.sendMessage(Utils.f("&2Location: &a" + Utils.teleportLocationToString(slide.getLocation())));
                    if (slide.getGameMode() != null)
                        s.sendMessage(Utils.f("&2GameMode: &a" + slide.getGameMode()));
                    if (slide.getActionBarMessage() != null)
                        s.sendMessage(Utils.f("&2Action Bar Message: &a" + slide.getActionBarMessage()));
                    Title title = slide.getTitle();
                    if (title != null)
                        s.sendMessage(
                                Utils.f("&2Title: &a" + title.getTitle() + ',' + title.getSubtitle() + ',' + title.getFadeIn() + ',' + title.getStay() + ',' + title.getFadeOut()));
                    if (slide.getSounds() != null)
                        for (SoundEffect sound : slide.getSounds()) {
                            s.sendMessage(Utils.f("&2Sound: &a" + sound.getSound() + " &2Volume: &a" + sound.getVolume() + " &2 Pitch: &a" + sound.getPitch() + " &2 Delay: &a"
                                    + sound.getDelay()));
                        }
                    slide.sendMessages(player);
                    return true;
                } else if ("play".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide play <id>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    int id;
                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                        return true;
                    }
                    Slide slide = tut.getSlide(id);
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7That slide does not exist!"));
                        return true;
                    }
                    slide.play(player);
                    return true;
                } else if ("add".equals(s2)) {
                    if (args.length > 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide add"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    if (args.length == 3) {
                        int id;
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            s.sendMessage(Lang.TUTORIALS.f("&7The ID must be a number!"));
                            return true;
                        }
                        id = tut.addSlide(id);
                        user.setTutorialSlide(id);
                        s.sendMessage(Lang.TUTORIALS.f("&7You have added a slide with id &a" + id + "&7! You are now editing this slide."));
                        return true;
                    }
                    tut.addSlide();
                    int id = tut.getSlides().size() - 1;
                    user.setTutorialSlide(id);
                    s.sendMessage(Lang.TUTORIALS.f("&7You have added a slide with id &a" + id + "&7! You are now editing this slide."));
                    return true;
                } else if ("remove".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide remove <id>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    int id;
                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                        return true;
                    }
                    Slide slide = tut.getSlide(id);
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7That slide does not exist!"));
                        return true;
                    }
                    tut.removeSlide(slide);
                    s.sendMessage(Lang.TUTORIALS.f("&7You have removed a slide with id &a" + id + "&7!"));
                    return true;
                } else if ("edit".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide remove <id>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    int id;
                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                        return true;
                    }
                    Slide slide = tut.getSlide(id);
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7That slide does not exist!"));
                        return true;
                    }
                    user.setTutorialSlide(id);
                    s.sendMessage(Lang.TUTORIALS.f("&7You are now editing a slide with id &a" + id + "&7!"));
                    return true;
                } else if ("canconfirm".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide canConfirm <boolean>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    boolean b;
                    if ("true".equalsIgnoreCase(args[2]))
                        b = true;
                    else if ("false".equalsIgnoreCase(args[2]))
                        b = false;
                    else {
                        player.sendMessage(Lang.TUTORIALS.f("&7Please specify true/false."));
                        return true;
                    }
                    slide.setCanConfirm(b);
                    s.sendMessage(Lang.TUTORIALS.f("&7Players can &a" + (b ? "now" : "no longer") + "&7 skip slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("delay".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide delay <ticks>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    int delay;
                    try {
                        delay = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The delay must be a number!")));
                        return true;
                    }
                    if (delay < 0) {
                        s.sendMessage(Lang.TUTORIALS.f("&7The delay must be at least 0!"));
                        return true;
                    }
                    slide.setDelay(delay);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the delay to &a" + delay + "&7 for slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("location".equals(s2) || "loc".equals(s2)) {
                    if (args.length != 2) {
                        s.sendMessage(Utils.f("&c/tutorial slide loc"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    slide.setLocation(player.getLocation());
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the location for slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("actionbarmessage".equals(s2)) {
                    if (args.length < 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide actionbarmessage <msg/none>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    if ("none".equalsIgnoreCase(args[2])) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You unset the ActionBar message for slide &a" + user.getTutorialSlide() + "&7!"));
                        slide.setActionBarMessage(null);
                        return true;
                    }
                    String message = args[2];
                    for (int i = 3; i < args.length; i++)
                        message = message + ' ' + args[i];
                    slide.setActionBarMessage(message);
                    slide.sendActionBarMessage(player);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the ActionBar message for slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("title".equals(s2)) {
                    if (args.length < 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide set title <title/none>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    if ("none".equalsIgnoreCase(args[2])) {
                        slide.setTitle(null);
                        s.sendMessage(Lang.TUTORIALS.f("&7You unset the Title for slide &a" + user.getTutorialSlide() + "&7!"));
                        return true;
                    }
                    String message = args[2];
                    for (int i = 3; i < args.length; i++)
                        message = message + ' ' + args[i];
                    Title title = new Title(message, null, 20, 20, 20);
                    slide.setTitle(title);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the title for slide &a" + user.getTutorialSlide() + "&7!"));
                    title.play(player);
                    return true;
                } else if ("subtitle".equals(s2)) {
                    if (args.length < 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide subtitle <subtitle/none>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    Title title = slide.getTitle();
                    if (title == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7Please set the Title first!"));
                        return true;
                    }
                    if ("none".equalsIgnoreCase(args[2])) {
                        title.setSubtitle(null);
                        s.sendMessage(Lang.TUTORIALS.f("&7You unset the subtitle for slide &a" + user.getTutorialSlide() + "&7!"));
                        return true;
                    }
                    String message = args[2];
                    for (int i = 3; i < args.length; i++)
                        message += ' ' + args[i];
                    title.setSubtitle(message);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the subtitle for slide &a" + user.getTutorialSlide() + "&7!"));
                    title.play(player);
                    return true;
                } else if ("gamemode".equals(s2)) {
                    if (args.length != 4) {
                        s.sendMessage(Utils.f("&c/tutorial slide gameMode <gameMode/none>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    GameMode gameMode = null;
                    String s3 = args[2].toLowerCase();
                    switch (s3) {
                        case "0":
                        case "survival":
                        case "s":
                            gameMode = GameMode.SURVIVAL;

                            break;
                        case "1":
                        case "creative":
                        case "c":
                            gameMode = GameMode.CREATIVE;

                            break;
                        case "2":
                        case "adventure":
                        case "a":
                            gameMode = GameMode.ADVENTURE;

                            break;
                        case "3":
                        case "spectator":
                            gameMode = GameMode.SPECTATOR;

                            break;
                        default:
                            s.sendMessage(Lang.TUTORIALS.f("&7You unset the gamemode for slide &a" + user.getTutorialSlide() + "&7!"));
                            slide.setGameMode(gameMode);
                            return true;
                    }
                    slide.setGameMode(gameMode);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the gamemode to &a" + gameMode + "&7 for slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("timings".equals(s2)) {
                    if (args.length != 5) {
                        s.sendMessage(Utils.f("&c/tutorial slide timings <fadeIn> <stay> <fadeOut>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    Title title = slide.getTitle();
                    if (title == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7Please set the Title first!"));
                        return true;
                    }
                    try {
                        title.setFadeIn(Integer.parseInt(args[2]));
                        title.setStay(Integer.parseInt(args[3]));
                        title.setFadeOut(Integer.parseInt(args[4]));
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.TUTORIALS.f("&7The values must be numbers!"));
                        return true;
                    }
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the title timings for slide &a" + user.getTutorialSlide() + "&7!"));
                    title.play(player);
                    return true;
                } else if ("headerandfooter".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide headerandfooter <boolean>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    boolean b;
                    if ("true".equalsIgnoreCase(args[2]))
                        b = true;
                    else if ("false".equalsIgnoreCase(args[2]))
                        b = false;
                    else {
                        player.sendMessage(Lang.TUTORIALS.f("&7Please specify true/false."));
                        return true;
                    }
                    slide.setHeaderAndFooter(b);
                    s.sendMessage(Lang.TUTORIALS.f("You set header and footer to &a" + b + "&7 for slide &a" + user.getTutorialSlide() + "&7!"));
                    return true;
                } else if ("message".equals(s2)) {
                    if (args.length == 2) {
                        s.sendMessage(Utils.f("&2/tutorial&7 slide message add &a<message>"));
                        s.sendMessage(Utils.f("&2/tutorial&7 slide message remove &a<id>"));
                        return true;
                    }
                    switch (args[2].toLowerCase()) {
                        case "add": {
                            if (args.length < 4) {
                                s.sendMessage(Utils.f("&c/tutorial slide message add <message>"));
                                return true;
                            }
                            Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                            if (tut == null || !user.isEditingTutorial()) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                                return true;
                            }
                            Slide slide = tut.getSlide(user.getTutorialSlide());
                            if (slide == null) {
                                s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                                return true;
                            }
                            String msg = args[3];
                            for (int i = 4; i < args.length; i++)
                                msg = msg + ' ' + args[i];
                            slide.addMessage(msg);
                            s.sendMessage(Lang.TUTORIALS.f("&7You added a message to slide &a" + user.getTutorialSlide() + "&7!"));
                            slide.sendMessages(player);
                            return true;
                        }
                        case "remove":
                            if (args.length != 4) {
                                s.sendMessage(Utils.f("&c/tutorial slide message remove <id>"));
                                return true;
                            }
                            Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                            if (tut == null || !user.isEditingTutorial()) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                                return true;
                            }
                            Slide slide = tut.getSlide(user.getTutorialSlide());
                            if (slide == null) {
                                s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                                return true;
                            }
                            int id;
                            try {
                                id = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                                return true;
                            }
                            if (slide.getMessages().length <= id) {
                                s.sendMessage(Lang.TUTORIALS.f("&7There are only &a" + slide.getMessages().length + "&7 messages!"));
                                return true;
                            }
                            slide.removeMessage(id);
                            s.sendMessage(Lang.TUTORIALS.f("&7You removed a message from slide &a" + user.getTutorialSlide() + "&7!"));
                            slide.sendMessages(player);
                            return true;
                    }

                    if (args.length == 2) {
                        s.sendMessage(Utils.f("&2/tutorial&7 slide sound add &a<sound> [volume] [pitch] [delay]"));
                        s.sendMessage(Utils.f("&2/tutorial&7 slide sound remove &a<id>"));
                        return true;
                    }
                    switch (args[2].toLowerCase()) {
                        case "add": {
                            if (args.length < 4 || args.length > 7) {
                                s.sendMessage(Utils.f("&c/tutorial slide sound add <sound> [volume] [pitch] [delay]"));
                                return true;
                            }
                            Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                            if (tut == null || !user.isEditingTutorial()) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                                return true;
                            }
                            Slide slide = tut.getSlide(user.getTutorialSlide());
                            if (slide == null) {
                                s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                                return true;
                            }
                            Sound sound = null;
                            for (Sound so : Sound.values())
                                if (so.toString().equalsIgnoreCase(args[3])) {
                                    sound = so;
                                    break;
                                }
                            if (sound == null) {
                                s.sendMessage(Lang.TUTORIALS.f("&7That sound does not exist!"));
                                return true;
                            }
                            float volume = 1;
                            float pitch = 1;
                            int delay = 0;
                            if (args.length > 4)
                                try {
                                    volume = Float.parseFloat(args[4]);
                                    if (args.length > 5)
                                        pitch = Float.parseFloat(args[5]);
                                    if (args.length > 6)
                                        delay = Integer.parseInt(args[6]);
                                } catch (NumberFormatException e) {
                                    s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The volume, pitch and delay must be numbers!")));
                                    return true;
                                }
                            SoundEffect e = new SoundEffect(sound, volume, pitch, delay);
                            slide.addSound(e);
                            s.sendMessage(Lang.TUTORIALS.f("&7You added a sound to slide &a" + user.getTutorialSlide() + "&7!"));
                            e.play(player);
                            return true;
                        }
                        case "remove":
                            if (args.length != 4) {
                                s.sendMessage(Utils.f("&c/tutorial slide sound remove <id>"));
                                return true;
                            }
                            Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                            if (tut == null || !user.isEditingTutorial()) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                                return true;
                            }
                            Slide slide = tut.getSlide(user.getTutorialSlide());
                            if (slide == null) {
                                s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                                return true;
                            }
                            int id = -1;
                            try {
                                Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                                return true;
                            }
                            if (slide.getSounds().size() <= id) {
                                s.sendMessage(Lang.TUTORIALS.f("&7There are only &a" + slide.getSounds().size() + "&7 sounds!"));
                                return true;
                            }
                            slide.removeSound(id);
                            s.sendMessage(Lang.TUTORIALS.f("&7You removed a sound from slide &a" + user.getTutorialSlide() + "&7!"));
                            slide.sendSoundEffects(player);
                            return true;
                    }
                } else if ("sound".equals(s2)) {
                    if (args.length == 2) {
                        s.sendMessage(Utils.f("&2/tutorial&7 slide sound add &a<sound> [volume] [pitch] [delay]"));
                        s.sendMessage(Utils.f("&2/tutorial&7 slide sound remove &a<id>"));
                        return true;
                    }
                    String s3 = args[2].toLowerCase();
                    if ("add".equals(s3)) {
                        if (args.length < 4 || args.length > 7) {
                            s.sendMessage(Utils.f("&c/tutorial slide sound add <sound> [volume] [pitch] [delay]"));
                            return true;
                        }
                        Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                        if (tut == null || !user.isEditingTutorial()) {
                            s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                            return true;
                        }
                        Slide slide = tut.getSlide(user.getTutorialSlide());
                        if (slide == null) {
                            s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                            return true;
                        }
                        Sound sound = null;
                        for (Sound so : Sound.values())
                            if (so.toString().equalsIgnoreCase(args[3])) {
                                sound = so;
                                break;
                            }
                        if (sound == null) {
                            s.sendMessage(Lang.TUTORIALS.f("&7That sound does not exist!"));
                            return true;
                        }
                        float volume = 1;
                        float pitch = 1;
                        int delay = 0;
                        if (args.length > 4)
                            try {
                                volume = Float.parseFloat(args[4]);
                                if (args.length > 5)
                                    pitch = Float.parseFloat(args[5]);
                                if (args.length > 6)
                                    delay = Integer.parseInt(args[6]);
                            } catch (NumberFormatException e) {
                                s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The volume, pitch and delay must be numbers!")));
                                return true;
                            }
                        SoundEffect e = new SoundEffect(sound, volume, pitch, delay);
                        slide.addSound(e);
                        s.sendMessage(Lang.TUTORIALS.f("&7You added a sound to slide &a" + user.getTutorialSlide() + "&7!"));
                        e.play(player);
                        return true;
                    } else if ("remove".equals(s3)) {
                        if (args.length != 4) {
                            s.sendMessage(Utils.f("&c/tutorial slide sound remove <id>"));
                            return true;
                        }
                        Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                        if (tut == null || !user.isEditingTutorial()) {
                            s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                            return true;
                        }
                        Slide slide = tut.getSlide(user.getTutorialSlide());
                        if (slide == null) {
                            s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                            return true;
                        }
                        int id = -1;
                        try {
                            Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            s.sendMessage(Utils.f(Lang.TUTORIALS.f("&7The ID must be a number!")));
                            return true;
                        }
                        if (slide.getSounds().size() <= id) {
                            s.sendMessage(Lang.TUTORIALS.f("&7There are only &a" + slide.getSounds().size() + "&7 sounds!"));
                            return true;
                        }
                        slide.removeSound(id);
                        s.sendMessage(Lang.TUTORIALS.f("&7You removed a sound from slide &a" + user.getTutorialSlide() + "&7!"));
                        slide.sendSoundEffects(player);
                        return true;
                    }
                } else if ("slot".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide slot <slot>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    int slot;
                    try {
                        slot = Integer.parseInt(args[2
                                ]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.TUTORIALS.f("&7The slot must be a number!"));
                        return true;
                    }
                    if (slot < 0 || slot > 8) {
                        s.sendMessage(Lang.TUTORIALS.f(
                                "&7The slot must be between 0 and 8!"));
                        return true;
                    }
                    slide.setSlot(slot);
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the slot for slide &a" + user.getTutorialSlide() + "&7 to slot &a" + slot + "&7!"));
                    return true;
                } else if ("menu".equals(s2)) {
                    if (args.length != 3) {
                        s.sendMessage(Utils.f("&c/tutorial slide menu <menu>"));
                        return true;
                    }
                    Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
                    if (tut == null || !user.isEditingTutorial()) {
                        s.sendMessage(Utils.f(Lang.TUTORIALS + "&7You are not editing any tutorial!"));
                        return true;
                    }
                    Slide slide = tut.getSlide(user.getTutorialSlide());
                    if (slide == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7You are not editing any slide!"));
                        return true;
                    }
                    Menu menu = MenuManager.getMenu(args[2]);
                    if (menu == null) {
                        s.sendMessage(Lang.TUTORIALS.f("&7That menu does not exist!"));
                        return true;
                    }
                    slide.setMenu(menu.getName());
                    s.sendMessage(Lang.TUTORIALS.f("&7You set the menu for slide &a" + user.getTutorialSlide() + "&7 to &a" + menu + "&7!"));
                    return true;
                }
        }
        return true;
    }
}
