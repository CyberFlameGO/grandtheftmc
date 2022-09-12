package com.j0ach1mmall3.wastedguns.listeners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponShootEvent;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class WeaponsListener implements Listener {

    private final GTMGuns plugin;
    private final HashMap<UUID, LinkedList<Long>> playerShots = new HashMap<>();

    public WeaponsListener(GTMGuns plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShoot(RangedWeaponShootEvent event){
        if(!(event.getLivingEntity() instanceof Player))
            return;
        Player player = (Player)event.getLivingEntity();
        RangedWeapon<?> weapon = (RangedWeapon<?>)event.getWeapon();

        if(!weapon.isAutomatic())
            return;

        double allowedSPS = 25.0; //bullets shot every tick (20 ticks = 1 sec) and have some buffer for lag.

        LinkedList<Long> shots = this.playerShots.getOrDefault(player.getUniqueId(), new LinkedList<Long>());

        if(shots.size() >= allowedSPS) {
            long difference = shots.getFirst() - shots.getLast();
            if(difference < 1000) { //player may be hacking.
                BaseComponent[] components = TextComponent.fromLegacyText(Lang.ANTICHEAT.s() + Utils.f("&c" + player.getName() + "&7 has triggered &cFASTPLACE &7 event! &f&l" + (allowedSPS/TimeUnit.MILLISECONDS.toSeconds(difference)) + "/s (Normal is " + allowedSPS + "/s &7"));
                for (BaseComponent c : components) {
                    c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Utils.f("&7Reports&f: &7&oClick to inspect"))));
                    c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect " + player.getName()));
                }

                ServerUtil.runTask(() -> {
                    for (User user : UserManager.getInstance().getUsers()) {
                        if (!user.isStaff()){
                        	continue;
                        }
                        Bukkit.getPlayer(user.getUUID()).spigot().sendMessage(components);
                    }
                });
            }
            shots.clear();
        }

        this.playerShots.put(player.getUniqueId(), shots);

    }
}