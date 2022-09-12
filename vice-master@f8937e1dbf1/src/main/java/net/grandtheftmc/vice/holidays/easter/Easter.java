package net.grandtheftmc.vice.holidays.easter;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.holidays.Holiday;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Rabbit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class Easter extends Holiday {
    private EasterTask easterTask;

    public Easter() {
        new EasterListener(this);
        if (this.isActive()) {
            if (this.easterTask == null) this.easterTask = new EasterTask(this);
        }
    }

    public boolean isActive() {
        return ViceUtils.getMonth() == Month.APRIL && ViceUtils.getDay() == 15
                || ViceUtils.getDay() == 16 || ViceUtils.getDay() == 17;
    }

    public Optional<EasterTask> getEasterTask() {
        return Optional.ofNullable(this.easterTask);
    }

    public ItemStack getEasterEgg() {
        ItemStack easterEgg = new ItemStack(Material.EGG);
        ItemMeta meta = easterEgg.getItemMeta();
        meta.setDisplayName(Utils.f(ViceUtils.randomColor() + "&lEaster Egg"));
        easterEgg.setItemMeta(meta);
        return easterEgg;
    }

    public ItemStack getChocolateBunny() {
        ItemStack chocolateBunny = new ItemStack(Material.COOKED_RABBIT);
        ItemMeta meta = chocolateBunny.getItemMeta();
        meta.setDisplayName(Utils.f(ViceUtils.randomColor() + "&lChocolate"));
        chocolateBunny.setItemMeta(meta);
        return chocolateBunny;
    }

    public Rabbit.Type[] getAllowedTypes() {
        Rabbit.Type[] allowed = {Rabbit.Type.BLACK, Rabbit.Type.BLACK_AND_WHITE,
                Rabbit.Type.GOLD, Rabbit.Type.WHITE};
        return allowed;
    }

    public Collection<Rabbit> getRabbitsByChunk(Chunk chunk) {
        Collection<Rabbit> rabbits = new ArrayList<>();
        if (chunk == null || !chunk.isLoaded()) return rabbits;
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() != EntityType.RABBIT) continue;
            rabbits.add((Rabbit) entity);
        }
        return rabbits;
    }

    public Collection<Item> getItemsByChunk(Chunk chunk) {
        Collection<Item> items = new ArrayList<>();
        if (chunk == null || !chunk.isLoaded()) return items;
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() != EntityType.DROPPED_ITEM) continue;
            items.add((Item) entity);
        }
        return items;
    }

}
