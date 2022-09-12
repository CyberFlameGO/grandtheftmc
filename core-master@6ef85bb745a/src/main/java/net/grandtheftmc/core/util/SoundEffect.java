package net.grandtheftmc.core.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;

public class SoundEffect {

    private Sound sound;
    private float volume;
    private float pitch;
    private int delay;

    public SoundEffect(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundEffect(Sound sound, float volume, float pitch, int delay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delay;
    }

    public void play() {
        if (this.hasDelay()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.playSound(player.getLocation(), SoundEffect.this.sound, SoundEffect.this.volume, SoundEffect.this.pitch);
                }
            }.runTaskLater(Core.getInstance(), this.delay);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers())
            player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }

    public void play(Location location) {
        if (this.hasDelay()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.playSound(location, SoundEffect.this.sound, SoundEffect.this.volume, SoundEffect.this.pitch);
                }
            }.runTaskLater(Core.getInstance(), this.delay);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers())
            player.playSound(location, this.sound, this.volume, this.pitch);
    }

    public void play(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.hasDelay()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null)
                        player.playSound(player.getLocation(), SoundEffect.this.sound, SoundEffect.this.volume, SoundEffect.this.pitch);
                }
            }.runTaskLater(Core.getInstance(), this.delay);
            return;
        }
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }

    public void play(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        if (this.hasDelay()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null)
                        player.playSound(location, SoundEffect.this.sound, SoundEffect.this.volume, SoundEffect.this.pitch);
                }
            }.runTaskLater(Core.getInstance(), this.delay);
            return;
        }
        player.playSound(location, this.sound, this.volume, this.pitch);
    }

    public Sound getSound() {
        return this.sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean hasDelay() {
        return this.delay > 0;
    }

}
