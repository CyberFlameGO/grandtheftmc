package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.hologram.CoreHologram;
import net.grandtheftmc.vice.hologram.CoreHologramNode;
import net.grandtheftmc.vice.hologram.event.HologramReceiveEvent;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class CartelsComponent implements Component<CartelsComponent, Vice> {

//    private final Vice vice;
//    private final TagManager<Vice> tagManager;
//
//    public CartelsComponent(Vice vice, TagManager<Vice> tagManager) {
//        this.vice = vice;
//        this.tagManager = tagManager;
//        Bukkit.getPluginManager().registerEvents(this, vice);
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    protected final void onCartelCreate(FactionCreateEvent event) {
//        if(event.isCancelled()) return;
//        Player player = event.getFPlayer().getPlayer();
//
//        Nametag nametag = tagManager.getPlayerNametag(player);
//        if(nametag == null) return;
//
//        Tag tag = tagManager.getTagByUid(player, 3);
//        if(tag == null) {
//            Tag parent = nametag.getTag(2).orElse(null);
//            if(parent == null) return;
//            tagManager.createTag(NMSVersion.MC_1_12, player, 3, event.getFactionTag(), parent);
//        }
//        else {
//            tagManager.changeTag(player, 3, event.getFactionTag());
//        }
//
//        tagManager.refreshAll(player);
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    protected final void onCartelTagChange(FactionRenameEvent event) {
//        if(event.isCancelled()) return;
//        Player player = event.getfPlayer().getPlayer();
//
//        Nametag nametag = tagManager.getPlayerNametag(player);
//        if(nametag == null) return;
//
//        Tag tag = tagManager.getTagByUid(player, 3);
//        if(tag != null) nametag.delete(player, tag);
//
//        this.tagManager.refreshAll(player);
//    }
}
