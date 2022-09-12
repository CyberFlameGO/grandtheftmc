package net.grandtheftmc.core.casino.game.component;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.casino.Casino;
import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.casino.game.CasinoRunnable;
import net.grandtheftmc.core.casino.game.bet.CasinoBet;
import net.grandtheftmc.core.casino.game.event.CasinoGameEndEvent;
import net.grandtheftmc.core.casino.game.event.CasinoGameStartEvent;
import net.grandtheftmc.core.util.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class CasinoGameComponent<T extends JavaPlugin> implements Component<CasinoGameComponent, T> {

    private final HashMap<UUID, CasinoGame> game_users;
    private final Casino casino;

    public CasinoGameComponent(Casino casino) {
        this.casino = casino;
        this.game_users = Maps.newHashMap();
    }

    @Override
    public CasinoGameComponent onEnable(T plugin) {
        return this;
    }

    @Override
    public CasinoGameComponent onDisable(T plugin) {
        return this;
    }

    @EventHandler
    protected final void onArmorStandClick(PlayerArmorStandManipulateEvent event) {
        if(event.getPlayer() == null || event.getRightClicked() == null) return;
        if(!event.getRightClicked().getWorld().getName().equals("spawn")) return;

        Optional<CasinoGame> optional = casino.getGames().stream().filter(game -> game.isClicked(event.getRightClicked())).findFirst();
        if(!optional.isPresent()) return;

        event.setCancelled(true);
    }

    @EventHandler
    protected final void onMachineInteract(PlayerInteractAtEntityEvent event) {
        if(event.getPlayer() == null || event.getRightClicked() == null) return;
        Optional<CasinoGame> optional = casino.getGames().stream().filter(game -> game.isClicked(event.getRightClicked())).findFirst();
        if(!optional.isPresent()) return;

        if(this.game_users.containsKey(event.getPlayer().getUniqueId())) {
            this.casino.getTitle().sendTitle(event.getPlayer(), Utils.f("&9&lCASINO"), Utils.f("&cYou cannot use multiple Casino games at once!"), 1*20, 2*20, 1*20);
            return;
        }

        if(optional.get() instanceof CasinoBet) {
            ((CasinoBet) optional.get()).openMenu(event.getPlayer());
            return;
        }

        if(optional.get() instanceof CasinoRunnable) {
            ((CasinoRunnable) optional.get()).start(event.getPlayer());
        }
    }

    @EventHandler
    protected final void onPlayerQuit(PlayerQuitEvent event) {
        game_users.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    protected final void onCasinoGameEnd(CasinoGameEndEvent event) {
        game_users.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    protected final void onCasinoGameStart(CasinoGameStartEvent event) {
        game_users.putIfAbsent(event.getPlayer().getUniqueId(), event.getGame());
    }

//    @EventHandler
//    protected final void onEntityDeath(EntityDamageEvent event) {
//        if(event.getEntity() == null) return;
//
//        Optional<CasinoGame> optional = casino.getGames().stream().filter(game -> game.isClicked(event.getEntity())).findFirst();
//        if(!optional.isPresent()) return;
//
//        if(optional.get().registered())
//            event.setCancelled(true);
//    }
}
