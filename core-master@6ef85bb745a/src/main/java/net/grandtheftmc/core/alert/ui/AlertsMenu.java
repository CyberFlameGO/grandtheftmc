package net.grandtheftmc.core.alert.ui;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.alert.Alert;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public class AlertsMenu extends CoreMenu {

    /**
     * Construct a new Menu.
     */
    public AlertsMenu(AlertManager alertManager) {
        this(1, alertManager);
    }

    private AlertsMenu(int page, AlertManager alertManager) {
        super(
                6,
                "Alerts (page " + page + ")",
                CoreMenuFlag.RESET_CURSOR_ON_OPEN
        );

        /**
         * SOLID_RED = Disabled.
         * SOLID_YELLOW = Expired.
         * SOLID_GREEN = Active.
         */

//        for(int slot : super.getEdgeSlots(super.getRows()))
//            super.addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE, (byte) 7).setName(" ").build(), false));

        addItem(new ClickableItem(49, new ItemFactory(Material.BOOK_AND_QUILL).setName(C.AQUA + "Create Alert").build(), (player, clickType) -> {
            player.closeInventory();
            new AlertCreationMenu(alertManager, player);
        }));

        int[] middle = {10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34};
        int perPage = middle.length, pages = 1, x = alertManager.getAlerts().size(), z = 0;
        if(x / perPage > 0) pages = x / perPage;
        if(x % perPage > 0) pages += 1;
        if(page > pages) return;
        for(int i = ((page * perPage) - perPage); i < (page * perPage); i += 1) {
            if(i >= x) {
                //Fill in the blank spaces.
//                for(int j = z; j < perPage; j += 1) {
//                    addItem(null);
//                }
                break;
            }
            Alert alert = alertManager.getAlerts().get(i);
            addItem(new ClickableItem(middle[z++], createAlertItem(alert).build(), (player, clickType) -> {
                player.sendMessage("#" + alert.getUniqueIdentifier());
                new AlertCreationMenu(alertManager, alert.getName(), alert.getImageUrl(), alert.getLink(), alert.getDescription(), alert.getShowType(), alert.getAlertType(), alert.getStart().getTime(), alert.getEnd().getTime(), alert.isDisabled(), player).openInventory(player);
            }));
        }

        if (page < pages) {
            addItem(new ClickableItem(52, new ItemStack(Material.ARROW), (player, clickType) -> new AlertsMenu(page + 1, alertManager).openInventory(player)));
        }

        if (page > 1) {
            addItem(new ClickableItem(46, new ItemStack(Material.ARROW), (player, clickType) -> new AlertsMenu(page - 1, alertManager).openInventory(player)));
        }
    }

    private ItemFactory createAlertItem(Alert alert) {
        byte data = 5;
        String status = "";
        if(alert.isInProgress()) {
            data = 5;
            status = C.GREEN + "Active";
        }
        if(alert.hasExpired()) {
            data = 4;
            status = C.YELLOW + "Expired";
        }
        if(alert.isDisabled()) {
            data = 14;
            status = C.RED + "Disabled";
        }
        ItemFactory factory = new ItemFactory(Material.STAINED_CLAY, data);
        factory.setName(this.getNameByByte(alert.getName(), data));
        List<String> lore = Lists.newArrayList();
        lore.addAll(this.getDescription(alert));
        lore.addAll(Arrays.asList(" ", C.GRAY + "Status" + C.WHITE + ": " + status, " "));
        lore.addAll(Arrays.asList(
                C.WHITE + (alert.hasStarted() ? "Started at" : (alert.hasExpired() ? "Started at" : "Starts at")), "  " + C.GRAY + alert.getStart().toString(),
                " ",
                C.WHITE + (alert.hasExpired() ? "Expired" : "Expires at"), "  " + C.GRAY + alert.getEnd().toString(),
                " ",
                C.WHITE + "Created by", "  " + C.GRAY + alert.getPlayer(),
                " ",
                C.WHITE + "Shift " + C.GRAY + "&" + C.WHITE + " Right Click " + C.GRAY + "to edit"
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

    public List<String> getDescription(Alert alert) {
        if(alert.getDescription().equalsIgnoreCase("none"))
            return Collections.singletonList(" ");

        List<String> desc = Lists.newArrayList();
        for(String line : alert.getDescription().split("~ln")) {
            desc.add(C.DARK_GRAY + line);
        }
        desc.add(" ");

        return desc;
    }
}
