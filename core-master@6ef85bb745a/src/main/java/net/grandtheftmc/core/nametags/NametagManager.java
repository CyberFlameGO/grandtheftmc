package net.grandtheftmc.core.nametags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.NametagUpdateEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.Utils;

public class NametagManager implements Component<NametagManager, Core> {

	private final List<Nametag> nametags = new ArrayList<>();

	public NametagManager() {
		this.loadNametags();
	}

	@Override
	public NametagManager onDisable(Core plugin) {
		this.nametags.clear();
		return this;
	}

	public static void updateNametag(Player player) {
		
		// if invalid player
		if (player == null || !player.isOnline()){
			return;
		}
		// create nametag update event
		NametagUpdateEvent event = new NametagUpdateEvent(player);

		User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
		if (user != null){
			
			if (user.isSpecial()){
				event.setPrefix(user.getUserRank().getTabPrefix());
			}
			
			// call nametag update event
			Bukkit.getPluginManager().callEvent(event);
			
			String prefix = event.getPrefix() == null ? "&r" : event.getPrefix() + (event.getNameColor() == null ? " &r" : ' ' + event.getNameColor());
			String suffix = event.getSuffix() == null ? "" : ' ' + event.getSuffix();

			for (Player p : Bukkit.getOnlinePlayers()) {
				User u = UserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
				if (u != null){
					
					Scoreboard board = u.getScoreboard();
					Team team = board.getTeam(player.getName());
					if (team == null)
						team = board.registerNewTeam(player.getName());
					team.setPrefix(Utils.f(prefix));
					team.setSuffix(Utils.f(suffix.length() > 16 ? suffix.substring(0,16) : suffix));
					team.addEntry(player.getName());
		            /*if (event.getBelowName() != null) {
		                Objective obj = board.getObjective(event.getBelowName());
		                if (obj == null)
		                    obj = board.registerNewObjective(event.getBelowName(), "dummy");
		                obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
		                obj.setDisplayName(event.getBelowName());
		                obj.getScore(player.getName()).setScore(event.getValue());
		            }*/
					p.setScoreboard(board);
				}
			}
		}
	}

	public static void updateNametagsTo(Player player, User user) {
		if (player == null || !player.isOnline() || user == null){
			return;
		}
		
		// grab scoreboard
		Scoreboard board = user.getScoreboard();
		
		// for all other players
		for (Player p : Bukkit.getOnlinePlayers()) {
			
			// create nametag update event
			NametagUpdateEvent event = new NametagUpdateEvent(p);
			
			// grab user object
			User u = UserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
			if (u != null){

				if (u.isSpecial()){
					event.setPrefix(u.getUserRank().getTabPrefix());
				}
				
				
				// call event
				Bukkit.getPluginManager().callEvent(event);
				
				String prefix = event.getPrefix() == null ? "&r" : event.getPrefix() + (event.getNameColor() == null ? " &r" : ' ' + event.getNameColor());
				String suffix = event.getSuffix() == null ? "" : ' ' + event.getSuffix();

				Team team = board.getTeam(p.getName());
				if (team == null){
					team = board.registerNewTeam(p.getName());
				}
				team.setPrefix(Utils.f(prefix));
				team.setSuffix(Utils.f(suffix.length() > 16 ? suffix.substring(0,16) : suffix));
				team.addEntry(p.getName());
	            /*if (event.getBelowName() != null) {
	                Objective obj = board.getObjective(event.getBelowName());
	                if (obj == null)
	                    obj = board.registerNewObjective(event.getBelowName(), "dummy");
	                obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
	                obj.setDisplayName(event.getBelowName());
	                obj.getScore(p.getName()).setScore(event.getValue());
	            }*/
			}
		}
		player.setScoreboard(board);
	}

	public void loadNametags() {
		YamlConfiguration c = Core.getSettings().getNametagsConfig();
		this.nametags.clear();
		for (String s : c.getKeys(false)) {
			try {
				String displayName = c.getString(s + ".displayName");
				int price = c.getInt(s + ".price");
				this.nametags.add(new Nametag(s, displayName, price));
			} catch (Exception e) {
				Core.log("Error loading nametag: " + s);
				e.printStackTrace();
			}
		}
	}

	public List<Nametag> getNametags() {
		return this.nametags;
	}

	public List<Nametag> getNametags(User user) {
		List<Nametag> list = new ArrayList<>();
		List<Nametag> list2 = new ArrayList<>();
		for (Nametag tag : this.nametags)
			if (user.hasNametag(tag)) list.add(tag);
			else list2.add(tag);
		list.addAll(list2);
		return list;

	}

	public Nametag getNametag(String s) {
		for (Nametag t : this.nametags)
			if (t.getName().equalsIgnoreCase(s))
				return t;
		return null;
	}

	public Nametag getNametagFromDisplayName(String s) {
		for (Nametag t : this.nametags)
			if (t.getDisplayName().equalsIgnoreCase(s))
				return t;
		return null;
	}

}
