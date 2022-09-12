package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.npcs.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class CoreNPCCommand extends CoreCommand<Player> {
    public CoreNPCCommand() {
        super("corenpc", "commands dealing with corenpcs");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(!player.isOp()) {
            player.sendMessage(Lang.NOPERM.f(""));
            return;
        }

        if(args.length==0) {
            player.sendMessage(Lang.GTM.f("&7Avaliable NPCs: "));
            player.sendMessage(Utils.f("&aPlay &7- Opens the taxi menu"));
            player.sendMessage(Utils.f("&aBank &7- Opens the bank menu"));
            player.sendMessage(Utils.f("&aFood &7- Opens a food shop menu"));
            player.sendMessage(Utils.f("&aCasino &7- Opens the casino chip dealer menu"));
            player.sendMessage(Utils.f("&aCar &7- Opens a car shop menu"));
            player.sendMessage(Utils.f("&aRewards &7- Opens the rewards menu"));
            player.sendMessage(Utils.f("&aArmor &7- Opens an armor shop menu"));
            player.sendMessage(Utils.f("&aShop &7- Opens an inventory where players and buy and sell items."));
            player.sendMessage(Utils.f("&aSkins &7- Opens an inventory where players can manage and view their weapon skins."));
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
            case "casino":
                new CasinoNPC(player.getLocation());
                break;
            case "play":
                new TaxiNPC(player.getLocation());
                break;
            case "bank":
                new BankTellerNPC(player.getLocation());
                break;
            case "food":
                new FoodNPC(player.getLocation());
                break;
            case "car":
                new CarNPC(player.getLocation());
                break;
            case "rewards":
                new RewardsNPC(player.getLocation());
                break;
            case "armor":
                new ArmorNPC(player.getLocation());
                break;
            case "shop":
                new ShopNPC(GTM.getWastedGuns().getWeaponManager(), player.getLocation());
                break;
            case "skins":
                new SkinsNPC(player.getLocation());
                break;
            case "heads":
                new HeadSellerNPC(player.getLocation());
                break;
            case "mechanic":
                new MechanicNPC(player.getLocation());
                break;
        }
    }
}
