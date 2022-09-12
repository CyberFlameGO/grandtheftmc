package net.grandtheftmc.vice.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class RTPCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (!viceUser.canRTP()) {
            player.sendMessage(Lang.TAXI.f("&7Please wait &a&l" + Utils.timeInMillisToText(viceUser.getTimeUntilRTP()) + "&7 before using this command!"));
            return true;
        }
        World world = Bukkit.getWorld("world");
        boolean unfit = true;
        int tries = 0;
        Location loc = new Location(world, 0, 0, 0);
        while (unfit) {
            if (tries > 100) {
                player.sendMessage(Lang.TAXI.f("&7Could not find suitable location to teleport you to. Please try again."));
                return true;
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
        return true;
    }
}
