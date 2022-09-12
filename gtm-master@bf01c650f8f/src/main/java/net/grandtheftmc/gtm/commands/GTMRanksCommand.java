package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.LockedWeapon;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GTMRanksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        player.sendMessage(Lang.RANKS.s());
        List<LockedWeapon> lockedWeapons = new LinkedList<>(Arrays.asList(LockedWeapon.values()));
        for (GTMRank gtmRank : GTMRank.values()) {
            List<LockedWeapon> unlockedWeapons = lockedWeapons.stream().filter(lockedWeapon -> lockedWeapon.getGTMRank() == gtmRank).collect(Collectors.toList());
            String unlocks = "";
            if (!unlockedWeapons.isEmpty()) {
                unlocks = StringUtils.join(unlockedWeapons, "&7, &c&l");
            }
            player.sendMessage(Utils.f(gtmRank.getColoredNameBold() + " &7costs &a&l$" + gtmRank.getPrice()
                    + (unlocks.isEmpty() ? "" : " &7unlocks &c&l" + unlocks)));
        }
        return true;
    }
}
