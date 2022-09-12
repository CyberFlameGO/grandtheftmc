package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Lampen on 2017-08-14.
 */
public class LogoutCommand extends CoreCommand<Player> {
    public LogoutCommand() {//currently disabled, if enabled again, if a player logs out successfully, logs in, logs out without the command, the npc does not spawn see USED_LOGOUT
        super("logout", "safetly logs out the user from the server without an NPC");
    }

    @Override
    public void execute(Player player, String[] args) {
        new LogoutTask(player).runTaskTimer(Vice.getInstance(), 0, 20);
    }

    private class LogoutTask extends BukkitRunnable {

        private final Location location;
        private int counter = 15;
        private final Player player;

        public LogoutTask(Player player){
            this.player = player;
            this.location = player.getLocation();
        }

        @Override
        public void run() {
            if(player.getLocation().distance(location)>1) {
                cancel();
                player.sendMessage(Lang.COMBATTAG.f("&7Stopped logging your player out as you have moved."));
                return;
            }
            player.sendMessage(Lang.COMBATTAG.f("&7Logging your player out in &e&l" + counter + " &7second" + (counter==1 ? "s." : ".")));
            if(counter==0){
                Vice.getUserManager().getLoadedUser(player.getUniqueId()).setBooleanToStorage(BooleanStorageType.USED_LOGOUT, true);
                player.kickPlayer(Lang.COMBATTAG.f("&7You have successfully logged out."));
            }
            else
                counter--;
        }
    }
}
