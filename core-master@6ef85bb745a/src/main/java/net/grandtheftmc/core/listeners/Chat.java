package net.grandtheftmc.core.listeners;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.TabCompleteEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.GlobalMuteCommand;
import net.grandtheftmc.core.events.ChatEvent;
import net.grandtheftmc.core.handlers.chat.ChatManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.IconConverter;
import net.grandtheftmc.core.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Chat implements Listener {

    private final Pattern pattern;

    public Chat() {
        this.pattern = Pattern.compile("^\\\\/(?:about|bukkit:about|minecraft:about|ver|bukkit:ver|version|bukkit:version).*$");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
    	
    	// grab event variables
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = e.getMessage();
        
        User u = Core.getUserManager().getUser(player.getUniqueId()).orElse(null);
        if (u == null || u.isInTutorial()) {
            e.setCancelled(true);
            return;
        }

        for (Player p : new ArrayList<>(e.getRecipients())) {
            User pu = Core.getUserManager().getUser(p.getUniqueId()).orElse(null);
            if (pu == null || pu.isInTutorial() || pu.isIgnored(player.getName())){
                e.getRecipients().remove(p);
            }
        }
        if (u.isRank(UserRank.VIP)) {
            for (String word : msg.split(" ")) {
                if (word.startsWith(":") && word.endsWith(":")) {
                    msg = IconConverter.convertInput(msg);
                }
            }
        }
        if (!u.isRank(UserRank.MOD)) {
            if (GlobalMuteCommand.chatMuted) {
                e.setCancelled(true);
                player.sendMessage(Lang.GTM.f("&7Chat has been muted! Please wait"));
                return;
            }
        }
        u.updateDisplayName(player);
        TextComponent textComponent;
        if (u.isSpecial()) {
            e.setMessage(u.isAdmin() ? Utils.f(msg) : Utils.fColor(msg));
            e.setFormat(Utils.f("%s&f %s"));
            textComponent = new TextComponent(Utils.f(player.getDisplayName() + "&f ") + e.getMessage());
        } else {
            e.setFormat(Utils.f("%s&7 " + "%s"));
            textComponent = new TextComponent(Utils.f(player.getDisplayName() + "&7 ") + e.getMessage());
            textComponent.setColor(ChatColor.GRAY);
        }
        ChatEvent chatEvent;
        try {
            chatEvent = new ChatEvent(player, textComponent, e.getRecipients());
            Bukkit.getPluginManager().callEvent(chatEvent);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return;
        }
        if (chatEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }
        textComponent = chatEvent.getTextComponent();
        if (u.getUUID() != null && !u.isStaff()) {
            for (String text : e.getMessage().split(" ")) {
                if (ChatManager.getAdHandler().matchesAdvertisement(text)) {
                    player.sendMessage(Lang.GTM.f("&7URL prohibited. Please do not attempt to advertise."));
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(target -> Core.getUserManager()
                                    .getLoadedUser(target.getUniqueId())
                                    .isRank(UserRank.HELPOP))
                            .forEach(target -> target.sendMessage(Lang.ANTIAD.f(u.getColoredName(player) +
                                        " &cattempted to advertise &a''" + e.getMessage() + "''")));
                    e.setCancelled(true);
                    return;
                }
            }
            int cooldown = u.isSpecial() ? ChatManager.getVipChatCooldown() : ChatManager.getDefaultChatCooldown();
            if (ChatManager.getCooldownHandler().canChatAgain(uuid, cooldown)) {
                ChatManager.getCooldownHandler().setCanChatAgain(uuid, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cooldown));
            } else {
                player.sendMessage(Lang.GTM.f("&7You are sending messages too fast, please slow down."));
                e.setCancelled(true);
                return;
            }
            cooldown = u.isSpecial() ? 3 : 5;
            if (ChatManager.getRepeatHandler().canChatAgain(uuid, e.getMessage())) {
                ChatManager.getRepeatHandler().addRecentMessage(uuid, e.getMessage(), cooldown);
            } else {
                player.sendMessage(Lang.GTM.f("&7Please wait a few seconds before repeating that message."));
                e.setCancelled(true);
                return;
            }
        }
        for (Player target : chatEvent.getRecipients()) target.spigot().sendMessage(textComponent);
        e.getRecipients().clear();
    }

    @EventHandler
    protected void onTabComplete(TabCompleteEvent event) {
        event.setCancelled(this.pattern.matcher(ChatColor.stripColor(event.getBuffer())).find());
    }
}
