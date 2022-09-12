package net.grandtheftmc.core.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class ChatEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player sender;
    private TextComponent textComponent;
    private Set<Player> recipients;

    private boolean cancelled;

    public ChatEvent(Player sender, TextComponent textComponent, Set<Player> recipients) {
        this.sender = sender;
        this.textComponent = textComponent;
        this.recipients = recipients;
    }

    public Player getSender() {
        return this.sender;
    }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }

    public void setTextComponent(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public Set<Player> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
