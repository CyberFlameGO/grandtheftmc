package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AntiAuraCommand implements CommandExecutor {

    //Checking for existance, hashsets are faster
    public static final Set<String> TOGGLED_PLAYERS = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.hasPermission("antiaura.admin")) return false;
        if (args.length == 0) {
            s.sendMessage(Lang.ANTIAURA.f("&7/antiaura toggle"));
            return false;
        }
        if (!(s instanceof Player)) {
            if (args.length >= 1 && Objects.equals("notify", args[0])) {
                this.notify(StringUtils.join(new ArrayList<>(Arrays.asList(args).subList(1, args.length)), " "));
            } else {
                s.sendMessage(Lang.NOTPLAYER.s());
            }
            return true;
        }
        Player player = (Player) s;
        if (args.length >= 1) {
            if ("toggle".equalsIgnoreCase(args[0])) {
                if (TOGGLED_PLAYERS.contains(player.getName())) {
                    player.sendMessage(Lang.ANTIAURA.f("&7AntiAura notifications disabled."));
                    TOGGLED_PLAYERS.remove(player.getName());
                } else {
                    player.sendMessage(Lang.ANTIAURA.f("&7AntiAura notifications enabled."));
                    TOGGLED_PLAYERS.add(player.getName());
                }
            }
        }
        return true;
    }

    public void notify(String msg) {
        for (String string : TOGGLED_PLAYERS) {
            Player player;
            if ((player = Bukkit.getPlayer(string)) != null) {
                Player target = Bukkit.getPlayer(msg.split(" ")[0]);
                if (target.isSprinting() && target.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE
                        || "spawn".equals(target.getWorld().getName())) {
                    return;
                }

                TextComponent component = new TextComponent(Lang.ANTIAURA.f("&7 " + msg));
                component.setColor(ChatColor.GRAY);
                if (target != null) {
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click to teleport to " + target.getDisplayName()).create()));
                }
                player.spigot().sendMessage(component);
            }
        }
    }
}