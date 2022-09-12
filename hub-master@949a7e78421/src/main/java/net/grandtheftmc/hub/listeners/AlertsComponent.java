package net.grandtheftmc.hub.listeners;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.mapmanager.MapManagerPlugin;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.manager.MapManager;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import com.google.common.collect.Maps;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.alert.Alert;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ImageRenderer;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.hub.Hub;

/**
 * Created by Luke Bingham on 12/09/2017.
 */
public class AlertsComponent implements Component<AlertsComponent, Hub> {

    private final HashMap<UUID, Alert> userAlerts;
    private final HashMap<Integer, MapData> renders;
    private final AlertManager alertManager;
    private final NMSTitle title;

    private final MapManager mapManager = ((MapManagerPlugin) Bukkit.getPluginManager().getPlugin("MapManager")).getMapManager();

    public AlertsComponent(AlertManager alertManager) {
        this.alertManager = alertManager;
        this.userAlerts = Maps.newHashMap();
        this.renders = Maps.newHashMap();
        this.title = new NMSTitle();
    }

    @Override
    public AlertsComponent onEnable(Hub plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        log(true);
        return this;
    }

    @Override
    public AlertsComponent onDisable(Hub plugin) {
        this.userAlerts.clear();
        return this;
    }

    @EventHandler
    protected final void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            if(this.alertManager.getAlerts().size() > 0 && this.alertManager.getAvailableAlerts().size() > 0) {
                this.alertManager.getAvailableAlertsForPlayer(event.getUniqueId(), alerts -> {
                    if(alerts.size() > 0) {
                        Alert alert = alerts.get(ThreadLocalRandom.current().nextInt(alerts.size()));
                        this.userAlerts.putIfAbsent(event.getUniqueId(), alert);
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer() == null) return;
        UUID uuid = event.getPlayer().getUniqueId();
        if(this.userAlerts.containsKey(uuid)) {
            Alert alert = this.userAlerts.get(uuid);

//            ItemStack map = new ItemStack(Material.MAP);
//            MapMeta meta = (MapMeta) map.getItemMeta();
//            meta.setScaling(false);
//            meta.setDisplayName(C.GREEN + C.BOLD + alert.getName());
//            map.setItemMeta(meta);


//            MapView view = Bukkit.getMap(map.getDurability());
//            for (MapRenderer mapRenderer : view.getRenderers())
//                view.removeRenderer(mapRenderer);

            try {
//                ImageRenderer renderer = null;

                MapData mapData;
                if(!this.renders.containsKey(alert.getUniqueIdentifier())) {
//                    renderer = new ImageRenderer(alert.getImageUrl());
//                    this.renders.put(alert.getUniqueIdentifier(), renderer);
                    MapWrapper mapWrapper = mapManager.wrapImage(ImageIO.read(new URL(alert.getImageUrl())));
                    mapData = new MapData(mapManager.wrapImage(ImageIO.read(new URL(alert.getImageUrl()))), mapWrapper.getController());
                    this.renders.putIfAbsent(alert.getUniqueIdentifier(), mapData);
                }
                else mapData = this.renders.get(alert.getUniqueIdentifier());

//                view.addRenderer(renderer);

//                event.getPlayer().getInventory().setItem(4, map);
//                event.getPlayer().updateInventory();

                MapController controller = mapData.getController();
                controller.addViewer(event.getPlayer());//TODO Remove on join.
                controller.sendContent(event.getPlayer());

                ServerUtil.runTaskLater(() -> {
                    if(Bukkit.getPlayer(uuid) != null){
                    	controller.showInInventory(Bukkit.getPlayer(uuid), 4, true, alert.getName());
                        //controller.showInInventory(Bukkit.getPlayer(uuid), 4, true);
                    }
                }, 10);

                Location current = event.getPlayer().getLocation();
                current.setPitch(50);
                event.getPlayer().teleport(current);

                this.alertManager.alertShown(event.getPlayer(), alert, aBoolean -> {});

            } catch (IOException e) {
                this.userAlerts.remove(uuid);
                event.getPlayer().getInventory().setItem(4, new ItemStack(Material.AIR));
                event.getPlayer().updateInventory();
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    protected final void onLeave(PlayerQuitEvent event) {
        if(event.getPlayer() == null) return;
        if(this.userAlerts.containsKey(event.getPlayer().getUniqueId())) {
            this.userAlerts.remove(event.getPlayer().getUniqueId());
            ImageRenderer.RENDERED_USERS.remove(event.getPlayer().getUniqueId());
        }

        for(MapData data : this.renders.values()) {
            data.controller.removeViewer(event.getPlayer());
        }
    }

    @EventHandler
    protected final void onInteract(PlayerInteractEvent event) {
//        if(event.getItem() == null) return;
//        ItemStack item = event.getItem();
//        if(!item.hasItemMeta()) return;
//        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
//
//        for(Alert alert : this.userAlerts.values()) {
//            if(alert == null) continue;
//            if(alert.getName() == null || alert.getLink() == null) continue;
//            if(alert.getName().equals(name)) {
//                event.getPlayer().sendMessage(Lang.ALERTS.f(alert.getLink()));
//                this.title.sendTitle(event.getPlayer(), "", Utils.f("&eClick the link in chat!"), 1, 1, 1);
//                break;
//            }
//        }

        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getPlayer().getInventory().getHeldItemSlot() != 4) return;

        if(this.userAlerts.containsKey(event.getPlayer().getUniqueId())) {
            Alert alert = this.userAlerts.get(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(Lang.ALERTS.f(alert.getLink()));
            NMSTitle.sendTitle(event.getPlayer(), "", Utils.f("&eClick the link in chat!"), 30, 20, 20);
        }
    }

    @EventHandler
    protected final void onInteract(PlayerInteractAtEntityEvent event) {
//        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
//        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
//        if(item == null || item.getType() != Material.MAP) return;
//        if(!item.hasItemMeta()) return;
//        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
//        System.out.println(name);
//
//        for(UUID uuid : this.userAlerts.keySet()) {
//
//            if(alert == null) continue;
//            if(alert.getName() == null) continue;
//            if(alert.getName().equals(name)) {
//                event.getPlayer().sendMessage(Lang.ALERTS.f(alert.getLink()));
//                this.title.sendTitle(event.getPlayer(), "", Utils.f("&eClick the link in chat!"), 1, 1, 1);
//                break;
//            }
//        }

        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getPlayer().getInventory().getHeldItemSlot() != 4) return;

        if(this.userAlerts.containsKey(event.getPlayer().getUniqueId())) {
            Alert alert = this.userAlerts.get(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(Lang.ALERTS.f(alert.getLink()));
            NMSTitle.sendTitle(event.getPlayer(), "", Utils.f("&eClick the link in chat!"), 1, 1, 1);
        }
    }

//    private final Random random = new Random();
//
//    @EventHandler
//    protected final void onMapInitialize(MapInitializeEvent event) {
//        MapView mapView = event.getMap();
//        mapView.getRenderers().clear();
//
//
//
//        mapView.addRenderer(new Renderer());
//    }
//
//    public class Renderer extends MapRenderer {
//        @Override
//        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
//            for (int x = 25; x < 50; x++) {
//                for (int y = 25; y < 50; y++) {
//                    mapCanvas.setPixel(x, y, MapPalette.RED);
//                }
//            }
//
//            mapCanvas.drawText(15, 15, MinecraftFont.Font, player.getName());
//
//            byte[] dirs = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, color = {0,1,2,3,4,5,6,7};
//            MapCursorCollection cursors = new MapCursorCollection();
//            for(int i = 0; i < 10; i++) {
//                cursors.addCursor(random.nextInt(128), random.nextInt(128), dirs[random.nextInt(dirs.length)], color[random.nextInt(color.length)]);
//            }
//
//            mapCanvas.setCursors(cursors);
//        }
//    }

    public class MapData {
        private final MapWrapper mapWrapper;
        private final MapController controller;

        public MapData(MapWrapper mapWrapper, MapController controller) {
            this.mapWrapper = mapWrapper;
            this.controller = controller;
        }

        public MapWrapper getMapWrapper() {
            return mapWrapper;
        }

        public MapController getController() {
            return controller;
        }
    }
}
