package net.grandtheftmc.core.servers.menu;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.StringUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerSetCooldown;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisModule;
import net.grandtheftmc.jedis.message.ServerQueueMessage;

public class GTMTranzitMenu extends CoreMenu {

    private int[] edgeInOrder = null;
    private int[] colours = null;

    /**
     * Construct a new Menu.
     */
    public GTMTranzitMenu() {
        super(6, StringUtil.getCenteredMenuText(Core.getSettings().getServer_GTM_shortName() + " Server Travel"), CoreMenuFlag.CLOSE_ON_NULL_CLICK, CoreMenuFlag.RESET_CURSOR_ON_OPEN);

        this.edgeInOrder = new int[]{0, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26, 17, 8, 7, 6, 5, 4, 3, 2, 1};
        this.colours = new int[]{13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5, 13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5};

        this.refreshEdge();
        this.refreshButtons();
    }

    public void refreshEdge() {
        for (int i = 0; i < this.edgeInOrder.length; i++) {
            if (this.colours[i] < 0) {
                super.deleteItem(this.edgeInOrder[i]);
                continue;
            }

//            super.addItem(new MenuItem(this.edgeInOrder[i], new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) this.colours[i]), false));
            super.addItem(new MenuItem(this.edgeInOrder[i], new ItemFactory(Material.STAINED_GLASS_PANE, (byte) this.colours[i]).setName(C.WHITE).build(), false));
        }

        ServerUtil.runTaskAsync(() -> {
            WrapperPlayServerSetCooldown cooldown = new WrapperPlayServerSetCooldown();
            cooldown.setItem(Material.STAINED_GLASS_PANE);
            cooldown.setTicks(500);
            try {
                HumanEntity[] viewers = this.getInventory().getViewers().toArray(new HumanEntity[this.getInventory().getViewers().size()]);
                for (HumanEntity player : viewers)
                    cooldown.sendPacket((Player) player);
            } catch (Exception e) {
            }
        });
    }

    public void rotate() {
        int[] arr = new int[this.colours.length];
        for (int i = 0; i < this.colours.length; i++) {
            if (i == this.colours.length - 1) {
                arr[0] = this.colours[i];
                continue;
            }

            arr[i + 1] = this.colours[i];
        }

        this.colours = arr;
    }

    public void refreshButtons() {
        int[] slots = {20, 24, 29, 22, 31, 33};
        for (int i = 1; i < (Core.getSettings().isSister() ? 3 : 7); i++) {
            Server gtm = Core.getServerManager().getServer("gtm" + i);
            boolean online = gtm != null && !gtm.isOffline();

            Material material = !online ? Material.EXPLOSIVE_MINECART : Material.MINECART;
            String displayName = " " + C.RESET + C.WHITE + C.BOLD + (gtm != null ? gtm.getNumber() : 0) + C.RESET + C.GRAY + "  v" + Core.GTM_VERSION;
            if (Core.getSettings().isSister()) {
                if (i == 1) displayName = (!online ? C.RED : C.GREEN) + C.BOLD + "Capital City" + displayName;
                else if (i == 2) displayName = (!online ? C.RED : C.GREEN) + C.BOLD + "Sandy Shores" + displayName;
            } else {
                if (i == 1)
                    displayName = (!online ? C.RED : C.GREEN) + C.BOLD + "GTM" + C.WHITE + C.BOLD + " MineSantos" + C.RESET + C.GRAY + "  v" + Core.GTM_VERSION;
                else if (i == 4)
                    displayName = (!online ? C.RED : C.GREEN) + C.BOLD + "GTM" + C.WHITE + C.BOLD + " Sanktburg" + C.RESET + C.GRAY + "  v" + Core.GTM_VERSION;
                else
                    displayName = (!online ? C.RED : C.GREEN) + C.BOLD + "GTM" + displayName;
            }

            ItemFactory item = new ItemFactory(material).setName(displayName).setLore(getGtmLore(gtm));
            addItem(new ClickableItem(slots[i - 1], item.build(), (player, clickType) -> {
                if (!online) return;

                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());

                JedisModule module = Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE);
                if (module == null) {
                    Core.getServerManager().sendToServer(player, "gtm" + gtm.getNumber());
                    return;
                }

                module.sendMessage(
                        new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.GTM, gtm.getNumber())),
                        new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
                );
            }));
        }
    }

    protected String[] getGtmLore(Server server) {
        boolean online = server != null && !server.isOffline();
        if (server!=null&&(server.getNumber() == 1 || server.getNumber() == 4)) {
            String[] description = {C.DARK_GRAY + "Shoot enemies, drive tanks,", C.DARK_GRAY + "and buy penthouses in the", C.DARK_GRAY + "epic city!"};
            String id = C.GRAY + "Server Number " + C.LIGHT_PURPLE + C.BOLD + (server != null ? server.getNumber() : 0);
            String playersOnline = C.GRAY + "Players online " + (!online ? C.RED + C.BOLD + "0" + C.GRAY + " / " + C.RED + C.BOLD + "0" + C.RESET : C.GREEN + C.BOLD + server.getOnlinePlayers() + C.GRAY + " / " + C.GREEN + C.BOLD + server.getMaxPlayers() + C.RESET);
//            String onlineServers = C.GRAY + "Servers online " + (allOffline ? C.RED : C.GREEN) + C.BOLD + totalUsers[2];
            String[] language = {C.GRAY + "Favored Language", "  " + (online && server.getNumber() == 4 ? C.YELLOW + C.BOLD + "GERMAN" : C.AQUA + C.BOLD + "ENGLISH")};
            String[][] version = {
                    {C.GRAY + "Recommended versions", C.GREEN + "  1.12" + C.WHITE + ", " + C.GREEN + "1.12.1" + C.WHITE + ", " + C.GREEN + "1.12.2"},
                    {C.GRAY + "Supported versions", C.YELLOW + "  1.8.*" + C.WHITE + ", " + C.YELLOW + "1.9.*" + C.WHITE + ", " + C.YELLOW + "1.10.*" + C.WHITE + ", " + C.YELLOW + "1.11.*" + C.WHITE + ", " + C.YELLOW + "1.12.*"}
            };
            String clickInfo = !online ? C.RED + C.ITALIC + "\u27A3 Offline for maintenance!" : C.WHITE + C.ITALIC + "\u27A3 Click to join server!";
//        String secret = C.DARK_GRAY + C.ITALIC + "/gtmlog to view updates";

            return new String[]{description[0], description[1], description[2], "", id, "", playersOnline, "", language[0], language[1], "", version[0][0], version[0][1], "", version[1][0], version[1][1], "", clickInfo/*, "", secret*/};
        } else {
            return new String[]{C.RED+ "This server will be",C.RED+"closed on September 20.",C.RED+"If you are a new player,",C.RED+"please choose a different server.",C.RED+"",C.RED+"If you are an existing player,",C.RED+"please read our post at",C.RED+"grandtheftmc.net",C.RED+"to transfer some of your",C.RED+"progress to another server."};
        }
    }
}
