package net.grandtheftmc.core.anticheat.component;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.anticheat.Anticheat;
import net.grandtheftmc.core.anticheat.data.ClientData;
import net.grandtheftmc.core.anticheat.trigger.MovementTrigger;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;

public class AnticheatComponent implements Component<AnticheatComponent, Core> {

    private final Anticheat anticheat;

    public AnticheatComponent(Core core, Anticheat anticheat) {
        this.anticheat = anticheat;
        Bukkit.getPluginManager().registerEvents(this, core);
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ() && event.getFrom().getY() == event.getTo().getY()) return;

        ClientData data = anticheat.getClientHandler().getClientData(event.getPlayer());
        if (data == null) return;

        ServerUtil.runTaskAsync(() -> {
        	
        	// due another check just in case it takes too long to run
        	if (data != null){
        		MovementTrigger trigger = new MovementTrigger(data, event.getFrom(), event.getTo(), false);
                data.addToEventQueue(trigger);
                data.readQueue();
        	}
        });
    }

    @EventHandler
    protected final void onPlayerJoin(PlayerJoinEvent event) {
//        Anticheat.getInstance().getClientHandler().addClientData(event.getPlayer());
    }

//    @EventHandler
//    protected final void onUse(PlayerInteractEvent event) {
//        if(event.getAction() == Action.RIGHT_CLICK_AIR) {
//            ClientData data = Anticheat.getInstance().getClientHandler().getClientData(event.getPlayer());
//            CombatTrigger trigger = new CombatTrigger(data, null, CombatTrigger.FightAction.RIGHT_CLICK);
//            data.addToEventQueue(trigger);
//            data.readQueue();
//        }
//
//        else if(event.getAction() == Action.LEFT_CLICK_AIR) {
//            ClientData data = Anticheat.getInstance().getClientHandler().getClientData(event.getPlayer());
//            CombatTrigger trigger = new CombatTrigger(data, null, CombatTrigger.FightAction.LEFT_CLICK);
//            data.addToEventQueue(trigger);
//            data.readQueue();
//        }
//    }
//
//    @EventHandler
//    protected final void onHit(EntityDamageByEntityEvent e) {
//        if (!(e.getDamager() instanceof Player)) return;
//
//        ClientData playerData = Anticheat.getInstance().getClientHandler().getClientData((Player) e.getDamager());
//        ClientData targetData = e.getEntity() instanceof Player ? Anticheat.getInstance().getClientHandler().getClientData((Player) e.getEntity()) : null;
//        CombatTrigger trigger = new CombatTrigger(playerData, targetData, CombatTrigger.FightAction.BUKKIT_HIT);
//        playerData.addToEventQueue(trigger);
//        playerData.readQueue();
//    }
}
