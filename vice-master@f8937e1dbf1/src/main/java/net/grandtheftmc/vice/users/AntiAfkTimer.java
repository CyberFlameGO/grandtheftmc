package net.grandtheftmc.vice.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 3/6/2018.
 */
public class AntiAfkTimer implements Runnable {

    private final HashMap<UUID, Vector> directions = new HashMap<>();


    @Override
    public void run() {
        purgeData();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(this.directions.containsKey(player.getUniqueId())){
                if(this.directions.get(player.getUniqueId()).equals(player.getLocation().getDirection())) {
                    player.kickPlayer(Lang.VICE.f("&cYou were kicked for being afk!"));
                    continue;
                }
            }
            this.directions.put(player.getUniqueId(), player.getLocation().getDirection());
        }
    }


    private void purgeData(){
        directions.entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey()) == null);
    }
}
