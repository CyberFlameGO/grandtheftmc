package net.grandtheftmc.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class ConfirmationMenu implements Listener {

	/** The owning plugin */
	private Plugin plugin;
	/** The item that represents what the confirmation is about */
	private ItemStack infoItem;
	/** The item that represents the confirm item */
	private ItemStack confirmItem;
	/** The item that represents the deny item */
	private ItemStack denyItem;
	/** The GUI involved */
	private Inventory gui;

	/** Whether or not this inventory has been destroyed */
	private boolean destroyed;

	/**
	 * Create a generic confirmation menu, where the handling of the
	 * confirm/deny item can be per project specific.
	 * <p>
	 * Note: Typically you would want to override
	 * {@link #onConfirm(InventoryClickEvent, Player)} and
	 * {@link #onDeny(InventoryClickEvent, Player)}.
	 * <p>
	 * 
	 * @param plugin - the owning plugin
	 * @param infoItem - the item that holds the info
	 * @param confirmItem - the confirm item
	 * @param denyItem - the deny item
	 */
	public ConfirmationMenu(Plugin plugin, ItemStack infoItem, ItemStack confirmItem, ItemStack denyItem) {
		this.plugin = plugin;
		this.infoItem = infoItem;
		this.confirmItem = confirmItem;
		this.denyItem = denyItem;
		this.gui = createGUI();

		// register this as an event
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Create a generic confirmation menu, where the handling of the
	 * confirm/deny item can be per project specific.
	 * <p>
	 * Note: Typically you would want to override
	 * {@link #onConfirm(InventoryClickEvent, Player)} and
	 * {@link #onDeny(InventoryClickEvent, Player)}.
	 * <p>
	 * Wrapper around
	 * {@link #ConfirmationMenu(Plugin, ItemStack, ItemStack, ItemStack)}.
	 * 
	 * @param plugin - the owning plugin
	 * @param infoItem - the item that holds the info
	 */
	public ConfirmationMenu(Plugin plugin, ItemStack infoItem) {
		this(plugin, infoItem, getYesItem(), getNoItem());
	}

	/**
	 * Opens the menu for the specified player.
	 * 
	 * @param p - the player viewing the menu.
	 */
	public void open(Player p) {

		if (gui != null) {
			p.openInventory(gui);

			// optional call-back
			onOpen();
		}
	}

	/**
	 * Close the confirmation menu.
	 */
	public void close() {
		if (gui != null) {

			List<HumanEntity> viewers = new ArrayList<>();
			viewers.addAll(gui.getViewers());

			// close all the viewers
			viewers.forEach(v -> v.closeInventory());
		}

		// optional call-back
		onClose();

		if (!destroyed) {

			// destroy the menu
			destroy();
		}
	}

	/**
	 * Destroy this menu, marking it for garbage collection, and unregistering
	 * it as a Listener.
	 */
	private void destroy() {

		// optional call-back
		onDestroy();

		plugin = null;
		infoItem = null;
		confirmItem = null;
		denyItem = null;
		gui = null;

		// unregister the event
		HandlerList.unregisterAll(this);

		destroyed = true;
	}

	/**
	 * Optional call-back to be filled by superclass. This is called when the
	 * menu is opened.
	 */
	protected void onOpen() {
	}

	/**
	 * Optional call-back to be filled by superclass. This is called when the
	 * menu is closed.
	 */
	protected void onClose() {
	}

	/**
	 * Optional call-back to be filled by superclass. This is called when the
	 * menu is destroyed.
	 */
	protected void onDestroy() {
	}

	/**
	 * Optional call-back to be filled by superclass. This is called when the
	 * confirm button is clicked by the user.
	 * 
	 * @param e - the click event
	 * @param p - the player who clicked
	 */
	protected void onConfirm(InventoryClickEvent e, Player p) {
	}

	/**
	 * Optional call-back to be filled by superclass. This is called when the
	 * deny button is clicked by the user.
	 * 
	 * @param e - the click event
	 * @param p - the player who clicked
	 */
	protected void onDeny(InventoryClickEvent e, Player p) {
	}

	/**
	 * Creates the GUI for the confirmation menu.
	 * 
	 * @return The GUI that was created for this menu.
	 */
	private Inventory createGUI() {

		Inventory gui = plugin.getServer().createInventory(null, 9 * 3, "          " + ChatColor.BOLD + ChatColor.UNDERLINE + "Are you sure?");

		// page item
		gui.setItem(4, infoItem);

		// set yes items
		gui.setItem(10, confirmItem);
		gui.setItem(11, confirmItem);
		gui.setItem(12, confirmItem);

		// set no items
		gui.setItem(14, denyItem);
		gui.setItem(15, denyItem);
		gui.setItem(16, denyItem);

		for (int i = 0; i < gui.getSize(); i++) {

			// populate whitespace
			if (gui.getItem(i) == null) {
				gui.setItem(i, getMenuBorder());
			}
		}

		return gui;
	}

	/**
	 * Handles the confirmation menu interaction.
	 * 
	 * @param e - the inventory click event
	 * @param p - the player involved in the event
	 * 
	 * @return {@code true} if the menu should be closed, {@code false}
	 *         otherwise.
	 */
	private boolean handleConfirmation(InventoryClickEvent e, Player p) {

		ItemStack is = e.getCurrentItem();

		if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {

			if (is.isSimilar(confirmItem)) {
				onConfirm(e, p);
				return true;
			}
			else if (is.isSimilar(denyItem)) {
				onDeny(e, p);
				return true;
			}
		}

		return false;
	}

	/**
	 * Listens in on inventory clicks.
	 * 
	 * @param e - the event
	 */
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inven = e.getInventory();

		if (inven.equals(gui)) {
			e.setCancelled(true);

			if (e.isLeftClick() || e.isRightClick() || e.isShiftClick()) {
				if (e.getCurrentItem() != null && e.getRawSlot() < inven.getSize()) {

					boolean success = handleConfirmation(e, p);
					if (success) {

						// close the inventory
						close();
					}
				}
			}
		}
	}
	
	/**
	 * Get the itemstack representation for a generic YES item.
	 * 
	 * @return The itemstack representation for a generic YES item.
	 */
	private static ItemStack getYesItem(){
		String displayName = ChatColor.GREEN + "Yes";
		
		ItemStack is = new ItemStack(Material.WOOL, 1, (short) 13);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		
		return is;
	}
	
	/**
	 * Get the itemstack representation for a generic NO item.
	 * 
	 * @return The itemstack representation for a generic NO item.
	 */
	private static ItemStack getNoItem(){
		String displayName = ChatColor.RED + "No";
		
		ItemStack is = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		
		return is;
	}
	
	/**
	 * Get the generic representation of a menu border.
	 * 
	 * @return The menu border for this menu.
	 */
	private static ItemStack getMenuBorder() {
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("");
		is.setItemMeta(im);
		
		return is;
	}
}

