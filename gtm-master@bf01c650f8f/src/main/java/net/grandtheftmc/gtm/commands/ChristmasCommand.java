package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.event.christmas.SpawnSantaDropTask;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.weapon.ranged.special.Clausinator;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Timothy Lampen on 2017-12-16.
 */
public class ChristmasCommand extends CoreCommand<Player> {
    public ChristmasCommand() {
        super("christmas", "Commands related to the christmas event");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length==0) {
            player.sendMessage("/christmas clausinator - reloads the clausinator weapon");
            return;
        }
        switch (args[0]) {
            case "spawndrop": {
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                if(player.isOp() && user.getUserRank().isHigherThan(UserRank.ADMIN))
                    new SpawnSantaDropTask(player.getLocation());
                return;
            }
            case "clausinator": {
                GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if(gtmUser.isInCombat()) {
                    player.sendMessage(Lang.GTM.f("&cYou cannot load this weapon in combat!"));
                    return;
                }
                if(!gtmUser.hasMoney(100000)){
                    player.sendMessage(Lang.GTM.f("&cYou do not have enough money ($100,000) to reload this gun!"));
                    return;
                }
                Weapon<?> optClaus = GTMGuns.getInstance().getWeaponManager().getWeaponByItem(player.getInventory().getItemInMainHand());
                if(optClaus==null) {
                    player.sendMessage(Lang.GTM.f("&cYou are not holding a clausinator in your hand currently"));
                    return;
                }
                Clausinator claus = (Clausinator)optClaus;

                if(claus.getAmmo(player.getInventory().getItemInMainHand())!=0) {
                    player.sendMessage(Lang.CHRISTMAS.f("&cThe clip of the clausinator isn't empty!"));
                    return;
                }

                gtmUser.takeMoney(100000);
                ItemStack updated = claus.setAmmo(player.getInventory().getItemInMainHand(), 600,600);
                player.getInventory().setItemInMainHand(updated);
                player.updateInventory();
                player.sendMessage(Lang.CHRISTMAS.f("&aReloaded your weapon."));
                return;
            }
        }
    }
}
