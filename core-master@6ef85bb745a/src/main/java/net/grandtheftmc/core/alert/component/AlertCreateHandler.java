package net.grandtheftmc.core.alert.component;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.alert.AlertCreateStage;
import net.grandtheftmc.core.alert.ui.AlertCreationMenu;
import net.grandtheftmc.core.util.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Luke Bingham on 11/09/2017.
 */
public class AlertCreateHandler implements Component<AlertCreateHandler, Core> {

    private final String user;
    private final AlertCreateStage stage;
    private final AlertCreationMenu menu;

    public AlertCreateHandler(String user, AlertCreateStage stage, AlertCreationMenu menu) {
        this.user = user;
        this.stage = stage;
        this.menu = menu;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if(!event.getPlayer().getName().equals(user)) return;
        event.setCancelled(true);

        if(stage == AlertCreateStage.NAME) menu.setName(event.getMessage());
        if(stage == AlertCreateStage.DESC) menu.setDescription(event.getMessage());
        if(stage == AlertCreateStage.IMAGE) menu.setImageUrl(event.getMessage());
        if(stage == AlertCreateStage.LINK) menu.setLink(event.getMessage());

        menu.refresh();
    }
}
