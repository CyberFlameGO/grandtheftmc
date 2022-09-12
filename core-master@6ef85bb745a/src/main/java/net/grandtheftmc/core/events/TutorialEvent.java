package net.grandtheftmc.core.events;

import net.grandtheftmc.core.tutorials.Tutorial;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TutorialEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private User user;
    private Tutorial tutorial;
    private TutorialEventType type;
    private String cancelMessage;

    public TutorialEvent(Player player, User user, Tutorial tutorial, TutorialEventType type) {
        super(player);
        this.user = user;
        this.tutorial = tutorial;
        this.type = type;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tutorial getTutorial() {
        return this.tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public TutorialEventType getType() {
        return this.type;
    }

    public void setType(TutorialEventType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return this.cancelMessage != null;
    }

    public void setCancelled(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }

    public String getCancelMessage() {
        return this.cancelMessage;
    }

    public TutorialEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum TutorialEventType {
        PRE_START,
        START,
        SLIDE,
        END,
        QUIT
    }

}
