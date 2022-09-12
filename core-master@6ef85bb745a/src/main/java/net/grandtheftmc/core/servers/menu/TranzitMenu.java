package net.grandtheftmc.core.servers.menu;

import net.grandtheftmc.ServerType;
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
import net.grandtheftmc.jedis.message.ServerQueueMessage;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class TranzitMenu extends CoreMenu {

    private int[] edgeInOrder = null;
    private int[] colours = null;

    private GTMTranzitMenu gtmMenu;

    /**
     * Construct a new Menu.
     */
    public TranzitMenu(GTMTranzitMenu gtmMenu) {
        super(6, StringUtil.getCenteredMenuText("Server Travel"), CoreMenuFlag.CLOSE_ON_NULL_CLICK, CoreMenuFlag.RESET_CURSOR_ON_OPEN);
        this.gtmMenu = gtmMenu;

        this.edgeInOrder = new int[]{0, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26, 17, 8, 7, 6, 5, 4, 3, 2, 1};
        this.colours = new int[]{13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5, 13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5};

        this.refreshEdge();
        this.refreshButtons();
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
            }
            catch (Exception e) {}
        });
    }

    public void refreshButtons() {
        refreshGTM();

//        System.out.println(Core.getSettings().isSister());
        if (!Core.getSettings().isSister()) {
//            System.out.println("RAN");
            refreshVice();
            refreshCreative();
        }
    }

    private void refreshGTM() {
        List<Server> gtmServers = Arrays.stream(Core.getSettings().isSister() ? new String[]{"gtm1","gtm2"} : new String[]{"gtm1", "gtm4","gtm2", "gtm3",  "gtm5", "gtm6"}).map(st -> Core.getServerManager().getServer(st)).collect(Collectors.toList());
        boolean allOffline;
        try {
            allOffline = gtmServers.stream().filter(Server::isOffline).count() >= gtmServers.size();
        } catch (Exception e) {
            allOffline = true;
        }

        Material material = allOffline ? Material.EXPLOSIVE_MINECART : Material.MINECART;
        String displayName = (allOffline ? C.RED : C.GREEN) + C.BOLD + Core.getSettings().getServer_GTM_name() + C.RESET + C.GRAY + "  v" + Core.GTM_VERSION;

        ItemFactory item = new ItemFactory(material).setName(displayName).setLore(getGtmServersLore(gtmServers, allOffline));
        super.addItem(new ClickableItem(Core.getSettings().isSister() ? 22 : 31, item.build(), (player, clickType) -> {
            player.closeInventory();
            gtmMenu.openInventory(player);
        }));
    }

    private void refreshVice() {
        Server vice = Core.getServerManager().getServer("vice2");
        boolean Coffline = vice == null || vice.isOffline();

        Material Cmaterial = Coffline ? Material.REDSTONE : Material.SUGAR;
        String CdisplayName = (Coffline ? C.RED + C.BOLD + "ViceMC" : C.LIGHT_PURPLE + C.BOLD + "Vice" + C.WHITE + C.BOLD + "MC") + C.RESET + C.GRAY + "  season " + Core.VICE_VERSION;

        ItemFactory Citem = new ItemFactory(Cmaterial).setName(CdisplayName).setLore(getViceLore(vice));
        addItem(new ClickableItem(24, Citem.build(), (player, clickType) -> {
            if (Coffline) return;

            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                    new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(net.grandtheftmc.ServerType.VICE, 1)),
                    new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
            );
        }));
    }

    private void refreshCreative() {
        Server creative = Core.getServerManager().getServer("creative1");
        boolean Coffline = creative == null || creative.isOffline();

        Material Cmaterial = Coffline ? Material.REDSTONE_BLOCK : Material.GOLD_BLOCK;
        String displayName = (Coffline ? C.RED + C.BOLD + "Creative" : C.GOLD + C.BOLD + "Creative") + C.RESET + C.GRAY + "  v" + Core.CREATIVE_VERSION;

        ItemFactory item = new ItemFactory(Cmaterial).setName(displayName).setLore(getCreativeLore(creative));
        super.addItem(new ClickableItem(20, item.build(), (player, clickType) -> {
            if (Coffline) return;

            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                    new ServerQueueMessage(player.getUniqueId(), user.getUserRank().name(), new ServerTypeId(ServerType.CREATIVE, 1)),
                    new ServerTypeId(net.grandtheftmc.ServerType.OPERATOR, -1)
            );
        }));
    }

//    public static void main(String[] args) {
//        edgeInOrder = new int[]{0, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26, 17, 8, 7, 6, 5, 4, 3, 2, 1};
//        colours = new int[]{13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5, 13, 4, 1, 14, 6, 2, 10, -1, -1, 11, 9, 3, 5};
//
//        System.out.println("#" + 0 + Arrays.toString(colours));
//
//        for (int i = 0; i < colours.length; i++) {
//            rotate();
//            System.out.println("#" + (i+1) + Arrays.toString(colours));
//        }
//    }

    private String[] getGtmServersLore(List<Server> gtmServers, boolean allOffline) {
        int[] totalUsers = {0, 0, 0};
        if (!allOffline) {
            gtmServers.stream().filter(server -> !server.isOffline()).forEach(server -> {
                totalUsers[0] += server.getOnlinePlayers();
                totalUsers[1] += server.getMaxPlayers();
                totalUsers[2]++;
            });
        }

        String[] description = {C.DARK_GRAY + "Shoot enemies, drive tanks,", C.DARK_GRAY + "and buy penthouses in the", C.DARK_GRAY + "epic city!"};
        String playersOnline = C.GRAY + "Players online " + (allOffline ? C.RED + C.BOLD + "0" + C.GRAY + " / " + C.RED + C.BOLD + "0" + C.RESET : C.GREEN + C.BOLD + totalUsers[0] + C.GRAY + " / " + C.GREEN + C.BOLD + totalUsers[1] + C.RESET);
            String onlineServers = C.GRAY + "Servers online " + (allOffline ? C.RED : C.GREEN) + C.BOLD + totalUsers[2];
        String[][] version = {
                {C.GRAY + "Recommended versions", C.GREEN + "  1.12" + C.WHITE + ", " + C.GREEN + "1.12.1" + C.WHITE + ", " + C.GREEN + "1.12.2"},
                {C.GRAY + "Supported versions", C.YELLOW + "  1.8.*" + C.WHITE + ", " + C.YELLOW + "1.9.*" + C.WHITE + ", " + C.YELLOW + "1.10.*" + C.WHITE + ", " + C.YELLOW + "1.11.*" + C.WHITE + ", " + C.YELLOW + "1.12.*"}
        };
        String clickInfo = allOffline ? C.RED + C.ITALIC + "\u27A3 Offline for maintenance!" : C.WHITE + C.ITALIC + "\u27A3 Click to join a server!";
        String secret = C.DARK_GRAY + C.ITALIC + "/gtalog to view updates";

        return new String[] {description[0], description[1], description[2], "", playersOnline, onlineServers, "", version[0][0], version[0][1], "", version[1][0], version[1][1], "", clickInfo, /*, "", secret*/};
    }

    private String[] getViceLore(Server server) {
        boolean online = server != null && !server.isOffline();

        String[] description = {C.DARK_GRAY + "Grow drugs, form cartels,", C.DARK_GRAY + "and become the next", C.DARK_GRAY + "Pablo Escabar!"};
        String playersOnline = C.GRAY + "Players online " + (!online ? C.RED + C.BOLD + "0" + C.GRAY + " / " + C.RED + C.BOLD + "0" + C.RESET : C.GREEN + C.BOLD + server.getOnlinePlayers() + C.GRAY + " / " + C.GREEN + C.BOLD + server.getMaxPlayers() + C.RESET);
//            String onlineServers = C.GRAY + "Servers online " + (allOffline ? C.RED : C.GREEN) + C.BOLD + totalUsers[2];
        String[][] version = {
                {C.GRAY + "Recommended versions", C.GREEN + "  1.12" + C.WHITE + ", " + C.GREEN + "1.12.1" + C.WHITE + ", " + C.GREEN + "1.12.2"},
                {C.GRAY + "Supported versions", C.YELLOW + "  1.8.*" + C.WHITE + ", " + C.YELLOW + "1.9.*" + C.WHITE + ", " + C.YELLOW + "1.10.*" + C.WHITE + ", " + C.YELLOW + "1.11.*" + C.WHITE + ", " + C.YELLOW + "1.12.*"}
        };
        String clickInfo = !online ? C.RED + C.ITALIC + "\u27A3 Offline for maintenance!" : C.WHITE + C.ITALIC + "\u27A3 Click to join server!";
        String secret = C.DARK_GRAY + C.ITALIC + "/vicelog to view updates";

        return new String[] {description[0], description[1], description[2], "", playersOnline, "", version[0][0], version[0][1], "", version[1][0], version[1][1], "", clickInfo, /*, "", secret*/};
    }

    private String[] getCreativeLore(Server server) {
        boolean online = server != null && !server.isOffline();

        String[] description = {C.DARK_GRAY + "Build to your hearts", C.DARK_GRAY + "content and help inspire", C.DARK_GRAY + "new updates to the network"};
        String playersOnline = C.GRAY + "Players online " + (!online ? C.RED + C.BOLD + "0" + C.GRAY + " / " + C.RED + C.BOLD + "0" + C.RESET : C.GREEN + C.BOLD + server.getOnlinePlayers() + C.GRAY + " / " + C.GREEN + C.BOLD + server.getMaxPlayers() + C.RESET);
//            String onlineServers = C.GRAY + "Servers online " + (allOffline ? C.RED : C.GREEN) + C.BOLD + totalUsers[2];
        String[][] version = {
                {C.GRAY + "Recommended versions", C.GREEN + "  1.10.*" + C.WHITE + ", " + C.GREEN + "1.11.*" + C.WHITE + ", " + C.GREEN + "1.12.*"},
                {C.GRAY + "Supported versions", C.YELLOW + "  1.8.*" + C.WHITE + ", " + C.YELLOW + "1.9.*" + C.WHITE + ", " + C.YELLOW + "1.10.*" + C.WHITE + ", " + C.YELLOW + "1.11.*" + C.WHITE + ", " + C.YELLOW + "1.12.*"}
        };
        String clickInfo = !online ? C.RED + C.ITALIC + "\u27A3 Offline for maintenance!" : C.WHITE + C.ITALIC + "\u27A3 Click to join server!";
        String secret = C.DARK_GRAY + C.ITALIC + "/buildlog to view updates";

        return new String[] {description[0], description[1], description[2], "", playersOnline, "", version[0][0], version[0][1], "", version[1][0], version[1][1], "", clickInfo, /*, "", secret*/};
    }
}
