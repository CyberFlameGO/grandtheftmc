package net.grandtheftmc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.currency.component.CurrencySource;
import net.grandtheftmc.core.transaction.state.user.UserStateTransactionEvent;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.PluginAssociated;
import net.grandtheftmc.core.util.json.JSONParser;

public class UserStateTransactionListener implements Listener, PluginAssociated {

	/** The owning plugin */
	private final Plugin plugin;
	
	/**
	 * Create a UserStateTransactionListener that will listen and handle all user state transaction events.
	 * 
	 * @param plugin - the owning plugin
	 */
	public UserStateTransactionListener(Plugin plugin){
		this.plugin = plugin;
	}
	
	/**
	 * Listens in on user state transaction events. 
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onUserStateTransaction(UserStateTransactionEvent event){
		
		Player player = event.getPlayer();
		JSONParser parser = new JSONParser(event.getTransaction().getPayload());
		
		// get the type of the payload
		if (parser.hasKey("type")){
			String type = parser.getString("type");
			
			switch(type.toLowerCase()){
				case "currency":
					Currency curr = Currency.fromID(parser.getString("currency")).orElse(null);
					int amount = parser.getInt("amount");
					
					if (curr != null && amount != 0){
						User coreUser = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
						if (coreUser != null){
							
							// custom source unless vote token
							CurrencySource source = CurrencySource.CUSTOM;
							if (curr == Currency.VOTE_TOKEN){
								source = CurrencySource.VOTE;
							}

							int result = coreUser.getPurse().deposit(source, curr, amount);
							event.setProcessed(true);

							// if autoclaim votes
							if (curr == Currency.VOTE_TOKEN && coreUser.getPref(Pref.AUTO_CLAIM_VOTE_REWARD)){
								Core.getVoteManager().spendAllVotes(player, coreUser);
							}
						}
					}
					break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Plugin getPlugin() {
		return plugin;
	}
}
