package net.grandtheftmc.gtm.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.gtm.GTM;

/**
 * Created by Timothy Lampen on 1/12/2018.
 */
public class TransferCommand extends CoreCommand<Player>{

	/** The allowed transfer servers */
    private static final Set<Integer> TRANSFERABLE_SERVERS = new HashSet<Integer>(Arrays.asList(2,3,5,6,0));

    /**
     * Construct a new TransferCommand.
     */
    public TransferCommand() {
        super("transfer", "transfer player data from one server to another");
    }

    @Override
    public void execute(Player player, String[] args) {
    	
    	if (!GTM.getSettings().isServerTransfer()){
    		player.sendMessage(Lang.GTM.f("&7Transferring is currently disabled!"));
    		return;
    	}
    	
    	// stop tranfers
        if(!TRANSFERABLE_SERVERS.contains(Core.getSettings().getNumber())) {//you have to be on server 2,3,5,6 to transfer
            player.sendMessage(Lang.GTM.f("&cSorry, transfering is currently &4&lLOCKED &cfor your server."));
            return;
        }
        
        // open transferconfirm menu
        MenuManager.openMenu(player, "transferconfirm");
    }
}
