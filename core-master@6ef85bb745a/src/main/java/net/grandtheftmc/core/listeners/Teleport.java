package net.grandtheftmc.core.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.PlayerSwitchWorldEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;

public class Teleport implements Listener {


    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
    	
    	// grab event variables
    	World fromWorld = event.getFrom().getWorld();
    	World toWorld = event.getTo().getWorld();
    	
        if (!Objects.equals(fromWorld.getName(), toWorld.getName())) {
            PlayerSwitchWorldEvent e = new PlayerSwitchWorldEvent(event.getPlayer(), event.getFrom(), event.getTo(), Core.getWorldManager().getWorldConfig(event.getTo().getWorld().getName()));
            switch (e.getToWorldConfig().getType()) {
                case USERRANK:
                	
                	// grab the user
                	User user = UserManager.getInstance().getUser(event.getPlayer().getUniqueId()).orElse(null);
                	if (user != null){
                		UserRank requiredRank = UserRank.getUserRank(e.getToWorldConfig().getRestricted());
                		
                		if (!user.isRank(requiredRank)){
                			event.setCancelled(true);
                		}
                	}

                    break;
                case RESTRICTED:
                    e.setCancelled(true);
                    break;
                default:
                    break;
            }
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) event.setCancelled(true);
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.setCancelled(true);
        }
    }

    /*
     * // Try increasing this. May be dependent on lag. private final int
     * TELEPORT_FIX_DELAY = 15; // ticks
     *
     * @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
     * public void onPlayerTeleport(PlayerTeleportEvent event) { if
     * (!Core.getSettings().getUseTeleportFix()) return;
     *
     * final Player player = event.getPlayer(); final int visibleDistance =
     * Bukkit.getServer().getViewDistance() * 16;
     * Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), new
     * Runnable() {
     *
     * @Override public void run() { updateEntities(getPlayersWithin(player,
     * visibleDistance)); } }, TELEPORT_FIX_DELAY); }
     *
     * public void updateEntities(List<Player> observers) { // Refresh every
     * single player for (Player player : observers) updateEntity(player,
     * observers);
     *
     * }
     *
     * public void updateEntity(Entity entity, List<Player> observers) {
     *
     * World world = entity.getWorld(); WorldServer worldServer =
     * ReflectionAPI.getHandle(world);
     *
     * EntityTracker tracker = worldServer.tracker; EntityTrackerEntry entry =
     * (EntityTrackerEntry) tracker.trackedEntities.get(entity.getEntityId());
     *
     * List<EntityHuman> nmsPlayers = getNmsPlayers(observers);
     *
     * // Force Minecraft to resend packets to the affected clients
     * entry.trackedPlayers.removeAll(nmsPlayers);
     * entry.scanPlayers(nmsPlayers); }
     *
     * private List<EntityHuman> getNmsPlayers(List<Player> players) {
     * List<EntityHuman> nsmPlayers = new ArrayList<EntityHuman>();
     *
     * for (Player bukkitPlayer : players) { CraftPlayer craftPlayer =
     * (CraftPlayer) bukkitPlayer; nsmPlayers.add(craftPlayer.getHandle()); }
     *
     * return nsmPlayers; }
     *
     * private List<Player> getPlayersWithin(Player player, int distance) {
     * List<Player> res = new ArrayList<Player>(); int d2 = distance * distance;
     *
     * for (Player p : Bukkit.getOnlinePlayers()) { if (p.getWorld() ==
     * player.getWorld() &&
     * p.getLocation().distanceSquared(player.getLocation()) <= d2) {
     * res.add(p); } }
     *
     * return res; }
     */

}
