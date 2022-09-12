package net.grandtheftmc.core.voting.crates.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.voting.crates.Crate;
import net.grandtheftmc.core.voting.crates.CrateReward;
import net.grandtheftmc.core.voting.crates.CrateStars;
import net.grandtheftmc.core.voting.crates.events.CrateOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CrateOpenListener implements Listener {

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        Crate crate = event.getCrate();
        CrateStars stars = crate.getCrateStars();
        Player player = event.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (crate.isBeingOpened()) {
            player.sendMessage(Lang.CRATES.f("&7This crate is already being opened!"));
            return;
        }
        if (user.getCrowbars() == -1) {
            user.setCrowbars(5);
            player.sendMessage(Lang.CRATES.f("&7You are using crates for the first time! You have been given &9&l5 Crowbars&7 as a welcome gift. Use them to buy five &6&l1 Star Crates&7 or one &a&l2 Star Crate&7! You can earn more crowbars by &e&lvoting&7!"));
            return;
        }
        player.openInventory(generateCratePreview(user, stars));
        Core.getUserManager().getLoadedUser(player.getUniqueId()).setSelectedCrate(crate);
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getClickedInventory();
        Crate crate = user.getSelectedCrate();
        if (inv == null || !ChatColor.stripColor(inv.getTitle()).contains(" Rewards")) {
            return;
        }

        event.setCancelled(true);

        if (crate == null) {
            user.setSelectedCrate(null);
            player.closeInventory();
            player.sendMessage(Lang.CRATES.f("&7Unable to find selected crate while in crate menu. Crate==" + crate + ". Report this error to an admin immediately."));
            return;
        }

        if (crate.isBeingOpened()) {
            user.setSelectedCrate(null);
            player.closeInventory();
            player.sendMessage(Lang.CRATES.f("&7This " + crate.getCrateStars().getType().toLowerCase() + " is already being opened!"));
            return;
        }

        if (item.getType() != Material.FLINT_AND_STEEL) return;

        if (!user.hasCrowbars(crate.getCrateStars().getCrowbars())) {
            user.setSelectedCrate(null);
            player.closeInventory();
            player.sendMessage(Lang.CRATES.f("&7You do not have enough crowbars to open this " + crate.getCrateStars().getType().toLowerCase() + "! You need &9&l" + crate.getCrateStars().getCrowbars() + " Crowbar" + (crate.getCrateStars().getCrowbars() == 1 ? "" : "s") + "&7!"));
            return;
        }

        if (crate.getCrateStars().getStars() >= 4) {
            MenuManager.openMenu(player, "confirmexpensivecrate");
            return;
        }

        player.closeInventory();
        user.takeCrowbars(crate.getCrateStars().getCrowbars());
        crate.startAnimation(player, user);
    }

    @EventHandler
    protected final void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() == null) return;
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND) return;
        if (!event.getRightClicked().hasMetadata("CRATE")) return;

        Core.getCrateManager().getCrate((LivingEntity) event.getRightClicked()).ifPresent(crate -> {
            CrateOpenEvent openEvent = new CrateOpenEvent(event.getPlayer(), crate);
            Bukkit.getPluginManager().callEvent(openEvent);
        });
    }

    private static int getDisplayCaseSize(CrateStars rank) {//TODO: use actual math to figure this out.
        int items = rank == null ? 63:Core.getCrateManager().getRewards(rank).size();
        if (items <= 7)
            return 27;
        if (items <= 14)
            return 36;
        if (items <= 21)
            return 45;
        if (items <= 28)
            return 54;
        return 63;
    }

    public static Inventory generateCratePreview(User user, CrateStars crateStars){
        List<CrateReward> rewards = Core.getCrateManager().getRewards(crateStars);
        int totalRows = getDisplayCaseSize(crateStars) / 9;
        int currentRewardIndex = 0;
        Inventory inv = Bukkit.createInventory(null, totalRows * 9, Utils.f("&e&l" + crateStars.getType() + " Rewards"));
        DecimalFormat df = new DecimalFormat("#.##");
        for (int row = 0; row < totalRows; row++) {
            for (int slot = 0; slot < 9; slot++) {
                if (row == totalRows - 1 && slot == 4) {
                    ItemStack is = Utils.addItemFlags(Utils.createItem(Material.FLINT_AND_STEEL, 45,
                            Utils.f("&9&lCosts " + crateStars.getCrowbars() + " Crowbar" + (crateStars.getCrowbars()==1 ? "" : "s")), user.getCrowbars() > 64 ? 64 : user.getCrowbars()
                            , "&7You have &a&l" + user.getCrowbars() + " &7Crowbar" + (crateStars.getCrowbars() == 1 ? "" : "s"), "",
                            "&7Click to open the " + crateStars.getDisplayName() + "&7!"));
                    ItemMeta im = is.getItemMeta();
                    im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                    is.setItemMeta(im);
                    inv.setItem(row * 9 + 4, is);
                } else if (row == 0 || row == totalRows - 1 || slot == 0 || slot == 8) {
                    inv.setItem(row * 9 + slot, Utils.createItem(Material.STAINED_GLASS_PANE, 7, " "));
                } else if (currentRewardIndex < rewards.size()) {
                    CrateReward reward = rewards.get(currentRewardIndex);
                    ItemStack is = rewards.get(currentRewardIndex).getItem().clone();
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(Utils.f(reward.getDisplayName()));
                    List<String> lore = new ArrayList<>();
                    lore.add(Utils.f("&7Chance: &a&l" + df.format(reward.getWeight() / Core.getCrateManager().getTotalWeight(crateStars) * 100) + "&a%"));
                    if (reward.getRewardPack().get().size() > 1) {
                        lore.add("");
                        lore.add(Utils.f("&7Contains:"));
                        reward.getRewardPack().get().forEach(item -> lore.add(Utils.f(item.getDisplayName())));
                    }
                    if (reward.getRewardPack().getDescription() != null && !reward.getRewardPack().getDescription().isEmpty()) {
                        lore.add("");
                        lore.add(Utils.f("&7" + reward.getRewardPack().getDescription()));
                    }
                    im.setLore(lore);
                    is.setItemMeta(im);
                    inv.setItem(row * 9 + slot, is);
                    currentRewardIndex += 1;
                }
            }
        }
        return inv;
    }
}
