package net.grandtheftmc.vice.durability;

import net.grandtheftmc.vice.events.ArmorEquipEvent;
import net.grandtheftmc.vice.utils.DurabilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

/**
 * Created by ThatAbstractWolf on 2017-08-02.
 */
public class DurabilityListener implements Listener {

	@EventHandler
	public void onDurabilityDecrease(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.isCancelled()) return;

			int durabilityDecreaseAmount = 0;
			switch (event.getCause()) {
				case ENTITY_ATTACK:
				case ENTITY_EXPLOSION:
				case ENTITY_SWEEP_ATTACK:
				case BLOCK_EXPLOSION:
				case FALLING_BLOCK:
				case PROJECTILE:
				case LIGHTNING:
				case THORNS:
					durabilityDecreaseAmount = 1;
					break;

				default:
					break;
			}

			runDecreaseChecks(player, durabilityDecreaseAmount);
		}
	}

	@EventHandler
	public void onBlockDamage(EntityDamageByBlockEvent event) {

		if (event.getDamager() == null || event.getEntity() == null || event.getCause() == null) return;

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Block damager = event.getDamager();
			if (event.isCancelled()) return;

			int durabilityDecreaseAmount = 0;
			switch (damager.getType()) {
				case CACTUS:
				case LAVA:
				case STATIONARY_LAVA:
				case MAGMA:
				case FIRE:
					durabilityDecreaseAmount = 1;
					break;

				default:
					break;
			}

			runDecreaseChecks(player, durabilityDecreaseAmount);
		}
	}

	@EventHandler
	public void onArmourEquip(ArmorEquipEvent event) {
		if (event.getNewArmorPiece() != null) {
			ItemStack item = event.getNewArmorPiece();

			switch (item.getType()) {
				case CHAINMAIL_LEGGINGS:
				case IRON_LEGGINGS:
				case IRON_BOOTS:
				case DIAMOND_LEGGINGS:
				case DIAMOND_BOOTS:
				case GOLD_LEGGINGS:
				case GOLD_BOOTS:
					event.setCancelled(true);
					return;
			}

			if (DurabilityUtil.getDurability(item) == -1) {
				Optional<DurabilityItems> durabilityItems = DurabilityUtil.getDurabilityItem(item);
				if (!durabilityItems.isPresent()) return;

				if (item.getItemMeta() != null && !item.getItemMeta().isUnbreakable()) {
					ItemMeta itemMeta = item.getItemMeta();
					itemMeta.setUnbreakable(true);
					itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
					item.setItemMeta(itemMeta);
				}

				DurabilityItems durabilityItem = durabilityItems.get();
				DurabilityUtil.setDurabilityOnArmour(event.getPlayer(), item, durabilityItem.getMaximumDurability());
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if (player.getItemOnCursor() != null) {
			ItemStack item = player.getItemOnCursor();
			if (item != null ) {
				ItemStack itemStack = applyDurabilitySpecifics(item);
				if (itemStack != null) {
					player.setItemOnCursor(itemStack);
				}
			}
		}
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getContents()[i];
			if (item == null) continue;
			ItemStack itemStack = applyDurabilitySpecifics(item);
			if (itemStack == null) continue;
			player.getInventory().setItem(i, itemStack);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();
		if (item != null) {
			ItemStack itemStack = applyDurabilitySpecifics(item);
			if (itemStack != null) {
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					ItemStack check = player.getInventory().getContents()[i];
					if (check != null && check == item) {
						player.getInventory().setItem(i, itemStack);
						break;
					}
				}
			}
		}
	}

	private void runDecreaseChecks(Player player, int durabilityDecreaseAmount) {
		if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getItemMeta().getLore() != null && !hasDurable(player.getInventory().getHelmet()))
			DurabilityUtil.decreaseDurability(player, player.getInventory().getHelmet(), durabilityDecreaseAmount);
		if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getItemMeta().getLore() != null && !hasDurable(player.getInventory().getChestplate()))
			DurabilityUtil.decreaseDurability(player, player.getInventory().getChestplate(), durabilityDecreaseAmount);
		if (player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getItemMeta().getLore() != null && !hasDurable(player.getInventory().getLeggings()))
			DurabilityUtil.decreaseDurability(player, player.getInventory().getLeggings(), durabilityDecreaseAmount);
		if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getItemMeta().getLore() != null && !hasDurable(player.getInventory().getBoots()))
			DurabilityUtil.decreaseDurability(player, player.getInventory().getBoots(), durabilityDecreaseAmount);
	}

	private ItemStack applyDurabilitySpecifics(ItemStack item) {

		if (DurabilityUtil.getDurability(item) == -1) {
			Optional<DurabilityItems> durabilityItems = DurabilityUtil.getDurabilityItem(item);

			if (item == null || !durabilityItems.isPresent()) return null;

			if (item.getItemMeta() != null && !item.getItemMeta().isUnbreakable()) {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setUnbreakable(true);
				itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
				item.setItemMeta(itemMeta);
			}

			DurabilityItems durabilityItem = durabilityItems.get();
			ItemStack newArmour = DurabilityUtil.setDurability(item, durabilityItem.getMaximumDurability());
			DurabilityUtil.setDurabilityLore(newArmour, durabilityItem.getMaximumDurability(), durabilityItem);
			return newArmour;
		}

		return null;
	}

	private boolean hasDurable(ItemStack item) {

		if (item.getItemMeta().getLore() == null) return false;

		List<String> lore = item.getItemMeta().getLore();

		for (String lorePart : lore) {
			if (ChatColor.stripColor(lorePart).contains("Durable")) {
				return true;
			}
		}

		return false;
	}
}
