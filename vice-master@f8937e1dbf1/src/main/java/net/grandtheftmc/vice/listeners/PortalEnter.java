package net.grandtheftmc.vice.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PortalEnter implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void playerPortalEvent(EntityPortalEnterEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();

        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (viceUser.getTaxiTarget() != null) return;

        if (event.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
//            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
//            Warp randomWarp = Vice.getWarpManager().getRandomWarp();
//            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1F);
//            Vice.getWarpManager().warp(player, user, Vice.getUserManager().getLoadedUser(player.getUniqueId()),
//                    new TaxiTarget(randomWarp), 0, user.isPremium() ? 1 : 10);

            //Random teleport.
            World world = Bukkit.getWorld("world");
            boolean unfit = true;
            int tries = 0;
            Location loc = new Location(world, 0, 0, 0);
            while (unfit) {
                if (tries > 100) {
                    player.sendMessage(Lang.TAXI.f("&7Could not find suitable location to teleport you to. Please try again."));
                    return;
                }
                loc = new Location(world,
                        ThreadLocalRandom.current().nextInt(5000),
                        0,
                        ThreadLocalRandom.current().nextInt(5000));
                loc.setY(world.getHighestBlockYAt(loc));
                Faction factionAt = Board.getInstance().getFactionAt(new FLocation(loc));
                Biome biome = world.getBiome(loc.getBlockX(), loc.getBlockZ());
                Material material = loc.getWorld().getHighestBlockAt(loc).getType();
                unfit = !factionAt.isWilderness()
                        || biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN
                        || biome == Biome.FROZEN_OCEAN || biome == Biome.SKY
                        || biome == Biome.VOID || biome == Biome.RIVER || material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.LAVA || material == Material.STATIONARY_LAVA || material == Material.CACTUS;
                tries += 1;
            }
            loc.setY(loc.getY() + 0.5);
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            viceUser.setLastRTP();
            Vice.getWorldManager().getWarpManager().warp(player, user, viceUser, new TaxiTarget(loc), 0, -1,
                    "&eYou called a taxi to take you to &a" + loc.getBlockX() + "&e, &a" + loc.getBlockY() + "&e, &a" + loc.getBlockZ() + "&e in the wilderness..");
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPortalEnter(PlayerPortalEvent event) {
        if(event.getFrom() != null && event.getFrom().getWorld() != null &&
                event.getFrom().getWorld().getName().equalsIgnoreCase("spawn"))
            event.setCancelled(true);
    }
}
