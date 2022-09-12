package net.grandtheftmc.core.resourcepack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import net.grandtheftmc.core.Core;

/**
 * Created by Luke Bingham on 07/08/2017.
 */
public class RSPack_1_12 implements ResourcePackReceiving {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReceiving(ResourcePackManager manager, PacketContainer packet, Player player) {

		if (packet.getType().equals(PacketType.Play.Client.RESOURCE_PACK_STATUS)) {

			ResourcePackEvent.ResourceStatus[] resourceStatus = { ResourcePackEvent.ResourceStatus.NO_RESPONSE };
			net.minecraft.server.v1_12_R1.PacketPlayInResourcePackStatus.EnumResourcePackStatus status = packet.getSpecificModifier(net.minecraft.server.v1_12_R1.PacketPlayInResourcePackStatus.EnumResourcePackStatus.class).read(0);
			
			switch (status) {
				case SUCCESSFULLY_LOADED:
					// manager.sendLoaded(player);
					resourceStatus[0] = ResourcePackEvent.ResourceStatus.SUCCESSFULLY_LOADED;
					break;
				case DECLINED:
					manager.sendDeclined(player);
					resourceStatus[0] = ResourcePackEvent.ResourceStatus.DECLINED;
					break;
				case FAILED_DOWNLOAD:
					manager.sendFailed(player);
					resourceStatus[0] = ResourcePackEvent.ResourceStatus.FAILED_DOWNLOAD;
					break;
				case ACCEPTED:
					// manager.sendLoading(player);
					resourceStatus[0] = ResourcePackEvent.ResourceStatus.ACCEPTED;
					break;
			}

			// run notification event 3 seconds later
			new BukkitRunnable() {
				@Override
				public void run() {
					ResourcePackEvent event = new ResourcePackEvent(player, resourceStatus[0]);
					Bukkit.getPluginManager().callEvent(event);
				}
			}.runTaskLater(Core.getInstance(), 3 * 20);
		}
	}
}
