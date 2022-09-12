package net.grandtheftmc.vice.hologram;

import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Hologram {

    int getId();
    Location getOrigin();

    List<HologramNode> getNodes();
    HologramNode getNode(int id);

    void spawn(Player player);
    void refresh();
    void refresh(int nodeId);
    void refresh(Player player);
    void refresh(int nodeId, Player player);
    void destroy();

    HologramNode addNode(int id) throws HologramDuplicateNodeException;
    HologramNode addNode(int id, String text) throws HologramDuplicateNodeException;
    HologramNode addNode(HologramNode node) throws HologramDuplicateNodeException;
}
