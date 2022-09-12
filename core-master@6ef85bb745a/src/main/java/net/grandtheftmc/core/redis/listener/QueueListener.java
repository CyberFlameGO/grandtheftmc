package net.grandtheftmc.core.redis.listener;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.jedis.JMessageListener;
import net.grandtheftmc.jedis.message.ServerQueueNotifyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueListener implements JMessageListener<ServerQueueNotifyMessage> {

    @Override
    public void onReceive(ServerTypeId serverTypeId, ServerQueueNotifyMessage serverQueueNotifyMessage) {
        if(!Core.isCoreEnabled()) return;
        ServerUtil.runTask(() -> {
            Player player = Bukkit.getPlayer(serverQueueNotifyMessage.getUniqueId());
            if (player == null) return;

            player.sendMessage(Lang.QUEUE.f("&7You're #" + serverQueueNotifyMessage.getPossition() + " in the Queue for joining, " + serverQueueNotifyMessage.getTargetServer().getServerType().name() + "-" + serverQueueNotifyMessage.getTargetServer().getId()));
        });
    }
}
