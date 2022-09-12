package net.grandtheftmc.core.alert.ui;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.alert.Alert;
import net.grandtheftmc.core.alert.AlertCreateStage;
import net.grandtheftmc.core.alert.AlertEntry;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.alert.component.AlertCreateHandler;
import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Luke Bingham on 11/09/2017.
 */
public class AlertCreationMenu extends CoreMenu {

    private final AlertManager alertManager;
    private final Player user;

    private boolean newEntry = true;

    private String name = "none", imageUrl = "none";
    private AlertShowType showType = AlertShowType.ONCE;
    private AlertType alertType = AlertType.NEWS;
    private long startTime = -1, expireTime = -1;
    private boolean disabled = true;
    private String link = "none", playerName = "none", description = "none";

    private AlertCreateHandler handler = null;

    /**
     * Construct a new Menu.
     */
    public AlertCreationMenu(AlertManager alertManager, Player player) {
        super(
                6,
                "Alert Setup Wizard",
                CoreMenuFlag.RESET_CURSOR_ON_OPEN
        );
        this.alertManager = alertManager;
        this.user = player;

        this.playerName = player.getName();
        this.startTime = System.currentTimeMillis();
        this.expireTime = System.currentTimeMillis();
        refresh();
    }

    /**
     * Construct a new Menu.
     */
    public AlertCreationMenu(AlertManager alertManager, String name, String imageUrl, String link, String description, AlertShowType showType, AlertType type, long startTime, long expireTime, boolean disabled, Player player) {
        super(
                6,
                "Alert Setup Wizard",
                CoreMenuFlag.RESET_CURSOR_ON_OPEN
        );
        this.alertManager = alertManager;
        this.user = player;
        this.name = name;
        this.imageUrl = imageUrl;
        this.link = link;
        this.description = description;
        this.showType = showType;
        this.alertType = type;
        this.startTime = startTime;
        this.expireTime = expireTime;
        this.disabled = disabled;

        this.newEntry = false;

        this.playerName = player.getName();
        this.startTime = System.currentTimeMillis();
        this.expireTime = System.currentTimeMillis();
        refresh();
    }

    public void refresh() {
        if(this.handler != null) {
            HandlerList.unregisterAll(this.handler);
            this.handler = null;
        }

        addItem(new ClickableItem(10, new ItemFactory(Material.NAME_TAG).setName("Name").setLore(C.GREEN + this.name).build(), (player, clickType) -> {
            player.closeInventory();
            player.sendMessage(Lang.ALERTS.f("Enter a name for the Alert."));
            Bukkit.getPluginManager().registerEvents(this.handler = new AlertCreateHandler(player.getName(), AlertCreateStage.NAME, this), Core.getInstance());
        }));

        addItem(new ClickableItem(12, new ItemFactory(Material.BOOK).setName("Description").setLore(getDescription()).build(), (player, clickType) -> {
            player.closeInventory();
            player.sendMessage(Lang.ALERTS.f("Enter a description for the Alert. (split with '~ln')"));
            Bukkit.getPluginManager().registerEvents(this.handler = new AlertCreateHandler(player.getName(), AlertCreateStage.DESC, this), Core.getInstance());
        }));

        addItem(new ClickableItem(14, new ItemFactory(Material.PAINTING).setName("Image URL").setLore(C.GREEN + this.imageUrl).build(), (player, clickType) -> {
            player.closeInventory();
            player.sendMessage(Lang.ALERTS.f("Enter a Image URL for the Alert."));
            Bukkit.getPluginManager().registerEvents(this.handler = new AlertCreateHandler(player.getName(), AlertCreateStage.IMAGE, this), Core.getInstance());
        }));

        addItem(new ClickableItem(16, new ItemFactory(Material.COMPASS).setName("Redirect Link").setLore(C.GREEN + this.link).build(), (player, clickType) -> {
            player.closeInventory();
            player.sendMessage(Lang.ALERTS.f("Enter a redirect link for the Alert."));
            Bukkit.getPluginManager().registerEvents(this.handler = new AlertCreateHandler(player.getName(), AlertCreateStage.LINK, this), Core.getInstance());
        }));

        addItem(new ClickableItem(20, new ItemFactory(Material.SIGN).setName("Alert Type").setLore(C.GREEN + this.alertType.name(), " ", C.GRAY + "Click to change").build(), (player, clickType) -> {
            this.alertType = this.alertType.next();
            refresh();
        }));

        addItem(new ClickableItem(22, new ItemFactory(this.showType == AlertShowType.ONCE ? Material.REDSTONE : Material.REDSTONE_COMPARATOR).setName("Show Type").setLore(C.GREEN + this.showType.name(), " ", C.GRAY + "Click to change").build(), (player, clickType) -> {
            this.showType = this.showType.next();
            refresh();
        }));

        addItem(new ClickableItem(24, new ItemFactory(this.disabled ? Material.MAGMA_CREAM : Material.SLIME_BALL).setName("Disabled").setLore("" + (this.disabled ? C.RED : C.GREEN) + this.disabled, " ", C.GRAY + "Click to change").build(), (player, clickType) -> {
            this.disabled = !this.disabled;
            refresh();
        }));

        addItem(new ClickableItem(38, new ItemFactory(Material.WATCH).setName("Start").setLore(C.GRAY + new Timestamp(this.startTime).toString()).build(), (player, clickType) -> {
            new AlertTimeEditor(this, false, user);
        }));

        addItem(new ClickableItem(42, new ItemFactory(Material.WATCH).setName("Expire").setLore(C.GRAY + new Timestamp(this.expireTime).toString()).build(), (player, clickType) -> {
            new AlertTimeEditor(this, true, user);
        }));

        addItem(new ClickableItem(49, createAlertItem().build(), (player, clickType) -> refresh()));

        if(!name.equals("none") && !imageUrl.equals("none") && !link.equals("none") && !playerName.equals("none") && !description.equals("none")) {
            if (newEntry) {
                addItem(new ClickableItem(53, new ItemFactory(Material.ARROW).setName(C.GREEN + "Finish").build(), (player, clickType) -> {
                    long s = System.currentTimeMillis();
                    AlertEntry alert = new AlertEntry(name, imageUrl, showType, alertType, link, new Timestamp(this.startTime), new Timestamp(this.expireTime), disabled);
                    alert.setDescription(description);
                    alert.setPlayer(playerName);
                    player.closeInventory();
                    alertManager.insertAlert(alert, obj -> {
                        if (obj) {
                            player.sendMessage(Lang.ALERTS.f("New Alert entry has been added! (" + (System.currentTimeMillis() - s) + "ms)"));
                        } else {
                            player.sendMessage(Lang.ALERTS.f("An error occurred while adding entry!"));
                        }
                    });
                }));
            }
            else {
                addItem(new ClickableItem(53, new ItemFactory(Material.ARROW).setName(C.GREEN + "Finish").build(), (player, clickType) -> {
                    long s = System.currentTimeMillis();
                    AlertEntry alert = new AlertEntry(name, imageUrl, showType, alertType, link, new Timestamp(this.startTime), new Timestamp(this.expireTime), disabled);
                    alert.setDescription(description);
                    alert.setPlayer(playerName);
                    player.closeInventory();
                    alertManager.updateAlert(alert, obj -> {
                        if (obj) {
                            player.sendMessage(Lang.ALERTS.f("Alert " + alert.getUniqueIdentifier() + " has been updated! (" + (System.currentTimeMillis() - s) + "ms)"));
                        } else {
                            player.sendMessage(Lang.ALERTS.f("An error occurred while updating entry!"));
                        }
                    });
                }));
            }
        }

        if(!newEntry) {
            addItem(new ClickableItem(45, new ItemFactory(Material.FERMENTED_SPIDER_EYE).setName(C.RED + "Delete Alert").build(), (player, clickType) -> {
                Optional<Alert> optional = this.alertManager.getAlertById(this.imageUrl);
                long s = System.currentTimeMillis();
                optional.ifPresent(alert -> this.alertManager.deleteAlert(player, alert, obj -> {
                    if (obj) {
                        player.sendMessage(Lang.ALERTS.f("Alert #" + alert.getUniqueIdentifier() + " has deleted! (" + (System.currentTimeMillis() - s) + "ms)"));
                    } else {
                        player.sendMessage(Lang.ALERTS.f("An error occurred while deleting entry!"));
                    }
                    new AlertsMenu(this.alertManager).openInventory(player);
                }));
            }));
        }

        openInventory(user);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void addTime(boolean expire, long add) {
        if(expire) this.expireTime += add;
        else this.startTime += add;
    }

    public void takeTime(boolean expire, long take) {
        if(expire) this.expireTime -= take;
        else this.startTime -= take;
    }

    public long getTime(boolean expire) {
        return expire ? this.expireTime : this.startTime;
    }

    private ItemFactory createAlertItem() {
        byte data = 14;

        if(!name.equals("none") && !imageUrl.equals("none") && !link.equals("none") && !playerName.equals("none") && !description.equals("none"))
            data = 5;

        ItemFactory factory = new ItemFactory(Material.STAINED_CLAY, data);
        factory.setName(this.getNameByByte(name, data));
        List<String> lore = Lists.newArrayList();
        lore.addAll(this.getDescription());
        lore.addAll(Arrays.asList(" ", C.GRAY + "Status" + C.WHITE + ": " + (disabled ? C.RED + "Disabled" : C.GREEN + "Enabled"), " "));
        lore.addAll(Arrays.asList(
                C.WHITE + "Start", "  " + C.GRAY + new Timestamp(this.startTime).toString(),
                " ",
                C.WHITE + "Expire", "  " + C.GRAY + new Timestamp(this.expireTime).toString(),
                " ",
                C.WHITE + "Created by", "  " + C.GRAY + playerName
        ));
        factory.setLore(lore);
        factory.addFlags(ItemFlag.HIDE_ATTRIBUTES);
        return factory;
    }

    private String getNameByByte(String name, byte data) {
        if(data == 5) return C.GREEN + C.BOLD + name;
        if(data == 14) return C.RED + C.BOLD + name;
        return C.YELLOW + C.BOLD + name;
    }

    private List<String> getDescription() {
        if(description.equalsIgnoreCase("none"))
            return Collections.singletonList(" ");

        List<String> desc = Lists.newArrayList();
        for(String line : description.split("~ln")) {
            desc.add(C.DARK_GRAY + line);
        }
        desc.add(" ");

        return desc;
    }
}
