package net.grandtheftmc.core.util.factory;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkFactory extends Factory<Firework> {

    private FireworkMeta meta;

    private boolean flicker = false, trail = false;
    private Color color = Color.BLACK, fade = Color.WHITE;
    private FireworkEffect.Type type = FireworkEffect.Type.BALL;

    public FireworkFactory(Location location) {
        super.object = location.getWorld().spawn(location, Firework.class);
        this.meta = super.object.getFireworkMeta();
    }

    public FireworkFactory setPower(int power) {
        this.meta.setPower(power);
        return this;
    }

    public FireworkFactory setFlicker(boolean flicker) {
        this.flicker = flicker;
        return this;
    }

    public FireworkFactory setTrail(boolean trail) {
        this.trail = trail;
        return this;
    }

    public FireworkFactory setColor(Color color) {
        this.color = color;
        return this;
    }

    public FireworkFactory setFadeColor(Color fade) {
        this.fade = fade;
        return this;
    }

    public FireworkFactory setType(FireworkEffect.Type type) {
        this.type = type;
        return this;
    }

    @Override
    public Firework build() {
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(this.flicker)
                .withColor(this.color).withFade(this.fade)
                .with(this.type)
                .trail(this.trail)
                .build();

        this.meta.addEffect(effect);
        super.object.setFireworkMeta(this.meta);

        return super.object;
    }
}
