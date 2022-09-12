package net.grandtheftmc.core.alert.ui;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * Created by Luke Bingham on 12/09/2017.
 */
public class AlertTimeEditor extends CoreMenu {

    private final AlertCreationMenu menu;
    private final Player user;

    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    /**
     * Construct a new Menu.
     */
    public AlertTimeEditor(AlertCreationMenu menu, boolean s, Player user) {
        super(6, s ? "Alert Expire Time" : "Alert Start Time");
        this.menu = menu;
        this.user = user;

        update(s);
    }

    protected void update(boolean expire) {
        addItem(new ClickableItem(10, new ItemFactory(Material.LONG_GRASS, (byte) 2).setName(C.GREEN + "+1 Day").build(), (player, clickType) -> {
            this.days += 1;
            menu.addTime(expire, 86400000);
            update(expire);
        }));
        addItem(new ClickableItem(11, new ItemFactory(Material.DOUBLE_PLANT).setName(days + " Days").build(), (player, clickType) -> {}));
        addItem(new ClickableItem(12, new ItemFactory(Material.HOPPER).setName(C.RED + "-1 Day").build(), (player, clickType) -> {
            this.days -= 1;
            menu.takeTime(expire, 86400000);
            update(expire);
        }));

        addItem(new ClickableItem(14, new ItemFactory(Material.LONG_GRASS, (byte) 2).setName(C.GREEN + "+1 Hour").build(), (player, clickType) -> {
            this.hours += 1;
            menu.addTime(expire, 3600000);
            update(expire);
        }));
        addItem(new ClickableItem(15, new ItemFactory(Material.DOUBLE_PLANT).setName(hours + " Hours").build(), (player, clickType) -> {}));
        addItem(new ClickableItem(16, new ItemFactory(Material.HOPPER).setName(C.RED + "-1 Hour").build(), (player, clickType) -> {
            this.hours -= 1;
            menu.takeTime(expire, 3600000);
            update(expire);
        }));

        addItem(new ClickableItem(28, new ItemFactory(Material.LONG_GRASS, (byte) 2).setName(C.GREEN + "+1 Minute").build(), (player, clickType) -> {
            this.minutes += 1;
            menu.addTime(expire, 60000);
            update(expire);
        }));
        addItem(new ClickableItem(29, new ItemFactory(Material.DOUBLE_PLANT).setName(minutes + " Minutes").build(), (player, clickType) -> {}));
        addItem(new ClickableItem(30, new ItemFactory(Material.HOPPER).setName(C.RED + "-1 Minute").build(), (player, clickType) -> {
            this.minutes -= 1;
            menu.takeTime(expire, 60000);
            update(expire);
        }));

        addItem(new ClickableItem(32, new ItemFactory(Material.LONG_GRASS, (byte) 2).setName(C.GREEN + "+1 Second").build(), (player, clickType) -> {
            this.seconds += 1;
            menu.addTime(expire, 1000);
            update(expire);
        }));
        addItem(new ClickableItem(33, new ItemFactory(Material.DOUBLE_PLANT).setName(seconds + " Seconds").build(), (player, clickType) -> {}));
        addItem(new ClickableItem(34, new ItemFactory(Material.HOPPER).setName(C.RED + "-1 Second").build(), (player, clickType) -> {
            this.seconds -= 1;
            menu.takeTime(expire, 1000);
            update(expire);
        }));

        addItem(new ClickableItem(49, new ItemFactory(Material.WATCH).setName(new Timestamp(menu.getTime(expire)).toString()).build(), (player, clickType) -> update(expire)));

        addItem(new ClickableItem(44, new ItemFactory(Material.ARROW).setName(C.RED + "Back").build(), (player, clickType) -> {
            player.closeInventory();
            menu.refresh();
        }));

        this.openInventory(user);
    }
}
