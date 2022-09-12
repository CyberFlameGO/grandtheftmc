package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Liam on 26/07/2017.
 */
public class WorldManager implements Component<WorldManager, Core> {

    private final HashMap<String, WorldConfig> worldConfigs = new HashMap<>();

    public WorldManager() {
        this.loadWorlds();
    }

    @Override
    public WorldManager onDisable(Core plugin) {
        this.worldConfigs.clear();
        return this;
    }

    private void loadWorlds() {
        YamlConfiguration c = Core.getSettings().getWorldsConfig();
        this.worldConfigs.clear();
        if (c.getKeys(false).isEmpty())
            for (World world : Bukkit.getWorlds())
                this.worldConfigs.put(world.getName(), new WorldConfig(world.getName(), true));
        if (c.get("worlds") != null) {
            for (World world : Bukkit.getWorlds())
                this.worldConfigs.put(world.getName(), new WorldConfig(world.getName(), true));
            for (String s : c.getStringList("worlds")) {
                this.worldConfigs.put(s, new WorldConfig(s, true));
                if (Bukkit.getWorld(s) == null)
                    new WorldCreator(s).createWorld();
            }
            c.getStringList("worlds").stream().filter(s -> Bukkit.getWorld(s) == null).forEach(s -> new WorldCreator(s).createWorld());
            return;
        }
        for (String s : c.getKeys(false)) {
            boolean editMode = c.getBoolean(s + ".editMode", true);
            if (Bukkit.getWorld(s) == null) {
                WorldCreator wc = new WorldCreator(s);
                if (c.get(s + ".worldType") != null)
                    wc.type(WorldType.valueOf(c.getString(s + ".worldType")));
                if (c.get(s + ".seed") != null)
                    wc.seed(c.getLong(s + ".seed"));
                if (c.get(s + ".environment") != null)
                    wc.environment(World.Environment.valueOf(c.getString(s + ".environment")));
                if (c.get(s + ".generator") != null)
                    wc.generator(c.getString(s + ".generator"));
                if (c.get(s + ".generatorSettings") != null)
                    wc.generatorSettings(c.getString(s + ".generatorSettings"));
                if (c.get(s + ".generateStructures") != null)
                    wc.generateStructures(c.getBoolean(s + ".generateStructures"));
                wc.createWorld();
            }
            WorldConfig.RestrictedType type = c.get(s + ".restrictedType") == null ? WorldConfig.RestrictedType.NONE : WorldConfig.RestrictedType.valueOf(c.getString(s + ".restrictedType"));
            String restricted = c.getString(s + ".restricted");
            this.worldConfigs.put(s, new WorldConfig(s, editMode, type, restricted));


        }
    }

    public List<String> getEditModeWorlds() {
        List<String> list = new ArrayList<>();
        this.worldConfigs.values().stream().filter(WorldConfig::isEditMode).forEach(w -> list.add(w.getWorld()));
        return list;

    }

    public void addEditModeWorlds(String... worlds) {
        for (String str : worlds) {
            if (this.worldConfigs.containsKey(str)) this.worldConfigs.get(str).setEditMode(true);
            this.worldConfigs.put(str, new WorldConfig(str, true));
        }
    }

//    public void setEditModeWorlds(List<String> editModeWorlds) {
//        this.editModeWorlds = editModeWorlds;
//    }

    public boolean usesEditMode(String world) {
        return this.worldConfigs.get(world).isEditMode();
    }

    public HashMap<String, WorldConfig> getWorldConfigs() {
        return this.worldConfigs;
    }

    public WorldConfig getWorldConfig(String s) {
        return this.worldConfigs.getOrDefault(s, new WorldConfig(s, true, WorldConfig.RestrictedType.RESTRICTED, null));
    }

}
