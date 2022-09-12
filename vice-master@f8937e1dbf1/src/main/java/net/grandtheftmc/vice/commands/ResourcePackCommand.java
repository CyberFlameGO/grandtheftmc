package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.resourcepack.ResourcePack;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.vice.Vice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public class ResourcePackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.VICE.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
//        int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
//        String url;
//        if (version >= 315) {
//            player.setResourcePack(Vice.getSettings().getOneElevenRespack());
//            url = Vice.getSettings().getOneElevenRespack();
//        } else {
//            player.setResourcePack(Vice.getSettings().getOneTenRespack());
//            url = Vice.getSettings().getOneTenRespack();
//        }
//        player.sendMessage(Lang.VICE.f("&cYou can download the server resource pack here: &b" + url));

        NMSVersion version = NMSVersion.getVersion(player);
        if(version== NMSVersion.MC_1_8) {
            player.sendMessage(Lang.GTM.f("&4Your client version is not supported! Please use 1.9+"));
            return false;
        }
        ResourcePack pack = Vice.getResourcePackManager().getResourcePack(version);
        if(pack != null) {
//        player.setResourcePack(pack.getPack(), pack.getHash().getBytes()); Correct hash?
            player.setResourcePack(pack.getPack());
        }
        else {
            player.setResourcePack(Vice.getResourcePackManager().getResourcePack(NMSVersion.MC_1_11).getPack());
        }
        return true;
    }
}
