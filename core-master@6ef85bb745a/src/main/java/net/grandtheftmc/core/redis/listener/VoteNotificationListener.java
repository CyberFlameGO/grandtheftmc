package net.grandtheftmc.core.redis.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.voting.events.PlayerVoteEvent;
import net.grandtheftmc.jedis.JMessageListener;
import net.grandtheftmc.jedis.message.VoteNotificationMessage;

public class VoteNotificationListener implements JMessageListener<VoteNotificationMessage> {

	@Override
	public void onReceive(ServerTypeId serverTypeId, VoteNotificationMessage voteMessage) {

		// ignore if core is not setup
		if (!Core.isCoreEnabled())
			return;

		// run on sync
		ServerUtil.runTask(() -> {
			
			// TODO remove as its a test
			Core.log("[VoteNotificationListener][DEBUG] Received vote notification for " + voteMessage.getUUID().toString());

			// grab the player
			Player player = Bukkit.getPlayer(voteMessage.getUUID());

			// if no player online, skip, user state transactions will be
			// handled
			if (player == null) {
				return;
			}
			
			// create the voter in the vote manager
			Core.getVoteManager().createVoter(player.getName());
			
			// fire vote event, null params for legacy purposes
			PlayerVoteEvent voteEvent = new PlayerVoteEvent(player.getUniqueId(), null, null, null);
	        Bukkit.getPluginManager().callEvent(voteEvent);

			player.sendMessage(Lang.VOTE.f("&7Thank you for &e&lvoting&7 for the server! Open the &e&lVote&7 menu to claim your prize!"));

			if (voteMessage != null) {
				player.sendMessage(voteMessage.getMessage().replaceAll("&", "§"));
			}
		});
	}
}