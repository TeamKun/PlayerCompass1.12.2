package net.kunmc.lab.playercompass1_12_2;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kunmc.lab.playercompass1_12_2.command.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class PlayerCompassPlugin extends JavaPlugin {
    private static PlayerCompassPlugin INSTANCE;
    private static PlayerCompassPluginData DATA;
    private static ProtocolManager protocolManager;

    public static PlayerCompassPlugin getInstance() {
        return INSTANCE;
    }

    public static PlayerCompassPluginData getData() {
        return DATA;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        DATA = new PlayerCompassPluginData(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        getServer().getPluginCommand("changeupdateperiod").setExecutor(new ChangeUpdatePeriodCommand());
        getServer().getPluginCommand("compass").setExecutor(new CompassCommand());
        getServer().getPluginCommand("playerpos").setExecutor(new ShowPositionCommand());
        getServer().getPluginCommand("playerposoff").setExecutor(new HidePositionCommand());

        CommandExecutor kuncommand = new ForKunCommand();
        getServer().getPluginCommand("kun").setExecutor(kuncommand);
        getServer().getPluginCommand("kunxyz").setExecutor(kuncommand);

        getServer().getPluginManager().registerEvents(new CompassClickListener(), this);

        Map<String, String> applicants = DATA.getApplicants();
        if (applicants != null) {
            for (String senderName : applicants.keySet()) {
                CompassTaskManager.getInstance().register(senderName, applicants.get(senderName));
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
