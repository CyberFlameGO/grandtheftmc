package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.TutorialEvent;
import net.grandtheftmc.core.events.TutorialEvent.TutorialEventType;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tutorial {

    private String name;
    private List<Slide> slides;
    private boolean invisible;
    private boolean playersInvisible;

    public Tutorial(String name, List<Slide> slides, boolean invisible, boolean playersInvisible) {
        this.name = name;
        this.slides = slides;
        this.invisible = invisible;
        this.playersInvisible = playersInvisible;
    }

    public Tutorial(String name) {
        this.name = name;
        this.slides = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Slide> getSlides() {
        return this.slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public Slide getSlide(int id) {
        if (this.slides.size() <= id)
            return null;
        return this.slides.get(id);
    }

    public int addSlide(int id) {
        Slide slide = new Slide();
        int i = id > this.slides.size() ? this.slides.size() : id;
        this.slides.add(i, slide);
        return i;
    }

    public Slide addSlide() {
        Slide slide = new Slide();
        this.slides.add(slide);
        return slide;
    }

    public void removeSlide(Slide slide) {
        this.slides.remove(slide);
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isPlayersInvisible() {
        return this.playersInvisible;
    }

    public void setPlayersInvisible(boolean playersInvisible) {
        this.playersInvisible = playersInvisible;
    }

    public void setInvisible(Player player) {
        if (this.invisible)
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                if (this.playersInvisible)
                    player.hidePlayer(p);
            }
    }

    public void setVisible(Player player) {
        if (this.invisible)
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(player);
                if (this.playersInvisible)
                    player.showPlayer(p);
            }
    }

    public void start(Player player, User user) {
        TutorialEvent event = new TutorialEvent(player, user, this, TutorialEventType.PRE_START).call();
        if (event.isCancelled()) {
            player.sendMessage(Lang.TUTORIALS.f(event.getCancelMessage()));
            return;
        }
        if (user.isInTutorial()) {
            player.sendMessage(Lang.TUTORIALS.f("&7You are in a tutorial already!"));
            return;
        }
        event = new TutorialEvent(player, user, this, TutorialEventType.START).call();
        if (event.isCancelled()) {
            player.sendMessage(Lang.TUTORIALS.f(event.getCancelMessage()));
            return;
        }
        if (this.invisible || this.playersInvisible) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                if (this.playersInvisible) player.hidePlayer(p);
            }
        }
        user.setTutorial(this.name);
        this.playNextSlide(player, user);
    }

    public void playNextSlide(Player player, User user) {
        user.setTutorialSlide(user.getTutorialSlide() + 1);
        Slide slide = this.getSlide(user.getTutorialSlide());
        if (slide == null) {
            new TutorialEvent(player, user, this, TutorialEventType.END).call();
            user.setTutorialSlide(-1);
            user.setTutorial(null);
            if (this.invisible || this.playersInvisible) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showPlayer(player);
                    if (this.playersInvisible) player.showPlayer(p);
                }
            }
            player.sendMessage(Lang.TUTORIALS.f("&7Tutorial &a" + this.name + "&7 has ended!"));
            return;
        }
        TutorialEvent event = new TutorialEvent(player, user, this, TutorialEventType.SLIDE).call();
        if (event.isCancelled()) {
            player.sendMessage(Lang.TUTORIALS.f(event.getCancelMessage()));
            user.setTutorialSlide(-1);
            user.setTutorial(null);
            return;
        }
        UUID uuid = player.getUniqueId();
        slide.play(player);
        if (slide.getDelay() <= 0) {
            slide.setCanConfirm(true);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;
                User user = Core.getUserManager().getLoadedUser(uuid);
                Tutorial tutorial = Core.getTutorialManager().getTutorial(user.getTutorial());
                if (tutorial == null) return;
                tutorial.playNextSlide(player, user);
            }
        }.runTaskLater(Core.getInstance(), slide.getDelay());
    }

}
