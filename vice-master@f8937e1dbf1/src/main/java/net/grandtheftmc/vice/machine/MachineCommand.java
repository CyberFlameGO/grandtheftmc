package net.grandtheftmc.vice.machine;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.machine.repair.MachineRepairMenu;
import org.bukkit.entity.Player;

public class MachineCommand extends CoreCommand<Player> implements RankedCommand {

    private final MachineManager machineManager;

    public MachineCommand(MachineManager machineManager) {
        super("machine", "Admin command to give a machine.");
        this.machineManager = machineManager;
    }

    @Override
    public void execute(Player sender, String[] strings) {
        if (strings.length == 0) {
            new MachineRepairMenu(this.machineManager).openInventory(sender);
            return;
        }

        sender.getInventory().addItem(machineManager.getMachineItemById(Integer.parseInt(strings[0])));
        sender.updateInventory();
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
