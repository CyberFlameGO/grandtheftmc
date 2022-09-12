package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.vice.machine.MachineManager;
import net.grandtheftmc.vice.users.npcs.MachineNPC;
import net.grandtheftmc.vice.users.npcs.shopnpc.ShopNPC;
import net.grandtheftmc.vice.users.npcs.TaxiNPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class CoreNPCCommand extends CoreCommand<Player> {

    private final MachineManager machineManager;

    public CoreNPCCommand(MachineManager machineManager) {
        super("corenpc", "commands dealing with corenpcs");
        this.machineManager = machineManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        if(!player.isOp()) {
            player.sendMessage(Lang.NOPERM.f(""));
            return;
        }

        if(args.length==0) {
            player.sendMessage(Lang.GTM.f("&7Avaliable NPCs: "));
            player.sendMessage(Utils.f("&aTaxi&7- Opens the taxi menu"));
            player.sendMessage(Utils.f("&aShop &7- Opens a food shop menu"));
            player.sendMessage(Utils.f("&aMachine &7- NPC for trading machine shards"));
            player.sendMessage(Lang.GTM.f("&c/corenpc delete &7- Removes the nearby entity (please stand within 1 block)"));
            player.sendMessage(Lang.GTM.f("&c/corenpc spawn <npc>"));
            return;
        }

        if(args.length==1){
            switch (args[0].toLowerCase()) {
                case "delete":
                case "remove":
                    for(Entity e : player.getNearbyEntities(2,2,2))
                        Core.getNPCManager().deleteNPC(e);
                    player.sendMessage(Lang.GTM.f("&cYou have removed nearby npcs."));
                    break;
            }
            return;
        }

        switch (args[1].toLowerCase()) {
            case "shop":
                new ShopNPC(player.getLocation());
                break;
            case "machine":
                new MachineNPC(this.machineManager, player.getLocation());
                break;
            case "taxi":
                new TaxiNPC(player.getLocation());
                break;

        }
    }
}
