package net.grandtheftmc.core.announcer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.Utils;

public class Announcer implements Component<Announcer, Core> {

	private List<Announcement> announcements = new ArrayList<>();
	private int delay = 60;
	private boolean random = true;
	private String[] header = new String[] { "Header: error" };
	private String[] footer = new String[] { "Footer: error" };
	private int taskId = -1;
	private int id = -1;

	public Announcer() {
		this.loadAnnouncements();
		this.startSchedule();
	}

	@Override
	public Announcer onDisable(Core plugin) {
		this.announcements.clear();
		return this;
	}

	public void loadAnnouncements() {
		YamlConfiguration c = Core.getSettings().getAnnouncerConfig();
		this.announcements = new ArrayList<>();
		try {
			Core.getSettings().setUseAnnouncer(c.getBoolean("enable"));
			this.delay = c.getInt("delay");
			this.random = c.getBoolean("random");
			this.header = Utils.stringsToArray(c.getStringList("header"));
			this.footer = Utils.stringsToArray(c.getStringList("footer"));
			int i = 0;
			if (c.get("announcements") != null)
				for (String key : c.getConfigurationSection("announcements").getKeys(false)) {
					String[] array = Utils.stringsToArray(c.getStringList("announcements." + key));
					if (array != null) {
						this.announcements.add(new Announcement(i, array));
						i++;
					}
				}
		}
		catch (Exception e) {
			Core.log("An error has occured while launching the announcer: ");
			e.printStackTrace();
		}
	}

	public void saveAnnouncements(boolean shutdown) {
		YamlConfiguration c = Core.getSettings().getAnnouncerConfig();

		c.set("enable", Core.getSettings().useAnnouncer());
		c.set("delay", this.delay);
		c.set("random", this.random);
		c.set("header", this.header);
		c.set("footer", this.footer);
		int i = 0;
		if (c.getConfigurationSection("announcements") != null)
			for (String key : c.getConfigurationSection("announcements").getKeys(false))
				c.set(key, null);
		for (Announcement an : this.announcements) {
			c.set("announcements." + i, an.getLines());
			i++;
		}

		Utils.saveConfig(c, "announcer");
	}

	public void startSchedule() {
		if (!Core.getSettings().useAnnouncer())
			return;
		if (this.taskId != -1)
			Bukkit.getScheduler().cancelTask(this.taskId);

		this.taskId = new BukkitRunnable() {
			@Override
			public void run() {
				Announcer.this.broadcastAnnouncement();
			}

		}.runTaskTimer(Core.getInstance(), this.delay * 20L, this.delay * 20L).getTaskId();
	}

	private void broadcastAnnouncement() {
		Announcement an = this.pickAnnouncement();
		if (an == null)
			return;
		String[] header = Utils.f(this.header);
		String[] lines = Utils.fc(an.getLines());
		String[] footer = Utils.f(this.footer);
		for (Player player : Bukkit.getOnlinePlayers()) {

			User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
			if (user != null) {

				if (!user.getPref(Pref.ANNOUNCEMENTS) || user.isInTutorial())
					continue;
				if (header != null)
					player.sendMessage(header);
				player.sendMessage(lines);
				if (footer != null)
					player.sendMessage(footer);
			}
		}
	}

	private Announcement pickAnnouncement() {
		if (this.announcements.isEmpty())
			return null;
		if (this.random)
			return this.announcements.get(Utils.getRandom().nextInt(this.announcements.size()));
		this.id += 1;
		if (this.id >= this.announcements.size())
			this.id = 0;
		return this.announcements.get(this.id);
	}

	public Announcement addAnnouncement(String[] lines) {
		Announcement an = new Announcement(this.getUnusedId(), lines);
		this.announcements.add(an);
		return an;
	}

	public void removeAnnouncement(int id) {
		Announcement an = this.getAnnouncement(id);
		if (an != null)
			this.announcements.remove(id);
	}

	private int getUnusedId() {
		for (int i = 0;; i++)
			if (this.getAnnouncement(this.id) == null)
				return i;
	}

	public Announcement getAnnouncement(int id) {
		for (Announcement an : this.announcements)
			if (an.getId() == id)
				return an;
		return null;
	}

	public String[] getHeader() {
		return this.header;
	}

	public String[] getFooter() {
		return this.footer;
	}

	public List<Announcement> getAnnouncements() {
		return this.announcements;
	}

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
