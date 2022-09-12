package net.grandtheftmc.vice.utils;

import net.grandtheftmc.core.util.factory.Factory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class TextHelper extends Factory<TextComponent> {

    public TextHelper() {
        this.object = new TextComponent();
    }

    public TextHelper(TextComponent component) {
        this.object = component;
    }

    public TextHelper(String text) {
        this();
        this.object.setText(text);
    }

    public TextHelper setText(String text) {
        this.object.setText(text);
        return this;
    }

    public TextHelper setColor(ChatColor color) {
        this.object.setColor(color);
        return this;
    }

    public TextHelper setBold(boolean bold) {
        this.object.setBold(bold);
        return this;
    }

    public TextHelper setItalic(boolean italic) {
        this.object.setItalic(italic);
        return this;
    }

    public TextHelper setHover(HoverEvent event) {
        this.object.setHoverEvent(event);
        return this;
    }

    public TextHelper setClick(ClickEvent event) {
        this.object.setClickEvent(event);
        return this;
    }

    public TextHelper addExtra(TextHelper component) {
        this.object.addExtra(component.build().duplicate());
        return this;
    }

    @Override
    public TextComponent build() {
        return this.object;
    }

    public BaseComponent[] toBaseComponent() {
        return new BaseComponent[] { this.object };
    }
}
