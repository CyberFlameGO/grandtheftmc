package net.grandtheftmc.vice.dropship;

import com.google.common.collect.Sets;
import net.grandtheftmc.vice.dropship.event.DropShipCountdownEvent;
import net.grandtheftmc.vice.dropship.event.DropShipStartEvent;
import net.grandtheftmc.vice.utils.TextHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public final class DropShipTask extends BukkitRunnable {

    private final DropShipManager dropShipManager;
    private final DropShip dropShip;
    private final int[] announceTimes = new int[] { 60, 30, 10 };
    private int timer;

    private final Set<Player> players;
    private boolean major;

    public DropShipTask(boolean major, DropShipManager dropShipManager, DropShip dropShip) {
        this.dropShipManager = dropShipManager;
        this.dropShip = dropShip;
        this.timer = 60;
        this.major = major;

        this.players = Sets.newHashSet();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public final void run() {

        for (Player player : this.players) {
            if (player.hasPotionEffect(PotionEffectType.LEVITATION) && player.isOnGround()) {
                player.removePotionEffect(PotionEffectType.LEVITATION);
            }
        }

        DropShipCountdownEvent countdownEvent = new DropShipCountdownEvent(this.dropShip, this.timer);
        Bukkit.getPluginManager().callEvent(countdownEvent);

        if (!this.canBroadcast()) {

            if (this.isDone()) {

                TextHelper hover = new TextHelper().setText("Click here to teleport to\nthe drop ship event!").setColor(ChatColor.YELLOW).setItalic(true);
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dropshiptp");
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.toBaseComponent());

                TextHelper text = new TextHelper("DROP SHIP\n").setBold(true).setColor(ChatColor.YELLOW)
                        .setHover(hoverEvent).setClick(clickEvent)
                        .addExtra(new TextHelper(" A" + (this.major ? " major" : "n") + " event has started at ").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                        .addExtra(new TextHelper(this.dropShip.getSettlement().getName().replace("_", " ")).setBold(false).setColor(ChatColor.GOLD).setItalic(true))
                        .addExtra(new TextHelper(".").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                        .addExtra(new TextHelper("\n Click this message to teleport to the " + (this.major ? "Town" : "Village") + " and collect the loot!").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                        .addExtra(new TextHelper("\n"));

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(text.build());
                }

                DropShipStartEvent startEvent = new DropShipStartEvent(this.dropShip);
                Bukkit.getPluginManager().callEvent(startEvent);

                this.dropShip.stop();
                return;
            }

            timer -= 1;
            return;
        }

        TextHelper hover = new TextHelper().setText("Click here to teleport to\nthe drop ship event!").setColor(ChatColor.YELLOW).setItalic(true);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dropshiptp");
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.toBaseComponent());

        TextHelper text = new TextHelper("DROP SHIP\n").setBold(true).setColor(ChatColor.YELLOW)
                .setHover(hoverEvent).setClick(clickEvent)
                .addExtra(new TextHelper(" A" + (this.major ? " major" : "n") + " event is starting at ").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                .addExtra(new TextHelper(this.dropShip.getSettlement().getName().replace("_", " ")).setBold(false).setColor(ChatColor.GOLD).setItalic(true))
                .addExtra(new TextHelper(" in " + this.timer + " seconds.").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                .addExtra(new TextHelper("\n Click this message to teleport to the " + (this.major ? "Town" : "Village") + " and collect the loot!").setBold(false).setColor(ChatColor.WHITE).setItalic(true))
                .addExtra(new TextHelper("\n"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            player.spigot().sendMessage(text.build());
        }

        timer -= 1;
    }

    public void stop() {
        this.cancel();
        this.players.clear();
    }

    private final boolean canBroadcast() {
        for (int i : this.announceTimes) {
            if (i == this.timer) return true;
        }
        return false;
    }

    private final boolean isDone() {
        return this.timer <= 0;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public boolean contains(Player player) {
        return this.players.contains(player);
    }
}
