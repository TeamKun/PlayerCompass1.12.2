package net.kunmc.lab.playercompass1_12_2;

import net.kunmc.lab.playercompass1_12_2.command.CompassCommand;
import net.kunmc.lab.playercompass1_12_2.command.ForKunCommand;
import net.kunmc.lab.playercompass1_12_2.command.HidePositionCommand;
import net.kunmc.lab.playercompass1_12_2.command.ShowPositionCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerCompassPlugin extends JavaPlugin {
    private static PlayerCompassPlugin INSTANCE;
    private static PlayerCompassPluginData DATA;

    public static PlayerCompassPlugin getInstance() {
        return INSTANCE;
    }

    public static PlayerCompassPluginData getData() {
        return DATA;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        DATA = new PlayerCompassPluginData(this);

        getServer().getPluginCommand("compass").setExecutor(new CompassCommand());
        getServer().getPluginCommand("playerpos").setExecutor(new ShowPositionCommand());
        getServer().getPluginCommand("playerposoff").setExecutor(new HidePositionCommand());

        CommandExecutor kuncommand = new ForKunCommand();
        getServer().getPluginCommand("kun").setExecutor(kuncommand);
        getServer().getPluginCommand("kunxyz").setExecutor(kuncommand);

        getServer().getPluginManager().registerEvents(new CompassClickListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
