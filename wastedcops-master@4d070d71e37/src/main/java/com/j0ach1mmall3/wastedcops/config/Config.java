package com.j0ach1mmall3.wastedcops.config;

import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.wastedcops.Main;
import com.j0ach1mmall3.wastedcops.api.CopProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 4/06/2016
 */
public final class Config extends ConfigLoader<Main> {
    private final List<CopProperties> copProperties;
    private final Map<Integer, Map<String, int[]>> groups;

    public Config(Main plugin) {
        super("config.yml", plugin);
        this.copProperties = this.loadCopProperties();
        this.groups = this.loadGroups();
    }

    public List<CopProperties> getCopProperties() {
        return this.copProperties;
    }

    public Map<Integer, Map<String, int[]>> getGroups() {
        return this.groups;
    }

    private List<CopProperties> loadCopProperties() {
        return this.customConfig.getKeys("Cops").stream().map(s -> new CopProperties(
                s,
                this.config.getString("Cops." + s + ".Entity").toUpperCase(),
                this.config.getDouble("Cops." + s + ".Health"),
                this.config.getInt("Cops." + s + ".WeaponDropChance"),
                this.config.getString("Cops." + s + ".Name"),
                this.config.getInt("Cops." + s + ".KillReward"),
                this.config.getStringList("Cops." + s + ".Weapons")
        )).collect(Collectors.toList());
    }

    private Map<Integer, Map<String, int[]>> loadGroups() {
        return this.customConfig.getKeys("Groups").stream().collect(Collectors.toMap(Integer::valueOf, s -> this.customConfig.getKeys("Groups." + s).stream().collect(Collectors.toMap(s1 -> s1, s2 -> new int[]{
                this.config.getInt("Groups." + s + '.' + s2 + ".Min"),
                this.config.getInt("Groups." + s + '.' + s2 + ".Max"),
        }))));
    }
}