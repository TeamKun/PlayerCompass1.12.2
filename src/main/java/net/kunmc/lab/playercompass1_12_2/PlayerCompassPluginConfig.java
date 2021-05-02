package net.kunmc.lab.playercompass1_12_2;

import org.bukkit.configuration.file.FileConfiguration;

public class PlayerCompassPluginConfig {
    private final FileConfiguration config;
    private final PlayerCompassPlugin plugin;

    PlayerCompassPluginConfig(PlayerCompassPlugin plugin) {
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    public long getUpdatePointPeriod() {
        return config.getInt("UpdatePointPeriod");
    }

    public void setUpdatePointPeriod(long period) {
        set("UpdatePointPeriod", period);
    }

    public void set(String key, Object value) {
        boolean needSave = config.get(key) == null;
        config.set(key, value);
        if (needSave) plugin.saveConfig();
    }
}
