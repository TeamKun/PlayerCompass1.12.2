package net.kunmc.lab.playercompass1_12_2;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCompassPluginData {
    private final FileConfiguration config;
    private final PlayerCompassPlugin plugin;

    PlayerCompassPluginData(PlayerCompassPlugin plugin) {
        this.config = plugin.getConfig();
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.saveConfig();
            }
        }.runTaskTimerAsynchronously(plugin, 0, getSavePeriod());
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

    private long getSavePeriod() {
        return config.getLong("SavePeriod");
    }

}
