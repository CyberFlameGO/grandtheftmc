package net.grandtheftmc.core.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandListener implements Listener {
    private SimpleCommandMap cmdMap;
    private List<String> socialSpyCmds = new ArrayList<>();

    public CommandListener() {
        try {
            this.cmdMap = this.getCommandMap(Core.getInstance().getServer());
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        this.loadSocialSpy();
    }

    private void loadSocialSpy() {
        YamlConfiguration c = Core.getSettings().getSocialSpyConfig();
        this.socialSpyCmds = c.getStringList("commands");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        String[] split = event.getCommand().split(" ");
        switch (split[0].toLowerCase()){
            case "restart":
            case "stop":
                event.setCancelled(true);
                Core.getInstance().setRestarting(true);
                if(Core.getSettings().getNumber() <= 0 || (split.length>1 && split[1].equalsIgnoreCase("force"))) {
                    Bukkit.shutdown();
                    return;
                }
                shutdownSequence();
                return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        switch (e.getMessage()) {
            case "/bankcraft deposit all":
            case "/bc deposit all":
            case "/bank deposit all":
                player.sendMessage(Utils.f("&c/bankcraft deposit <amount>"));
                return;
            default:
                break;
        }
        String[] split = e.getMessage().replaceFirst("/", "").split(" ");
        String cmd = split[0].toLowerCase();
        if (this.socialSpyCmds != null && this.socialSpyCmds.contains(cmd)){
            UserManager.getInstance().getUsers().stream().filter(user -> user.getPref(Pref.SOCIALSPY)).forEach(user -> {
                Player p = Bukkit.getPlayer(user.getUUID());
                if (p != null){
                    p.sendMessage(Lang.SS.f("&r" + player.getName() + ": " + e.getMessage()));
                }
            });
        }
        switch (cmd) {
            case "who": {
                e.setCancelled(true);
                player.sendMessage(Utils.f("&7Unknown command: \"&a" + e.getMessage() + "&7\"."));
                return;
            }
            case "me":
                e.setCancelled(true);
                player.sendMessage(Utils.f("&7Unknown command: \"&a" + e.getMessage() + "&7\"."));
                return;
            case "buy":
            case "donate":
            case "purchase":
                e.setCancelled(true);
                player.performCommand("/store");
                return;
            case "restart":
            case "stop":
                e.setCancelled(true);
                if (!player.isOp()) {
                    player.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                    return;
                }
                Core.getInstance().setRestarting(true);
                if(Core.getSettings().getNumber() <= 0 || (split.length>1 && split[1].equalsIgnoreCase("force"))) {
                    Bukkit.shutdown();
                    return;
                }
                shutdownSequence();
                return;
            case "minecraft:tell":
            case "minecraft:msg":
            case "minecraft:w":
            case "bukkit:tell":
            case "bukkit:msg":
            case "bukkit:w":
            case "bukkit:version":
            case "version":
            case "ver":
            case "about":
            case "bukkit:me":
            case "minecraft:me":
            case "plugins":
            case "pl":
            case "bukkit:plugins":
            case "bukkit:pl":
            case "?":
            case "bukkit:?":
            case "bukkit:help":
            case "minecraft:?":
            case "save-on":
            case "save-all":
                e.setCancelled(true);
                player.sendMessage(Utils.f("&cbit.ly/FreeCookiesOn" + Core.getSettings().getServer_GTM_shortName()));
                return;
            default:
                if (this.isCmdRegistered(cmd))
                    return;
                e.getPlayer().sendMessage(Utils.f("&7Unknown command: \"&a" + e.getMessage() + "&7\"."));
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent e) {
        if (!e.getChatMessage().startsWith("/")) return;
        String[] args = e.getChatMessage().split(" ");
        if (args.length == 1 && args[0].length() < 5) e.getTabCompletions().clear();
    }

    private SimpleCommandMap getCommandMap(Server svr) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        if (svr.getPluginManager() instanceof SimplePluginManager) {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            return (SimpleCommandMap) f.get(svr.getPluginManager());
        } else {
            return null;
        }
    }

    private void shutdownSequence(){
        Core.error("[Stop] Stop sequence started...");
        Core.error("[Stop] Blocking player requests to join...");
        new BukkitRunnable() {
            int counter = 60;
            @Override
            public void run() {
                if(counter<=0) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(Lang.ALERTS.f("&cThe server is restarting!"));
                    }
                    Core.error("[Stop] Players have been kicked, stopping server in 20 seconds.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.shutdown();
                        }
                    }.runTaskLater(Core.getInstance(), 400);
                    cancel();
                    return;
                }
                if(counter % 5 == 0){
                    Core.error("[Stop] Kicking players in " + counter + " seconds");
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.ALERTS.f("&6The server will stop in &4" + counter + "&6s")));
                }
                counter--;
            }
        }.runTaskTimer(Core.getInstance(), 0,20);
    }

    private boolean isCmdRegistered(String name) {
        return this.cmdMap.getCommand(name) != null;
    }
}
