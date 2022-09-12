package net.grandtheftmc.gtm.commands;

import org.bukkit.entity.Player;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.weapon.skins.menu.MainMenu;

public class SkinsCommand extends CoreCommand<Player> implements RankedCommand {
    public SkinsCommand() {
        super("skins", "View or equip your weapon skins.");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length == 0) {
            new MainMenu(sender).open();
        }
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.DEFAULT;
    }
}