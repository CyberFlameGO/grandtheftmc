package net.grandtheftmc.core.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.anticheat.check.CheckManager;
import net.grandtheftmc.core.anticheat.component.AnticheatComponent;
import net.grandtheftmc.core.anticheat.data.ClientData;
import net.grandtheftmc.core.anticheat.data.ClientHandler;
import net.grandtheftmc.core.anticheat.inspect.InspectCommand;
import net.grandtheftmc.core.anticheat.report.ReportManager;
import net.grandtheftmc.core.anticheat.trigger.MovementTrigger;
import net.grandtheftmc.core.util.C;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class Anticheat {

    private static final String NAME = "WatchDawg";

    private final ClientHandler clientHandler;
    private final CheckManager checkManager;

    public Anticheat(Core core) {
        this.checkManager = new CheckManager();
        new AnticheatComponent(core, this);
        ReportManager reportManager = new ReportManager(core, this);
        this.clientHandler = new ClientHandler(this, reportManager);
        new InspectCommand();

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(core, ListenerPriority.NORMAL, PacketType.Play.Client.FLYING) {
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
                if (clientHandler.getClientData(event.getPlayer()) == null) return;

                if (event.getPacketType().equals(PacketType.Play.Client.FLYING)) {
                    ClientData data = getClientHandler().getClientData(event.getPlayer());
                    MovementTrigger trigger = new MovementTrigger(data, null, null, true);
                    data.addToEventQueue(trigger);
                }
            }
        });

//        manager.addPacketListener(new PacketAdapter(core, ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {
//            public void onPacketReceiving(PacketEvent event) {
//                if(clientHandler.getClientData(event.getPlayer()) == null) return;
//
//                if (event.getPacketType().equals(PacketType.Play.Client.ARM_ANIMATION)) {
//                    ClientData checkPlayer = clientHandler.getClientData(event.getPlayer());
//                    CombatTrigger trigger = new CombatTrigger(checkPlayer, null, CombatTrigger.FightAction.SWING);
//                    checkPlayer.addToEventQueue(trigger);
//                }
//            }
//        });
//
//        manager.addPacketListener(new PacketAdapter(core, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
//            public void onPacketReceiving(PacketEvent event) {
//                if(clientHandler.getClientData(event.getPlayer()) == null) return;
//
//                if (event.getPacketType().equals(PacketType.Play.Client.USE_ENTITY)) {
//                    Player targetEntity = null;
//                    int entityId = event.getPacket().getIntegers().getValues().get(0);
//
//                    for (Entity e : event.getPlayer().getWorld().getEntities()) {
//                        if (e.getEntityId() == entityId) {
//                            if (e instanceof Player) targetEntity = (Player) e;
//                            else targetEntity = null;
//                        }
//                    }
//
//                    if (targetEntity != null) {
//                        ClientData checkPlayer = clientHandler.getClientData(event.getPlayer());
//                        ClientData targetPlayer = clientHandler.getClientData(targetEntity);
//                        if (Math.abs(checkPlayer.lastSwingTime - System.currentTimeMillis()) < 100) {
//                            CombatTrigger trigger = new CombatTrigger(checkPlayer, targetPlayer, CombatTrigger.FightAction.HIT);
//                            checkPlayer.addToEventQueue(trigger);
//                        }
//                    }
//                }
//            }
//        });
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }

    public static void log(String log) {
        Bukkit.getConsoleSender().sendMessage(C.GOLD + NAME + C.AQUA + " " + log);
    }
}
