package net.grandtheftmc.vice.hologram;

import net.grandtheftmc.core.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.UUID;

public interface HologramNode {

    int getId();

    ArmorStand getEntity();
    Location getLocation();
    String getText();

    void setText(String text);
}
