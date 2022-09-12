package net.grandtheftmc.vice.hologram.event;

import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.hologram.Hologram;
import net.grandtheftmc.vice.hologram.HologramNode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class HologramReceiveEvent extends CoreEvent implements Cancellable {

    private final Player player;
    private final Hologram hologram;
    private final HologramNode node;

    private String text;
    private boolean c, display = true;

    public HologramReceiveEvent(Player player, Hologram hologram, HologramNode node) {
        super(false);
        this.player = player;
        this.hologram = hologram;
        this.node = node;
        this.text = node.getText();
    }

    public Player getPlayer() {
        return player;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public HologramNode getNode() {
        return node;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = Utils.f(text);
    }

    public boolean doDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    @Override
    public boolean isCancelled() {
        return this.c;
    }

    @Override
    public void setCancelled(boolean b) {
        this.c = b;
    }
}
