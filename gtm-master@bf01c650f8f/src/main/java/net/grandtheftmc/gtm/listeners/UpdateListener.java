package net.grandtheftmc.gtm.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.ChatEvent;
import net.grandtheftmc.core.events.DisplayNameUpdateEvent;
import net.grandtheftmc.core.events.GetPermsEvent;
import net.grandtheftmc.core.events.MoneyEvent;
import net.grandtheftmc.core.events.NametagUpdateEvent;
import net.grandtheftmc.core.events.RewardEvent;
import net.grandtheftmc.core.events.ServerSaveEvent;
import net.grandtheftmc.core.events.TutorialEvent;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.Reward.RewardType;
import net.grandtheftmc.core.voting.events.PlayerVoteEvent;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.events.TPEvent;
import net.grandtheftmc.gtm.events.WantedLevelChangeEvent;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.Head;
import net.grandtheftmc.gtm.items.ItemManager;
import net.grandtheftmc.gtm.items.Kit;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.utils.Stats;
import net.grandtheftmc.gtm.weapon.melee.Dildo;
import net.grandtheftmc.gtm.weapon.ranged.special.GoldMinigun;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class UpdateListener implements Listener {
	private final Map<UUID, Integer> voteCounts = new HashMap<>();

	@EventHandler
	public void voteEvent(PlayerVoteEvent event) {
		
		// grab event variables 
		UUID uuid = event.getUUID();
		
		// grab the player object
		Player player = Bukkit.getPlayer(uuid);
		if (player == null){
			return;
		}
		
		// add to vote counter
		this.voteCounts.put(uuid, this.voteCounts.getOrDefault(uuid, 0) + 1);
		
		// grab user objects
		GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
		User user = Core.getUserManager().getLoadedUser(uuid);
		
		Kit kit = GTM.getItemManager().getKit("vote");
		
		// currently only 4 active vote sites
		if (this.voteCounts.get(uuid) == 4) {
			player.sendMessage(Lang.VOTE.f("&7Thank you for voting on all 4 sites! Here is a special vote kit!"));
			GTM.getItemManager().giveKitItems(player, gtmUser, kit);
			user.addCrowbars(1);
		}
	}

	@EventHandler
	public void tpEvent(TPEvent event) {
		if (event.getType() == TPEvent.TPType.HOUSE_ENTER || event.getType() == TPEvent.TPType.PREMIUM_HOUSE_ENTER) {
			Player player = event.getPlayer();
			GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
			if (gtmUser!= null && gtmUser.isInCombat()) {
				event.setCancelled(Utils.f("&7You cannot enter a house while in combat!"));
			}
		}
	}

	@EventHandler
	public void serverSaveEvent(ServerSaveEvent event) {
		GTM.getShopManager().getHeads().forEach(Head::update);
		Collection<GTMUser> gtmUsers = new ArrayList<>(GTMUserManager.getInstance().getUsers());
		gtmUsers.forEach(gtmUser -> {
			if (gtmUser == null)
				return;
			gtmUser.checkAchievements();
		});
	}

	@EventHandler
	public void chatEvent(ChatEvent event) {
		Player player = event.getSender();
		TextComponent message = event.getTextComponent();
		List<String> hover = Stats.getInstance().getStats(player);
		String url = "";
		for (String string : message.getText().split(" ")) {
			if (GTMUtils.isValidURL(string)) {
				url = string;
				break;
			}
			if (GTMUtils.getUser(player).isRank(UserRank.VIP) && (string.equalsIgnoreCase(":hand:") || string.equalsIgnoreCase(":item:"))) {
				if (player.getInventory().getItemInMainHand() == null)
					continue;
				ItemStack hand = player.getInventory().getItemInMainHand();
				String json = GTMUtils.convertItemStackToJson(hand);
				if (json == null)
					continue;
				if (!hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) {
					ItemMeta meta = hand.getItemMeta();
					meta.setDisplayName(hand.getType().name().replace("_", " "));
					hand.setItemMeta(meta);
				}
				message.setText(message.getText().replace(string, hand.getItemMeta().getDisplayName() + message.getColor()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(json)
				}));
				break;
			}
		}
		if (url.isEmpty()) {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + ' '));
		}
		else {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		}
		if (message.getHoverEvent() == null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringUtils.join(hover, "\n")).create()));
		}
		event.setTextComponent(message);
	}

	@EventHandler
	public void onDisplayNameUpdate(DisplayNameUpdateEvent event) {

		// grab event variables
		Player player = event.getPlayer();
		GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);

		if (gtmUser != null) {
			event.setPrefix(gtmUser.getJobMode() == JobMode.CRIMINAL ? gtmUser.getRank().getColoredNameBold() : gtmUser.getJobMode().getColoredNameBold());
		}
	}

	@EventHandler
	public void onGetPerms(GetPermsEvent event) {

		// grab event variables
		UUID uuid = event.getUUID();

		GTMUser gtmUser = GTMUserManager.getInstance().getUser(uuid).orElse(null);
		if (gtmUser != null && gtmUser.getRank() != null) {
			gtmUser.getRank().getAllPerms().forEach(event::addPerm);
		}
	}

	@EventHandler
	public void onNametagChange(NametagUpdateEvent event) {

		// grab event variables
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}

		GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);

		if (gtmUser != null) {

			User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
			JobMode mode = gtmUser.getJobMode();

			if (gtmUser.getWantedLevel() == 0) {
				if (mode != JobMode.CRIMINAL) {
					event.setSuffix(mode.getColoredNameBold());
				}
				else {
					if (user.getEquipedTag() != null) {
						event.setSuffix(user.getEquipedTag().getBoldName());
					}
					else {
						event.setSuffix(gtmUser.getRank().getColoredNameBold());
					}
				}
			}
			else {
				event.setSuffix(GTMUtils.getWantedLevelStars(gtmUser.getWantedLevel()));
			}
		}
	}

	@EventHandler
	public void onTutorialEvent(TutorialEvent e) {
		Player player = e.getPlayer();
		switch (e.getType()) {
			case PRE_START:
				GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
				if (user.isInCombat()) {
					e.setCancelled("&7You can't join tutorials in combat!");
					return;
				}
				if (user.isArrested()) {
					e.setCancelled("&7You can't join tutorials in jail!");
					return;
				}
				if (!Objects.equals("spawn", player.getWorld().getName())) {
					e.setCancelled("&7You can only join tutorials at spawn!");
					return;
				}
				break;
			case START:
				GTMUtils.removeBoard(player);
				break;
			default:
				break;
		}

	}

	@EventHandler
	public void onReward(RewardEvent e) {

		Player player = e.getPlayer();
		Reward reward = e.getReward();

		if (reward.getCustomType() != null)
			switch (reward.getCustomType()) {
				case "drug": {
					Optional<Drug> drug = ((DrugService) GTM.getDrugManager().getService()).getDrug(reward.getCustomName());
					if (drug.isPresent()) {
						DrugItem item = DrugItem.getByDrug(drug.get());
						if (item != null) {
							Utils.giveItems(player, item.getItemStack());
						}
						else {
							player.sendMessage(Lang.GTM + "" + ChatColor.RED + "Unable to give you the drug " + reward.getCustomName() + ", couldn't find it.");
						}
					}
					return;
				}
				case "item": {
					String[] a = reward.getCustomName().split(":");
					GameItem item = GTM.getItemManager().getItem(a[0]);
					if (item == null)
						return;
					ItemStack stack = item.getItem();
					if (a.length > 1)
						try {
							stack.setAmount(Integer.parseInt(a[1]));
						}
						catch (NumberFormatException ignored) {
						}
					if (Utils.giveItems(player, stack))
						player.sendMessage(Utils.f(Lang.VOTE + "&7Your inventory was full so the item was dropped on the ground!"));
					e.setSuccessfull(true);
					return;
				}
				case "items":
					ItemManager im = GTM.getItemManager();
					boolean successfull = true;
					List<ItemStack> items = new ArrayList<>();
					for (String s : reward.getCustomList()) {
						String[] a = s.split(":");
						GameItem item = im.getItem(a[0]);
						if (item == null) {
							successfull = false;
							continue;
						}
						ItemStack stack = item.getItem();
						if (a.length > 1)
							try {
								stack.setAmount(Integer.parseInt(a[1]));
							}
							catch (NumberFormatException ignored) {
							}
						items.add(stack);
					}
					if (Utils.giveItems(player, Utils.toArray(items)))
						player.sendMessage(Utils.f(Lang.VOTE + "&7Your inventory was full so some items were dropped on the ground!"));
					e.setSuccessfull(successfull);
					return;
				case "kit":
					Kit kit = GTM.getItemManager().getKit(reward.getCustomName());
					if (kit != null) {
						GTM.getItemManager().giveKitItems(player, GTM.getUserManager().getLoadedUser(player.getUniqueId()), kit);
						e.setSuccessfull(true);
					}
					return;
				case "permits":
					GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
					user.addPermits((int) reward.getAmount());
					e.setSuccessfull(true);
					return;
				default:
					break;
			}

		if (reward.getType() == RewardType.WEAPON) {

			Weapon weapon = GTMGuns.getInstance().getWeaponManager().getWeapon(reward.getName()).orElse(null);
			if (weapon != null) {
				int stars = reward.getStars();
				WeaponSkin skin = null;

				// clamp bounds
				if (stars <= 0) {
					stars = 1;
				}
				if (stars > GTMGuns.MAX_STARS) {
					stars = GTMGuns.MAX_STARS;
				}

				if (GTMGuns.STAR_SYSTEM) {
					Utils.giveItems(player, weapon.createItemStack(stars, skin));
				}
				else {
					Utils.giveItems(player, weapon.createItemStack());
				}

				e.setSuccessfull(true);
			}
		}
		else if (reward.getType() == RewardType.SKIN) {
			Random random = new Random();

			List<Weapon<?>> weapons = new ArrayList<Weapon<?>>(GTMGuns.getInstance().getWeaponManager().getRegisteredWeapons().stream().filter(weapon -> !(weapon instanceof Dildo) && !(weapon instanceof GoldMinigun)).collect(Collectors.toList()));

			Weapon<?> randomWeapon = weapons.get(random.nextInt(weapons.size()));
			WeaponSkin randomSkin = null;

			if (randomWeapon != null && randomWeapon.getWeaponSkins() != null && randomWeapon.getWeaponSkins().length > 1) {
				short[] commonSkins = { 5, 7
				};

				short[] rareSkins = { 2, 6
				};

//                short[] epicSkins = {};
//                short[] legendarySkins = {};

				if (reward.getCustomName().equals("weapon_skin_common")) {
					randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, commonSkins[random.nextInt(commonSkins.length)]);
				}
				else if (reward.getCustomName().equals("weapon_skin_rare")) {
					randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, rareSkins[random.nextInt(rareSkins.length)]);
				}
//                else if (reward.getName().equals("weapon_skin_epic")) {
//                    randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, epicSkins[random.nextInt(rareSkins.length)]);
//                }
//                else if (reward.getName().equals("weapon_skin_legendary")) {
//                    randomSkin = GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(randomWeapon, legendarySkins[random.nextInt(rareSkins.length)]);
//                }

				if (randomWeapon == null) {
					return;
				}

				if (randomSkin == null) {
					return;
				}

				Utils.giveItems(player, GTM.getWeaponSkinManager().createSkinItem(randomWeapon, randomSkin));
				e.setSuccessfull(true);
			}
			else {
				this.onReward(e);
			}
		}
	}

	@EventHandler
	public void onMoneyEvent(MoneyEvent e) {
		GTMUser user = GTMUserManager.getInstance().getUser(e.getUUID()).orElse(null);
		if (user != null){
			switch (e.getType()) {
				case ADD:
					user.addMoney(e.getAmount());
					e.setSuccessfull();
					break;
				case BALANCE:
					e.setBalance(user.getMoney());
					break;
				case TAKE:
					user.takeMoney(e.getAmount());
					e.setSuccessfull();
					break;
			}
		}

		GTMUtils.updateBoard(Bukkit.getPlayer(e.getUUID()), user);
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		switch (e.getReason()) {
			case BOARD:
			case MONEY:
			case OTHER:
			case RANK:
				
				GTMUser gtmUser = GTMUserManager.getInstance().getUser(e.getPlayer().getUniqueId()).orElse(null);
				if (gtmUser != null){
					GTMUtils.updateBoard(e.getPlayer(), gtmUser);
				}
				break;
			case PREF:
				switch (e.getPref()) {
					case USE_SCOREBOARD:
						gtmUser = GTMUserManager.getInstance().getUser(e.getPlayer().getUniqueId()).orElse(null);
						if (gtmUser != null){
							GTMUtils.updateBoard(e.getPlayer(), gtmUser);
						}
						break;
					case TINT_HEALTH:
						gtmUser = GTMUserManager.getInstance().getUser(e.getPlayer().getUniqueId()).orElse(null);
						if (gtmUser != null){
							User coreUser = UserManager.getInstance().getUser(e.getPlayer().getUniqueId()).orElse(null);
							if (coreUser != null){
								gtmUser.updateTintHealth(e.getPlayer(), coreUser);
							}
						}
						break;
					default:
						break;
				}
			default:
				break;
		}
	}

	@EventHandler
	public void onWantedLevelChange(WantedLevelChangeEvent e) {
		GTMUtils.updateBoard(e.getPlayer(), e.getUser());
		NametagManager.updateNametag(e.getPlayer());
	}
}
