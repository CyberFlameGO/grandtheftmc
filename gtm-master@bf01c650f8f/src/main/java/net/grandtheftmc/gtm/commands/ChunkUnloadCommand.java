package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ChunkUnloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player)s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(!user.isRank(UserRank.DEV)) {
            player.sendMessage(Lang.GTM.f("&7You don't have permission to use this command."));
            return true;
        }
        Collection<Chunk> chunks = Arrays
                .stream(player.getWorld().getLoadedChunks())
                .collect(Collectors.toList());
        for(Chunk chunk : player.getWorld().getLoadedChunks()) {
            for(Entity entity : chunk.getEntities()) {
                if (entity.getType() == EntityType.PLAYER) {
                    chunks.remove(chunk);
                    break;
                }
            }
        }
        chunks.forEach(Chunk::unload);
        return true;
    }
}