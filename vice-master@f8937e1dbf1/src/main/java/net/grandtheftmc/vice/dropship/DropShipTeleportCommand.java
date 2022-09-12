package net.grandtheftmc.vice.dropship;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DropShipTeleportCommand extends CoreCommand<Player> {

    private final DropShipManager dropShipManager;
    private final ThreadLocalRandom random;

    public DropShipTeleportCommand(DropShipManager dropShipManager) {
        super("dropshiptp", "Teleport to a drop ship event.");
        this.dropShipManager = dropShipManager;
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public void execute(Player sender, String[] strings) {
        if (!this.dropShipManager.isActive()) {
            sender.sendMessage(C.RED + "There isn't an acitve drop ship event.");
            return;
        }

        if (this.dropShipManager.getDropShip().getDropShipTask().contains(sender)) {
            sender.sendMessage(C.RED + "You can only teleport once..");
            return;
        }

        this.dropShipManager.getDropShip().getDropShipTask().addPlayer(sender);

        Location location = this.getRandomLocation();
        if (location == null) {
            sender.sendMessage(C.RED + "There was an error teleporting you, try again.");
            return;
        }

        sender.teleport(location);

        sender.removePotionEffect(PotionEffectType.LEVITATION);
        sender.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 120 * 20, -4, true, false));
        this.dropShipManager.userDamageMap.put(sender.getUniqueId(), System.currentTimeMillis() + (1000 * 120));
    }

    private Location getRandomLocation() {
        Area area = this.dropShipManager.getDropShip().getSettlement();
        int x = this.random.nextInt(area.getMinX(), area.getMaxX());
        int z = this.random.nextInt(area.getMinZ(), area.getMaxZ());
        int y = area.getWorld().getHighestBlockYAt(x, z) + this.random.nextInt(80, 100);
        return new Location(area.getWorld(), x, y, z);
    }
}
