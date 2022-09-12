package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public class ResourcePackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        NMSVersion version = NMSVersion.getVersion(player);
        if(version== NMSVersion.MC_1_8) {
            player.sendMessage(Lang.GTM.f("&4Your client version is not supported! Please use 1.9+"));
            return false;
        }
        String url;
//        if (version==NMSVersion.MC_1_12_1 || version == NMSVersion.MC_1_11_2 || version == NMSVersion.MC_1_12 || version == NMSVersion.MC_1_11) {
//            player.setResourcePack(GTM.getSettings().getOneElevenRespack());
//            url = GTM.getSettings().getOneElevenRespack();
//        } else {
//            player.setResourcePack(GTM.getSettings().getOneTenRespack());
//            url = GTM.getSettings().getOneTenRespack();
//        }

        if (version.getProtocol() >= NMSVersion.MC_1_11.getProtocol()) {
            player.setResourcePack(GTM.getSettings().getOneElevenRespack());
            url = GTM.getSettings().getOneElevenRespack();
        } else {
            player.setResourcePack(GTM.getSettings().getOneTenRespack());
            url = GTM.getSettings().getOneTenRespack();
        }

        player.sendMessage(Lang.GTM.f("&cYou can download the server resource pack here: &b" + url));
        return true;
    }
}
