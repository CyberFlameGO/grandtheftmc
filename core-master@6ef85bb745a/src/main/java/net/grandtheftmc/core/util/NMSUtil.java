package net.grandtheftmc.core.util;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerPlayerListHeaderFooter;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerWorldBorder;
import org.bukkit.entity.Player;

public class NMSUtil {

    public static void sendTabTitle(Player player, String header, String footer) {
//        org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer craftplayer = (org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player;
//        net.minecraft.server.v1_12_R1.PlayerConnection connection = craftplayer.getHandle().playerConnection;
//        net.minecraft.server.v1_12_R1.IChatBaseComponent JSONheader = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
//        net.minecraft.server.v1_12_R1.IChatBaseComponent JSONfooter = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
//        net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter();
//        try {
//            Field headerField = packet.getClass().getDeclaredField("a");
//            headerField.setAccessible(true);
//            headerField.set(packet, JSONheader);
//            headerField.setAccessible(!headerField.isAccessible());
//
//            Field footerField = packet.getClass().getDeclaredField("b");
//            footerField.setAccessible(true);
//            footerField.set(packet, JSONfooter);
//            footerField.setAccessible(!footerField.isAccessible());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        connection.sendPacket(packet);

        WrapperPlayServerPlayerListHeaderFooter wrappedPacket = new WrapperPlayServerPlayerListHeaderFooter();
        wrappedPacket.setHeader(WrappedChatComponent.fromJson("{\"text\": \"" + header + "\"}"));
        wrappedPacket.setFooter(WrappedChatComponent.fromJson("{\"text\": \"" + footer + "\"}"));
        wrappedPacket.sendPacket(player);
    }

    public static void setWorldBoarderTint(Player player, int percentage) {
//        if (percentage < 0) percentage = 0;
//        if (percentage > 100) percentage = 100;
//
//        WrapperPlayServerWorldBorder wrappedPacket = new WrapperPlayServerWorldBorder();
//        wrappedPacket.setCenterX(player.getLocation().getX());
//        wrappedPacket.setCenterZ(player.getLocation().getZ());
//        wrappedPacket.setWarningDistance(5000000 + percentage * 2000000);
//        wrappedPacket.setWarningTime(0);
//        wrappedPacket.setAction(EnumWrappers.WorldBorderAction.INITIALIZE);
//        wrappedPacket.setRadius(6.0E7D);
//        wrappedPacket.setOldRadius(6.0E7D);
//        wrappedPacket.setSpeed(0L);
//        wrappedPacket.sendPacket(player);
    }
}
