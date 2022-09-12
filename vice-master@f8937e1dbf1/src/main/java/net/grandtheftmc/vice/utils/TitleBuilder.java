package net.grandtheftmc.vice.utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-04-11.
 */
public class TitleBuilder {
    private String bigText = "", smallText = "";
    private int fadeIn = 20, duration = 20, fadeOut = 20;

    public TitleBuilder setTitleText(String bigText) {
        this.bigText = bigText;
        return this;
    }

    public TitleBuilder setSubTitleText(String smallText) {
        this.smallText = smallText;
        return this;
    }

    public TitleBuilder setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public TitleBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public TitleBuilder setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public void send(Player player){
        if(!bigText.equals("")) {

            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes(
                    '&',
                    "{\"text\": \"" + bigText + "\"}")),
                    fadeIn,
                    duration,
                    fadeOut);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);

        }

        if(!smallText.equals("")) {

            PacketPlayOutTitle title = new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes(
                    '&',
                    "{\"text\": \"" + smallText + "\"}")),
                    fadeIn,
                    duration,
                    fadeOut);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        }
    }
}
