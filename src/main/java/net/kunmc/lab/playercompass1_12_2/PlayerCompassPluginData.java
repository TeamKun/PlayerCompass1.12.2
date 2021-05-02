package net.kunmc.lab.playercompass1_12_2;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PlayerCompassPluginData {
    private final FileConfiguration config;
    private final PlayerCompassPlugin plugin;

    PlayerCompassPluginData(PlayerCompassPlugin plugin) {
        plugin.saveDefaultConfig();
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

    public Map<String, String> getApplicants() {
        MemorySection sec = ((MemorySection) config.get("Applicants"));

        Map<String, String> applicants = new HashMap<>();
        for (String key : sec.getKeys(false)) {
            applicants.put(key, sec.getString(key));
        }
        return applicants;
    }

    public void setApplicant(String senderName, String targetName) {
        set("Applicants." + senderName, targetName);
    }
}
