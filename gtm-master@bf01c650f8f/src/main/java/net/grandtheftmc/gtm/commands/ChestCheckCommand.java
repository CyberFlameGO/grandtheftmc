package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.houses.PremiumHouse;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.Set;

public class ChestCheckCommand extends CoreCommand<Player> implements RankedCommand {

    public ChestCheckCommand() {
        super("chestcheck", "A staff command for recognising house chests.");
    }

    @Override
    public void execute(Player sender, String[] strings) {
        Block targetBlock = sender.getTargetBlock((Set<Material>) null, 5);
        if (targetBlock == null || targetBlock.getState() == null || !(targetBlock.getState() instanceof Chest)) {
            sender.sendMessage(C.ERROR + "You don't seem to be looking at a chest?..");
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        HousesManager manager = Houses.getHousesManager();

        House house = manager.getHouseFromChest(chest.getLocation());
        if (house != null) {
            sender.sendMessage(C.DARK_GREEN + C.BOLD + "House found");
            sender.sendMessage(C.GREEN + "Premium?  " + C.YELLOW + "false");
            sender.sendMessage(C.GREEN + "Identifier  " + C.YELLOW + house.getId());
            sender.sendMessage(C.GREEN + "Doors  " + C.YELLOW + house.getDoors().size());
            sender.sendMessage(C.GREEN + "Chests  " + C.YELLOW + house.getChests().size());
            return;
        }

        PremiumHouse premiumHouse = manager.getPremiumHouseFromChest(chest.getLocation());
        if (premiumHouse != null) {
            sender.sendMessage(C.DARK_GREEN + C.BOLD + "Premium House found");
            sender.sendMessage(C.GREEN + "Premium?  " + C.YELLOW + "true");
            sender.sendMessage(C.GREEN + "Identifier  " + C.YELLOW + premiumHouse.getId());
            sender.sendMessage(C.GREEN + "Doors  " + C.YELLOW + premiumHouse.getDoors().size());
            sender.sendMessage(C.GREEN + "Chests  " + C.YELLOW + premiumHouse.getChests().size());
        }
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
