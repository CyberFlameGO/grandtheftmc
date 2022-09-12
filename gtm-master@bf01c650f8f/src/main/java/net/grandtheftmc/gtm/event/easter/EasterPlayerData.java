package net.grandtheftmc.gtm.event.easter;

import com.google.common.collect.Lists;
import de.slikey.effectlib.util.ParticleEffect;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class EasterPlayerData {

	private final Set<Integer> foundEggs;
	private final Plugin plugin;
	private final EasterEvent event;

	protected EasterPlayerData(Plugin plugin, EasterEvent event, Set<Integer> foundEggs) {
		this.event = event;
		this.plugin = plugin;
		this.foundEggs = foundEggs;
	}

	protected int getEggsFounds() {
		return this.foundEggs.size();
	}

	protected boolean hasFoundAllEggs() {
		return this.foundEggs.size() >= this.event.eggs.size();
	}

	protected boolean hasFoundEgg(int id) {
		return this.foundEggs.contains(id);
	}

	/**
	 * This method is ran when a player finds an Easter Egg.
	 *
	 * @param easterEgg - The Easter Egg object
	 */
	protected void find(Player player, EasterEgg easterEgg) {

		//If the 'foundEggs' list contains this egg, do nothing.
		if (this.foundEggs.contains(easterEgg.getUniqueIdentifier()))
			return;

		this.foundEggs.add(easterEgg.getUniqueIdentifier());

		ServerUtil.runTaskAsync(() -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				EasterDAO.addUserFind(connection, player.getUniqueId(), easterEgg.getUniqueIdentifier());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		boolean foundAll = this.foundEggs.size() == event.eggs.size();
		EasterFoundEggEvent foundEggEvent = new EasterFoundEggEvent(player, easterEgg, foundAll);
		Bukkit.getPluginManager().callEvent(foundEggEvent);
	}

	protected void startAnimation(Player player, EasterEgg easterEgg) {
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 1f, 1f);

		if (easterEgg.getArmorStand() == null) return;

		player.playSound(easterEgg.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
		ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 1, 15, easterEgg.getArmorStand().getEyeLocation(), player);
		ParticleEffect.SMOKE_LARGE.display(0.3f, 0.3f, 0.3f, 1, 15, easterEgg.getArmorStand().getEyeLocation(), player);

		//After animation,
		easterEgg.destroyFor(player);
	}

	private List<ItemStack> getRandomItems() {
		List<ItemStack> items = Lists.newArrayList();
		int a = ThreadLocalRandom.current().nextInt(8);
		for (int i = 0; i < a; i++) items.add(new ItemStack(Material.EGG));
		return items;
	}
}
