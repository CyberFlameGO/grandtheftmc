package net.grandtheftmc.Creative;

import org.bukkit.configuration.file.YamlConfiguration;

public class CreativeSettings {

	private YamlConfiguration creativeConfig;

	public YamlConfiguration getCreativeConfig() {
		return creativeConfig;
	}

	public void setCreativeConfig(YamlConfiguration creativeConfig) {
		this.creativeConfig = creativeConfig;
	}

}
