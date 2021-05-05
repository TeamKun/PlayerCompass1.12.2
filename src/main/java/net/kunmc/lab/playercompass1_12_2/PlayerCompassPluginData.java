package net.kunmc.lab.playercompass1_12_2;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PlayerCompassPluginData {
    private final FileConfiguration config;
    private final PlayerCompassPlugin plugin;

    PlayerCompassPluginData(PlayerCompassPlugin plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    public String getKunName() {
        return config.getString("KunName");
    }

    public long getUpdatePointPeriod() {
        return config.getInt("UpdatePointPeriod");
    }

    public void setUpdatePointPeriod(long period) {
        set("UpdatePointPeriod", period);
    }

    private void set(String key, Object value) {
        config.set(key, value);
        plugin.saveConfig();
    }

    public Map<String, String> getApplicants() {
        MemorySection sec = ((MemorySection) config.get("Applicants"));
        if (sec == null) return null;

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
