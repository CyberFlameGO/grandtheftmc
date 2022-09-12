package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.util.SoundEffect;
import net.grandtheftmc.core.util.Title;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class Slide {

    private boolean canConfirm;
    private int delay;
    private Location location;
    private GameMode gameMode;
    private String[] messages;
    private boolean headerAndFooter;
    private String actionBarMessage;
    private Title title;
    private List<SoundEffect> sounds;
    private int slot;
    private String menu;

    public Slide() {
    }

    public Slide(boolean canConfirm, int delay, Location location, GameMode gameMode, String[] messages, boolean headerAndFooter, String actionBarMessage, Title title, List<SoundEffect> sounds, int slot, String menu) {
        this.canConfirm = canConfirm;
        this.delay = delay;
        this.location = location;
        this.gameMode = gameMode;
        this.messages = messages;
        this.headerAndFooter = headerAndFooter;
        this.actionBarMessage = actionBarMessage;
        this.title = title;
        this.sounds = sounds;
        this.slot = slot;
        this.menu = menu;
    }

    public void play(Player player) {
        this.teleport(player);
        this.sendSoundEffects(player);
        this.sendMessages(player);
        this.sendActionBarMessage(player);
        this.sendTitle(player);
        this.setGameMode(player);
        if (this.menu != null)
            MenuManager.openMenu(player, this.menu);
    }

    public void teleport(Player player) {
        if (this.location != null)
            player.teleport(this.location);
        if (this.slot >= 0 && this.slot <= 8)
            player.getInventory().setHeldItemSlot(this.slot);
    }

    public void sendMessages(Player player) {
        if (this.headerAndFooter) {
            player.sendMessage(new String[]{"", "", "", "", "", "", "", "",});
            player.sendMessage(Utils.f(Core.getAnnouncer().getHeader()));
        }
        if (this.messages != null && this.messages.length > 0)
            player.sendMessage(Utils.fc(this.messages));
        if (this.canConfirm) {
            TextComponent comp = new TextComponent(Utils.fc("&nClick to continue..."));
            comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/next"));
            player.spigot().sendMessage(comp);
        } else
            player.sendMessage(Utils.fc("Continuing in &a&l" + (this.delay / 20) + "&r seconds..."));

        if (this.headerAndFooter)
            player.sendMessage(Utils.f(Core.getAnnouncer().getFooter()));
    }

    public void sendActionBarMessage(Player player) {
        if (this.actionBarMessage != null)
            Utils.sendActionBar(player, this.actionBarMessage);
    }

    public void sendTitle(Player player) {
        if (this.title != null)
            Utils.sendTitle(player, this.title);
    }

    public void sendSoundEffects(Player player) {
        if (this.sounds != null)
            for (SoundEffect sound : this.sounds)
                sound.play(player);
    }

    public void setGameMode(Player player) {
        if (this.gameMode != null)
            player.setGameMode(this.gameMode);
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        List<String> msges = Utils.toList(this.messages);
        msges.add(message);
        this.messages = msges.toArray(new String[msges.size()]);
    }

    public void addMessage(String message, int id) {
        List<String> msges = Utils.toList(this.messages);
        msges.add(id, message);
        this.messages = msges.toArray(new String[msges.size()]);
    }

    public void removeMessage(int id) {
        List<String> msges = Utils.toList(this.messages);
        msges.remove(id);
        this.messages = msges.toArray(new String[msges.size()]);
    }

    public String getActionBarMessage() {
        return this.actionBarMessage;
    }

    public void setActionBarMessage(String actionBarMessage) {
        this.actionBarMessage = actionBarMessage;
    }

    public Title getTitle() {
        return this.title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public boolean isCanConfirm() {
        return this.canConfirm;
    }

    public void setCanConfirm(boolean canConfirm) {
        this.canConfirm = canConfirm;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<SoundEffect> getSounds() {
        return this.sounds;
    }

    public void setSounds(List<SoundEffect> sounds) {
        this.sounds = sounds;
    }

    public SoundEffect getSound(int id) {
        return this.sounds.get(id);
    }

    public void addSound(SoundEffect e) {
        this.sounds.add(e);
    }

    public void removeSound(int id) {
        this.sounds.remove(this.getSound(id));
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int i) {
        this.slot = i;
    }

    public void setHeaderAndFooter(boolean headerAndFooter) {
        this.headerAndFooter = headerAndFooter;
    }

    public boolean getHeaderAndFooter() {
        return this.headerAndFooter;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return this.menu;
    }
}
