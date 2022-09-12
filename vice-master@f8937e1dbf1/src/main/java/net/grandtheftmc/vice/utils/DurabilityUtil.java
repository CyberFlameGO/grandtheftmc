package net.grandtheftmc.vice.utils;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.durability.DurabilityItems;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by ThatAbstractWolf on 2017-08-03.
 */
public class DurabilityUtil {

	public static ItemStack setDurability(ItemStack item, int durability) {
		return NBTUtil.setNBTTag(item, "customDurability", new NBTTagInt(durability));
	}

	public static int getDurability(ItemStack item) {
		return (NBTUtil.hasNBTTag(item, "customDurability") ? Integer.parseInt(NBTUtil.getNBTTag(item, "customDurability").toString()) : -1);
	}

	public static void setDurabilityOnArmour(Player player, ItemStack item, int amount) {

		if (item == null || item.getType().equals(Material.AIR)) {
			return;
		}

		if (getDurabilityItem(item).isPresent()) {

			DurabilityItems durabilityItems = getDurabilityItem(item).get();

			int armourSlot = getArmourSlot(item);

			if (amount > 0) {

				ItemStack newArmour = DurabilityUtil.setDurability(item, amount);

				if (newArmour != null) {

					setDurabilityLore(newArmour, amount, durabilityItems);

					player.getInventory().remove(item);

					switch (armourSlot) {

						case 0:
							player.getInventory().setBoots(newArmour);
							break;
						case 1:
							player.getInventory().setLeggings(newArmour);
							break;
						case 2:
							player.getInventory().setChestplate(newArmour);
							break;
						case 3:
							player.getInventory().setHelmet(newArmour);
							break;
					}
				}
			} else {

				switch (armourSlot) {

					case 0:
						player.getInventory().setBoots(null);
						break;
					case 1:
						player.getInventory().setLeggings(null);
						break;
					case 2:
						player.getInventory().setChestplate(null);
						break;
					case 3:
						player.getInventory().setHelmet(null);
						break;
				}
			}
		}
	}

	public static void setDurabilityLore(ItemStack item, int amount, DurabilityItems durabilityItems) {

		ItemMeta durabilityMeta = item.getItemMeta();

		List<String> lore = new ArrayList<>();
		List<String> cachedLore = (item.getItemMeta().getLore() == null ? new ArrayList<>() : item.getItemMeta().getLore());

		if (durabilityMeta.getLore() != null) {
			durabilityMeta.getLore().clear();
		}

		lore.add(ChatColor.translateAlternateColorCodes('&', getDurabilityPercentage(amount, durabilityItems.getMaximumDurability()) + "&f &7[&a" + amount + "&7/&a" + durabilityItems.getMaximumDurability() + "&7]"));

		try {
			for (int i = (cachedLore.get(0).contains("-") ? 1 : 0); i < cachedLore.size(); i++) {
				lore.add(ChatColor.translateAlternateColorCodes('&', cachedLore.get(i)));
			}
		} catch (IndexOutOfBoundsException e) { /* Ignored */ }

		durabilityMeta.setLore(lore);
		item.setItemMeta(durabilityMeta);
	}

	public static void decreaseDurability(Player player, ItemStack item, int amount) {

		if (item == null) {
			return;
		}

		Optional<DurabilityItems> items = getDurabilityItem(item);

		if (items.isPresent()) {

			if (DurabilityUtil.getDurability(item) == -1) {
				setDurabilityOnArmour(player, item, items.get().getMaximumDurability());
			} else {
				int newDuration = DurabilityUtil.getDurability(item) - amount;
				setDurabilityOnArmour(player, item, newDuration);
			}
		}
	}

	private static int getArmourSlot(ItemStack item) {

		if (item.getType().name().contains("HELMET")) {
			return 3;
		} else if (item.getType().name().contains("CHESTPLATE")) {
			return 2;
		} else if (item.getType().name().contains("LEGGINGS")) {
			return 1;
		} else if (item.getType().name().contains("BOOTS")) {
			return 0;
		}

		return -1;
	}

	public static Optional<DurabilityItems> getDurabilityItem(ItemStack item) {

		for (DurabilityItems items : DurabilityItems.values()) {

			if (item != null && item.getType().equals(DurabilityItems.JETPACK.getMaterial()) && item.getItemMeta() != null && items.getDisplayName() != null && item.getItemMeta().getDisplayName().contains(items.getDisplayName())) {
				return Optional.of(DurabilityItems.JETPACK);
			}

			if (items.getMaterial().equals(item.getType())) {
				return Optional.of(items);
			}
		}

		return Optional.empty();
	}

	private static String getDurabilityPercentage(int currentDurability, int maxDurability) {

		int length = 18;
		String bar = "";

		for(int x = 1; x < (length + 1); x++) {
			if (x * (maxDurability / length) <= currentDurability)
				bar += getColour(currentDurability, maxDurability).toString() + ChatColor.STRIKETHROUGH + "-";
			else
				bar += ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-";
		}

		return bar;
	}

	private static ChatColor getColour(int currentDurability, int maxDurability) {

		int parts = (maxDurability / 3);

		if (currentDurability < parts) {
			return ChatColor.RED;
		} else if (currentDurability > parts && currentDurability < (parts * 2)) {
			return ChatColor.YELLOW;
		} else {
			return ChatColor.GREEN;
		}
	}
}
