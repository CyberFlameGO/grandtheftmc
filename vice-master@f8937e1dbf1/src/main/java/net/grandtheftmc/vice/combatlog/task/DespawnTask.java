package net.grandtheftmc.vice.combatlog.task;

import net.grandtheftmc.vice.Vice;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Lampen on 2017-08-14.
 */
public class DespawnTask extends BukkitRunnable{

    private final NPC villager;
    private int counter = 15;
    private final String name;
    public DespawnTask(NPC villager, String name){
        this.villager = villager;
        this.name =name;
    }

    @Override
    public void run() {
        if(villager.isDead() || !villager.isValid() || counter==0) {
            Vice.getCombatLogManager().removeNPC(villager);
            villager.remove();
            cancel();
            return;
        }
        else
            villager.setCustomName(name + ChatColor.GRAY + "'s Combat NPC (" + ChatColor.YELLOW + counter +  ChatColor.GRAY + ")");
        counter--;
    }
}
