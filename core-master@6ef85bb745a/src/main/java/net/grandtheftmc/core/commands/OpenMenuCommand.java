package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuManager;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-12-25.
 */
public class OpenMenuCommand extends CoreCommand<Player> {
    public OpenMenuCommand() {
        super("openmenu", "to open known menu names");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(!player.isOp())
            return;
        if(args.length!=1){
            player.sendMessage(Lang.ALERTS.f("&c/openmenu <menuname>"));
            return;
        }
        String name = args[0];
        Menu menu = MenuManager.getMenu(name);
        if(menu==null) {
            player.sendMessage(Lang.ALERTS.f("&cThat menu does not exist!"));
            return;
        }
        menu.openFor(player);
    }
}
